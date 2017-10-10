package message;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;



/**
 * @author huqic_000
 *
 */
public class BaseMsg  implements Serializable {
    private static final long serialVersionUID = 1L;
    private MsgType type;
    private String clientId;
    private String groupId;
    private long timeStamp;
    private Map<String,Object> params;
    
    public BaseMsg() {
        this.clientId = Constants.getClientId();
        params = new HashMap<String,Object>();
    }
    
    

	public long getTimeStamp() {
		return timeStamp;
	}



	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}



	public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public MsgType getType() {
        return type;
    }

    public void setType(MsgType type) {
        this.type = type;
    }
    
    public Map<String,Object> getParams(){
    	return params;
    }
    
    public void putParams(String key, Object val){
    	params.put(key, val);
    }

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
    
}
