package com.whu.eyerecongize.connect;

import androidx.annotation.Nullable;

import NetService.MessageProtocol.TextMessage;

public class ChatMessage{
    public String messageContent;
    public long senderID;
    public short messageSerial;
    public byte sentStatus;
    public static final byte SENDING = 0;
    public static final byte SENT = 1;
    public static final byte FAILED = 2;
    public long timestamp;  // 添加时间属性

    public ChatMessage(String messageContent, long senderID, long timestamp, short messageSerial, byte sentStatus) {
        this.messageContent = messageContent;
        this.senderID = senderID;
        this.timestamp = timestamp;
        this.messageSerial = messageSerial;
        this.sentStatus = sentStatus;
    }

    public ChatMessage(TextMessage textMessage) {
        this.messageContent = textMessage.getMessage();
        this.senderID = textMessage.getSenderID();
        this.timestamp = textMessage.getSendTime();
        this.messageSerial = textMessage.getMessageSerial();
        this.sentStatus = SENT;
    }
    // 添加 getter 和 setter 方法（根据需要）



    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof ChatMessage)) {
            return false;
        }else {
            return (senderID == ((ChatMessage) obj).senderID && timestamp == ((ChatMessage) obj).timestamp && messageSerial == ((ChatMessage) obj).messageSerial);
        }
    }
}
