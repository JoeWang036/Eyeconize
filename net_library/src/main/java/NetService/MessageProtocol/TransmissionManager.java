package NetService.MessageProtocol;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

public class TransmissionManager {

    public void sendMessage(byte[] message, Socket socket) throws IOException{

        OutputStream os = socket.getOutputStream();
        sendMessage(message, os);
    }
    public void sendMessage(byte[] message, OutputStream outputStream) throws IOException{
        ByteBuffer bb = ByteBuffer.allocate(message.length + 5);
        bb.put((byte) 156);
        bb.putInt(message.length);
        bb.put(message);
        outputStream.write(bb.array());
        outputStream.flush();
    }

    public byte[] readMessage(Socket socket) throws  IOException {
        InputStream inputStream = socket.getInputStream();
        return readMessage(inputStream);
    }

    public byte[] readMessage(InputStream inputStream) throws IOException {
        int len = 0;
        int count = 0;
        int totalLength = 0;
        byte[] buffer = new byte[1024];
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        boolean shouldEnd = false;
        int i = 1;
        int offset = 5;
        int read = 0;
        while ((len = inputStream.read(buffer,0, offset)) != -1 && !shouldEnd) {
            if (i == 1) {
                if (buffer[0] != (byte) 156){
                    return new byte[0];
                }
                else{

                    totalLength = ((buffer[1] & 0xFF) << 24) |
                            ((buffer[2] & 0xFF) << 16) |
                            ((buffer[3] & 0xFF) << 8) |
                            (buffer[4] & 0xFF);
                    totalLength += 5;//通过消息头取出消息总长度
                    read += offset; //通过offset更新已读数量，通过已读数量更新下一次的offset
                    int remaining = totalLength -read;
                    if (remaining > 1024) {
                        offset = 1024;
                    }else{
                        offset = remaining;
                    }
                    if (remaining == 0) {
                        shouldEnd = true;
                    }

                }
            }
            else{
                read += offset; //通过offset更新已读数量，通过已读数量更新下一次的offset
                int remaining = totalLength -read;
                if (remaining > 1024) {
                    offset = 1024;
                }else{
                    offset = remaining;
                }
                if (remaining == 0) {
                    shouldEnd = true;
                }
                baos.write(buffer, 0, len);
            }
            count += len;
            if (shouldEnd) {
                return baos.toByteArray();
            }
            i++;
        }
        return new byte[0];
    }
}
