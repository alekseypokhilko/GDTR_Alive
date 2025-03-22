package org.happysanta.gdtralive.server.controller;

import org.happysanta.gdtralive.game.api.dto.ScoreDto;
import org.happysanta.gdtralive.server.service.ScoreService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = {"/score"}, produces = MediaType.APPLICATION_JSON_VALUE)
public class ScoreController {
    private final ScoreService service;

    @GetMapping("/track/{trackId}/{league}")
    public List<ScoreDto> getTrackHighScores(@PathVariable("trackId") String trackId,
                                             @PathVariable("league") Integer league,
                                             @RequestParam(value = "roomId", required = false) String roomId) {
        return service.getTrackHighScores(trackId, league, roomId);
    }

    @PostMapping
    public String addTrackHighScore(@RequestBody ScoreDto score) {
        return service.createScore(score);
    }
}
