package com.replay.replayservice.service;

import com.replay.replayservice.dto.GetReplaysWithCharactersRequest;
import com.replay.replayservice.dto.ReplayRequest;
import com.replay.replayservice.dto.ReplayResponse;
import com.replay.replayservice.model.Replay;
import com.replay.replayservice.repository.ReplayRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReplayService {

    private final ReplayRepository replayRepository;
//    private final WebClient webClient;

    public String createReplay(ReplayRequest replayRequest, MultipartFile videoFile) throws Exception {
        try{
            String fileCode = handleFileUpload(videoFile);
            Replay replay = Replay.builder()
                    .uploaderId(replayRequest.getUploaderId())
                    .fileCode(fileCode)
                    .p1Username(replayRequest.getP1Username())
                    .p2Username(replayRequest.getP2Username())
                    .p1CharacterId(replayRequest.getP1CharacterId())
                    .p2CharacterId(replayRequest.getP2CharacterId())
                    .gameId(replayRequest.getGameId())
                    .views(0)
                    .build();
            replayRepository.save(replay);
            log.info("Replay {} is saved", replay.getId());
            return replay.getId();
        }
        catch(Exception e){
            throw new Exception("Video upload failed:" + e.getMessage());
        }
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

    public String handleFileUpload(MultipartFile videoFile) throws Exception {
        try{
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> requestBody = new LinkedMultiValueMap<>();
            requestBody.add("videoFile", videoFile.getResource());

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

            RestTemplate restTemplate = new RestTemplate();

            ResponseEntity<String> response = restTemplate.exchange(
                    "http://localhost:8089/api/upload",
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );
            if (response.getStatusCode() == HttpStatus.CREATED) {
                String fileCode = response.getBody();
                if (fileCode != null) {
                    return fileCode;
                }
                throw new Exception("Upload-Service gave created status, but no file-code.");
            }
            throw new Exception("Video wasn't stored!");
        }
        catch (Exception e){
            throw new Exception(e.getMessage());
        }

    }

    private ReplayResponse mapToReplayResponse(Replay replay) {
        return ReplayResponse.builder()
                .id(replay.getId())
                .uploaderId(replay.getUploaderId())
                .fileCode(replay.getFileCode())
                .p1Username(replay.getP1Username())
                .p2Username(replay.getP2Username())
                .p1CharacterId(replay.getP1CharacterId())
                .p2CharacterId(replay.getP2CharacterId())
                .gameId(replay.getGameId())
                .views(replay.getViews())
                .build();
    }
}
