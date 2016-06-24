package du.core.handler;

import du.core.module.msg.Hello;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class EncoderHandler extends MessageToByteEncoder<Hello> {

	@Override
	protected void encode(ChannelHandlerContext ctx, Hello msg, ByteBuf out) throws Exception {
		
		System.out.println("---encode");
	}

}
