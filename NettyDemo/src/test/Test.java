package test;



import service.RedisService;
import utils.AsyncTaskWrapper;

import com.googlecode.asyn4j.core.callback.AsynCallBack;
import com.googlecode.asyn4j.core.handler.CacheAsynWorkHandler;
import com.googlecode.asyn4j.core.handler.FileAsynServiceHandler;
import com.googlecode.asyn4j.service.AsynService;
import com.googlecode.asyn4j.service.AsynServiceImpl;


public class Test {

	 public static void testExecut2() throws InterruptedException {
	        AsynService anycService = AsyncTaskWrapper.getInstance().getService();
	        RedisService ts = new RedisService();
	        for (long i = 0; i < 10; i+=1) {
	            anycService.addWork(ts, "saveMessage",new Object[] { "testasync" , i+"" },new CallBack());
	        }
	        
	        

	    }
	 
	 public static class CallBack extends AsynCallBack {
		   
	        public void doNotify() {
	        	if(this.methodResult != null)
	        		System.out.println(this.methodResult);
	        }
	    }
	 
	 public static void main(String[] args) throws InterruptedException {
		 testExecut2();
	}
	 
	 
}
