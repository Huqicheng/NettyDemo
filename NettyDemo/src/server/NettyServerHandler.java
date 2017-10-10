package server;

import service.CommandService;
import service.NettyService;

import com.google.gson.Gson;

import container.NettyChannelMap;
import message.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.ReferenceCountUtil;

/**
 * @author huqic_000
 *
 */
public class NettyServerHandler extends SimpleChannelInboundHandler {
	NettyService ns = new NettyService();
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    	
    	ns.doLogout(ctx.channel());
    }
    
    protected void messageReceived(ChannelHandlerContext channelHandlerContext, String msg) throws Exception {

    	System.out.println(msg);
    	BaseMsg baseMsg = new Gson().fromJson(msg, BaseMsg.class);
    	if(MsgType.Debug.equals(baseMsg.getType())){
    		new CommandService().execute(channelHandlerContext.channel(), baseMsg);
    		
    		return;
    	}
        if(MsgType.LOGIN.equals(baseMsg.getType())){
            String result = ns.doLogin(baseMsg, channelHandlerContext);
            
        
        }else{
            if(NettyChannelMap.get(baseMsg.getClientId())==null){
                    //the client has not authenticated,notify the client to log in
                    BaseMsg loginMsg=new BaseMsg();
                    loginMsg.setType(MsgType.LOGIN);
                    channelHandlerContext.channel().writeAndFlush(new Gson().toJson(loginMsg));
            }
        }
        switch (baseMsg.getType()){
            case PING:{
                BaseMsg replyPing=new BaseMsg();
                replyPing.setType(MsgType.PING);
                
                NettyChannelMap.get(baseMsg.getClientId()).writeAndFlush(new Gson().toJson(replyPing));
            }break;
            case ASK:{
                //收到客户端的请求
                if("auth".equals(baseMsg.getParams().get("body"))){
                    
                	BaseMsg replyMsg=new BaseMsg();
                	replyMsg.setType(MsgType.REPLY);
                    replyMsg.putParams("body", "reply from server");
                    NettyChannelMap.get(baseMsg.getClientId()).writeAndFlush(new Gson().toJson(replyMsg));
                }
            }break;
            case REPLY:{
                //收到客户端回复
                System.out.println("receive client msg: "+baseMsg.getParams().get("body"));
            }break;
            
            default:break;
        }
        ReferenceCountUtil.release(baseMsg);
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
    		throws Exception {
    	// TODO Auto-generated method stub
    	System.out.println("Error: "+cause.getMessage());
    	NettyChannelMap.remove((SocketChannel)ctx.channel());
    }
	

	@Override
	protected void channelRead0(ChannelHandlerContext arg0, Object arg1)
			throws Exception {
		// TODO Auto-generated method stub
		messageReceived(arg0,(String)arg1);
	}

	
}
