package server;

public class Response {
    private String response;
    private String content;

    public void setResponse(String response) {
        this.response = response;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getResponse() {
        StringBuilder ret = new StringBuilder(response);
        if (content != null) {
            ret.append(" ");
            ret.append(content);
        }
        return ret.toString();
    }
}
