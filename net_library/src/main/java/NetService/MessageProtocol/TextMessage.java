package NetService.MessageProtocol;


public class TextMessage extends CommunicationMessage {
    private String message;
    private long senderID;

    public TextMessage(String message, long senderID) {
        this.message = message;
        this.senderID = senderID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getSenderID() {
        return senderID;
    }

    public void setSenderID(long senderID) {
        this.senderID = senderID;
    }
}
