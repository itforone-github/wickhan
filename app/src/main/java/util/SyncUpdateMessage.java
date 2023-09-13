package util;

public final class SyncUpdateMessage {
    
    // message codes
    public static final int NO_CONNECTION = 1;
    public static final int SYNC_MSG = 200;
    public static final int SYNC_BORAD = 300;
    public static final int SYNC_Calendar= 400;
    
    private int messageCode;
    private String what;
    
    public SyncUpdateMessage(int messageCode, String what){
        this.messageCode = messageCode;
        this.what = what;
    }
    
    public int getMessageCode(){
        return this.messageCode;
    }
    
    public String getWhat(){
        return this.what;
    }
}
