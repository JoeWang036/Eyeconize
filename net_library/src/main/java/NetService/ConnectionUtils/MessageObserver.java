package NetService.ConnectionUtils;

import NetService.MessageProtocol.ConfirmMessage;
import NetService.MessageProtocol.TextMessage;

public interface MessageObserver {

    void messageFail(long receiverID, long sendTime, short messageSerial);

    void updateSentStatus(ConfirmMessage confirmMessage);

}
