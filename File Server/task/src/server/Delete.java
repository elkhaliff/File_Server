package server;

public class Delete implements Command {
    private final DataBase dataBase;
    private final String fileName;
    private final int fileId;

    public Delete(DataBase dataBase, String fileName, int fileId) {
        this.dataBase = dataBase;
        this.fileName = fileName;
        this.fileId = fileId;
    }

    @Override
    public void execute() { dataBase.delete(fileName, fileId); }

    @Override
    public Response getResult() {
        return dataBase.getOut();
    }
}

