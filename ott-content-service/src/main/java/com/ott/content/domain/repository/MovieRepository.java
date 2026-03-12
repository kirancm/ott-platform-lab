package com.ott.content.domain.repository;

import com.ott.content.domain.model.Movie;
import java.util.List;
import java.util.Optional;

public interface MovieRepository {

    Movie save(Movie movie);

    Optional<Movie> findById(Long id);

    List<Movie> findAll();

}