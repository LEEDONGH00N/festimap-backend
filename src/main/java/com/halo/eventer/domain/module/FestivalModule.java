package com.halo.eventer.domain.module;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;

import com.halo.eventer.domain.category.Category;
import com.halo.eventer.domain.festival.Festival;
import com.halo.eventer.domain.module.enums.ServiceType;
import com.halo.eventer.global.common.BaseTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "festival_module")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FestivalModule extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "festival_id", nullable = false)
    private Festival festival;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ServiceType serviceType;

    @Column(nullable = false)
    private boolean enabled = false;

    @OneToMany(mappedBy = "festivalModule", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Category> categories = new ArrayList<>();

    private FestivalModule(Festival festival, ServiceType serviceType) {
        this.festival = festival;
        this.serviceType = serviceType;
    }

    public static FestivalModule create(Festival festival, ServiceType serviceType) {
        return new FestivalModule(festival, serviceType);
    }

    public void enable() {
        this.enabled = true;
    }

    public void disable() {
        this.enabled = false;
    }

    public void addCategory(Category category) {
        this.categories.add(category);
    }
}
