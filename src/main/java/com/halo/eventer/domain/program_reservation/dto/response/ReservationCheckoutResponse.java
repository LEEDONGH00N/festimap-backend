package com.halo.eventer.domain.program_reservation.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.halo.eventer.domain.program_reservation.entity.content.FestivalCommonTemplate;
import com.halo.eventer.domain.program_reservation.entity.content.ProgramBlock;
import com.halo.eventer.domain.program_reservation.entity.reservation.ProgramReservation;
import com.halo.eventer.domain.program_reservation.entity.reservation.ProgramReservationStatus;
import lombok.Getter;

@Getter
public class ReservationCheckoutResponse {
    private Long reservationId;
    private ProgramReservationStatus status;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime expiresAt;

    private Display display;

    public static ReservationCheckoutResponse from(
            ProgramReservation reservation, List<ProgramBlock> programBlocks, List<FestivalCommonTemplate> templates) {
        ReservationCheckoutResponse r = new ReservationCheckoutResponse();
        r.reservationId = reservation.getId();
        r.status = reservation.getStatus();
        r.expiresAt = reservation.getHoldExpiresAt();
        r.display = Display.of(programBlocks, templates);

        return r;
    }

    @Getter
    public static class Display {
        private List<CautionItem> cautions;
        private List<TemplateItem> templates;

        public static Display of(List<ProgramBlock> cautionBlocks, List<FestivalCommonTemplate> templates) {
            Display d = new Display();
            d.cautions = cautionBlocks.stream().map(CautionItem::from).toList();
            d.templates = templates.stream().map(TemplateItem::from).toList();
            return d;
        }

        @Getter
        public static class CautionItem {
            private Long id;
            private String content;

            public static CautionItem from(ProgramBlock block) {
                CautionItem item = new CautionItem();
                item.id = block.getId();
                item.content = block.getCautionContent();
                return item;
            }
        }

        @Getter
        public static class TemplateItem {
            private Long id;
            private String title;
            private String content;

            public static TemplateItem from(FestivalCommonTemplate template) {
                TemplateItem item = new TemplateItem();
                item.id = template.getId();
                item.title = template.getTitle();
                item.content = template.getContent();
                return item;
            }
        }
    }
}
