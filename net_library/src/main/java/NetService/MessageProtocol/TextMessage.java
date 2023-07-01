package NetService.MessageProtocol;


public class TextMessage extends CommunicationMessage{
    private String message;
    private long senderID;
    private long sendTime;
    public TextMessage(String message, long senderID, long sendTime) {
        this.message = message;
        this.senderID = senderID;
        this.sendTime = sendTime;
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

    public long getSendTime() {
        return sendTime;
    }

    public void setSenderID(long senderID) {
        this.senderID = senderID;
    }

    @Override
    public boolean equals(Object other){
        if (!(other instanceof TextMessage)) {
            return false;
        }else {
            return (this.message.equals(((TextMessage) other).message) && this.senderID == ((TextMessage) other).getSenderID() && this.sendTime == ((TextMessage) other).getSendTime());
        }
    }
}

