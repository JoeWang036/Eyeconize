package NetService.MessageProtocol;

public class ForwarderMessage extends CommunicationMessage{
    public byte[] body;
    public byte head;
    public short messageSerial;
    public long receiver;
    public long sender;
    public long sendTime;

    public ForwarderMessage(byte[] body, byte head,short messageSerial, long receiver, long sender, long sendTime) {
        this.body = body;
        this.head = head;
        this.messageSerial = messageSerial;
        this.receiver = receiver;
        this.sender = sender;
        this.sendTime = sendTime;
    }
}

