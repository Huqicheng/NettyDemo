package test;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;

import service.RedisService;
import utils.AsyncTaskWrapper;

import com.googlecode.asyn4j.core.callback.AsynCallBack;
import com.googlecode.asyn4j.core.handler.CacheAsynWorkHandler;
import com.googlecode.asyn4j.core.handler.FileAsynServiceHandler;
import com.googlecode.asyn4j.service.AsynService;
import com.googlecode.asyn4j.service.AsynServiceImpl;


public class Test {

	// test asynchronous connector
	 public static void testExecut2() throws InterruptedException {
	        AsynService anycService = AsyncTaskWrapper.getInstance().getService();
	        RedisService ts = new RedisService();
	        for (long i = 0; i < 10; i+=1) {
	            anycService.addWork(ts, "saveMessage",new Object[] { "testasync" , i+"" },new CallBack());
	            //anycService.addWork(ts, "getRecentMessage",new Object[] { "testasync"},new CallBack());
	        }
	        
	      Thread.sleep(3000);
	      anycService.addWork(ts, "getRecentMessage",new Object[] { "testasync"},new CallBack());
	      
	      Thread.sleep(3000);
	      anycService.addWork(ts, "getRecentMessage",new Object[] { "testasync"},new CallBack());
	        

	    }
	 
	 public static class CallBack extends AsynCallBack {
		   
	        public void doNotify() {
	        	if(this.methodResult != null)
	        		System.out.println(this.methodResult);
	        }
	    }
	 
	 public static void main(String[] args) throws InterruptedException {
		 //System.out.println("ReadTest, Please Enter Data:");   
	        InputStreamReader is = new InputStreamReader(System.in); //new构造InputStreamReader对象   
	        BufferedReader br = new BufferedReader(is); //拿构造的方法传到BufferedReader中   
	        try{  
	          String cmd = br.readLine();   
	          System.out.println("ReadTest Output:" + cmd); 
	          
	        }   
	        catch(IOException e){   
	          e.printStackTrace();   
	        }   
	}
	 
	 
}
