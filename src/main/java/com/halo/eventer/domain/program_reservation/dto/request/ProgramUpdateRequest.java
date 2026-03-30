package com.halo.eventer.domain.program_reservation.dto.request;

import java.util.List;

import com.halo.eventer.domain.program_reservation.Program;
import com.halo.eventer.domain.program_reservation.entity.content.ProgramBlock;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProgramUpdateRequest {

    private String thumbnailUrl;
    private Program.PricingType pricingType;
    private int priceAmount;
    private String durationTime;
    private String availableAge;
    private Program.PersonLimit personLimit;
    private int maxPersonCount;
    private List<TagRequest> tags;
    private List<BlockRequest> blocks;

    @Getter
    @NoArgsConstructor
    public static class TagRequest {
        private Long tagId;
    }

    @Getter
    @NoArgsConstructor
    public static class BlockRequest {
        private ProgramBlock.BlockType type;
        private String summaryLabel;
        private String summaryValue;
        private String descriptionOneLine;
        private String descriptionDetail;
        private String descriptionImageUrl;
        private String cautionContent;
    }
}
