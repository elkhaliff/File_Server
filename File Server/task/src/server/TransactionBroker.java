package server;

public class TransactionBroker {
    private Command command;

    public void setCommand(Command command) {
        this.command = command;
    }

    public void executeCommand() {
        command.execute();
    }

    public Response getResultCommand() { return command.getResult(); }
}
