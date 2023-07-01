package NetService.ConnectionUtils;

import NetService.MessageProtocol.TextMessage;

public interface MessageObserver {
    void getMessage(TextMessage textMessage);

    void messageFail(TextMessage textMessage);

}
