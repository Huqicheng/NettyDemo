package message;


/**
 * @author huqic_000
 *
 */
public enum  MsgType {
	/*
	 * heart beat
	 * params : None
	 */
    PING,
    /*
     * Authentication
     * params: cookie.clientId -- String
     *         clientVersion -- Long(optional)
     */
    LOGIN,
    /*
     * Group Chat
     * params: body -- String (content)
     *         msgList -- List         
     */
    ChatMsg,
    /*
     * Reply for ChatMsg
     * params: body -- String (exactly same as ChatMsg)
     *         status -- Integer    
     */
    ReplyForChatMsg,
   
    /*
     * For Debug
     * params: body -- string(command)
     *         
     */ 
    Debug
    
}
