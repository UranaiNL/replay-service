package com.replay.replayservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(value = "replay")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Replay {
    @Id
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
