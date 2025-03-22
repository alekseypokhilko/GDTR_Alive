package org.happysanta.gdtralive.server.api;

import lombok.Data;

@Data
public class Opponent {
    private String id;
    private String host;
    private Integer port;
    private String roomId;
}
