package utils;

import message.BaseMsg;
import message.Constants;
import client.NettyClientBootstrap;

/**
 * @author huqic_000
 *
 */
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
		
        try {
			client=new NettyClientBootstrap(8080,"localhost");
			client.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}
	}
	
	public static void send(String groupId, BaseMsg msg){
		
	}
}
