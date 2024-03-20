package com.replay.replayservice.controller;

import com.replay.replayservice.dto.GetReplaysWithCharactersRequest;
import com.replay.replayservice.dto.ReplayRequest;
import com.replay.replayservice.dto.ReplayResponse;
import com.replay.replayservice.service.ReplayService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value="/api/replay")
public class ReplayController {

    private final ReplayService replayService;

    // RequestBody & RequestParams & ModelAttribute don't work, so we have to resort to HttpServletRequest instead! :)
    // Hours wasted so far: 2 hours!
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String createReplay(HttpServletRequest request) throws Exception {
        String uploaderId = request.getParameter("uploaderId");
        String p1Username = request.getParameter("p1Username");
        String p2Username = request.getParameter("p2Username");
        String p1CharacterId = request.getParameter("p1CharacterId");
        String p2CharacterId = request.getParameter("p2CharacterId");
        String gameId = request.getParameter("gameId");

        MultipartFile videoFile = ((MultipartHttpServletRequest) request).getFile("videoFile");

        ReplayRequest replayRequest = new ReplayRequest();
        replayRequest.setUploaderId(uploaderId);
        replayRequest.setP1Username(p1Username);
        replayRequest.setP2Username(p2Username);
        replayRequest.setP1CharacterId(p1CharacterId);
        replayRequest.setP2CharacterId(p2CharacterId);
        replayRequest.setGameId(gameId);

        return replayService.createReplay(replayRequest, videoFile);
    }

    @GetMapping("/{replayId}")
    @ResponseStatus(HttpStatus.OK)
    public ReplayResponse getReplayById(@PathVariable("replayId") String replayId) {
        return replayService.getReplayById(replayId);
    }

    @GetMapping("/user/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public List<ReplayResponse> getReplaysWithUploaderId(@PathVariable("userId") String uploaderId){
        return replayService.getReplaysWithUploaderId(uploaderId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ReplayResponse> getReplaysWithCharactersByIds(@RequestBody GetReplaysWithCharactersRequest request){
        return replayService.getReplaysWithCharactersByIds(request);
    }
}