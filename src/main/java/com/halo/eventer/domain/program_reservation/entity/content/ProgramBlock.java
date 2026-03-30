package com.halo.eventer.domain.program_reservation.entity.content;

import jakarta.persistence.*;

import com.halo.eventer.domain.program_reservation.Program;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(
        name = "program_block",
        uniqueConstraints = {
            @UniqueConstraint(
                    name = "uk_program_block_program_type_sort",
                    columnNames = {"program_id", "type", "sort_order"})
        })
public class ProgramBlock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "program_id", nullable = false)
    private Program program;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BlockType type;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    public enum BlockType {
        SUMMARY, // 프로그램 요약 안내: (항목명, 요약정보)
        DESCRIPTION, // 프로그램 설명: (한줄 설명, 상세 설명, 이미지url)
        CAUTION // 프로그램 주의사항: (내용)
    }

    // SUMMARY: (항목명, 해당 요약 정보)
    @Column(name = "summary_label", length = 255)
    private String summaryLabel;

    @Column(name = "summary_value", length = 255)
    private String summaryValue;

    // DESCRIPTION: (한줄 설명, 상세 설명, 이미지url)
    @Column(name = "description_one_line", length = 255)
    private String descriptionOneLine;

    @Lob
    @Column(name = "description_detail", columnDefinition = "TEXT")
    private String descriptionDetail;

    @Column(name = "description_image_url", length = 500)
    private String descriptionImageUrl;

    // CAUTION: (내용)
    @Lob
    @Column(name = "caution_content", columnDefinition = "TEXT")
    private String cautionContent;

    public static ProgramBlock summary(Program program, int sortOrder, String label, String value) {
        ProgramBlock b = base(program, sortOrder, BlockType.SUMMARY);
        b.summaryLabel = label;
        b.summaryValue = value;
        return b;
    }

    public static ProgramBlock description(
            Program program, int sortOrder, String oneLine, String detail, String imageUrl) {
        ProgramBlock b = base(program, sortOrder, BlockType.DESCRIPTION);
        b.descriptionOneLine = oneLine;
        b.descriptionDetail = detail;
        b.descriptionImageUrl = imageUrl;
        return b;
    }

    public static ProgramBlock caution(Program program, int sortOrder, String content) {
        ProgramBlock b = base(program, sortOrder, BlockType.CAUTION);
        b.cautionContent = content;
        return b;
    }

    private static ProgramBlock base(Program program, int sortOrder, BlockType type) {
        ProgramBlock b = new ProgramBlock();
        b.program = program;
        b.sortOrder = sortOrder;
        b.type = type;
        return b;
    }
}
