package org.happysanta.gdtralive.game.http;

public class ServerConfig {
    private String host;
    private Integer port;

    public ServerConfig() {
    }

    public ServerConfig(String host, Integer port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String url() {
        return String.format("http://%s:%s", host, port);
    }
}
