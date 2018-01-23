package du.core.protobuf3;

import org.junit.Test;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageLite;

import du.proto.TestProto;
import du.proto.TestProto.Msg;
import du.proto.TestProto.Msg.Builder;

public class EncodeDecodeTest {
	
	private byte[] data = null;

	
	@Test
	public void test() throws Exception {
		
		long s = System.nanoTime();
		
		Builder builder = TestProto.Msg.newBuilder();
		
		
		System.out.println((System.nanoTime()-s)/1000000);
		
		builder.addIntList(1);
		builder.addIntList(2);
		
		builder.putIntIntMap(1, 1);
		builder.putIntIntMap(2, 2);
		
		builder.putStringIntMap("a", 1);
		builder.putStringIntMap("b", 2);
		
		builder.addStringList("a");
		builder.addStringList("b");
		
		builder.putStringIntMap("a", 1);
		builder.putStringIntMap("b", 2);
		
		builder.putStringStringMap("a", "a");
		builder.putStringStringMap("b", "b");
		
		Msg msg = builder.build();
		
//		builder.clear();
		
		System.out.println((System.nanoTime()-s)/1000000);
		
		byte[] byteArray = msg.toByteArray(); // 编码
		
		data = byteArray;
		
		s = System.nanoTime();
		
		Msg msg2 = getpb(TestProto.Msg.getDefaultInstance());
		
		System.out.println((System.nanoTime()-s)/1000000);
		
		
	}
	
	@SuppressWarnings("unchecked")
	public <T extends MessageLite> T getpb(T pb) throws Exception {
		
		MessageLite object = pb.getParserForType().parseFrom(data);
		
		return (T)object;
		
	}
}
