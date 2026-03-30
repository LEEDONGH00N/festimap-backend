package com.halo.eventer.domain.program_reservation.entity.slot;

import java.time.LocalTime;
import jakarta.persistence.*;

import com.halo.eventer.global.common.BaseTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(indexes = {@Index(name = "idx_time_pattern_template_sort", columnList = "template_id, sort_order")})
public class ProgramTimePattern extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private Integer durationMinutes;

    @Column(nullable = false)
    private Integer capacity;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "template_id", nullable = false)
    private ProgramScheduleTemplate template;

    public static ProgramTimePattern of(
            ProgramScheduleTemplate template,
            LocalTime startTime,
            Integer durationMinutes,
            Integer capacity,
            Integer sortOrder) {
        ProgramTimePattern p = new ProgramTimePattern();
        p.template = template;
        p.startTime = startTime;
        p.durationMinutes = durationMinutes;
        p.capacity = capacity;
        p.sortOrder = sortOrder;
        return p;
    }

    public void updateCapacity(Integer capacity) {
        this.capacity = capacity;
    }
}
