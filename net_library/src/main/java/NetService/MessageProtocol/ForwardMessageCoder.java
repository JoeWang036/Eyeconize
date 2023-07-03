package NetService.MessageProtocol;

import java.nio.ByteBuffer;

public class ForwardMessageCoder implements Coder {

    @Override
    public byte[] encode(CommunicationMessage message) {
        ForwarderMessage fm = (ForwarderMessage) message;
        return fm.body;
    }

    /*
    * 解码消息：TextMessage发送格式
    * 编码消息格式：textMessage接收格式
    * */
    @Override
    public ForwarderMessage decode(byte[] data) {

        ByteBuffer bb = ByteBuffer.wrap(data);
        byte type = bb.get();                       //1
        short messageSerial = bb.getShort();        //2
        long receiverID = bb.getLong();             //8
        long senderID = bb.getLong();               //8     19字节消息头
        long sendTime = System.currentTimeMillis();
        byte[] contents = new byte[bb.remaining()];
        bb.get(contents);
        ByteBuffer bodyBuffer = ByteBuffer.allocate(data.length);
        bodyBuffer.put(type);               //1
        bodyBuffer.putShort(messageSerial); //2
        bodyBuffer.putLong(senderID);       //8
        bodyBuffer.putLong(sendTime);       //8         19字节消息头
        bodyBuffer.put(contents);
        return new ForwarderMessage(bodyBuffer.array(), type, messageSerial, receiverID, senderID, sendTime);
    }
}
