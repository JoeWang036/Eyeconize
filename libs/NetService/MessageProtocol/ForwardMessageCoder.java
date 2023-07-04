package NetService.MessageProtocol;

import java.nio.ByteBuffer;

public class ForwardMessageCoder implements Coder {

    @Override
    public byte[] encode(CommunicationMessage communicationMessage) {
        ForwarderMessage fm = (ForwarderMessage) communicationMessage;
        return fm.body;
    }

    @Override
    public ForwarderMessage decode(byte[] data) {
        ByteBuffer bb = ByteBuffer.wrap(data);
        byte type = bb.get();
        long receiverID = bb.getLong();
        byte[] contents = new byte[bb.remaining()];
        bb.get(contents);
        ByteBuffer bodyBuffer = ByteBuffer.allocate(data.length - 8);
        bodyBuffer.put(type);
        bodyBuffer.put(contents);
        return new ForwarderMessage(bodyBuffer.array(), type, receiverID);
    }
}
