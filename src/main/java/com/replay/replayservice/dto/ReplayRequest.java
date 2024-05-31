package com.replay.replayservice.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ReplayRequest {
    private String uploaderId;
    private String publicUrl;
    private String p1Username;
    private String p2Username;
    private String p1CharacterId;
    private String p2CharacterId;
    private String gameId;
}
