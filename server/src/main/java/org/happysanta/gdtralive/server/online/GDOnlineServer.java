package org.happysanta.gdtralive.server.online;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.happysanta.gdtralive.game.api.Constants;
import org.happysanta.gdtralive.game.api.dto.*;
import org.happysanta.gdtralive.game.api.model.HighScores;
import org.happysanta.gdtralive.game.api.model.Score;
import org.happysanta.gdtralive.game.util.Mapper;
import org.happysanta.gdtralive.game.util.Utils;
import org.happysanta.gdtralive.server.api.Opponent;
import org.happysanta.gdtralive.server.service.mapper.OpponentMapper;
import org.springframework.stereotype.Component;

import java.net.DatagramPacket;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class GDOnlineServer {

    private final UdpPort port;
    private final ExecutorService executor;
    private final OpponentMapper opponentMapper;
    private final String roomId = UUID.fromString("7265de80-720a-47bb-b3b4-0b36e3e7fac6").toString();

    //opponentId <-> Opponent
    private final Map<String, Opponent> opponents = new ConcurrentHashMap<>();
    //roomId <-> HighScores
    private final Map<String, HighScores> highScores = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        port.setOnPacketReceived(this::onPacketReceived);
    }

    @PreDestroy
    public void stop() {
        try {
            port.close();
            executor.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onPacketReceived(DatagramPacket packet) {
        if (OpponentMapper.isHandshake(packet.getData())) {
            executor.submit(() -> addOpponent(packet));
        } else {
            executor.submit(() -> sendToAllOpponents(packet));
        }
    }

    public RoomDto opponentJoined(OpponentJoinRequest request) {
        synchronized (opponents) {
            Opponent inMap = opponents.get(request.getId());
            if (inMap == null) {
                Opponent opponent = opponentMapper.mapOpponent(request, roomId);
                opponents.put(opponent.getId(), opponent);
            } else {
                inMap.setRoomId(roomId);
            }
            log.info("Opponent %s joined the room %s".formatted(request.getId(), roomId));
        }
        RoomDto room = new RoomDto();
        room.setId(roomId);
        room.setTrackId("");
        return room;
    }

    public void opponentDisconnected(OpponentDisconnectRequest request) {
        Opponent removed = null;
        synchronized (opponents) {
            removed = opponents.remove(request.getId());
            log.info("Opponent %s left the room %s".formatted(request.getId(), roomId));
        }
        if (removed != null) {
            try {
                OpponentState opponentState = new OpponentState();
                opponentState.setStatus(Constants.DISCONNECTED);
                String msg = "%s%s".formatted(removed.getId(), Utils.toJson(opponentState));
                byte[] bytes = msg.getBytes(StandardCharsets.UTF_8);
                for (Opponent opponent : opponents.values()) {
                    executor.submit(() -> {
                        try {
                            port.send(bytes, opponent.getHost(), opponent.getPort());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void addOpponent(DatagramPacket packet) {
        Opponent opponent = opponentMapper.mapOpponent(packet);
        if (opponent == null) {
            return;
        }
        opponent.setRoomId(roomId);// todo  DEBUG ONLY REMOVE
        synchronized (opponents) {
            Opponent inMap = opponents.get(opponent.getId());
            if (inMap == null) {
                opponents.put(opponent.getId(), opponent);
            } else {
                inMap.setHost(opponent.getHost());
                inMap.setPort(opponent.getPort());
            }
        }
        log.info("Opponent %s connected".formatted(opponent.getId()));
    }

    private void sendToAllOpponents(DatagramPacket packet) {
        String opponentId = Utils.mapOpponentId(packet);
        Opponent sender = opponents.get(opponentId);
        if (sender == null) {
            return;
        }
        for (Opponent opponent : opponents.values()) {
            executor.submit(() -> {
                if (opponent.getHost() == null
                        || opponent.getPort() == null
                        || !Objects.equals(sender.getRoomId(), opponent.getRoomId())
                        || opponent.getId().equals(opponentId)) {
                    return;
                }
                try {
                    port.send(packet, opponent.getHost(), opponent.getPort());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public void opponentFinished(ScoreDto scoreDto) {
        executor.submit(() -> {
            try {
                String roomId = scoreDto.getRoomId();
                Score score = Mapper.fromDto(scoreDto);
                synchronized (highScores) {
                    HighScores roomScores = highScores.get(roomId);
                    if (roomScores == null) {
                        roomScores = new HighScores();
                        highScores.put(roomId, roomScores);
                    }
                    roomScores.add(score, score.getLeague());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public List<ScoreDto> getHighScores(String roomId, Integer league) {
        HighScores roomScores = highScores.get(roomId);
        if (roomScores == null) {
            return new ArrayList<>();
        }
        return roomScores.get(league).stream()
                .map(score -> Mapper.toDto(score, roomId))
                .limit(10)
                .collect(Collectors.toList());
    }
}
