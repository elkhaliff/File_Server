package server;

import java.io.File;
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
        DataBase dataBase = new DataBase();
        ExecutorService executor = Executors.newFixedThreadPool(poolSize);
        AtomicBoolean stopServer = new AtomicBoolean(false);

        // Deserialize dataBase
        try {
            File file = new File(dataBase.getDbFilePath());
            if (file.length() > 0) {
                dataBase = (DataBase) SerializationUtils.deserialize(dataBase.getDbFilePath());
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
            ServerSocket server = new ServerSocket(PORT);
            println("Server started!");
            while (!stopServer.get()) {
                Socket socket = server.accept();
                executor.submit(new Session(server, socket, dataBase, stopServer));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }
    }
}
