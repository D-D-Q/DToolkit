package du.core.server.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ServerHandler extends ChannelInboundHandlerAdapter {

	@Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        
		System.out.println("---msg");
		// 默默地丢弃收到的数据
//        ((ByteBuf) msg).release();
		System.out.println(((ByteBuf) msg).toString(io.netty.util.CharsetUtil.US_ASCII));
        
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
       
        cause.printStackTrace();
        
        // 当出现异常就关闭连接
        ctx.close();
    }
}
