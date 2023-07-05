package NetService.MessageProtocol;


public class TextMessage extends CommunicationMessage{
    private String message;
    private short messageSerial;
    private long senderID;

    private long sendTime;
    public boolean isQuestion = false;
    public boolean needToReply = true;
    public TextMessage(String message, short messageSerial, long senderID,long sendTime) {
        this.message = message;
        this.messageSerial = messageSerial;
        this.senderID = senderID;
        this.sendTime = sendTime;
    }

    public short getMessageSerial() {
        return messageSerial;
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
            return (this.senderID == ((TextMessage) other).getSenderID() && this.messageSerial == (((TextMessage) other).messageSerial) && this.sendTime == ((TextMessage) other).getSendTime());
        }
    }
}

