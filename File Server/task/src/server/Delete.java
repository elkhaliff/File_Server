package server;

public class Delete implements Command {
    private final DataBase dataBase;
    private final String fileName;

    public Delete(DataBase dataBase, String fileName) {
        this.dataBase = dataBase;
        this.fileName = fileName;
    }

    @Override
    public void execute() { dataBase.delete(fileName); }

    @Override
    public Response getResult() {
        return dataBase.getOut();
    }
}

