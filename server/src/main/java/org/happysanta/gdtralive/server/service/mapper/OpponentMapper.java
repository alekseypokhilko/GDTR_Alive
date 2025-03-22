package org.happysanta.gdtralive.server.service.mapper;

import lombok.extern.slf4j.Slf4j;
import org.happysanta.gdtralive.game.api.dto.OpponentJoinRequest;
import org.happysanta.gdtralive.server.api.Opponent;
import org.springframework.stereotype.Component;

import java.net.DatagramPacket;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class OpponentMapper {

    public static final String HANDSHAKE_MESSAGE_PREFIX = "~";
    public static final byte[] HANDSHAKE_PREFIX_BYTES = HANDSHAKE_MESSAGE_PREFIX.getBytes(StandardCharsets.UTF_8);

    public Opponent mapOpponent(DatagramPacket packet) {
        String msg = new String(packet.getData()).trim();
        try {
            Opponent opponent = new Opponent();
            opponent.setId(msg.substring(1));
            opponent.setHost(packet.getAddress().getHostAddress());
            opponent.setPort(packet.getPort());
            return opponent;
        } catch (Exception e) {
            log.error("Exception on receive handshake '%s'".formatted(msg), e);
            return null;
        }
    }

    public Opponent mapOpponent(OpponentJoinRequest request, String roomId) {
        Opponent opponent = new Opponent();
        opponent.setId(request.getId());
        opponent.setRoomId(roomId);
        return opponent;
    }

    public static boolean isHandshake(byte[] packetData) {
        return packetData.length > 1
                && packetData[0] == HANDSHAKE_PREFIX_BYTES[0];
    }
}
