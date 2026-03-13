package com.ott.content.interfaces.dto;

public class WatchHistoryRequest {

    private Long userId;
    private Long movieId;
    private Integer watchProgressSeconds;
    private Boolean completed;

    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    public Long getMovieId() {
        return movieId;
    }
    public void setMovieId(Long movieId) {
        this.movieId = movieId;
    }
    public Integer getWatchProgressSeconds() {
        return watchProgressSeconds;
    }
    public void setWatchProgressSeconds(Integer watchProgressSeconds) {
        this.watchProgressSeconds = watchProgressSeconds;
    }
    public Boolean getCompleted() {
        return completed;
    }
    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }
}