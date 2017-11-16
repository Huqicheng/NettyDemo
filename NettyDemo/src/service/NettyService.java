package service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import utils.AsyncTaskWrapper;

import com.google.gson.Gson;
import com.googlecode.asyn4j.service.AsynService;

import message.BaseMsg;
import message.MsgType;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.SocketChannel;
import container.NettyChannelMap;
import dao.JedisUtils;
import dao.JredisDao;
import dao.GroupDao;;
/**
 * @author Huqicheng
 *
 */
public class NettyService {
	protected Logger log = Logger.getLogger(NettyService.class);
	
	public static final String SUCCESS = "success";
	public static final String FAILED = "fail";
	private JredisDao jdeisDao = null;
	private GroupDao groupDao = null;
	
	public NettyService() {
		jdeisDao = new JredisDao();
		groupDao = new GroupDao();
	}
	
	
	/** 
	* @Title: doLogin 
	* @Description: login
	* @param @param baseMsg
	* @param @param channelHandlerContext
	* @param @return
	* @param @throws SQLException 
	* @return String  
	* @throws 
	*/
	public String doLogin(BaseMsg baseMsg,ChannelHandlerContext channelHandlerContext) throws SQLException{
		//for debug client
		if(baseMsg.getClientId().equals("debug")){
			NettyChannelMap.add(baseMsg.getClientId(),(SocketChannel)channelHandlerContext.channel());
			return SUCCESS;
		}
		
		if(channelHandlerContext == null){
			log.error("doLogin: channelHandlerContext is null");
			return FAILED;
		}
		
		if(NettyChannelMap.get(baseMsg.getClientId()) == null){
    		//add channel for client to notification map 
			NettyChannelMap.add(baseMsg.getClientId(),(SocketChannel)channelHandlerContext.channel());
			//add channel for client to group maps
			List<String> groups = groupDao.getGroups(baseMsg.getClientId());
			Map<String,Object> msgList = new HashMap<String, Object>();
			for(String id : groups){
				if(NettyChannelMap.getGroupMembers(id) == null){
					NettyChannelMap.addOneGroup(id);
					
				}
				//add channel to group map
				NettyChannelMap.addToGroup(baseMsg.getClientId(), id, (SocketChannel)channelHandlerContext.channel());
				
				List<String> res = jdeisDao.getRecentMessage(jdeisDao.getKeyByCliGrp(baseMsg.getClientId(), id));
				log.debug("size of msg list for group "+id+":"+res.size());
				if(res == null || res.size() != 1){
					continue;
				}
				msgList.put(id,res.get(0));
				
			}
			
			List<String> res = jdeisDao.getRecentMessage(jdeisDao.getNotificationKey(baseMsg.getClientId()));
			log.debug("size of notification list "+res.size());
			if(res != null && res.size() == 1)
				msgList.put("notification",res.get(0));
			res = jdeisDao.getRecentMessage(jdeisDao.getApplicationKey(baseMsg.getClientId()));
			log.debug("size of application list "+res.size());
			if(res != null && res.size() == 1)
				msgList.put("application",res.get(0));
			
			
			//send msgList to client
			BaseMsg replyForLogin = new BaseMsg();
			replyForLogin.setType(MsgType.ReplyForLogin);
			replyForLogin.putParams("msgList", new Gson().toJson(msgList));
			channelHandlerContext.channel().writeAndFlush(new Gson().toJson(replyForLogin));
			
			
            log.info("client"+baseMsg.getClientId()+" log on to server successfully!");
    	}else{
    		log.info("client"+baseMsg.getClientId()+" has already logged on!");
    	}
		return SUCCESS;
	}
	
	
	/** 
	* @Title: doLogout 
	* @Description: log out
	* @param @param channel
	* @param @return    
	* @return String 
	* @throws 
	*/
	public String doLogout(Channel channel){
		channel.close();
		NettyChannelMap.remove(channel);
		return SUCCESS;
	}
	
	/** 
	* @Title: pushGroupMsg 
	* @Description: 
	* @param @param baseMsg
	* @param @param channel
	* @param @return
	* @param @throws SQLException    
	* @return String 
	* @throws 
	*/
	public String pushGroupMsg(BaseMsg baseMsg, Channel channel) throws SQLException{
		if(baseMsg.getGroupId() == null){
			log.error("groupId is not existed");
			return FAILED;
		}
		baseMsg.setDate(new Date().getTime());
		
		String str = new Gson().toJson(baseMsg);
		
		//persist
		groupDao.saveMsg(baseMsg);
		
		
		List<String> clients = groupDao.getClientsOfGroup(baseMsg.getGroupId());
		for(String client:clients){
			if(client.equals(baseMsg.getClientId())) continue;
			Object obj = NettyChannelMap.get(client);
			if(obj == null){
				this.jdeisDao.SaveMessage(jdeisDao.getKeyByCliGrp(client, baseMsg.getGroupId()),
						String.valueOf(baseMsg.getDate()));
				continue;
			}
			
			Channel ch = (Channel)obj;
			if(ch != null){
				ch.writeAndFlush(str);
			}
			
		}
		
		if(channel == null){
			return FAILED;
		}
		
		//resend reply to sender of this message(new msg contains timestamp and status)
		BaseMsg reply = baseMsg;
		reply.setType(MsgType.ReplyForChatMsg);
		reply.setParams("status", "success");
		channel.writeAndFlush(new Gson().toJson(reply));
		
		return SUCCESS;
	}
	
	public String pushNotification2client(BaseMsg baseMsg, Channel channel){
		if(!baseMsg.getParams().containsKey("target_client_id")){
			return FAILED;
		}
		String client_id = (String)baseMsg.getParams().get("target_client_id");
		
		Channel ch = NettyChannelMap.get(client_id);
		
		if(ch == null){
			this.jdeisDao.SaveMessage(jdeisDao.getNotificationKey(client_id),
					String.valueOf(baseMsg.getDate()));
			return FAILED;
		}
		
		ch.writeAndFlush(new Gson().toJson(baseMsg));
		
		return SUCCESS;
		
	}
	
	public String pushApplication2client(BaseMsg baseMsg, Channel channel){
		if(!baseMsg.getParams().containsKey("target_client_id")){
			return FAILED;
		}
		String client_id = (String)baseMsg.getParams().get("target_client_id");
		
		Channel ch = NettyChannelMap.get(client_id);
		
		if(ch == null){
			this.jdeisDao.SaveMessage(jdeisDao.getApplicationKey(client_id),
					String.valueOf(baseMsg.getDate()));
			return FAILED;
		}
		
		ch.writeAndFlush(new Gson().toJson(baseMsg));
		
		return SUCCESS;
	}
	
}
