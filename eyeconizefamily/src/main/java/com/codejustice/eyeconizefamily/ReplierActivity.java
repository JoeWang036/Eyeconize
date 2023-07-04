package com.codejustice.eyeconizefamily;

import NetService.ConnectionUtils.ChatMessage;

public interface ReplierActivity {
    public void sendMessage(String content, long receiverID);
}
