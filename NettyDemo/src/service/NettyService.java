package service;

import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

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
			List<String> msgList = new LinkedList<String>();
			for(String id : groups){
				if(NettyChannelMap.getGroupMembers(id) == null){
					NettyChannelMap.addOneGroup(id);
					
				}
				//msgList.addAll(jdeisDao.getRecentMessage(jdeisDao.getKeyByCliGrp(baseMsg.getClientId(), id)));
				//add channel to group map
				NettyChannelMap.addToGroup(baseMsg.getClientId(), id, (SocketChannel)channelHandlerContext.channel());
			}
			
			//get cached message list from redis and send back to client
			msgList.addAll(jdeisDao.getRecentMessage(jdeisDao.getNotificationKey(baseMsg.getClientId())));
			
			//send msgList to client
			BaseMsg replyForLogin = new BaseMsg();
			replyForLogin.setType(MsgType.LOGIN);
			//replyForLogin.putParams("msgList", new Gson().toJson(msgList));
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
		baseMsg.setDate(baseMsg.getDate());
		
		String str = new Gson().toJson(baseMsg);
		
		//persist baseMsg to database , not be implemented yet
		groupDao.saveMsg(baseMsg);
		
		List<String> list = groupDao.getClientsOfGroup(baseMsg.getGroupId());
		for(String clientId:list){
			Channel ch = NettyChannelMap.getClientFromGroup(clientId, baseMsg.getGroupId());
			if(ch == null || clientId.equals(baseMsg.getClientId())){
				//add msg to redis cache
				//jdeisDao.SaveMessage(jdeisDao.getKeyByCliGrp(clientId, baseMsg.getGroupId()),str );
				continue;
			}
			ch.writeAndFlush(str);
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
		return SUCCESS;
	}
	
}
