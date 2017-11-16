package dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;












import com.google.gson.Gson;

import message.BaseMsg;
import utils.Dbconn;

/**
 * @author huqic_000
 *
 */
public class GroupDao {
	
	private Dbconn db = null;
	public GroupDao(){
		db = new Dbconn();
	}

	public List<String> getGroups(String clientId) throws SQLException{
		List<String> list = new ArrayList<String>();
		ResultSet rs = db.query("select group_id from user_group where user_id="+clientId);
		
		try {
			while(rs.next()){
				list.add(rs.getString(1));
			}
			
			
		} finally{
			if(rs!=null)
			{
				rs.close();
			}
			db.dispose();
		}
		return list;
	}
	
	public boolean addOneGroup(String groupId,String groupName){
		return true;
	}
	
	
	public List<String> getClientsOfGroup(String groupId) throws SQLException{
		List<String> users = new ArrayList<String>();
		
		Connection conn = db.getConnection();
		CallableStatement c = null;
		
		try {
			c = conn.prepareCall("{call get_users_of_group(?)}");
			c.setInt(1, Integer.parseInt(groupId));
			
			ResultSet rs = c.executeQuery();
			while(rs.next()){
				users.add(rs.getString("id"));
			}
			
			
			
		
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			
			c.close();
			
			db.dispose();
		}
		
		
		
		return users;
	}
	
	
	public void saveMsg(BaseMsg baseMsg) throws SQLException{
		Connection conn = db.getConnection();
		CallableStatement c = null;
		
		try {
			c = conn.prepareCall("{call insert_a_group_chat_msg(?,?,?,?)}");
			c.setInt(1, Integer.parseInt(baseMsg.getGroupId()));
			c.setString(2, new Gson().toJson(baseMsg.getParams()));
			c.setTimestamp(3, new java.sql.Timestamp(baseMsg.getDate()));
			c.setInt(4, Integer.parseInt(baseMsg.getClientId()));
			c.executeUpdate();
			
		
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			
			c.close();
			
			db.dispose();
		}
	}
	public static void main(String[] args) {
		GroupDao gd = new GroupDao();
		try {
			System.out.println(gd.getGroups("1").size());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
