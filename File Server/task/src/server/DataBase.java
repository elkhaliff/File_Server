package server;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class DataBase {
    public static final String SUCCESSFUL = "200";
    public static final String ERROR = "404";
    public static final String ALREADY_EXISTS = "403";

    private static final String fileDB = "db.txt";
    private static final String dbFilePath = System.getProperty("user.dir") + File.separator +
//            "File Server" + File.separator + "task" + File.separator +
            "src" + File.separator + "server" + File.separator + "data";

    private TreeMap<Integer, String> db;
    private Response out;

    private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock writeLock = readWriteLock.writeLock();
    private final Lock readLock = readWriteLock.readLock();

    public DataBase() {
        db = new TreeMap<>();
        // deserelisation
    }

    private void initTran() {
        out = new Response();
        out.setResponse(SUCCESSFUL);
    }

    public Response getOut() {
        return out;
    }

    private int getKeyByValue(String fileName) {
        for (var entry : db.entrySet()) { // var - Map.Entry<String, String>
            if (entry.getValue().equals(fileName)) return entry.getKey();
        }
        return 0;
    }

    private void saveFile(String fileName, String content) {
        try (FileWriter writer = new FileWriter(dbFilePath + File.separator + fileName)) {
            writer.write(content);
            int nextKey = (db.size() > 0) ? db.lastKey() + 1 : 1;
            db.put(nextKey, fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String loadFile(String fileName) {
        String content = "";
        File file = new File(dbFilePath + File.separator + fileName);
        if (file.length() > 0) {
            try (Scanner scanFile = new Scanner(file)) {
                content = scanFile.nextLine();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return content;
    }

    private void deleteFile(String fileName) {
        int key = getKeyByValue(fileName);
        if (key != 0) {
            File file = new File(dbFilePath + File.separator + fileName);
            file.delete();
            db.remove(key);
        }
    }

    public void put(String fileName, String content) {
        initTran();
        if (db.containsValue(fileName)) {
            out.setResponse(ALREADY_EXISTS);
        } else {
            saveFile(fileName, content);
            out.setResponse(SUCCESSFUL);
        }
    }

    public void get(String fileName) {
        initTran();
        if (!db.containsValue(fileName)) {
            out.setResponse(ERROR);
        } else {
            out.setContent(loadFile(fileName));
            out.setResponse(SUCCESSFUL);
        }
    }

    public void delete(String fileName) {
        initTran();
        if (!db.containsValue(fileName)) {
            out.setResponse(ERROR);
        } else {
            deleteFile(fileName);
            out.setResponse(SUCCESSFUL);
        }
    }

    public void exit() {
        initTran();
    }
}
