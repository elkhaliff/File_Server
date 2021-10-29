package server;

interface Command {

    void execute();

    Response getResult();
}