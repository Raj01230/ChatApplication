package com.dollop.app.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.Transport;

@org.springframework.context.annotation.Configuration
public class SocketIOConfig {
	@Value("${socket-server.host}")
	private String host;

	@Value("${socket-server.port}")
	private Integer port;

	@Bean
	public SocketIOServer socketIOServer() {
		Configuration config = new Configuration();
		config.setHostname(host);
		config.setPort(port);
		config.setOrigin("http://localhost:4200");
		config.setTransports(Transport.WEBSOCKET, Transport.POLLING);// Adjust if Angular runs on a different port
		config.setPingTimeout(60000); // Increase timeout to avoid premature disconnections
		config.setPingInterval(25000); // Regular ping to keep connection alive
		return new SocketIOServer(config);
	}
}
