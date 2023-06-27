package NetService.MessageProtocol;

import java.nio.ByteBuffer;

public class ConnectionRequestCoder implements Coder{

    /*
    * 连接请求消息编码方式：1字节类型+8字节ID+密码
    *
    *
    *
    * */

    //TODO: 有空需要完善异常的抛出：若某个message转型失败需要抛出异常


    private Coder headerCoder = new HeaderMessageCoder();
    @Override
    public byte[] encode(CommunicationMessage communicationMessage) {
        if (!(communicationMessage instanceof ConnectionRequestMessage)) {
            return new byte[0];
        }
        ConnectionRequestMessage crm = (ConnectionRequestMessage) communicationMessage;
        byte[] pswBytes = crm.password.getBytes();
        ByteBuffer bb = ByteBuffer.allocate(pswBytes.length + 9);
        bb.put(CodeTypeHeader.CONNECT_REQUEST);
        bb.putLong(crm.userID);
        bb.put(pswBytes);
        return bb.array();
    }

    @Override
    public CommunicationMessage decode(byte[] data) {
        HeaderMessage hm = (HeaderMessage) headerCoder.decode(data);
        if (hm.type != CodeTypeHeader.CONNECT_REQUEST){
            return null;
        }
        else{
            ByteBuffer bb = ByteBuffer.allocate(hm.content.length);
            bb.put(hm.content);
            bb.position(0);
            long senderID = bb.getLong();
            byte[] dataBytes = new byte[bb.remaining()];
            bb.get(dataBytes);
            String password = new String(dataBytes);
            return new ConnectionRequestMessage(senderID, password);

        }

    }
}
