package client;

import java.util.Scanner;

public class Action {

    private static final String PUT = "PUT";
    private static final String GET = "GET";
    private static final String DELETE = "DELETE";
    private static final String EXIT = "exit";
    private static final String ACT_PUT = "2";
    private static final String ACT_GET = "1";
    private static final String ACT_DELETE = "3";
    private static final String ACT_EXIT = "exit";

    private static final String ACTION = "Enter action (1 - get a file, 2 - create a file, 3 - delete a file):";
    private static final String GET_FILE_NAME = "Enter filename: ";
    private static final String GET_FILE_CONTENT = "Enter file content: ";

    public static final String SUCCESSFUL = "200";
    public static final String ERROR = "404";
    public static final String ALREADY_EXISTS = "403";

    private String response;
    private String currAction;

    public void setResponse(String response) {
        this.response = response;
    }

    public String getCommand() {
        String command = "";
        String receivedMsg = getString(ACTION);
        switch (receivedMsg) {
            case ACT_PUT: {
                currAction = PUT;
                command = PUT + " " + getFileName() + " " + getContent();
                break;
            }
            case ACT_GET: {
                currAction = GET;
                command = GET + " " + getFileName();
                break;
            }
            case ACT_DELETE: {
                currAction = DELETE;
                command = DELETE + " " + getFileName();
                break;
            }
            case ACT_EXIT: {
                currAction = EXIT;
                command = EXIT;
                break;
            }
            default:
                throw new IllegalStateException("Unexpected action: " + receivedMsg);
        }
        return command;
    }

    public static void print(String string) { System.out.print(string); }

    public static String getString(String string) {
        Scanner scanner = new Scanner(System.in);
        print(string + " ");
        return scanner.nextLine();
    }

    private String getFileName() {
        return getString(GET_FILE_NAME);
    }

    public String getContent() {
        return getString(GET_FILE_CONTENT);
    }

    public String getResponse() {
        String ret = "";
        StringBuilder sb = new StringBuilder();
        String[] responses = response.split(" ");
        if (currAction.equals(EXIT)) return ret;
        else
            for (int i = 1; i < responses.length; i++) {
                sb.append(responses[i]);
                sb.append(" ");
            }
        switch (responses[0]) {
            case ERROR: ret = "The response says that the file was not found!"; break;
            case ALREADY_EXISTS: ret = "The response says that creating the file was forbidden!"; break;
            case SUCCESSFUL: {
                String secs = "The response says that file was ";
                if (currAction.equals(PUT)) ret = secs + "created!";
                else if (currAction.equals(GET)) ret = "The content of the file is: " + sb.toString();
                else if (currAction.equals(DELETE)) ret = secs + "deleted!";
                break;
            }
        }
        return ret;
    }

}
