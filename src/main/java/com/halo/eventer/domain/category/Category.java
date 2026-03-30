package com.halo.eventer.domain.category;

import jakarta.persistence.*;

import com.halo.eventer.domain.category.enums.CategoryStatus;
import com.halo.eventer.domain.fieldops.FieldOpsSession;
import com.halo.eventer.domain.module.FestivalModule;
import com.halo.eventer.global.common.BaseTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "festival_module_id", nullable = false)
    private FestivalModule festivalModule;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoryStatus status = CategoryStatus.ACTIVE;

    @Column(name = "display_order", nullable = false)
    private int displayOrder = 0;

    @Column(name = "field_ops_enabled", nullable = false)
    private boolean fieldOpsEnabled = false;

    @OneToOne(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private FieldOpsSession fieldOpsSession;

    private Category(String name, FestivalModule festivalModule, int displayOrder) {
        this.name = name;
        this.festivalModule = festivalModule;
        this.displayOrder = displayOrder;
    }

    public static Category create(String name, FestivalModule festivalModule, int displayOrder) {
        Category category = new Category(name, festivalModule, displayOrder);
        festivalModule.addCategory(category);
        return category;
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updateStatus(CategoryStatus status) {
        this.status = status;
    }

    public void updateDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

    public void enableFieldOps() {
        this.fieldOpsEnabled = true;
    }

    public void disableFieldOps() {
        this.fieldOpsEnabled = false;
    }

    public void assignFieldOpsSession(FieldOpsSession session) {
        this.fieldOpsSession = session;
    }
}
