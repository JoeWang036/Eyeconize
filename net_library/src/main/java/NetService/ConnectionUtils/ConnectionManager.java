package NetService.ConnectionUtils;


import android.os.Message;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import NetService.MessageProtocol.CodeTypeHeader;
import NetService.MessageProtocol.CommunicationMessage;
import NetService.MessageProtocol.ConfirmMessage;
import NetService.MessageProtocol.ConnectionRequestCoder;
import NetService.MessageProtocol.ConnectionRequestMessage;
import NetService.MessageProtocol.HeaderMessageCoder;
import NetService.MessageProtocol.NetConfig;
import NetService.MessageProtocol.TextMessage;
import NetService.MessageProtocol.TextMessageCoder;
import NetService.MessageProtocol.TransmissionManager;

public class ConnectionManager {
    private boolean connected = false;
    public String password;
    private Socket socket;
    private long selfID;
    private short sendSerial;
    private static ConnectionManager instance;
    private TransmissionManager transmissionManager;
    private ConnectionRequestCoder connectionRequestCoder;
    private TextMessageCoder textMessageCoder;


    private final List<ConnectionObserver> connectionObservers = new ArrayList<>();
    private final List<MessageObserver> messageObservers = new ArrayList<>();
    private PageObserver pageObserver;



    public boolean isConnected() {
        return connected;
    }


    public long getSelfID() {
        return selfID;
    }
    public void setSelfID(){
        this.selfID = selfID;}

    public void setConnected(boolean connected) {
        synchronized (instance) {
            this.connected = connected;
            if (connected) {
                instance.notify(); // 发送唤醒等待线程的消息
            }
            notifyConnectionObservers();
        }
    }

    public static ConnectionManager getInstance(){
        synchronized (ConnectionManager.class) {
            if (instance == null) {
                instance = new ConnectionManager(CodeTypeHeader.VISITOR_ID, "123");
            }
            return instance;
        }
    }


//    private ConnectionManager(){
//        transmissionManager = new TransmissionManager();
//        connectionRequestCoder = new ConnectionRequestCoder();
//        textMessageCoder = new TextMessageCoder(CodeTypeHeader.VISITOR_ID);
//        selfID = 654;
//        try {
//            socket = new Socket(NetConfig.netAddress, NetConfig.port);
//            tryToConnect();
//
//        }catch(IOException e){
//            System.out.println("connection failed.");
//            setConnected(false);
//        }
//    }
    private ConnectionManager(long userID, String password){
        transmissionManager = new TransmissionManager();
        connectionRequestCoder = new ConnectionRequestCoder();
        textMessageCoder = new TextMessageCoder(userID);
        selfID = userID;
        sendSerial = 1;
        this.password = password;

    }

    public void registerConnectionObserver(ConnectionObserver observer) {
        connectionObservers.add(observer);
    }

    public void unregisterConnectionObserver(ConnectionObserver observer) {
        connectionObservers.remove(observer);
    }

    public void registerMessageObserver(MessageObserver observer) {
        if (!messageObservers.contains(observer)) {
            messageObservers.add(observer);
        }

    }

    public void unregisterMessageObserver(MessageObserver observer) {
        messageObservers.remove(observer);
    }

    public void registerPageObserver(PageObserver observer) {
        synchronized (this) {
            pageObserver = observer;
        }
    }
    public void unregisterPageObserver(PageObserver observer) {
        synchronized (this) {
            if (pageObserver == observer) {
                pageObserver= null;
            }
        }
    }

    public void notifyPageObservers(ChatMessage chatMessage) {

            pageObserver.newMessageAlert(chatMessage);
    }
    private void notifyConnectionObservers() {
        for (ConnectionObserver observer : connectionObservers) {
            observer.onValueChange(connected);
        }
    }


    public void notifyMessageObserverRefresh(ConfirmMessage confirmMessage) {
        for (MessageObserver observer :
                messageObservers) {
            observer.updateSentStatus(confirmMessage);
        }
    }
    private void notifyMessageObserversFail(long receiverID, long sendTime, short messageSerial ){
        for (MessageObserver observer :
                messageObservers) {
            observer.messageFail(receiverID, sendTime, messageSerial);
        }
    }
    private void tryToConnect(){
        synchronized (instance) {
            if (socket != null && socket.isConnected()) {
                try {
                    socket.close();
                    try {
                        socket = new Socket(NetConfig.netAddress, NetConfig.port);
                    }catch(IOException e){
                        System.out.println("connection failed.");
                        setConnected(false);
                    }

                } catch (IOException e) {
                    System.out.println("socket closure failed, but it's okay lol");
                }
            }

            ConnectionRequestMessage connectionRequestMessage;
            if (password != null) {
                connectionRequestMessage= new ConnectionRequestMessage(selfID, password);
            }else{
                connectionRequestMessage = new ConnectionRequestMessage(CodeTypeHeader.VISITOR_ID, "000000");

            }
            if (connectionRequestCoder == null) {
                System.out.println("null object detected: connectionRequestCoder.");
                connectionRequestCoder = new ConnectionRequestCoder();
            }
            byte[] connectionByte = connectionRequestCoder.encode(connectionRequestMessage);
            boolean success = false;
            for (int i = 0; i < 3 && !success; i++) {
                try {
                    if (socket == null) {
                        socket = new Socket(NetConfig.netAddress, NetConfig.port);
                    }
                    transmissionManager.sendMessage(connectionByte, socket);
                    success = true;
                    setConnected(true);
        }
                catch (IOException e) {
                    System.out.println(NetConfig.netAddress+"    "+NetConfig.port);
                    System.out.println("Failed to connect. Trying to reconnect to the server.");
                    setConnected(false);
                    try {
                        if (socket != null) {
                            socket.close();
                        }
                        socket = new Socket(NetConfig.netAddress, NetConfig.port);
                        transmissionManager.sendMessage(connectionByte, socket);
                        success = true;
                        setConnected(true);
                    } catch (IOException exception) {
                        System.out.println("failed to connect to server, probably due to a closed port.");
                        setConnected(false);
                    }
                }

            }
}

    }


