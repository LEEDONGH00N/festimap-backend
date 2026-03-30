package com.halo.eventer.domain.category.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.halo.eventer.domain.category.Category;
import com.halo.eventer.domain.module.FestivalModule;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByFestivalModuleOrderByDisplayOrderAsc(FestivalModule festivalModule);

    @Query("SELECT c FROM Category c " + "JOIN FETCH c.festivalModule fm " + "JOIN FETCH fm.festival f "
            + "WHERE c.id = :categoryId")
    Category findByIdWithFestival(@Param("categoryId") Long categoryId);

    int countByFestivalModule(FestivalModule festivalModule);
}
