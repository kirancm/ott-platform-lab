package com.ott.content.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ott.content.infrastructure.persistence.MovieEntity;

public interface JpaMovieRepository extends JpaRepository<MovieEntity, Long> {


}