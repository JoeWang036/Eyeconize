package com.codejustice.entities;

import android.icu.text.SimpleDateFormat;

import java.util.Date;
import java.util.Locale;

public class ChatMessage {
    public String messageContent;
    public long senderID;
    public long timestamp;  // 添加时间属性

    public ChatMessage(String messageContent, long senderID, long timestamp) {
        this.messageContent = messageContent;
        this.senderID = senderID;
        this.timestamp = timestamp;
    }

    // 添加 getter 和 setter 方法（根据需要）

    // 示例方法：获取格式化的时间字符串
    public String getFormattedTimestamp() {
        // 根据需要使用 SimpleDateFormat 等类来格式化时间
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return format.format(new Date(timestamp));
    }
}
