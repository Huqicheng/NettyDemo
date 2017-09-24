package utils;

import message.BaseMsg;
import message.Constants;
import client.NettyClientBootstrap;

public class ClientUtils {
	private static NettyClientBootstrap client = null;
	
	public static NettyClientBootstrap getInstance(){
		if(client == null){
			startClient();
		}
		
		return client;
	}

	private static void startClient() {
		// TODO Auto-generated method stub
		Constants.setClientId("001");
        try {
			client=new NettyClientBootstrap(8080,"192.168.11.6");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}
	}
	
	public static void send(String groupId, BaseMsg msg){
		
	}
}
