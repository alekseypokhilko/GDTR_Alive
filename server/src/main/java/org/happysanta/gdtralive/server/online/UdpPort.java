package org.happysanta.gdtralive.server.online;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.function.Consumer;

public class UdpPort {
    private final DatagramSocket udpSocket;
    private Consumer<DatagramPacket> onPacketReceived;
    private final Thread receiverThread;

    public UdpPort(Integer port) {
        try {
            udpSocket = port == null ? new DatagramSocket() : new DatagramSocket(port, InetAddress.getByName("0.0.0.0"));
            udpSocket.setSoTimeout(3000);
        } catch (IOException ex) {
            throw new RuntimeException("Error creating socket: " + ex.getMessage(), ex);
        }
        receiverThread = new Thread(this::receiverLoop, "udp-receiver-thread");
        receiverThread.start();
    }

    private void receiverLoop() {
        while (!Thread.interrupted()) {
            try {
                byte[] buf2 = new byte[(Integer) 1024];
                DatagramPacket packet = new DatagramPacket(buf2, buf2.length);
                if (udpSocket.isClosed()) {
                    return;
                }
                udpSocket.receive(packet);
                if (this.onPacketReceived != null) {
                    this.onPacketReceived.accept(packet);
                }
            } catch (SocketTimeoutException ignore) {
            } catch (Exception e) {
                e.printStackTrace();//todo
            }
        }
    }

    public void setOnPacketReceived(Consumer<DatagramPacket> onPacketReceived) {
        this.onPacketReceived = onPacketReceived;
    }

    public void send(DatagramPacket packet, String host, int port) throws IOException {
        DatagramPacket p = new DatagramPacket(packet.getData(), packet.getLength(), InetAddress.getByName(host), port);
        udpSocket.send(p);
    }

    public void send(byte[] data, String host, int port) throws IOException {
        DatagramPacket p = new DatagramPacket(data, data.length, InetAddress.getByName(host), port);
        udpSocket.send(p);
    }

    public void close() {
        try {
            receiverThread.interrupt();
            udpSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
