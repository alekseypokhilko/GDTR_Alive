package org.happysanta.gdtralive.server.service;

import org.happysanta.gdtralive.server.api.ScoreDto;
import org.happysanta.gdtralive.server.service.mapper.ScoreMapper;
import org.happysanta.gdtralive.server.storage.model.ScoreEntity;
import org.happysanta.gdtralive.server.storage.repository.ScoreRepository;
import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ScoreService {
    private final ScoreRepository repository;
    private final ScoreMapper mapper;

    @Transactional(readOnly = true)
    public List<ScoreDto> getTrackHighScores(String trackId, Integer league) {
        List<ScoreEntity> scores = repository.findAllByTrackIdAndLeagueOrderByTimeAsc(trackId, league, Limit.of(10));
        return mapper.toDto(scores);
    }

    @Transactional
    public String createScore(ScoreDto score) {
        ScoreEntity scoreEntity = mapper.fromDto(score);
        return repository.save(scoreEntity).getId().toString();
    }
}
