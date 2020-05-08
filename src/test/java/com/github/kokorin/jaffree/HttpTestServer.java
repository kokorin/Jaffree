package com.github.kokorin.jaffree;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

public class HttpTestServer implements Runnable {

    InetSocketAddress socketAddress;
    HttpServer server;

    public HttpTestServer() {
        try {
            socketAddress = new InetSocketAddress(InetAddress.getLoopbackAddress(), 0);
            server = HttpServer.create(socketAddress,0);
            server.setExecutor(null);
            // Define Route(s)
            server.createContext("/UserAgent", new UAHandler());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        server.start();
    }

    public InetSocketAddress getServerAddress() {
        return server.getAddress();
    }

    static class UAHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Get sample video from artifacts
            Path sampleVideo = Artifacts.getSample(URI.create("https://static.videezy.com/system/protected/files/000/007/213/Biking_Girl_Alpha.mov?md5=zJB3WS6tzcdWmKjzHnSTLA&expires=1553233302"));
            String ua = exchange.getRequestHeaders().get("User-Agent").get(0);
            // Test UA header match with `Jaffree`
            if(ua.startsWith("Jaffree")) {
                exchange.sendResponseHeaders(200, sampleVideo.toFile().length());
            } else {
                exchange.sendResponseHeaders(400,sampleVideo.toFile().length());
            }
            // Send a valid video file
            Files.copy(sampleVideo,exchange.getResponseBody());
            exchange.getResponseBody().close();
        }
    }
}
