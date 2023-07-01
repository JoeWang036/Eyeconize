package com.codejustice.entities;

import android.icu.text.SimpleDateFormat;

import androidx.annotation.Nullable;

import java.util.Date;
import java.util.Locale;

import NetService.MessageProtocol.TextMessage;

public class ChatMessage extends ChatMessageAbstract{
    public String messageContent;
    public long senderID;
    public long timestamp;  // 添加时间属性

    public ChatMessage(String messageContent, long senderID, long timestamp) {
        this.messageContent = messageContent;
        this.senderID = senderID;
        this.timestamp = timestamp;
    }

    public ChatMessage(TextMessage textMessage) {
        this.messageContent = textMessage.getMessage();
        this.senderID = textMessage.getSenderID();
        this.timestamp = textMessage.getSendTime();
    }
    // 添加 getter 和 setter 方法（根据需要）

    // 示例方法：获取格式化的时间字符串
    public String getFormattedTimestamp() {
        // 根据需要使用 SimpleDateFormat 等类来格式化时间
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return format.format(new Date(timestamp));
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof ChatMessage)) {
            return false;
        }else {
            return (messageContent.equals(((ChatMessage) obj).messageContent) && senderID == ((ChatMessage) obj).senderID && timestamp == ((ChatMessage) obj).timestamp);
        }
    }
}
