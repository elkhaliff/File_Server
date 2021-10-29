package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class Main {
    private static final int PORT = 34512;

    public static void println(String string) { System.out.println(string); }

    public static void main(String[] args) {
        int poolSize = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(poolSize);
        AtomicBoolean stopServer = new AtomicBoolean(false);

        try {
            ServerSocket server = new ServerSocket(PORT);
            println("Server started!");
            while (!stopServer.get()) {
                Socket socket = server.accept();
                executor.submit(new Session(server, socket, stopServer));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        executor.shutdown();
    }
}
