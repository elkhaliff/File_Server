package server;

public class Get implements Command {
    private final DataBase dataBase;
    private final String fileName;
    private final int fileId;

    public Get(DataBase dataBase, String fileName, int fileId) {
        this.dataBase = dataBase;
        this.fileName = fileName;
        this.fileId = fileId;
    }

    @Override
    public void execute() {
        dataBase.get(fileName, fileId);
    }

    @Override
    public Response getResult() {
        return dataBase.getOut();
    }
}

