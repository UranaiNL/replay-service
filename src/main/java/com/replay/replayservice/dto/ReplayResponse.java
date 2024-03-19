package com.replay.replayservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReplayResponse {
    private String id;
    private String uploaderId;
    private String embedUrl;
    private String p1Username;
    private String p2Username;
    private String p1CharacterId;
    private String p2CharacterId;
    private String gameId;
    private Integer views;
}
