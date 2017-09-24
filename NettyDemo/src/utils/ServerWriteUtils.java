package utils;
import java.util.Map;

import container.NettyChannelMap;
import server.NettyServerBootstrap;
import message.BaseMsg;
import io.netty.channel.socket.SocketChannel;

public class ServerWriteUtils {
	private static NettyServerBootstrap bootstrap = null;
	
	public static void notifyAllClientsInMap(Map<String,SocketChannel> map,BaseMsg msg){
		for(Map.Entry<String, SocketChannel> entry:map.entrySet()){
			entry.getValue().writeAndFlush(msg);
		}
	}
	
	public static void notifyOneClient(String clientId,BaseMsg msg){
		if(NettyChannelMap.get(clientId) == null) return;
		
		NettyChannelMap.get(clientId).writeAndFlush(msg);
	}
	
	public static NettyServerBootstrap getInstance(){
		if(bootstrap == null){
			startServer();
		}
		
		return bootstrap;
	}
	
	public static void startServer(){
		if(bootstrap != null) return;
		try {
			bootstrap=new NettyServerBootstrap(8080);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}
	}
	
	
}
