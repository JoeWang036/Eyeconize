package NetService.ConnectionUtils;


import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ConnectionManager {
    private boolean connected = false;
    public String password;
    private Socket socket;
    private long selfID;
    private static ConnectionManager instance = null;
    private TransmissionManager transmissionManager;
    private ConnectionRequestCoder connectionRequestCoder;
    private TextMessageCoder textMessageCoder;


    private List<ConnectionObserver> observers = new ArrayList<>();


    public boolean isConnected() {
        return connected;
    }


    public void setConnected(boolean connected) {
        synchronized (instance) {
            this.connected = connected;
            if (connected) {
                instance.notify(); // 发送唤醒等待线程的消息
            }
            notifyObservers();
        }
    }

    public static ConnectionManager getInstance(){
        synchronized (ConnectionManager.class) {
            if (instance == null) {
                instance = new ConnectionManager();
            }
            return instance;
        }
    }


    private ConnectionManager(){
        transmissionManager = new TransmissionManager();
        connectionRequestCoder = new ConnectionRequestCoder();
        textMessageCoder = new TextMessageCoder(CodeTypeHeader.VISITOR_ID);
        try {
            socket = new Socket(NetConfig.netAddress, NetConfig.port);
            tryToConnect();

        }catch(IOException e){
            System.out.println("connection failed.");
            setConnected(false);
        }
    }
    private ConnectionManager(long userID, String password){
        new Thread(() -> {
            transmissionManager = new TransmissionManager();
            connectionRequestCoder = new ConnectionRequestCoder();
            textMessageCoder = new TextMessageCoder(userID);
            selfID = userID;
            this.password = password;
            tryToConnect();
        }).start();

    }

    public void registerObserver(ConnectionObserver observer) {
        observers.add(observer);
    }

    public void unregisterObserver(ConnectionObserver observer) {
        observers.remove(observer);
    }

    private void notifyObservers() {
        for (ConnectionObserver observer : observers) {
            observer.onValueChange(connected);
        }
    }

    private void tryToConnect(){
        new Thread(()->{
            synchronized (instance) {
                if (socket != null && !socket.isConnected()) {
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

                    synchronized (instance) {
                        try {
                            if (socket == null) {
                                socket = new Socket(NetConfig.netAddress, NetConfig.port);
                            }
                            transmissionManager.sendMessage(connectionByte, socket);
                            success = true;
                            setConnected(true);
                        } catch (IOException e) {
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
        }).start();

    }

    public void loginConnect(long userID, String password) {
        selfID = userID;
        textMessageCoder.setSenderID(userID);
        this.password = password;
        tryToConnect();
    }

    //TODO: 后续需要修改交互逻辑，需要服务器返回确认信息才发送成功
    public boolean sendTextMessage(String textMessage, long receiverID) {

        byte[] data = textMessageCoder.encode(textMessage, receiverID);
        if (!connected) {
            tryToConnect();
        }
        try {

            transmissionManager.sendMessage(data, socket);
            return true;
        } catch (IOException e) {
            setConnected(false);
            tryToConnect();
            return false;
        } catch (NullPointerException e) {
            System.out.println("Socket is currently null, please try later.");
            return false;
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
                    case CodeTypeHeader.TEXT_MESSAGE:
                        TextMessage textMessage = textMessageCoder.decode(res);
                        System.out.println("Getting message from:" + textMessage.getSenderID());
                        System.out.println("CommunicationMessage content:" + textMessage.getMessage());
                        return textMessage;
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
        instance.tryToConnect();
    }


}
