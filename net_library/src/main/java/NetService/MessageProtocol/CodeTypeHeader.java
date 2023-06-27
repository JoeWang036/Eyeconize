package NetService.MessageProtocol;

public class CodeTypeHeader {
    public static final byte TEXT_MESSAGE = (byte) 0;
    public static final byte CONNECT_REQUEST = (byte) 1;


    public static final long VISITOR_ID = Long.MIN_VALUE;


    public static String interpret(byte type){
        switch (type) {
            case TEXT_MESSAGE:
                return  "TEXT MESSAGE";
            case CONNECT_REQUEST:
                return "CONNECT REQUEST";
            default:
                return "UNDEFINED";
        }
    }
}
