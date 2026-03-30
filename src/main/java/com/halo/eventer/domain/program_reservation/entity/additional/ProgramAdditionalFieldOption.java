package com.halo.eventer.domain.program_reservation.entity.additional;

import jakarta.persistence.*;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "program_additional_field_option",
        indexes = {@Index(name = "idx_additional_option_field_sort", columnList = "field_id, sort_order")})
public class ProgramAdditionalFieldOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String label;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    @Column(nullable = false)
    private boolean isActive;

    @Column(nullable = false)
    private boolean isFreeText;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "field_id", nullable = false)
    private ProgramAdditionalField field;

    public static ProgramAdditionalFieldOption of(
            ProgramAdditionalField field, String label, int sortOrder, boolean isFreeText) {
        ProgramAdditionalFieldOption o = new ProgramAdditionalFieldOption();
        o.field = field;
        o.label = label;
        o.sortOrder = sortOrder;
        o.isFreeText = isFreeText;
        o.isActive = true;
        return o;
    }

    public void changeSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    public void deactivate() {
        this.isActive = false;
    }

    public void update(String label, boolean isActive, int sortOrder, boolean isFreeText) {
        this.label = label;
        this.isActive = isActive;
        this.sortOrder = sortOrder;
        this.isFreeText = isFreeText;
    }
}
