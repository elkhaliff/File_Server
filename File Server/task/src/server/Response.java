package server;

public class Response {
    private int response;
    private int id_file;
    private byte[] content;

    public void setResponse(int response) {
        this.response = response;
    }

    public void setId_file(int id_file) { this.id_file = id_file; }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public byte[] getContent() { return content; }

    public int getId_file() { return id_file; }

    public int getResponse() { return response; }
}
