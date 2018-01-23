package du.core.protobuf3;

/**
 * @author ddq
 * 
 * protobuf3 response
 *
 */
public class PbRes {

	/**
	 * 状态
	 */
	private int status;
	
	/**
	 * 数据
	 */
	private byte[] data;

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}
	
}
