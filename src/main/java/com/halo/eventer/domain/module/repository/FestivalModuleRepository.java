package com.halo.eventer.domain.module.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.halo.eventer.domain.festival.Festival;
import com.halo.eventer.domain.module.FestivalModule;
import com.halo.eventer.domain.module.enums.ServiceType;

public interface FestivalModuleRepository extends JpaRepository<FestivalModule, Long> {

    List<FestivalModule> findByFestival(Festival festival);

    Optional<FestivalModule> findByFestivalAndServiceType(Festival festival, ServiceType serviceType);

    boolean existsByFestivalAndServiceType(Festival festival, ServiceType serviceType);
}
