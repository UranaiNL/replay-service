package com.replay.replayservice.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.replay.replayservice.config.RabbitMQConfig;
import com.replay.replayservice.dto.GetReplaysWithCharactersRequest;
import com.replay.replayservice.dto.ReplayRequest;
import com.replay.replayservice.dto.ReplayResponse;
import com.replay.replayservice.model.Replay;
import com.replay.replayservice.repository.ReplayRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReplayService {

    private final ReplayRepository replayRepository;
    private final ObjectMapper objectMapper;

    @RabbitListener(queues = RabbitMQConfig.REPLAY_QUEUE)
    public ReplayResponse createReplay(Message message) throws Exception {
        ReplayRequest replayRequest = objectMapper.readValue(message.getBody(), new TypeReference<ReplayRequest>() {});
        log.info("Received metadata request: {}", replayRequest);
        try{
            Replay replay = Replay.builder()
                    .uploaderId(replayRequest.getUploaderId())
                    .publicUrl(replayRequest.getPublicUrl())
                    .p1Username(replayRequest.getP1Username())
                    .p2Username(replayRequest.getP2Username())
                    .p1CharacterId(replayRequest.getP1CharacterId())
                    .p2CharacterId(replayRequest.getP2CharacterId())
                    .gameId(replayRequest.getGameId())
                    .views(0)
                    .build();
            replayRepository.save(replay);
            log.info("Replay {} is saved", replay.getId());
            return mapToReplayResponse(replay);
        }
        catch(Exception e){
            throw new Exception("Video upload failed:" + e.getMessage());
        }
    }

    public List<ReplayResponse> getReplayFeed(){
        List<Replay> replays = replayRepository.findAll(PageRequest.of(0,10)).getContent();
        return replays.stream().map(this::mapToReplayResponse).toList();
    }

    public ReplayResponse getReplayById(String replayId) {
        Optional<Replay> replay = replayRepository.findById(replayId);
        return replay.map(this::mapToReplayResponse).orElse(null);
    }

    public List<ReplayResponse> getReplaysWithUploaderId(String uploaderId) {
        List<Replay> replays = replayRepository.findAllByUploaderId(uploaderId);
        return replays.stream().map(this::mapToReplayResponse).toList();
    }

    public List<ReplayResponse> getReplaysWithCharactersByIds(GetReplaysWithCharactersRequest request) {
        String characterId1;
        String characterId2;
        characterId1 = request.getCharacterIds().get(0).toString();
        switch (request.getCharacterIds().size()){
            case 1:
                // If there is only one character requested, we want to check if it is present in P1 or P2.
                characterId2 = request.getCharacterIds().get(0).toString();
                break;
            case 2:
                characterId2 = request.getCharacterIds().get(1).toString();
                break;
            default:
                return Collections.emptyList();
        }
        List<Replay> replays = replayRepository.findAllByCharacterIds(characterId1, characterId2);
        return replays.stream().map(this::mapToReplayResponse).toList();
    }

    private ReplayResponse mapToReplayResponse(Replay replay) {
        return ReplayResponse.builder()
                .id(replay.getId())
                .uploaderId(replay.getUploaderId())
                .publicUrl(replay.getPublicUrl())
                .p1Username(replay.getP1Username())
                .p2Username(replay.getP2Username())
                .p1CharacterId(replay.getP1CharacterId())
                .p2CharacterId(replay.getP2CharacterId())
                .gameId(replay.getGameId())
                .views(replay.getViews())
                .build();
    }
}
