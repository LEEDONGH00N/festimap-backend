package com.halo.eventer.domain.program_reservation.entity.slot;

import java.time.LocalDate;
import jakarta.persistence.*;

import com.halo.eventer.domain.program_reservation.Program;
import com.halo.eventer.global.common.BaseTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(indexes = {@Index(name = "idx_schedule_template_program_start", columnList = "program_id, start_date")})
public class ProgramScheduleTemplate extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private ProgramSlotType slotType;

    private Integer durationMinutes;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "program_id", nullable = false)
    private Program program;

    public static ProgramScheduleTemplate of(
            Program program,
            LocalDate startDate,
            LocalDate endDate,
            ProgramSlotType slotType,
            Integer durationMinutes) {
        ProgramScheduleTemplate t = new ProgramScheduleTemplate();
        t.program = program;
        t.startDate = startDate;
        t.endDate = endDate;
        t.slotType = slotType;
        t.durationMinutes = durationMinutes;
        return t;
    }

    public void update(LocalDate startDate, LocalDate endDate, ProgramSlotType slotType, Integer durationMinutes) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.slotType = slotType;
        this.durationMinutes = durationMinutes;
    }
}
