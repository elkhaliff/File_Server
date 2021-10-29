package server;

public class Get implements Command {
    private final DataBase dataBase;
    private final String key;

    public Get(DataBase dataBase, String key) {
        this.dataBase = dataBase;
        this.key = key;
    }

    @Override
    public void execute() {
        dataBase.get(key);
    }

    @Override
    public Response getResult() {
        return dataBase.getOut();
    }
}

