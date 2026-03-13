package com.ott.content.infrastructure.mapper;

import com.ott.content.infrastructure.persistence.MovieEntity;
import com.ott.content.interfaces.dto.MovieRequest;
import com.ott.content.interfaces.dto.MovieResponse;

public class MovieMapper {

    public static MovieEntity toEntity(MovieRequest request) {
        MovieEntity entity = new MovieEntity();
        entity.setTitle(request.getTitle());
        entity.setDescription(request.getDescription());
        entity.setReleaseYear(request.getReleaseYear());
        entity.setDurationMinutes(request.getDurationMinutes());
        entity.setLanguage(request.getLanguage());
        entity.setRating(request.getRating());
        entity.setGenre(request.getGenre());
        return entity;
    }
    public static MovieResponse toResponse(MovieEntity entity) {

        MovieResponse movieResponse = new MovieResponse();
        movieResponse.setId(entity.getId());
        movieResponse.setTitle(entity.getTitle());
        movieResponse.setDescription(entity.getDescription());
        movieResponse.setReleaseYear(entity.getReleaseYear());
        movieResponse.setDurationMinutes(entity.getDurationMinutes());
        movieResponse.setLanguage(entity.getLanguage());
        movieResponse.setRating(entity.getRating());
        movieResponse.setGenre(entity.getGenre());
        return movieResponse;
    }
}    