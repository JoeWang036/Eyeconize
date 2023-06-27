package NetService.MessageProtocol;

import java.nio.ByteBuffer;

public class TextMessageCoder implements Coder {
    private long senderID;
    private long receiverID;

    public TextMessageCoder(long senderID, long receiverID) {
        this.senderID = senderID;
        this.receiverID = receiverID;
    }

    public TextMessageCoder(long senderID) {
        this.senderID = senderID;
    }

    public void setReceiverID(long receiverID) {
        this.receiverID = receiverID;
    }

    public void setSenderID(long senderID) {
        this.senderID = senderID;
    }

    @Override
    public TextMessage decode(byte[] byteArray){
        ByteBuffer bb = ByteBuffer.wrap(byteArray);
        byte type = bb.get();
        if (type == CodeTypeHeader.TEXT_MESSAGE) {
            long senderID = bb.getLong();
            byte[] dataBytes = new byte[bb.remaining()];
            bb.get(dataBytes);
            String data = new String(dataBytes);
            return new TextMessage(data, senderID);
        }else return null;

    }

    /*
    *
    * 文字消息格式：一个字节的消息类型，8个字节的接收者id，8个字节的发送者id
    *
    * */
    @Override
    public byte[] encode(CommunicationMessage communicationMessage) {
        TextMessage textMessage = (TextMessage) communicationMessage;
        byte[] bytes = textMessage.getMessage().getBytes();
        int dataLength = bytes.length;
        ByteBuffer buffer = ByteBuffer.allocate(dataLength + 17);
        buffer.put(CodeTypeHeader.TEXT_MESSAGE);
        buffer.putLong(receiverID);
        buffer.putLong(textMessage.getSenderID());
        System.out.println(bytes.length);
        System.out.println(buffer.capacity());
        buffer.put(bytes);
        return buffer.array();
    }
    public byte[] encode(String message, long receiverID) {
        byte[] bytes = message.getBytes();
        int dataLength = bytes.length;
        ByteBuffer buffer = ByteBuffer.allocate(dataLength + 17);
        buffer.put(CodeTypeHeader.TEXT_MESSAGE);
        buffer.putLong(receiverID);
        buffer.putLong(senderID);
        System.out.println(bytes.length);
        System.out.println(buffer.capacity());
        buffer.put(bytes);
        return buffer.array();
    }


}
