package com.halo.eventer.domain.member.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.halo.eventer.domain.member.Member;
import com.halo.eventer.domain.member.NotificationRecipient;
import com.halo.eventer.domain.member.dto.NotificationRecipientRequest;
import com.halo.eventer.domain.member.exception.MemberNotFoundException;
import com.halo.eventer.domain.member.repository.MemberRepository;
import com.halo.eventer.domain.member.repository.NotificationRecipientRepository;
import com.halo.eventer.global.error.ErrorCode;
import com.halo.eventer.global.error.exception.BaseException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationRecipientService {

    private final NotificationRecipientRepository recipientRepository;
    private final MemberRepository memberRepository;

    public List<NotificationRecipient> getRecipients(Long memberId) {
        return recipientRepository.findByMemberId(memberId);
    }

    @Transactional
    public NotificationRecipient addRecipient(Long memberId, NotificationRecipientRequest request) {
        Member member = memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);
        NotificationRecipient recipient = new NotificationRecipient(request.getName(), request.getPhone(), member);
        return recipientRepository.save(recipient);
    }

    @Transactional
    public void updateRecipient(Long memberId, Long recipientId, NotificationRecipientRequest request) {
        NotificationRecipient recipient = getRecipientWithOwnerCheck(memberId, recipientId);
        recipient.update(request.getName(), request.getPhone());
    }

    @Transactional
    public void deleteRecipient(Long memberId, Long recipientId) {
        NotificationRecipient recipient = getRecipientWithOwnerCheck(memberId, recipientId);
        recipientRepository.delete(recipient);
    }

    private NotificationRecipient getRecipientWithOwnerCheck(Long memberId, Long recipientId) {
        NotificationRecipient recipient = recipientRepository
                .findById(recipientId)
                .orElseThrow(() -> new BaseException(ErrorCode.ENTITY_NOT_FOUND));

        if (!recipient.getMember().getId().equals(memberId)) {
            throw new BaseException(ErrorCode.UN_AUTHORIZED);
        }
        return recipient;
    }
}
