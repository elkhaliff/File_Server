package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Main {
    private static final String SERVER_ADDRESS = "127.0.0.1";
    private static final int SERVER_PORT = 34512;

    public static void println(String string) { System.out.println(string); }

    public static void main(String[] args) {
        try (
                Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
                DataInputStream input = new DataInputStream(socket.getInputStream());
                DataOutputStream output  = new DataOutputStream(socket.getOutputStream())
        ) {
            println("Client started!");

            Action action = new Action();
            String commandLine = action.getCommand();

            output.writeUTF(commandLine);
            System.out.println("The request was sent.");

            String response = input.readUTF();
            action.setResponse(response);
            println(action.getResponse());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
