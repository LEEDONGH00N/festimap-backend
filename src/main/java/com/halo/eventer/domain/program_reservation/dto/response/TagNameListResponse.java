package com.halo.eventer.domain.program_reservation.dto.response;

import java.util.List;

import com.halo.eventer.domain.program_reservation.entity.content.Tag;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TagNameListResponse {
    private List<TagNameResponse> tags;

    public static TagNameListResponse from(List<Tag> tags) {
        List<TagNameResponse> list = tags.stream().map(TagNameResponse::from).toList();
        return new TagNameListResponse(list);
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class TagNameResponse {
        private Long id;
        private String name;

        public static TagNameResponse from(Tag tag) {
            return new TagNameResponse(tag.getId(), tag.getName());
        }
    }
}
