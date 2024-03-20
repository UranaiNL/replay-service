package com.replay.replayservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReplayRequest {
    private String uploaderId;
    private String p1Username;
    private String p2Username;
    private String p1CharacterId;
    private String p2CharacterId;
    private String gameId;
}
