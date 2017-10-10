package dao;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;


import org.apache.commons.pool.impl.GenericObjectPool;  
/**
 * @author huqic_000
 *
 */
public class JedisUtilsImpl extends JedisUtils{

	  
    private static String ADDR = "127.0.0.1";  
     
    private static int PORT = 6379;  
    
    private static int MAX_ACTIVE = 1024;  
     
    private static int MAX_IDLE = 200;  
     
    private static int MAX_WAIT = 10000;  
          
     
    private static boolean TEST_ON_BORROW = false;  
	@Override
	public int getDBIndex() {
		// TODO Auto-generated method stub
		return 1;
	}
	
	@Override
	public String getKeyByCliGrp(String clientId, String groupId){
		return "client"+clientId+"@group"+groupId;
	}
	public JedisUtilsImpl(){
		ConfigWrapper configWrapper = new ConfigWrapper();
		configWrapper.setMaxActive(MAX_ACTIVE);
		configWrapper.setMaxIdle(MAX_IDLE);
		configWrapper.setMaxWait(MAX_WAIT);
		configWrapper.setTestOnBorrow(TEST_ON_BORROW);
        jedisPool = new JedisPool(configWrapper.getConfig(), ADDR, PORT);  
	}
	
	

}
