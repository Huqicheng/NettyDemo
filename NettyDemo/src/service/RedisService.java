package service;

import java.util.List;

import dao.JredisDao;

public class RedisService {

	private JredisDao dao = null;
	
	public RedisService(){
		dao = new JredisDao();
	}
	
	public void saveMessage(String key, String message){
		dao.SaveMessage(key, message);
	}
	
	public List<String> getRecentMessage(String key)
    {
		return dao.getRecentMessage(key);
    }
}