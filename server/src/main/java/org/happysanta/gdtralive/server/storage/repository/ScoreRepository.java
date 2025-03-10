package org.happysanta.gdtralive.server.storage.repository;

import org.happysanta.gdtralive.server.storage.model.ScoreEntity;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScoreRepository extends JpaRepository<ScoreEntity, Long> {
    List<ScoreEntity> findAllByTrackIdAndLeagueOrderByTimeAsc(String trackId, Integer league, Limit limit);
}
