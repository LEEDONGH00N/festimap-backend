package com.halo.eventer.domain.member.dto;

import com.halo.eventer.domain.member.NotificationRecipient;
import lombok.Getter;

@Getter
public class NotificationRecipientResponse {

    private final Long id;
    private final String name;
    private final String phone;

    public NotificationRecipientResponse(NotificationRecipient recipient) {
        this.id = recipient.getId();
        this.name = recipient.getName();
        this.phone = recipient.getPhone();
    }
}
