package server;

import container.NettyChannelMap;
import message.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.ReferenceCountUtil;

/**
 * Created by yaozb on 15-4-11.
 */
public class NettyServerHandler extends SimpleChannelInboundHandler<BaseMsg> {
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        NettyChannelMap.remove((SocketChannel)ctx.channel());
    }
    
    protected void messageReceived(ChannelHandlerContext channelHandlerContext, BaseMsg baseMsg) throws Exception {

        if(MsgType.LOGIN.equals(baseMsg.getType())){
            
            NettyChannelMap.add(baseMsg.getClientId(),(SocketChannel)channelHandlerContext.channel());
            System.out.println("client"+baseMsg.getClientId()+" 登录成功");
        
        }else{
            if(NettyChannelMap.get(baseMsg.getClientId())==null){
                    //说明未登录，或者连接断了，服务器向客户端发起登录请求，让客户端重新登录
                    BaseMsg loginMsg=new BaseMsg();
                    loginMsg.setType(MsgType.LOGIN);
                    channelHandlerContext.channel().writeAndFlush(loginMsg);
            }
        }
        switch (baseMsg.getType()){
            case PING:{
                BaseMsg replyPing=new BaseMsg();
                replyPing.setType(MsgType.PING);
                NettyChannelMap.get(baseMsg.getClientId()).writeAndFlush(replyPing);
            }break;
            case ASK:{
                //收到客户端的请求
                if("auth".equals(baseMsg.getParams().get("body"))){
                    
                	BaseMsg replyMsg=new BaseMsg();
                	replyMsg.setType(MsgType.REPLY);
                    replyMsg.putParams("body", "reply from server");
                    NettyChannelMap.get(baseMsg.getClientId()).writeAndFlush(replyMsg);
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
	protected void channelRead0(ChannelHandlerContext arg0, BaseMsg arg1)
			throws Exception {
		// TODO Auto-generated method stub
		messageReceived(arg0,arg1);
	}
}