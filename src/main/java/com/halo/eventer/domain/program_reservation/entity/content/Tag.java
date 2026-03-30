package com.halo.eventer.domain.program_reservation.entity.content;

import jakarta.persistence.*;

import com.halo.eventer.global.common.BaseTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Tag extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String code;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 7)
    private String bgColorHex;

    @Column(nullable = false, length = 7)
    private String mainColorHex;

    @Column(length = 255)
    private String iconUrl;

    @Column(nullable = false)
    private boolean isActive;
}
