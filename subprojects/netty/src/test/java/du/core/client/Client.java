package du.core.client;

import du.core.client.handler.ClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class Client {

	public void run() {
		 
		int port = 888;

		EventLoopGroup workerGroup = new NioEventLoopGroup(1);

		try {
			Bootstrap b = new Bootstrap(); 
			b.group(workerGroup);
			b.channel(NioSocketChannel.class);
			b.option(ChannelOption.SO_KEEPALIVE, true);
			b.handler(new ChannelInitializer<SocketChannel>() {
				@Override
				public void initChannel(SocketChannel ch) throws Exception {
					
					// 添加handler处理链
					ch.pipeline().addLast(new ClientHandler());
				}
			});

			// 启动客户端
			ChannelFuture f = b.connect("localhost", port).sync();

			// 等待连接关闭
			f.channel().closeFuture().sync();
			
			
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			workerGroup.shutdownGracefully();
		}
	}
	
	public static void main(String[] args) {
		 new Client().run();
	}
}
