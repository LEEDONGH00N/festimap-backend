package com.halo.eventer.domain.alimtalk.dto;

import java.time.LocalDateTime;

import com.halo.eventer.domain.alimtalk.AlimtalkOutbox;
import com.halo.eventer.domain.alimtalk.AlimtalkOutboxStatus;
import lombok.Getter;

@Getter
public class AlimtalkOutboxResponse {
    private final Long id;
    private final String templateCode;
    private final String toPhone;
    private final String messageId;
    private final String content;
    private final AlimtalkOutboxStatus status;
    private final String lastError;
    private final LocalDateTime sentAt;
    private final LocalDateTime createdAt;

    private AlimtalkOutboxResponse(AlimtalkOutbox outbox) {
        this.id = outbox.getId();
        this.templateCode = outbox.getTemplateCode();
        this.toPhone = maskPhone(outbox.getToPhone());
        this.messageId = outbox.getMessageId();
        this.content = outbox.getContent();
        this.status = outbox.getStatus();
        this.lastError = outbox.getLastError();
        this.sentAt = outbox.getSentAt();
        this.createdAt = outbox.getCreatedAt();
    }

    public static AlimtalkOutboxResponse from(AlimtalkOutbox outbox) {
        return new AlimtalkOutboxResponse(outbox);
    }

    private static String maskPhone(String phone) {
        if (phone == null || phone.length() < 4) {
            return "***";
        }
        return "***" + phone.substring(phone.length() - 4);
    }
}
