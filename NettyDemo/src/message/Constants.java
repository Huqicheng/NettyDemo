package message;

public class Constants {
    private static String clientId;

    public static String getClientId() {
        return clientId;
    }

    public static void setClientId(String clientId) {
        Constants.clientId = clientId;
    }
    
    //keywords
    public static final String pwd = "pwd";
    public static final String user = "user";
    public static final String body = "body";
}
