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
public class ProgramDetailResponse {
    private Long id;
    private String name;
    private String thumbnailUrl;
    private Program.PricingType pricingType;
    private int priceAmount;
    private String durationTime;
    private String availableAge;
    private Program.PersonLimit personLimit;
    private int maxPersonCount;
    private boolean isActive;
    private LocalDateTime activeStartAt;
    private LocalDateTime activeEndAt;
    private List<TagResponse> tags;
    private List<BlockResponse> blocks;
    private List<TemplateResponse> templates;

    public static ProgramDetailResponse from(
            Program program,
            List<ProgramTag> programTags,
            List<ProgramBlock> blocks,
            List<FestivalCommonTemplate> templates) {
        ProgramDetailResponse dto = new ProgramDetailResponse();
        dto.id = program.getId();
        dto.name = program.getName();
        dto.thumbnailUrl = program.getThumbnailUrl();
        dto.pricingType = program.getPricingType();
        dto.priceAmount = program.getPriceAmount();
        dto.durationTime = program.getDurationTime();
        dto.availableAge = program.getAvailableAge();
        dto.personLimit = program.getPersonLimit();
        dto.maxPersonCount = program.getMaxPersonCount();
        dto.isActive = program.isActive();
        dto.activeStartAt = program.getActiveStartAt();
        dto.activeEndAt = program.getActiveEndAt();
        dto.tags = programTags.stream().map(TagResponse::from).collect(Collectors.toList());
        dto.blocks = blocks.stream().map(BlockResponse::from).collect(Collectors.toList());
        dto.templates = templates.stream().map(TemplateResponse::from).collect(Collectors.toList());
        return dto;
    }

    @Getter
    @NoArgsConstructor
    public static class TagResponse {
        private Long tagId;
        private String tagName;

        public static TagResponse from(ProgramTag programTag) {
            TagResponse dto = new TagResponse();
            dto.tagId = programTag.getTag().getId();
            dto.tagName = programTag.getTag().getName();
            return dto;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class BlockResponse {
        private Long id;
        private ProgramBlock.BlockType type;
        private int sortOrder;
        private String summaryLabel;
        private String summaryValue;
        private String descriptionOneLine;
        private String descriptionDetail;
        private String descriptionImageUrl;
        private String cautionContent;

        public static BlockResponse from(ProgramBlock block) {
            BlockResponse dto = new BlockResponse();
            dto.id = block.getId();
            dto.type = block.getType();
            dto.sortOrder = block.getSortOrder();
            dto.summaryLabel = block.getSummaryLabel();
            dto.summaryValue = block.getSummaryValue();
            dto.descriptionOneLine = block.getDescriptionOneLine();
            dto.descriptionDetail = block.getDescriptionDetail();
            dto.descriptionImageUrl = block.getDescriptionImageUrl();
            dto.cautionContent = block.getCautionContent();
            return dto;
        }
    }
}
