package com.halo.eventer.domain.program_reservation.entity.additional;

import jakarta.persistence.*;

import com.halo.eventer.domain.program_reservation.Program;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "program_additional_field",
        indexes = {@Index(name = "idx_additional_field_program_sort", columnList = "program_id, sort_order")})
public class ProgramAdditionalField {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private AdditionalFieldType type;

    @Column(nullable = false, length = 100)
    private String label;

    @Column(nullable = false)
    private boolean required;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    @Column(nullable = false)
    private boolean isActive;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "program_id", nullable = false)
    private Program program;

    public static ProgramAdditionalField of(
            Program program, AdditionalFieldType type, String label, boolean required, int sortOrder) {
        ProgramAdditionalField f = new ProgramAdditionalField();
        f.program = program;
        f.type = type;
        f.label = label;
        f.required = required;
        f.sortOrder = sortOrder;
        f.isActive = true;
        return f;
    }

    public void changeSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    public void deactivate() {
        this.isActive = false;
    }

    public void update(AdditionalFieldType type, String label, boolean required, boolean isActive, int sortOrder) {
        this.type = type;
        this.label = label;
        this.required = required;
        this.isActive = isActive;
        this.sortOrder = sortOrder;
    }
}
