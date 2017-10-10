package server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.ssl.SslHandler;

import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLEngine;

import com.google.gson.Gson;

import utils.ServerWriteUtils;
import container.NettyChannelMap;
import message.BaseMsg;
import message.MsgType;



public class NettyServerBootstrap {
    private int port;
    private SocketChannel socketChannel;
    public NettyServerBootstrap(int port) throws InterruptedException {
        this.port = port;
        bind();
    }

    private void bind() throws InterruptedException {
    	
        EventLoopGroup boss=new NioEventLoopGroup();
        EventLoopGroup worker=new NioEventLoopGroup();
        ServerBootstrap bootstrap=new ServerBootstrap();
        bootstrap.group(boss,worker);
        bootstrap.channel(NioServerSocketChannel.class);
        bootstrap.option(ChannelOption.SO_BACKLOG, 128);
        bootstrap.option(ChannelOption.TCP_NODELAY, true);
        bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
//            	SSLEngine sslEngine = SslContextFactory.getServerContext().createSSLEngine();
//                sslEngine.setUseClientMode(false);
//                sslEngine.setWantClientAuth(false);
                
                ChannelPipeline p = socketChannel.pipeline();
                //
                //p.addLast(new SslHandler(sslEngine));
                p.addLast(new StringEncoder());
                p.addLast(new StringDecoder());
                
                p.addLast(new NettyServerHandler());
                
            }
        });
        ChannelFuture f= bootstrap.bind(port).sync();
        if(f.isSuccess()){
            System.out.println("server start---------------");
        }
    }
    public static void main(String []args) throws InterruptedException {
    	
        NettyServerBootstrap bootstrap=ServerWriteUtils.getInstance();
        while (true){
            SocketChannel channel=(SocketChannel)NettyChannelMap.get("001");
            if(channel!=null){
                BaseMsg ping=new BaseMsg();
                ping.setType(MsgType.PING);
                channel.writeAndFlush(new Gson().toJson(ping));
            }
            TimeUnit.SECONDS.sleep(1000);
        }
    }
}
