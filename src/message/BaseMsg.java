package message;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;



public class BaseMsg  implements Serializable {
    private static final long serialVersionUID = 1L;
    private MsgType type;
    //必须唯一，否者会出现channel调用混乱
    private String clientId;
    private Map<String,Object> params;
    //初始化客户端id
    public BaseMsg() {
        this.clientId = Constants.getClientId();
        params = new HashMap<String,Object>();
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
    
}
