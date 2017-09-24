package container;

import io.netty.channel.Channel;
import io.netty.channel.socket.SocketChannel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NettyChannelMap {
    private static Map<String,Map<String,SocketChannel>> map=new ConcurrentHashMap<String, Map<String,SocketChannel>>();
    private static Map<String,SocketChannel> mapForNotification = new ConcurrentHashMap<String,SocketChannel >();
    
    public static void addToGroup(String clientId,String groupId,SocketChannel socketChannel){
        map.get(groupId).put(clientId,socketChannel);
    }
    
    public static void add(String clientId,SocketChannel socketChannel){
    	mapForNotification.put(clientId, socketChannel);
    }
    
    
    public static Map<String,SocketChannel> getGroupMembers(String groupId){
       return map.get(groupId);
    }
    
    public static Channel get(String clientId){
    	return mapForNotification.get(clientId);
    }
    
    public static void remove(SocketChannel socketChannel){
    	for (Map.Entry entry:map.entrySet()){
   		 	Map<String,SocketChannel> mapGroup = (Map<String,SocketChannel>)entry.getValue();
   		 for (Map.Entry entryGroup:mapGroup.entrySet()){
   			 if(entryGroup.getValue() == socketChannel){
   				mapGroup.remove(entryGroup.getKey());
   			 }
   		 }
   	 	}
    }
    
    public static void remove(String clientId){
    	
    	 for (Map.Entry entry:map.entrySet()){
    		 Map<String,SocketChannel> mapGroup = (Map<String,SocketChannel>)entry.getValue();
    		 if (mapGroup.containsKey(clientId)){
    			 mapGroup.remove(clientId);
    		 }
    	 }
    }

}
