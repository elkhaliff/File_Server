package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

public class Session  extends Thread { // implements Runnable {
    private static final DataBase dataBase = new DataBase();

    private static final String SET = "PUT";
    private static final String GET = "GET";
    private static final String DELETE = "DELETE";
    private static final String EXIT = "exit";

    private final Socket socket;
    private final ServerSocket server;
    private AtomicBoolean stopServer;

    public Session(ServerSocket server, Socket socket, AtomicBoolean stopServer) {
        this.server = server;
        this.socket = socket;
        this.stopServer = stopServer;
    }

    @Override
    public void run() {
        try (
                socket;
                DataInputStream input = new DataInputStream(socket.getInputStream());
                DataOutputStream output  = new DataOutputStream(socket.getOutputStream())
        ) {
            TransactionBroker transactionBroker = new TransactionBroker();
            Command command;

            String receivedMsg = input.readUTF();
            String[] commands = receivedMsg.split(" ");

            switch (commands[0]) {
                case SET: {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 2; i < commands.length; i++) {
                        sb.append(commands[i]);
                        sb.append(" ");
                    }
                    command = new Put(dataBase, commands[1], sb.toString().trim());
                    break;
                }
                case GET: {
                    command = new Get(dataBase, commands[1]);
                    break;
                }
                case DELETE: {
                    command = new Delete(dataBase, commands[1]);
                    break;
                }
                case EXIT: {
                    stopServer.set(true);
                    command = new Exit(dataBase);
                    break;
                }
                default: {
                    throw new IllegalStateException("Unexpected type: " + commands[0]);
                }
            }
            transactionBroker.setCommand(command);
            transactionBroker.executeCommand();
            Response msgOut = transactionBroker.getResultCommand();
            output.writeUTF(msgOut.getResponse());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (stopServer.get()) {
                try {
                    server.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.exit(0);
            }
        }
    }
}
