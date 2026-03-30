package com.halo.eventer.domain.program_reservation.entity.additional;

import jakarta.persistence.*;

import com.halo.eventer.domain.program_reservation.entity.reservation.ProgramReservation;
import com.halo.eventer.global.common.BaseTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "program_reservation_additional_answer",
        uniqueConstraints = {
            @UniqueConstraint(
                    name = "uk_answer_reservation_field",
                    columnNames = {"reservation_id", "field_id"})
        })
public class ProgramReservationAdditionalAnswer extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String fieldLabelSnapshot;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private AdditionalFieldType typeSnapshot;

    @Column(columnDefinition = "TEXT")
    private String valueText;

    @Column(length = 100)
    private String optionLabelSnapshot;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reservation_id", nullable = false)
    private ProgramReservation reservation;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "field_id", nullable = false)
    private ProgramAdditionalField field;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_id")
    private ProgramAdditionalFieldOption option;

    public static ProgramReservationAdditionalAnswer ofText(
            ProgramReservation reservation, ProgramAdditionalField field, String valueText) {
        ProgramReservationAdditionalAnswer a = new ProgramReservationAdditionalAnswer();
        a.reservation = reservation;
        a.field = field;
        a.fieldLabelSnapshot = field.getLabel();
        a.typeSnapshot = AdditionalFieldType.TEXT;
        a.valueText = valueText;
        return a;
    }

    public static ProgramReservationAdditionalAnswer ofSelect(
            ProgramReservation reservation,
            ProgramAdditionalField field,
            ProgramAdditionalFieldOption option,
            String freeText) {
        ProgramReservationAdditionalAnswer a = new ProgramReservationAdditionalAnswer();
        a.reservation = reservation;
        a.field = field;
        a.fieldLabelSnapshot = field.getLabel();
        a.typeSnapshot = AdditionalFieldType.SELECT;
        a.option = option;
        a.optionLabelSnapshot = option.getLabel();
        a.valueText = option.isFreeText() ? freeText : null;
        return a;
    }
}
