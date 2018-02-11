package du.core.handler;

import java.io.IOException;

import org.junit.Test;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.handler.codec.CorruptedFrameException;

public class Varint32Test {
	
	/**
	 * 测试多个Varint32类型字段当协议数据报头
	 * 
	 * @throws Exception
	 */
	public void manyTest() throws Exception {
		
		int no = 1000;
		String str = "这是测试字符串";
		
		
		byte[] strBytes = str.getBytes("utf-8");
		
		ByteBuf out = UnpooledByteBufAllocator.DEFAULT.heapBuffer();
		
		int nol = computeRawVarint32Size(no);
		int bodyl = strBytes.length;
		int herdl = computeRawVarint32Size(bodyl);
		
		out.ensureWritable(nol + herdl + bodyl);
		
		writeRawVarint32(out, no);
		writeRawVarint32(out, bodyl);
		out.writeBytes(strBytes);
		
		//-----------------------------------------------------------------------------------------------
		
		ByteBuf in = out;
		
		in.markReaderIndex();
        int preIndex = in.readerIndex();
        int inNo = readRawVarint32(in);
        int length = readRawVarint32(in);
        
        System.out.println("in:" + inNo + "_" + length);
        
        if (preIndex == in.readerIndex()) {
            return;
        }
        if (length < 0) {
            throw new CorruptedFrameException("negative length: " + length);
        }

        if (in.readableBytes() < length) {
            in.resetReaderIndex();
        } else {
        	byte[] array;
        	int offset = 0;
        	if (in.hasArray()) {
                array = in.array();
                offset = in.arrayOffset() + in.readerIndex();
            } else {
            	array = new byte[length];
                in.getBytes(in.readerIndex(), array, 0, length);
                offset = 0;
            }
        	String str2 = new String(array,offset,length,"utf-8");
        	System.out.println(str2);
//            out.add(in.readRetainedSlice(length));
        	
        }
	}
	
	
	static void writeRawVarint32(ByteBuf out, int value) throws IOException {
        while (true) {
            if ((value & ~0x7F) == 0) {
                out.writeByte(value);
                return;
            } else {
                out.writeByte((value & 0x7F) | 0x80);
                value >>>= 7;
            }
        }
    }

    static int computeRawVarint32Size(final int value) {
        if ((value & (0xffffffff <<  7)) == 0) {
            return 1;
        }
        if ((value & (0xffffffff << 14)) == 0) {
            return 2;
        }
        if ((value & (0xffffffff << 21)) == 0) {
            return 3;
        }
        if ((value & (0xffffffff << 28)) == 0) {
            return 4;
        }
        return 5;
    }
    
    private static int readRawVarint32(ByteBuf buffer) {
        if (!buffer.isReadable()) {
            return 0;
        }
        buffer.markReaderIndex();
        byte tmp = buffer.readByte();
        if (tmp >= 0) {
            return tmp;
        } else {
            int result = tmp & 127;
            if (!buffer.isReadable()) {
                buffer.resetReaderIndex();
                return 0;
            }
            if ((tmp = buffer.readByte()) >= 0) {
                result |= tmp << 7;
            } else {
                result |= (tmp & 127) << 7;
                if (!buffer.isReadable()) {
                    buffer.resetReaderIndex();
                    return 0;
                }
                if ((tmp = buffer.readByte()) >= 0) {
                    result |= tmp << 14;
                } else {
                    result |= (tmp & 127) << 14;
                    if (!buffer.isReadable()) {
                        buffer.resetReaderIndex();
                        return 0;
                    }
                    if ((tmp = buffer.readByte()) >= 0) {
                        result |= tmp << 21;
                    } else {
                        result |= (tmp & 127) << 21;
                        if (!buffer.isReadable()) {
                            buffer.resetReaderIndex();
                            return 0;
                        }
                        result |= (tmp = buffer.readByte()) << 28;
                        if (tmp < 0) {
                            throw new CorruptedFrameException("malformed varint.");
                        }
                    }
                }
            }
            return result;
        }
    }
    
}
