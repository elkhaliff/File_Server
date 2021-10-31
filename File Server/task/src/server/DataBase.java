package server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.TreeMap;

public class DataBase implements Serializable {
    private static final long serialVersionUID = 86L;

    public static final int SUCCESSFUL = 200;
    public static final int ERROR = 404;
    public static final int ALREADY_EXISTS = 403;

    private static final String fileDB = "db.data";
    private static final String SP = File.separator;
    private static final String dbFilePath =
            System.getProperty("user.dir") + SP +
//            "File Server" + SP + "task" + SP +
            "src" + SP + "server" + SP + "data";

    private final TreeMap<Integer, String> db;
    private transient Response out;

    public DataBase() {
        db = new TreeMap<>();
    }

    public String getDbFilePath() {
        return dbFilePath + SP + fileDB;
    }

    private void initTran() {
        out = new Response();
        out.setResponse(SUCCESSFUL);
    }

    public Response getOut() {
        return out;
    }

    private int getKeyByValue(String fileName) {
        for (var entry : db.entrySet()) {
            if (entry.getValue().equals(fileName)) return entry.getKey();
        }
        return 0;
    }

    private void saveFile(String fileName, byte[] content) {
        File file = new File(dbFilePath + SP + fileName);
        if (content.length > 0) {
            try (FileOutputStream outputStream = new FileOutputStream(file)) {
                outputStream.write(content);
                int nextKey = (db.size() > 0) ? db.lastKey() + 1 : 1;
                db.put(nextKey, fileName);
                out.setId_file(nextKey);
                out.setResponse(SUCCESSFUL);
            } catch (Exception e) {
                out.setResponse(ERROR);
            }
        }
    }

    private void loadFile(String fileName, int fileId) {
        if (fileName.equals("") || fileId != 0) fileName = db.get(fileId);
        if (fileId == 0) fileId = getKeyByValue(fileName);
        if (fileId != 0) {
            File file = new File(dbFilePath + SP + fileName);
            if (file.length() > 0) {
                try (FileInputStream inputStream = new FileInputStream(file)) {
                    out.setId_file(fileId);
                    out.setContent(inputStream.readAllBytes());
                    out.setResponse(SUCCESSFUL);
                } catch (Exception e) {
                    out.setContent(null);
                    out.setResponse(ERROR);
                }
            }
        } else
            out.setResponse(ERROR);
    }

    private void deleteFile(String fileName, int fileId) {
        if (fileName.equals("") || fileId != 0) fileName = db.get(fileId);
        if (fileId == 0) fileId = getKeyByValue(fileName);
        if (fileId != 0) {
            File file = new File(dbFilePath + SP + fileName);
            if (file.delete()) {
                db.remove(fileId);
                out.setResponse(SUCCESSFUL);
            } else
                out.setResponse(ERROR);
        } else
            out.setResponse(ERROR);
    }

    public void put(String fileName, byte[] content) {
        initTran();
        if (db.containsValue(fileName)) {
            out.setResponse(ALREADY_EXISTS);
        } else {
            saveFile(fileName, content);
        }
    }

    public void get(String fileName, int fileId) {
        initTran();
        if (!db.containsValue(fileName) && !db.containsKey(fileId)) {
            out.setResponse(ERROR);
        } else {
            loadFile(fileName, fileId);
        }
    }

    public void delete(String fileName, int fileId) {
        initTran();
        if (!db.containsValue(fileName) && !db.containsKey(fileId)) {
            out.setResponse(ERROR);
        } else {
            deleteFile(fileName, fileId);
        }
    }

    public void exit() {
        initTran();
    }
}
