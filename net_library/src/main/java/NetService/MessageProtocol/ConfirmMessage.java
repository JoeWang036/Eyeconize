package NetService.MessageProtocol;

import java.nio.ByteBuffer;

public class ConfirmMessage extends CommunicationMessage{


    public short messageSerial;
    public long senderID;
    public long receiverID;
    public long sendTime;

    public ConfirmMessage(short messageSerial, long senderID, long receiverID, long sendTime) {
        this.messageSerial = messageSerial;
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.sendTime = sendTime;
    }

    public static byte[] encode(ConfirmMessage confirmMessage) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(27);
        byteBuffer.put(CodeTypeHeader.CONFIRM_MESSAGE);
        byteBuffer.putShort(confirmMessage.messageSerial);
        byteBuffer.putLong(confirmMessage.senderID);
        byteBuffer.putLong(confirmMessage.receiverID);
        byteBuffer.putLong(confirmMessage.sendTime);
        return byteBuffer.array();
    }
    public static byte[] encode(short messageSerial, long senderID, long receiverID, long sendTime) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(27);
        byteBuffer.put(CodeTypeHeader.CONFIRM_MESSAGE);
        byteBuffer.putShort(messageSerial);
        byteBuffer.putLong(senderID);
        byteBuffer.putLong(receiverID);
        byteBuffer.putLong(sendTime);
        return byteBuffer.array();
    }

    public static ConfirmMessage decode(byte[] data) {
        ByteBuffer bb = ByteBuffer.wrap(data);
        byte type = bb.get();
        if (type != CodeTypeHeader.CONFIRM_MESSAGE) {
            return null;
        }
        ConfirmMessage res;
        short messageSerial = bb.getShort();
        long senderID = bb.getLong();
        long receiverID = bb.getLong();
        long sendTime = bb.getLong();
        res = new ConfirmMessage(messageSerial, senderID, receiverID, sendTime);
        return res;
    }


}
