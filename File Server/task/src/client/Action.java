package client;

import java.io.*;
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

    private static final String BY_NAME = "BY_NAME";
    private static final String BY_ID = "BY_ID";

    private static final String ACTION = "Enter action (1 - get a file, 2 - save a file, 3 - delete a file):";
    private static final String ACTION_TYPE = "Do you want to get the file by name or by id (1 - name, 2 - id):";
    private static final String GET_FILE_NAME = "Enter filename:";
    private static final String GET_FILE_ID = "Enter id:";
    private static final String GET_FILE_NAME_ON_SERVER = "Enter name of the file to be saved on server:";
    private static final String SET_FILE_NAME = "The file was downloaded! Specify a name for it:";


    public static final int SUCCESSFUL = 200;
    public static final int ERROR = 404;
    public static final int ALREADY_EXISTS = 403;

    private static final String SP = File.separator;
    private static final String dbFilePath = System.getProperty("user.dir") + SP +
//            "File Server" + SP + "task" + SP +
            "src" + SP + "client" + SP + "data";

    DataInputStream input;
    DataOutputStream output;

    private String response;
    private String currAction;

    public Action(DataInputStream input, DataOutputStream output) {
        this.input = input;
        this.output = output;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public void run() {
        String receivedMsg = getString(ACTION);
        switch (receivedMsg) {
            case ACT_PUT: {
                String fileName = getFileName();
                String fileNameOnServer = getFileNameOnServer();
                fileNameOnServer = (fileNameOnServer == null) ? fileName : fileNameOnServer;
                putFile(fileNameOnServer, loadFile(fileName));
                break;
            }
            case ACT_GET: {
                editFile(GET);
                break;
            }
            case ACT_DELETE: {
                editFile(DELETE);
                break;
            }
            case ACT_EXIT: {
                exit();
                break;
            }
            default:
                throw new IllegalStateException("Unexpected action: " + receivedMsg);
        }
    }

    private String getFileNameOnServer() {
        return getString(GET_FILE_NAME_ON_SERVER);
    }

    private void exit() {
        currAction = EXIT;
        try {
            output.writeChars(currAction);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void putFile(String fileName, byte[] content) {
        currAction = PUT;
        try {
            output.writeChars(currAction);
            output.writeInt(content.length);
            output.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void editFile(String action) {
        currAction = action;
        String actionType = getActionType();
        try {
            output.writeChars(currAction);
            output.writeChars(actionType);
            if (actionType.equals(BY_NAME)) {
                output.writeChars(getFileName());
            } else
                output.writeInt(getFileId());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void print(String string) { System.out.print(string); }

    private String getString(String string) {
        Scanner scanner = new Scanner(System.in);
        print(string + " ");
        return scanner.nextLine();
    }

    private int getInt(String string) {
        Scanner scanner = new Scanner(System.in);
        print(string + " ");
        return scanner.nextInt();
    }

    private String getFileName() {
        return getString(GET_FILE_NAME);
    }

    private String getActionType() {
        return (getInt(ACTION_TYPE) == 1) ? BY_NAME : BY_ID;
    }

    private int getFileId() {
        return getInt(GET_FILE_ID);
    }

    public String getResponse() {
        String ret = "";
        if (currAction.equals(EXIT)) return ret;

        Scanner inpScan = new Scanner(input);
        int response = inpScan.nextInt();

        switch (response) {
            case ERROR: ret = "The response says that the file was not found!"; break;
            case ALREADY_EXISTS: ret = "The response says that creating the file was forbidden!"; break;
            case SUCCESSFUL: {
                String secs = "The response says that file ";
                if (currAction.equals(PUT)) ret = secs + "is saved! ID = " + inpScan.nextInt();
                else if (currAction.equals(GET)) {
                    int fileLength = inpScan.nextInt();
                    byte[] content = new byte[fileLength];
                    try {
                        input.readFully(content, 0, content.length);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    saveFile(getString(SET_FILE_NAME), content);
                    ret = "File saved on the hard drive!";
                }
                else if (currAction.equals(DELETE)) ret = secs + "was deleted successfully!";
                break;
            }
        }
        return ret;
    }

    private void saveFile(String fileName, byte[] content) {
        File file = new File(dbFilePath + SP + fileName);
        if (content.length > 0) {
            try (FileOutputStream outputStream = new FileOutputStream(file)) {
                outputStream.write(content);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private byte[] loadFile(String fileName) {
        File file = new File(dbFilePath + SP + fileName);
        byte[] content = null;
        if (file.length() > 0) {
            try (FileInputStream inputStream = new FileInputStream(file)) {
                content = inputStream.readAllBytes();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return content;
    }

}
