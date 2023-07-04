package com.codejustice.global;

import NetService.MessageProtocol.TextMessage;

import java.util.ArrayList;
import java.util.List;

public class Global {
    private static Global instance;
    public static long selfID = 123456;
    public static String password = "123456";
    public static short messageSerial = 11;
    public static long receiverID = 2;
    public static boolean SEND_NEW_MESSAGE = false;

    public static List<TextMessage> messagesToSend = new ArrayList<>();



}
