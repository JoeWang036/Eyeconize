package NetService.MessageProtocol;

import java.nio.ByteBuffer;

public class ForwardMessageCoder implements Coder {

    @Override
    public byte[] encode(CommunicationMessage message) {
        ForwarderMessage fm = (ForwarderMessage) message;
        return fm.body;
    }

    @Override
    public ForwarderMessage decode(byte[] data) {
        ByteBuffer bb = ByteBuffer.wrap(data);
        byte type = bb.get();
        long receiverID = bb.getLong();
        long senderID = bb.getLong();
        long sendTime = System.currentTimeMillis();
        byte[] contents = new byte[bb.remaining()];
        bb.get(contents);
        ByteBuffer bodyBuffer = ByteBuffer.allocate(data.length - 8);
        bodyBuffer.put(type);
        bodyBuffer.putLong(senderID);
        bodyBuffer.putLong(sendTime);
        bodyBuffer.put(contents);
        return new ForwarderMessage(bodyBuffer.array(), type, receiverID, senderID);
    }
}
