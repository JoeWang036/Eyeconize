package com.codejustice.eyeconizefamily;

import NetService.ConnectionUtils.ChatMessage;

public interface ReplierActivity {
    void sendMessage(String content, long receiverID);
}
