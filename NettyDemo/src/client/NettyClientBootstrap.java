package client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLEngine;

import com.google.gson.Gson;

import server.SslContextFactory;
import utils.ClientUtils;
import message.BaseMsg;
import message.Constants;
import message.MsgType;


/**
 * @author huqic_000
 *
 */
public class NettyClientBootstrap {
    private static int port;
    private static String host;
    private static SocketChannel socketChannel;
    private int seconds = 1;
    public NettyClientBootstrap(int port, String host) {
        this.port = port;
        this.host = host;
    }
    
    private class ConnectionListener implements ChannelFutureListener {  
    	  
    	  
        private NettyClientBootstrap client;  
        
      
        public ConnectionListener(NettyClientBootstrap client) {  
            this.client = client;  
        }  
      
      
        @Override  
        public void operationComplete(ChannelFuture future) throws Exception {  
            if (!future.isSuccess()) {  
            	seconds = (seconds>=16)? seconds:seconds*2;
                System.out.println("Reconnection in "+seconds+" seconds");  
                final EventLoop eventLoop = future.channel().eventLoop();  
                eventLoop.schedule(new Runnable() {  
      
      
                    @Override  
                    public void run() {  
                        try {
                        	client.start();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                    }  
                }, seconds, TimeUnit.SECONDS);  
            }  
        }  
      
      
    }  
    public void start() throws InterruptedException {
        EventLoopGroup eventLoopGroup=new NioEventLoopGroup();
        Bootstrap bootstrap=new Bootstrap();
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.SO_KEEPALIVE,true);
        bootstrap.group(eventLoopGroup);
        bootstrap.remoteAddress(host,port);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
//            	SSLEngine engine = SslContextFactory.getClientContext().createSSLEngine();
//            	engine.setUseClientMode(true);
//                engine.setWantClientAuth(false);
//                socketChannel.pipeline().addLast(new SslHandler(engine));
            	
            	

                // On top of the SSL handler, add the text line codec.
            	//
            	//socketChannel.pipeline().addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
                
                socketChannel.pipeline().addLast(new StringEncoder());
                socketChannel.pipeline().addLast(new StringDecoder());
                //socketChannel.pipeline().addLast("length-decoder", new LengthFieldBasedFrameDecoder(369295620, 0, 4, 0, 4));
                socketChannel.pipeline().addLast(new IdleStateHandler(20,10,0));
                socketChannel.pipeline().addLast(new NettyClientHandler());
                
            }
        });
        ChannelFuture future =bootstrap.connect(host,port).addListener(new ConnectionListener(this)).sync();
        if (future.isSuccess()) {
            socketChannel = (SocketChannel)future.channel();
            this.seconds = 1;
            System.out.println("connect server  成功---------");
        }
    }
    
    public static String ReadTest(){   
        //System.out.println("ReadTest, Please Enter Data:");   
        InputStreamReader is = new InputStreamReader(System.in); //new构造InputStreamReader对象   
        BufferedReader br = new BufferedReader(is); //拿构造的方法传到BufferedReader中   
        try{  
          String cmd = br.readLine();   
          //System.out.println("ReadTest Output:" + name); 
          return cmd;
        }   
        catch(IOException e){   
          e.printStackTrace();   
        }   
        return "";
            
      }   
    public static void main(String[]args){
    	System.out.println("start");
    	Constants.setClientId("debug");
        NettyClientBootstrap bootstrap=ClientUtils.getInstance();
        BaseMsg login=new BaseMsg();
        login.setType(MsgType.LOGIN);
        bootstrap.socketChannel.writeAndFlush(new Gson().toJson(login));
        String s = ReadTest().trim();
        while(!s.equals("exit")){
        	BaseMsg askMsg=new BaseMsg();
        	askMsg.setType(MsgType.Debug);
        	askMsg.putParams("body", s);
        	bootstrap.socketChannel.writeAndFlush(new Gson().toJson(askMsg));
        	s = ReadTest().trim();
        }
        
        
        
//        while (true){
//            TimeUnit.SECONDS.sleep(3);
//            BaseMsg askMsg=new BaseMsg();
//            askMsg.setType(MsgType.ASK);
//            askMsg.putParams("body", "auth");
//            bootstrap.socketChannel.writeAndFlush(askMsg);
//        }
    }
}
