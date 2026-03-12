package com.ott.content.interfaces.controller;

import com.ott.content.application.service.MovieService;
import com.ott.content.infrastructure.mapper.MovieMapper;
import com.ott.content.infrastructure.persistence.MovieEntity;
import com.ott.content.interfaces.dto.MovieRequest;
import com.ott.content.interfaces.dto.MovieResponse;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import com.ott.content.infrastructure.mapper.MovieMapper;


@RestController
@RequestMapping("/movies")
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @PostMapping
    public MovieResponse createMovie(@RequestBody MovieRequest request) {
        MovieEntity entity = MovieMapper.toEntity(request);
        MovieEntity saved = movieService.save(entity);
        return MovieMapper.toResponse(saved);
    }

   
    @GetMapping
    public List<MovieResponse> getAllMovies() {
        return movieService.getAllMovies()
        .stream()
        .map(MovieMapper::toResponse)
        .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public MovieResponse getMovie(@PathVariable Long id) {
        MovieEntity entity = movieService.getMovie(id);
        return MovieMapper.toResponse(entity);
    }
}