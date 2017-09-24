package server;

import com.google.gson.Gson;

import container.NettyChannelMap;
import message.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.ReferenceCountUtil;

public class NettyServerHandler extends SimpleChannelInboundHandler {
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        NettyChannelMap.remove((SocketChannel)ctx.channel());
    }
    
    protected void messageReceived(ChannelHandlerContext channelHandlerContext, String msg) throws Exception {

    	BaseMsg baseMsg = new Gson().fromJson(msg, BaseMsg.class);
        if(MsgType.LOGIN.equals(baseMsg.getType())){
            NettyChannelMap.add(baseMsg.getClientId(),(SocketChannel)channelHandlerContext.channel());
            System.out.println("client"+baseMsg.getClientId()+" log on to server successfully!");
        
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
    	System.out.println(cause.getMessage());
    }
	

	@Override
	protected void channelRead0(ChannelHandlerContext arg0, Object arg1)
			throws Exception {
		// TODO Auto-generated method stub
		messageReceived(arg0,(String)arg1);
	}

	
}
