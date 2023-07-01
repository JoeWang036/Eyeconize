package NetService.MessageProtocol;

public class ForwarderMessage extends CommunicationMessage{
    public byte[] body;
    public byte head;
    public long receiver;
    public long sender;

    public ForwarderMessage(byte[] body, byte head, long receiver, long sender) {
        this.body = body;
        this.head = head;
        this.receiver = receiver;
        this.sender = sender;
    }
}

