package message;


public enum  MsgType {
	/*
	 * heart beat
	 * params : None
	 */
    PING,
    /*
     * 
     */
    ASK,
    /*
     * 
     */
    REPLY,
    /*
     * Authentication
     * params: cookie.clientId -- String
     *         clientVersion -- Long(optional)
     */
    LOGIN,
    /*
     * Group Chat
     * params: body -- String         
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
     * params: body -- command
     *         
     */ 
    Debug
    
}
