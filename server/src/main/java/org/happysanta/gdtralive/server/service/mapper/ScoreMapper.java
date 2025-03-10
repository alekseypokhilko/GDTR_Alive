package org.happysanta.gdtralive.server.service.mapper;

import org.happysanta.gdtralive.server.api.ScoreDto;
import org.happysanta.gdtralive.server.storage.model.ScoreEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ScoreMapper {
    public ScoreEntity fromDto(ScoreDto score) {
        ScoreEntity entity = new ScoreEntity();
        entity.setId(score.getId());
        entity.setTrackId(score.getTrackId());
        entity.setLeague(score.getLeague());
        entity.setTime(score.getTime());
        entity.setName(score.getName());
        entity.setDate(score.getDate());
        return entity;
    }

    public ScoreDto toDto(ScoreEntity score) {
        ScoreDto dto = new ScoreDto();
        dto.setId(score.getId());
        dto.setTrackId(score.getTrackId());
        dto.setLeague(score.getLeague());
        dto.setTime(score.getTime());
        dto.setName(score.getName());
        dto.setDate(score.getDate());
        return dto;
    }

    public List<ScoreDto> toDto(List<ScoreEntity> scores) {
        return scores.stream()
                .map(this::toDto)
                .toList();
    }
}
