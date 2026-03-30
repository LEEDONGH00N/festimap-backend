package com.halo.eventer.domain.fieldops;

import java.util.UUID;
import jakarta.persistence.*;

import com.halo.eventer.domain.category.Category;
import com.halo.eventer.domain.fieldops.enums.FieldOpsSessionStatus;
import com.halo.eventer.global.common.BaseTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "field_ops_session")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FieldOpsSession extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 36)
    private String token;

    @Column(name = "pw_hash", nullable = false)
    private String pwHash;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FieldOpsSessionStatus status = FieldOpsSessionStatus.ACTIVE;

    @Column(name = "session_ttl_hours", nullable = false)
    private int sessionTtlHours = 24;

    private FieldOpsSession(Category category, String pwHash) {
        this.token = UUID.randomUUID().toString();
        this.pwHash = pwHash;
        this.category = category;
    }

    public static FieldOpsSession create(Category category, String pwHash) {
        FieldOpsSession session = new FieldOpsSession(category, pwHash);
        category.assignFieldOpsSession(session);
        return session;
    }

    public boolean isValid() {
        return this.status == FieldOpsSessionStatus.ACTIVE
                && this.category.getFestivalModule().getFestival().isActive();
    }

    public void resetPassword(String newPwHash) {
        this.pwHash = newPwHash;
    }

    public void disable() {
        this.status = FieldOpsSessionStatus.DISABLED;
    }

    public void regenerateToken() {
        this.token = UUID.randomUUID().toString();
    }

    public void updateSessionTtl(int hours) {
        this.sessionTtlHours = hours;
    }
}
