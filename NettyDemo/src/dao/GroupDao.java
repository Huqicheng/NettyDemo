package dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;






import message.BaseMsg;
import utils.Dbconn;

/**
 * @author huqic_000
 *
 */
public class GroupDao {

	public List<String> getGroups(String clientId) throws SQLException{
		List<String> list = new ArrayList<String>();
		Dbconn db = new Dbconn();
		ResultSet rs = db.query("select group_id from group_client where client_id="+clientId);
		
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
		List<String> list = new ArrayList<String>();
		Dbconn db = new Dbconn();
		ResultSet rs = db.query("select client_id from group_client where group_id="+groupId);
		
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
	
	
	public void saveMsg(BaseMsg baseMsg){
		
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
