package NetService.MessageProtocol;

public class CodeTypeHeader {
    public static final byte TEXT_MESSAGE = (byte) 0;
    public static final byte CONNECT_REQUEST = (byte) 1;

    public static final byte CONFIRM_MESSAGE = (byte) 2;
    public static final byte SIMPLE_QUESTION_MESSAGE = (byte) 3;
    public static final byte NO_NEED_TO_REPLY_MESSAGE = (byte) 4;

    public static final long VISITOR_ID = Long.MIN_VALUE;

    public static boolean determineLegal(byte type) {
        return (type >= 0 && type <= 4);
    }

    public static boolean belongsToTextMessage(byte type) {
        return (type == 0 || type == 3 || type == 4);
    }


    public static String interpret(byte type){
        switch (type) {
            case TEXT_MESSAGE:
                return  "TEXT MESSAGE";
            case CONNECT_REQUEST:
                return "CONNECT REQUEST";
            case CONFIRM_MESSAGE:
                return "CONFIRM REQUEST";
            case SIMPLE_QUESTION_MESSAGE:
                return "SIMPLE QUESTION MESSAGE";
            case NO_NEED_TO_REPLY_MESSAGE:
                return "NO NEED TO REPLY MESSAGE";
            default:
                return "UNDEFINED";
        }
    }
}
