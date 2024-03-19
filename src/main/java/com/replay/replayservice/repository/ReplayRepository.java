package com.replay.replayservice.repository;

import com.replay.replayservice.model.Replay;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ReplayRepository extends MongoRepository<Replay, String> {
    List<Replay> findAllByUploaderId(String uploaderId);
    List<Replay> findAllByP1CharacterIdOrP2CharacterId(String p1CharacterId, String p2CharacterId);
    @Query(value = "SELECT * FROM replay WHERE (p1_character_id = ?1 OR p2_character_id = ?1) AND (p1_character_id = ?2 OR p2_character_id = ?2)")
    List<Replay> findAllByCharacterIds(String characterId1, String characterId2);
}
