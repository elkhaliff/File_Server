package server;

public class Exit implements Command {
    private final DataBase dataBase;

    public Exit(DataBase dataBase) {
        this.dataBase = dataBase;
    }

    @Override
    public void execute() {
        dataBase.exit();
    }

    @Override
    public Response getResult() {
        return dataBase.getOut();
    }
}

