package org.happysanta.gdtralive.server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.happysanta.gdtralive.game.api.dto.OpponentDisconnectRequest;
import org.happysanta.gdtralive.game.api.dto.OpponentJoinRequest;
import org.happysanta.gdtralive.game.api.dto.RoomDto;
import org.happysanta.gdtralive.server.online.GDOnlineServer;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = {"/online"}, produces = MediaType.APPLICATION_JSON_VALUE)
public class OnlineController {
    private final GDOnlineServer server;

    @PostMapping("/room/join")
    public RoomDto opponentJoined(@RequestBody OpponentJoinRequest request) {
        return server.opponentJoined(request);
    }

    @PostMapping("/room/disconnect")
    public void opponentDisconnected(@RequestBody OpponentDisconnectRequest request) {
        server.opponentDisconnected(request);
    }
}
