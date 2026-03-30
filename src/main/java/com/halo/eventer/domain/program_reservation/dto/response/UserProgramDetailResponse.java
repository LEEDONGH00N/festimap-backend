package com.halo.eventer.domain.program_reservation.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.halo.eventer.domain.program_reservation.Program;
import com.halo.eventer.domain.program_reservation.entity.content.FestivalCommonTemplate;
import com.halo.eventer.domain.program_reservation.entity.content.ProgramBlock;
import com.halo.eventer.domain.program_reservation.entity.content.ProgramTag;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserProgramDetailResponse {
    private Long id;
    private String name;
    private String thumbnailUrl;
    private LocalDateTime bookingOpenAt;
    private LocalDateTime bookingCloseAt;
    private int price;
    private String durationTime;
    private String availableAge;
    private int maxPersonCount;
    private List<ProgramDetailResponse.TagResponse> tags;
    private List<ReservationDateListResponse.DateItem> dates;
    private List<SummaryResponse> summaries;
    private List<DescriptionResponse> descriptions;
    private List<CautionResponse> cautions;
    private List<TemplateResponse> templates;

    public static UserProgramDetailResponse from(
            Program program,
            List<ProgramTag> programTags,
            List<ReservationDateListResponse.DateItem> dates,
            List<ProgramBlock> blocks,
            List<FestivalCommonTemplate> templates) {
        UserProgramDetailResponse response = new UserProgramDetailResponse();
        response.id = program.getId();
        response.name = program.getName();
        response.thumbnailUrl = program.getThumbnailUrl();
        response.bookingOpenAt = program.getBookingOpenAt();
        response.bookingCloseAt = program.getBookingCloseAt();
        response.price = program.getPriceAmount();
        response.durationTime = program.getDurationTime();
        response.availableAge = program.getAvailableAge();
        response.maxPersonCount = program.getMaxPersonCount();
        response.tags = programTags.stream()
                .map(ProgramDetailResponse.TagResponse::from)
                .collect(Collectors.toList());
        response.dates = dates;
        response.summaries = blocks.stream()
                .filter(b -> b.getType() == ProgramBlock.BlockType.SUMMARY)
                .map(SummaryResponse::from)
                .collect(Collectors.toList());
        response.descriptions = blocks.stream()
                .filter(b -> b.getType() == ProgramBlock.BlockType.DESCRIPTION)
                .map(DescriptionResponse::from)
                .collect(Collectors.toList());
        response.cautions = blocks.stream()
                .filter(b -> b.getType() == ProgramBlock.BlockType.CAUTION)
                .map(CautionResponse::from)
                .collect(Collectors.toList());
        response.templates = templates.stream().map(TemplateResponse::from).collect(Collectors.toList());
        return response;
    }

    @Getter
    @NoArgsConstructor
    public static class SummaryResponse {
        private String label;
        private String value;

        public static SummaryResponse from(ProgramBlock block) {
            SummaryResponse dto = new SummaryResponse();
            dto.label = block.getSummaryLabel();
            dto.value = block.getSummaryValue();
            return dto;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class DescriptionResponse {
        private String oneLine;
        private String detail;
        private String imageUrl;

        public static DescriptionResponse from(ProgramBlock block) {
            DescriptionResponse dto = new DescriptionResponse();
            dto.oneLine = block.getDescriptionOneLine();
            dto.detail = block.getDescriptionDetail();
            dto.imageUrl = block.getDescriptionImageUrl();
            return dto;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class CautionResponse {
        private String content;

        public static CautionResponse from(ProgramBlock block) {
            CautionResponse dto = new CautionResponse();
            dto.content = block.getCautionContent();
            return dto;
        }
    }
}
