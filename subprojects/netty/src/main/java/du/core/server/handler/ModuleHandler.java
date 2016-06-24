package du.core.server.handler;

import du.core.module.msg.Hello;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ModuleHandler extends SimpleChannelInboundHandler<Hello> {

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Hello msg) throws Exception {

		System.out.println("---Module");
	}

}
