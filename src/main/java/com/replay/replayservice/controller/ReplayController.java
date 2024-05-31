package com.replay.replayservice.controller;

import com.replay.replayservice.dto.GetReplaysWithCharactersRequest;
import com.replay.replayservice.dto.ReplayResponse;
import com.replay.replayservice.service.ReplayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(value="/api/replay")
public class ReplayController {

    private final ReplayService replayService;

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

    @GetMapping("/feed")
    @ResponseStatus(HttpStatus.OK)
    public List<ReplayResponse> getReplayFeed(){
        return replayService.getReplayFeed();
    }
}