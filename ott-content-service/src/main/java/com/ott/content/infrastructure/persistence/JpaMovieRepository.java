package com.ott.content.infrastructure.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaMovieRepository extends JpaRepository<MovieEntity, Long> {
        List<MovieEntity> findByTitleContainingIgnoreCase(String title);
        
}