package utils;

import com.googlecode.asyn4j.core.handler.CacheAsynWorkHandler;
import com.googlecode.asyn4j.service.AsynService;
import com.googlecode.asyn4j.service.AsynServiceImpl;

public class AsyncTaskWrapper {
	private AsynService anycService = null;
	
	public static AsyncTaskWrapper wrapper = null;
	
	private AsyncTaskWrapper(){
		this.anycService = AsynServiceImpl.getService(300, 3000L, 100, 100,1000);
        anycService.setWorkQueueFullHandler(new CacheAsynWorkHandler(100));
        anycService.init();
	}
	
	public static AsyncTaskWrapper getInstance(){
		if(wrapper == null){
			wrapper = new AsyncTaskWrapper();
		}
		
		return wrapper;
	}
	
	public AsynService getService(){
		return anycService;
	}
}
