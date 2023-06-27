package NetService.MessageProtocol;

public class ForwarderMessage extends CommunicationMessage {
    public byte[] body;
    public byte head;
    public long receiver;

    public ForwarderMessage(byte[] body, byte head, long receiver) {
        this.body = body;
        this.head = head;
        this.receiver = receiver;
    }
}
