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
     * msgList
     */
    ReplyForLogin,
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
    Debug,
    /*
     * For Notification
     */
    Norification,
    /*
     * For Application
     * params: target_client_id
     * 		   group_name
     *         group_owner_name
     *         
     */
    Application
    
}
