package service;

import com.google.gson.Gson;

import container.NettyChannelMap;
import message.BaseMsg;
import io.netty.channel.Channel;

public class CommandService {

	private String doExecute(String command){
		String str = "";
		if(command.startsWith("online")){
			String clientId = command.substring(6).trim();
			if(NettyChannelMap.get(clientId) == null){
				str = "client"+clientId+"is not online";
			}
			else{
				str = "client"+clientId+"online";
			}
			
			return str;
		}else if(command.startsWith("status-all")){
			
		}else if(command.startsWith("status-group")){
			String groupId = command.substring(12).trim();
			
			str = NettyChannelMap.getStatusGroup(groupId);
			
			return str;
		}
		return str;
	}
	public void execute(Channel channel, BaseMsg baseMsg){
		String res = this.doExecute((String)baseMsg.getParams().get("body"));
		baseMsg.putParams("result", res);
		
		channel.writeAndFlush(new Gson().toJson(baseMsg));
	}
	
	
}
