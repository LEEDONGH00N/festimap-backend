package com.halo.eventer.domain.alimtalk;

import java.time.LocalDateTime;
import jakarta.persistence.*;

import lombok.*;

@Entity
@Table(
        name = "alimtalk_outbox",
        indexes = {@Index(name = "idx_alimtalk_outbox_status_created", columnList = "status,createdAt")})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class AlimtalkOutbox {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String templateCode;

    @Column(nullable = false)
    private String toPhone;

    // SENS 응답으로 전달되는 messageId (상태 조회용)
    private String messageId;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String buttonsJson;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AlimtalkOutboxStatus status;

    @Lob
    private String lastError;

    private LocalDateTime sentAt;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public static AlimtalkOutbox pending(String templateCode, String toPhone, String content, String buttonsJson) {
        return AlimtalkOutbox.builder()
                .templateCode(templateCode)
                .toPhone(toPhone)
                .content(content)
                .buttonsJson(buttonsJson)
                .status(AlimtalkOutboxStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public void markSent(String messageId) {
        this.status = AlimtalkOutboxStatus.SENT;
        this.messageId = messageId;
        this.sentAt = LocalDateTime.now();
        this.lastError = null;
    }

    public void markFailed(String error) {
        this.status = AlimtalkOutboxStatus.FAILED;
        this.lastError = error;
    }

    public void markPendingForResend() {
        this.status = AlimtalkOutboxStatus.PENDING;
        this.lastError = null;
        this.messageId = null;
        this.sentAt = null;
    }
}
