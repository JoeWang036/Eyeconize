package NetService.MessageProtocol;

public class HeaderMessageCoder implements Coder {

    @Override
    public byte[] encode(CommunicationMessage communicationMessage) {
        return new byte[0];
    }

    @Override
    public CommunicationMessage decode(byte[] data) {
        byte type = data[0];
        byte[] result = new byte[data.length - 1];
        System.arraycopy(data, 1, result, 0, result.length);
        return new HeaderMessage(type, result);
    }
    public byte determineType(byte[] data) {
        return data[0];
    }
}
