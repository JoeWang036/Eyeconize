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


    /*
    * 解码格式：一字节消息类型，2字节消息序号，8字节发送者，8字节接收者，8字节发送时间
    *
    * */
    @Override
    public TextMessage decode(byte[] byteArray){
        if (byteArray.length < 19) {
            return null;
        }
        ByteBuffer bb = ByteBuffer.wrap(byteArray);
        byte type = bb.get();
        if (type == CodeTypeHeader.TEXT_MESSAGE || type == CodeTypeHeader.SIMPLE_QUESTION_MESSAGE) {
            short messageSerial = bb.getShort();
            long senderID = bb.getLong();
            long sendTime = bb.getLong();
            byte[] dataBytes = new byte[bb.remaining()];
            bb.get(dataBytes);
            String data = new String(dataBytes);
            TextMessage textMessage = new TextMessage(data, messageSerial, senderID, sendTime);
            textMessage.isQuestion = (type==CodeTypeHeader.SIMPLE_QUESTION_MESSAGE);
            return textMessage;
        } else {
            return null;
        }

    }

    /*
    *
    * 文字消息格式：发送：一个字节的消息类型，2个字节消息序号，8个字节的接收者id，8个字节的发送者id
    * 接收：一个字节消息类型，2个字节消息序号，8个字节发送者id，8个字节发送时间
    *
    * */
    @Override
    public byte[] encode(CommunicationMessage communicationMessage) {
        TextMessage textMessage = (TextMessage) communicationMessage;
        byte[] bytes = textMessage.getMessage().getBytes();
        int dataLength = bytes.length;
        ByteBuffer buffer = ByteBuffer.allocate(dataLength + 19);
        buffer.put(CodeTypeHeader.TEXT_MESSAGE);
        buffer.putShort(textMessage.getMessageSerial());
        buffer.putLong(receiverID);
        buffer.putLong(textMessage.getSenderID());
        System.out.println(bytes.length);
        buffer.put(bytes);
        return buffer.array();
    }
    public byte[] encode(String message, short messageSerial, long receiverID) {
        byte[] bytes = message.getBytes();
        int dataLength = bytes.length;
        ByteBuffer buffer = ByteBuffer.allocate(dataLength + 19);
        buffer.put(CodeTypeHeader.TEXT_MESSAGE);
        buffer.putShort(messageSerial);
        buffer.putLong(receiverID);
        buffer.putLong(senderID);
        System.out.println(bytes.length);
        buffer.put(bytes);
        return buffer.array();
    }
    public byte[] encode(byte type, String message, short messageSerial, long receiverID) {
        byte[] bytes = message.getBytes();
        int dataLength = bytes.length;
        ByteBuffer buffer = ByteBuffer.allocate(dataLength + 19);
        buffer.put(type);
        buffer.putShort(messageSerial);
        buffer.putLong(receiverID);
        buffer.putLong(senderID);
        System.out.println(bytes.length);
        buffer.put(bytes);
        return buffer.array();
    }


}
