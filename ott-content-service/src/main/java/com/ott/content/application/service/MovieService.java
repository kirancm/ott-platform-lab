package com.ott.content.application.service;

import com.ott.content.infrastructure.persistence.JpaMovieRepository;
import com.ott.content.infrastructure.persistence.MovieEntity;

import org.springframework.cache.annotation.Cacheable;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovieService {

    private final JpaMovieRepository movieRepository;

    public MovieService(JpaMovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public MovieEntity save(MovieEntity movie) {
        return movieRepository.save(movie);
    }

 
    public List<MovieEntity> getAllMovies() {
        return movieRepository.findAll();
    }

    @Cacheable(value = "movieCache", key = "#id")
    public MovieEntity getMovie(Long id) {
        return movieRepository.findById(id).orElseThrow();
    }

    public List<MovieEntity> searchByTitle(String title) {
    return movieRepository.findByTitleContainingIgnoreCase(title);
}
}