package org.happysanta.gdtralive.server.controller;

import org.happysanta.gdtralive.server.api.ScoreDto;
import org.happysanta.gdtralive.server.service.ScoreService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = {"/score"}, produces = MediaType.APPLICATION_JSON_VALUE)
public class ScoreController {
    private final ScoreService service;

    @GetMapping("/track/{trackId}/{league}")
    public List<ScoreDto> getTrackHighScores(@PathVariable("trackId") String trackId, @PathVariable("league") Integer league) {
        return service.getTrackHighScores(trackId, league);
    }

    @PostMapping
    public String addTrackHighScore(@RequestBody ScoreDto score) {
        return service.createScore(score);
    }
}
