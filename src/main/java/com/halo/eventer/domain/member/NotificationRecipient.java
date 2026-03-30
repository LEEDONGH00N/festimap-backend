package com.halo.eventer.domain.member;

import jakarta.persistence.*;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "notification_recipient")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class NotificationRecipient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 20)
    private String phone;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    public NotificationRecipient(String name, String phone, Member member) {
        this.name = name;
        this.phone = phone;
        this.member = member;
    }

    public void update(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }
}
