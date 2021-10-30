package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

public class Session  extends Thread { // implements Runnable {

    private static final String PUT = "PUT";
    private static final String GET = "GET";
    private static final String DELETE = "DELETE";
    private static final String EXIT = "exit";

    public static final int ERROR = 404;

    private static final String BY_ID = "BY_ID";

    private final Socket socket;
    private final ServerSocket server;
    private final AtomicBoolean stopServer;
    private final DataBase dataBase;

    public Session(ServerSocket server, Socket socket, DataBase dataBase, AtomicBoolean stopServer) {
        this.server = server;
        this.socket = socket;
        this.stopServer = stopServer;
        this.dataBase = dataBase;
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

            String currAction = input.readUTF();
            String fileName = "";
            int fileId = 0;

            switch (currAction) {
                case PUT: {
                    fileName = input.readUTF();
                    int fileLength = input.readInt();
                    byte[] content = new byte[fileLength];
                    input.readFully(content, 0, content.length);

                    command = new Put(dataBase, fileName, content);
                    break;
                }
                case GET: {
                    String type = input.readUTF();
                    if (type.equals(BY_ID))
                        fileId = input.readInt();
                    else
                        fileName = input.readUTF();

                    command = new Get(dataBase, fileName, fileId);
                    break;
                }
                case DELETE: {
                    String type = input.readUTF();
                    if (type.equals(BY_ID))
                        fileId = input.readInt();
                    else
                        fileName = input.readUTF();
                    command = new Delete(dataBase, fileName, fileId);
                    break;
                }
                case EXIT: {
                    stopServer.set(true);
                    command = new Exit(dataBase);
                    break;
                }
                default: {
                    throw new IllegalStateException("Unexpected action: " + currAction);
                }
            }
            transactionBroker.setCommand(command);
            transactionBroker.executeCommand();

            Response response = transactionBroker.getResultCommand();
            output.writeInt(response.getResponse());

            if (currAction.equals(PUT)) {
                if (response.getId_file() != 0)
                    output.writeInt(response.getId_file());
            }
            if (currAction.equals(GET) || response.getResponse() != ERROR) {
                output.writeInt(response.getContent().length);
                output.write(response.getContent());
            }
            if (stopServer.get()) {
                try {
                    // Serialize dataBase
                    SerializationUtils.serialize(dataBase, dataBase.getDbFilePath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
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
