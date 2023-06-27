package NetService.MessageProtocol;

public class ConnectionRequestMessage extends CommunicationMessage {
    public long userID;
    public String password;

    public ConnectionRequestMessage(long userID, String password) {
        this.userID = userID;
        this.password = password;
    }
}
