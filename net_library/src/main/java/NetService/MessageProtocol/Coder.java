package NetService.MessageProtocol;


public interface Coder {
    byte[] encode(CommunicationMessage communicationMessage);
    CommunicationMessage decode(byte[] data);
}
