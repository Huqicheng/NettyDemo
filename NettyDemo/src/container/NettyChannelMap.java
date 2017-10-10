package container;

import io.netty.channel.Channel;
import io.netty.channel.socket.SocketChannel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;


public class NettyChannelMap {
    private static Map<String,Map<String,Channel>> map=new ConcurrentHashMap<String, Map<String,Channel>>();
    private static Map<String,Channel> mapForNotification = new ConcurrentHashMap<String,Channel >();
    protected static Logger log = Logger.getLogger(NettyChannelMap.class);
    
    public static void addToGroup(String clientId,String groupId,Channel socketChannel){
    	if(!map.containsKey(groupId)){
    		log.error("addToGroup: group "+groupId+" is not existed! ");
    		return;
    	}
        if(map.get(groupId).containsKey(clientId)){
        	log.error("addToGroup: client "+clientId+" is existed!Don't add duplicate clients!");
    		return;
        }
        
        map.get(groupId).put(clientId, socketChannel);
        log.debug("addToGroup: client "+clientId+" is added to group map for "+groupId);
    }
    
    public static void add(String clientId,Channel socketChannel){
    	if(mapForNotification.containsKey(clientId)){
    		log.error("add: client "+clientId+" is existed!Don't add duplicate clients!");
    		return;
    	}
    	mapForNotification.put(clientId, socketChannel);
    	log.debug("add: client "+clientId+" is added to mapForNotification!");
    }
    
    public static void addOneGroup(String groupId){
    	if(map.containsKey(groupId)){
    		log.error("addOneGroup: group "+groupId+" is existed!Don't add duplicate groups!");
    		return;
    	}
    	map.put(groupId, new ConcurrentHashMap<String,Channel>());
    	log.debug("addOneGroup: group "+groupId+" is added to map!");
    }
    
    public static Map<String,Channel> getGroupMembers(String groupId){
       return map.get(groupId);
    }
    
    public static Channel get(String clientId){
    	return mapForNotification.get(clientId);
    }
    
    public static Channel getClientFromGroup(String clientId, String groupId){
    	if(!map.containsKey(groupId)){
    		log.error("getClientFromGroup: group "+groupId+" is not existed!");
    		return null;
    	}
    	return map.get(groupId).get(groupId);
    }
    
    public static void remove(Channel socketChannel){
    	for (Map.Entry entry:mapForNotification.entrySet()){
    		if(entry.getValue() == socketChannel){
    			mapForNotification.remove(entry.getKey());
    			log.debug("remove: remove client from mapForNotification");
   			 }
    	}
    	for (Map.Entry entry:map.entrySet()){
   		 	Map<String,SocketChannel> mapGroup = (Map<String,SocketChannel>)entry.getValue();
   		 for (Map.Entry entryGroup:mapGroup.entrySet()){
   			 if(entryGroup.getValue() == socketChannel){
   				mapGroup.remove(entryGroup.getKey());
   				log.debug("remove: remove client from map for group "+entry.getKey());
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
