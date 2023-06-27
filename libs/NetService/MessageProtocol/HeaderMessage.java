package NetService.MessageProtocol;

public class HeaderMessage extends CommunicationMessage {
    public byte type;
    public byte[] content;

    public HeaderMessage(byte type, byte[] content) {
        this.type = type;
        this.content = content;
    }
}
