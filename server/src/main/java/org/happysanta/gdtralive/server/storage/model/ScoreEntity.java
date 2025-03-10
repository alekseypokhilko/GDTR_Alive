package org.happysanta.gdtralive.server.storage.model;

import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(name = "score")
@Entity
public class ScoreEntity {
    @Id
    @UuidGenerator
    @Column(name = "id", nullable = false)
    private UUID id;
    @Column(name = "track_id", nullable = false)
    private String trackId;
    @Column(name = "league", nullable = false)
    private Integer league;
    @Column(name = "time", nullable = false)
    private Long time;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "date", nullable = false)
    private LocalDate date;
}
