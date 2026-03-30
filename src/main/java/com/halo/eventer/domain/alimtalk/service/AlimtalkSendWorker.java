package com.halo.eventer.domain.alimtalk.service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.halo.eventer.domain.alimtalk.AlimtalkOutbox;
import com.halo.eventer.domain.alimtalk.AlimtalkOutboxStatus;
import com.halo.eventer.domain.alimtalk.button.AlimtalkButton;
import com.halo.eventer.domain.alimtalk.button.AlimtalkButtonType;
import com.halo.eventer.domain.alimtalk.repository.AlimtalkRepository;
import com.halo.eventer.global.error.ErrorCode;
import com.halo.eventer.global.error.exception.BaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class AlimtalkSendWorker {

    private final AlimtalkRepository alimtalkRepository;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    @Value("${alimtalk.enabled:false}")
    private boolean enabled;

    @Value("${ncp.sens.accessKey:}")
    private String accessKey;

    @Value("${ncp.sens.secretKey:}")
    private String secretKey;

    @Value("${ncp.sens.serviceId:}")
    private String serviceId;

    @Value("${ncp.sens.plusFriendId:}")
    private String plusFriendId;

    @Async
    @Transactional
    public void sendAsync(Long outboxId) {
        AlimtalkOutbox outbox = alimtalkRepository.findById(outboxId).orElse(null);
        if (outbox == null) {
            log.warn("AlimtalkOutbox not found. outboxId={}", outboxId);
            return;
        }
        log.info(
                "[ALIMTALK][OUTBOX] id={}, status={}, templateCode={}, to={}, contentLen={}, buttonsJsonLen={}",
                outbox.getId(),
                outbox.getStatus(),
                outbox.getTemplateCode(),
                outbox.getToPhone(),
                outbox.getContent() == null ? 0 : outbox.getContent().length(),
                outbox.getButtonsJson() == null ? 0 : outbox.getButtonsJson().length());

        if (outbox.getStatus() != AlimtalkOutboxStatus.PENDING) {
            return;
        }

        if (!enabled) {
            log.info("Alimtalk disabled. Simulating success. outboxId={}", outboxId);
            outbox.markSent(null);
            return;
        }

        try {
            List<AlimtalkButton> buttons = readButtons(outbox.getButtonsJson());
            String templateCode = outbox.getTemplateCode();
            validateConfig(templateCode);

            String to = sanitizePhone(outbox.getToPhone());
            if (to.isBlank()) throw new BaseException("to phone is blank", ErrorCode.INTERNAL_SERVER_ERROR);

            SensRequest request =
                    buildRequest(templateCode, sanitizePhone(to), outbox.getContent(), toSensButtons(buttons));

            String path = "/alimtalk/v2/services/" + serviceId + "/messages";
            String url = "https://sens.apigw.ntruss.com" + path;
            String timestamp = String.valueOf(System.currentTimeMillis());
            String signature = makeSignature("POST", path, timestamp);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("x-ncp-iam-access-key", accessKey);
            headers.set("x-ncp-apigw-timestamp", timestamp);
            headers.set("x-ncp-apigw-signature-v2", signature);

            String requestJson = objectMapper.writeValueAsString(request);
            log.info("[ALIMTALK][REQ] outboxId={}, url={}, body={}", outboxId, url, maskRequestJson(requestJson));

            ResponseEntity<String> resp =
                    restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(request, headers), String.class);

            log.info(
                    "[ALIMTALK][RESP] outboxId={}, httpStatus={}, body={}",
                    outboxId,
                    resp.getStatusCodeValue(),
                    resp.getBody());

            if (!resp.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("SENS response " + resp.getStatusCodeValue() + ": " + resp.getBody());
            }

            String messageId = extractMessageId(resp.getBody());

            outbox.markSent(messageId);
            log.info("Alimtalk sent. outboxId={}, messageId={}", outboxId, messageId);

        } catch (Exception e) {
            log.warn("Alimtalk send failed. outboxId={}, error={}", outboxId, e.getMessage());
            outbox.markFailed(truncate(e.getMessage(), 2000));
        }
    }

    private String maskRequestJson(String json) {
        if (json == null) return "";
        // "to":"01012345678" → "***5678"
        return json.replaceAll("(\"to\"\\s*:\\s*\")([0-9]{0,7})([0-9]{4})(\")", "$1***$3$4");
    }

    private List<AlimtalkButton> readButtons(String json) throws Exception {
        if (json == null || json.isBlank()) return Collections.emptyList();
        return objectMapper.readValue(json, new TypeReference<List<AlimtalkButton>>() {});
    }

    private void validateConfig(String templateCode) {
        if (isBlank(accessKey)
                || isBlank(secretKey)
                || isBlank(serviceId)
                || isBlank(plusFriendId)
                || isBlank(templateCode)) {
            throw new IllegalStateException("SENS config missing");
        }
    }

    private SensRequest buildRequest(String templateCode, String to, String content, List<SensButton> buttons) {
        SensMessage msg = new SensMessage("82", to, content, buttons, false);
        return new SensRequest(plusFriendId, templateCode, Collections.singletonList(msg));
    }

    private List<SensButton> toSensButtons(List<AlimtalkButton> buttons) {
        if (buttons == null || buttons.isEmpty()) return Collections.emptyList();
        List<SensButton> result = new ArrayList<>();
        for (AlimtalkButton b : buttons) {
            if (b.getType() == AlimtalkButtonType.WL) {
                String link = sanitizeLink(b.getLinkMobile());
                result.add(new SensButton("WL", b.getName(), link, link)); // 카카오 요구사항: PC/Mobile 동일하게 세팅
            } else if (b.getType() == AlimtalkButtonType.MD) {
                result.add(new SensButton("MD", b.getName(), null, null));
            }
        }
        return result;
    }

    private String sanitizePhone(String phone) {
        return phone == null ? "" : phone.replaceAll("[^0-9]", "");
    }

    private String sanitizeLink(String link) {
        if (link == null || link.isBlank())
            throw new BaseException("button link is required", ErrorCode.INTERNAL_SERVER_ERROR);
        return link;
    }

    private String extractMessageId(String body) throws Exception {
        if (isBlank(body)) return null;
        JsonNode root = objectMapper.readTree(body);
        JsonNode messages = root.path("messages");
        if (messages.isArray() && messages.size() > 0) {
            String id = text(messages.get(0), "messageId");
            if (!isBlank(id)) return id;
        }
        return text(root, "messageId");
    }

    private String makeSignature(String method, String path, String timestamp) throws Exception {
        String message = method + " " + path + "\n" + timestamp + "\n" + accessKey;
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        return Base64.getEncoder().encodeToString(mac.doFinal(message.getBytes(StandardCharsets.UTF_8)));
    }

    private String truncate(String s, int max) {
        if (s == null) return "";
        return s.length() > max ? s.substring(0, max) : s;
    }

    private boolean isBlank(String s) {
        return s == null || s.isBlank();
    }

    private String text(JsonNode node, String field) {
        if (node == null || node.isMissingNode()) return "";
        JsonNode v = node.get(field);
        return v == null || v.isNull() ? "" : v.asText();
    }

    // SENS API DTOs
    public static class SensRequest {
        private String plusFriendId;
        private String templateCode;
        private List<SensMessage> messages;

        public SensRequest() {}

        public SensRequest(String plusFriendId, String templateCode, List<SensMessage> messages) {
            this.plusFriendId = plusFriendId;
            this.templateCode = templateCode;
            this.messages = messages;
        }

        public String getPlusFriendId() {
            return plusFriendId;
        }

        public String getTemplateCode() {
            return templateCode;
        }

        public List<SensMessage> getMessages() {
            return messages;
        }

        public void setPlusFriendId(String v) {
            this.plusFriendId = v;
        }

        public void setTemplateCode(String v) {
            this.templateCode = v;
        }

        public void setMessages(List<SensMessage> v) {
            this.messages = v;
        }
    }

    public static class SensMessage {
        private String countryCode;
        private String to;
        private String content;
        private List<SensButton> buttons;
        private boolean useSmsFailover;

        public SensMessage() {}

        public SensMessage(
                String countryCode, String to, String content, List<SensButton> buttons, boolean useSmsFailover) {
            this.countryCode = countryCode;
            this.to = to;
            this.content = content;
            this.buttons = buttons;
            this.useSmsFailover = useSmsFailover;
        }

        public String getCountryCode() {
            return countryCode;
        }

        public String getTo() {
            return to;
        }

        public String getContent() {
            return content;
        }

        public List<SensButton> getButtons() {
            return buttons;
        }

        public boolean isUseSmsFailover() {
            return useSmsFailover;
        }

        public void setCountryCode(String v) {
            this.countryCode = v;
        }

        public void setTo(String v) {
            this.to = v;
        }

        public void setContent(String v) {
            this.content = v;
        }

        public void setButtons(List<SensButton> v) {
            this.buttons = v;
        }

        public void setUseSmsFailover(boolean v) {
            this.useSmsFailover = v;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class SensButton {
        private String type;
        private String name;
        private String linkMobile;
        private String linkPc;

        public SensButton() {}

        public SensButton(String type, String name, String linkMobile, String linkPc) {
            this.type = type;
            this.name = name;
            this.linkMobile = linkMobile;
            this.linkPc = linkPc;
        }

        public String getType() {
            return type;
        }

        public String getName() {
            return name;
        }

        public String getLinkMobile() {
            return linkMobile;
        }

        public String getLinkPc() {
            return linkPc;
        }

        public void setType(String v) {
            this.type = v;
        }

        public void setName(String v) {
            this.name = v;
        }

        public void setLinkMobile(String v) {
            this.linkMobile = v;
        }

        public void setLinkPc(String v) {
            this.linkPc = v;
        }
    }
}
