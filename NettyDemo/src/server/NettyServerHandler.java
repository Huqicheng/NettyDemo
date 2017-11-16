package server;

import service.CommandService;
import service.NettyService;
import utils.AsyncTaskWrapper;

import com.google.gson.Gson;
import com.googlecode.asyn4j.service.AsynService;

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
	AsynService anycService = AsyncTaskWrapper.getInstance().getService();
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    	anycService.addWork(ns, "doLogout",new Object[] { ctx.channel()});
    	//ns.doLogout(ctx.channel());
    }
    
    protected void messageReceived(ChannelHandlerContext channelHandlerContext, String msg) throws Exception {

    	System.out.println(msg);
    	BaseMsg baseMsg = new Gson().fromJson(msg, BaseMsg.class);
    	if(MsgType.Debug.equals(baseMsg.getType())){
    		new CommandService().execute(channelHandlerContext.channel(), baseMsg);
    		
    		return;
    	}
    	
        if(MsgType.LOGIN.equals(baseMsg.getType())){
        	anycService.addWork(ns, "doLogin",new Object[] {baseMsg, channelHandlerContext});
            //String result = ns.doLogin(baseMsg, channelHandlerContext);
            
        
        }else{
            if(NettyChannelMap.get(baseMsg.getClientId())==null){
                    //To fix, the client has not authenticated,notify the client to log in, client cannot handle this message now.
                    BaseMsg loginMsg=new BaseMsg();
                    loginMsg.setType(MsgType.LOGIN);
                    channelHandlerContext.channel().writeAndFlush(new Gson().toJson(loginMsg));
            }
        }
        
        
        switch (baseMsg.getType()){
            case PING:{
               
            }break;
            case ChatMsg:{
            	anycService.addWork(ns, "pushGroupMsg",new Object[] {baseMsg, channelHandlerContext.channel()});
            }break;
            case Application:{
            	anycService.addWork(ns, "pushApplication2client",new Object[] {baseMsg, channelHandlerContext.channel()});
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
    	anycService.addWork(ns, "doLogout",new Object[] { ctx.channel()});
    }
	

	@Override
	protected void channelRead0(ChannelHandlerContext arg0, Object arg1)
			throws Exception {
		// TODO Auto-generated method stub
		messageReceived(arg0,(String)arg1);
	}

	
}
