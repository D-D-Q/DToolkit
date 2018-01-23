package du.core.protobuf3;

import com.google.protobuf.MessageLite;

/**
 * @author ddq
 *	
 * protobuf3 request
 *	
 */
public class PbReq {

	/**
	 * 协议号
	 */
	private int id;
	
	/**
	 * 数据
	 */
	private byte[] data;
	
	public PbReq() {
	}
	
	public PbReq(int id, MessageLite pb) {
		this.id = id;
		setpb(pb);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}
	
	public void setpb(MessageLite pb) {
		this.data = pb.toByteArray();
	}
	
	public void setpb(MessageLite.Builder pb) {
		this.data = pb.build().toByteArray();
	}
	
	@SuppressWarnings("unchecked")
	public <T extends MessageLite> T getpb(T pb) throws Exception {
		
		MessageLite object = pb.getParserForType().parseFrom(data);
		
		return (T)object;
		
	}
}
