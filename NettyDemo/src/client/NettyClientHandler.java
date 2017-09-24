package client;

import com.google.gson.Gson;

import message.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;

public class NettyClientHandler extends SimpleChannelInboundHandler {
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            switch (e.state()) {
                case WRITER_IDLE:
                    BaseMsg pingMsg=new BaseMsg();
                    pingMsg.setType(MsgType.PING);
                    ctx.writeAndFlush(pingMsg);
                    System.out.println("send ping to server----------");
                    break;
                default:
                    break;
            }
        }
    }
    protected void messageReceived(ChannelHandlerContext channelHandlerContext, String msg) throws Exception {
        BaseMsg baseMsg = new Gson().fromJson(msg, BaseMsg.class);
    	MsgType msgType=baseMsg.getType();
        switch (msgType){
            case LOGIN:{
                //向服务器发起登录
            	BaseMsg loginMsg=new BaseMsg();
                loginMsg.setType(MsgType.LOGIN);
                loginMsg.putParams("user", "huqicheng");
                loginMsg.putParams("pwd", "huqicheng");
                channelHandlerContext.writeAndFlush(loginMsg);
            }break;
            case PING:{
                System.out.println("receive ping from server----------");
                BaseMsg replyMsg=new BaseMsg();
            	replyMsg.setType(MsgType.REPLY);
                replyMsg.putParams("body", "reply for ping");
                
                channelHandlerContext.writeAndFlush(new Gson().toJson(replyMsg));
            }break;
            case ASK:{
            	BaseMsg replyMsg=new BaseMsg();
            	replyMsg.setType(MsgType.REPLY);
                replyMsg.putParams("body", "reply from client");
                channelHandlerContext.writeAndFlush(replyMsg);
            }break;
            case REPLY:{
                System.out.println("receive client msg: "+baseMsg.getParams().get("body"));
            }
            default:break;
        }
        ReferenceCountUtil.release(msgType);
    }
    
	@Override
	protected void channelRead0(ChannelHandlerContext arg0, Object arg1)
			throws Exception {
		// TODO Auto-generated method stub
		messageReceived(arg0,(String)arg1);
	}
	
}
