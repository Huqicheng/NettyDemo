package dao;

import java.io.Closeable;
import java.util.List;
import redis.clients.jedis.Jedis;



public class JredisDao {

    static int MAX_MESSAGE_QUEUE = 10;
    JedisUtils impl = null;
    
    public JredisDao(){
    	impl = new JedisUtilsImpl();
    }
    public void SaveMessage(String key, String message)
    {
        impl.lpushOneString(key,message);
        impl.ltrim(key, 0, MAX_MESSAGE_QUEUE-1);
    }

    public List<String> getRecentMessage(String key)
    {
        
        List<String> messageList = impl.getStrings(key, MAX_MESSAGE_QUEUE);
        if(messageList == null) return null;
        
        impl.clear(key);
        
        return messageList;
    }

    public void SAMPLE_MessageQueue()
    { 	
    	for (int i = 0; i < 1000; i++)
        {
              String message = "Hello World " + i + " !";
              SaveMessage( "test", message);
              getRecentMessage(  "test");
        }
    }
    
    public static void main(String[] args) {
    	JredisDao test = new JredisDao();
    	test.SAMPLE_MessageQueue();
	}
}