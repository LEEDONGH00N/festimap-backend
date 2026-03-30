package com.halo.eventer.domain.program_reservation.entity.content;

import jakarta.persistence.*;

import com.halo.eventer.domain.program_reservation.Program;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(
        uniqueConstraints = {
            @UniqueConstraint(columnNames = {"program_id", "tag_id"}),
            @UniqueConstraint(columnNames = {"program_id", "sort_order"})
        })
public class ProgramTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 프로그램 하나에 태그 여러개(최대 3개)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "program_id", nullable = false)
    private Program program;

    // 태그 하나가 여러 프로그램에 붙을 수 있음
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tag_id", nullable = false)
    private Tag tag;

    // 1~3 표시 순서
    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    public static ProgramTag of(Program program, Tag tag, int sortOrder) {
        ProgramTag pt = new ProgramTag();
        pt.program = program;
        pt.tag = tag;
        pt.sortOrder = sortOrder;
        return pt;
    }
}