    public void loginConnect(long userID, String password) {
        selfID = userID;
        textMessageCoder.setSenderID(userID);
        this.password = password;
        tryToConnect();
    }

    //TODO: 后续需要修改交互逻辑，需要服务器返回确认信息才发送成功
    public short sendTextMessage(String textMessage, long receiverID, short sendSerial, long sendTime) {

        byte[] data = textMessageCoder.encode(textMessage,sendSerial, receiverID);
        textMessageCoder.setSenderID(selfID);
        if (!connected) {
            System.out.println("Connection manager 176: trying to connect.");
            tryToConnect();
        }
        try {
            transmissionManager.sendMessage(data, socket);
            return sendSerial;
        } catch (IOException e) {
            setConnected(false);
            tryToConnect();

            notifyMessageObserversFail(receiverID, sendTime, sendSerial);
            return -1;
        } catch (NullPointerException e) {
            System.out.println("Socket is currently null, please try later.");
            notifyMessageObserversFail(receiverID, sendTime, sendSerial);
            return -1;
        }
    }
    public short sendQuestionMessage(String textMessage, long receiverID, short sendSerial) {

        byte[] data = textMessageCoder.encode(CodeTypeHeader.SIMPLE_QUESTION_MESSAGE, textMessage,sendSerial, receiverID);
        if (!connected) {
            System.out.println("Connection manager 228: trying to connect.");
            tryToConnect();
        }
        try {

            transmissionManager.sendMessage(data, socket);

            return sendSerial;
        } catch (IOException e) {
            setConnected(false);
            tryToConnect();

            return -1;
        } catch (NullPointerException e) {
            System.out.println("Socket is currently null, please try later.");
            return -1;
        }
    }
    public short sendNoNeedToReplyMessage(String textMessage, long receiverID, short sendSerial) {

        byte[] data = textMessageCoder.encode(CodeTypeHeader.NO_NEED_TO_REPLY_MESSAGE, textMessage,sendSerial, receiverID);
        if (!connected) {
            System.out.println("Connection manager 228: trying to connect.");
            tryToConnect();
        }
        try {

            transmissionManager.sendMessage(data, socket);

            return sendSerial;
        } catch (IOException e) {
            setConnected(false);
            tryToConnect();

            return -1;
        } catch (NullPointerException e) {
            System.out.println("Socket is currently null, please try later.");
            return -1;
        }
    }
    public CommunicationMessage readMessage(){
        HeaderMessageCoder headerMessageCoder = new HeaderMessageCoder();
        byte[] res;
        try {
            System.out.println("Connection manager: trying to read message. invoking transmission manager.");
            if ((res = transmissionManager.readMessage(socket)).length > 0) {
                System.out.println("Received message.");

                byte type = headerMessageCoder.determineType(res);
                switch (type) {
                    case CodeTypeHeader.SIMPLE_QUESTION_MESSAGE:
                    case CodeTypeHeader.NO_NEED_TO_REPLY_MESSAGE:
                    case CodeTypeHeader.TEXT_MESSAGE:
                        TextMessage textMessage = textMessageCoder.decode(res);
                        System.out.println("Getting message from:" + textMessage.getSenderID());
                        System.out.println("CommunicationMessage content:" + textMessage.getMessage());
                        return textMessage;
                    case CodeTypeHeader.CONFIRM_MESSAGE:
                        ConfirmMessage confirmMessage = ConfirmMessage.decode(res);
                        System.out.println("confirmation message detected.");
                        return confirmMessage;

                    default:
                        return null;
                }
            }
        } catch (IOException e) {
            System.out.println("Read failed.");
        }
        return null;
    }


    public static void instantiate(long userID, String password) {
        new Thread(()->{
            synchronized (ConnectionManager.class) {
                if (instance == null) {
                    instance = new ConnectionManager(userID, password);
                }
                else{
                    instance.selfID = userID;
                    instance.textMessageCoder.setSenderID(userID);
                    instance.password = password;
                }
            }
            System.out.println("connection manager 229:instantiating.....");
            System.out.println("id:"+ instance.selfID);
            instance.tryToConnect();
        }).start();
    }


}
