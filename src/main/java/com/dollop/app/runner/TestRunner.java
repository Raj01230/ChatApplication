package com.dollop.app.runner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.corundumstudio.socketio.SocketIOServer;

import jakarta.annotation.PreDestroy;

@Component
public class TestRunner implements CommandLineRunner {
	@Autowired
	private SocketIOServer server;

	@Override

	public void run(String... args) throws Exception {

		try {
			server.start();
			System.out.println("Socket.IO server started...");
		} catch (Exception e) {
			System.err.println("Error starting Socket.IO server: " + e.getMessage());
		}

	}

	@PreDestroy
	public void shutdown() {
		try {
			server.stop();
			System.out.println("Socket.IO server stopped.");
		} catch (Exception e) {
			System.err.println("Error stopping Socket.IO server: " + e.getMessage());
		}
	}

}
