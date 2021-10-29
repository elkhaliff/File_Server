package server;

public class Put implements Command {
    private final DataBase dataBase;
    private final String fileName;
    private final byte[] content;

    public Put(DataBase dataBase, String fileName, byte[] content) {
        this.dataBase = dataBase;
        this.fileName = fileName;
        this.content = content;
    }

    @Override
    public void execute() {
        dataBase.put(fileName, content);
    }

    @Override
    public Response getResult() {
        return dataBase.getOut();
    }
}
