package org.happysanta.gdtralive.server.api;

import java.time.LocalDate;
import java.util.UUID;

import lombok.Data;

@Data
public class ScoreDto {
    private UUID id;
    private String trackId;
    private Integer league;
    private Long time;
    private String name;
    private LocalDate date;
}
