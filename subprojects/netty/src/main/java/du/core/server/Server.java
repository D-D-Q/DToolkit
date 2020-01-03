package du.core.server;

import du.core.handler.DecoderHandler;
import du.core.handler.EncoderHandler;
import du.core.server.handler.ModuleHandler;
import du.core.server.handler.ServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;

public class Server {

	 private int port = 888;
	 
	 public void run() throws Exception {
         EventLoopGroup bossGroup = new NioEventLoopGroup(1);
         EventLoopGroup workerGroup = new NioEventLoopGroup(1);
         try {
             ServerBootstrap b = new ServerBootstrap();
             b.group(bossGroup, workerGroup)
              .channel(NioServerSocketChannel.class)
              .childHandler(new ChannelInitializer<SocketChannel>() {
                  @Override
                  public void initChannel(SocketChannel ch) throws Exception {
                	  System.out.println("handler---------");
                	  // 添加handler处理链
                	  ch.pipeline().addLast(new DecoderHandler());
                	  
                	  ch.pipeline().addLast(new ModuleHandler());
                      ch.pipeline().addLast(new ServerHandler());
                      
                      ch.pipeline().addLast(new EncoderHandler());
                  }
              })
              .option(ChannelOption.SO_BACKLOG, 128)
              .childOption(ChannelOption.SO_KEEPALIVE, true);

             // 绑定端口，开始接收进来的连接
             ChannelFuture f = b.bind(port).sync();

             // 等待服务器  socket 关闭 。
             // 在这个例子中，这不会发生，但你可以优雅地关闭你的服务器。
             f.channel().closeFuture().sync();
             
             if(f.isSuccess()){
            	 System.out.println("启动完成");
             }
             
             
             new ProtobufVarint32LengthFieldPrepender();
             new ProtobufVarint32FrameDecoder();
             new ProtobufDecoder(null);
             new ProtobufEncoder();
         } finally {
             workerGroup.shutdownGracefully();
             bossGroup.shutdownGracefully();
         }
     }

     public static void main(String[] args) throws Exception {
         new Server().run();
     }
}