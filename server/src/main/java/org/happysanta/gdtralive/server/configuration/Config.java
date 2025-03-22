package org.happysanta.gdtralive.server.configuration;

import org.happysanta.gdtralive.server.online.UdpPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class Config {

    @Bean
    public UdpPort gdOnlineServerPort() {
        return new UdpPort(27654);
    }

    @Bean
    public ExecutorService executorService() {
        return Executors.newFixedThreadPool(10);
    }
}
