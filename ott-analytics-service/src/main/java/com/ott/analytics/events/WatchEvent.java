package com.ott.analytics.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WatchEvent {

    private Long userId;
    private Long contentId;
    private Integer watchPosition;
    private Long timestamp;

}
