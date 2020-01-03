package du.core.varint32;

import java.io.IOException;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.CorruptedFrameException;

public class Varint32Util {
	
	/**
	 * 计算一个值使用Varint32编码所需长度
	 * 
	 * @param value
	 * @return
	 */
	public static int computeRawVarint32Size(final int value) {
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
	
	/**
	 * 计算多个值使用Varint32编码所需长度
	 * 
	 * @param value
	 * @return
	 */
	public static int computeRawVarint32Size(final int... values) {
		int i = 0;
		for(int value : values)
			i += computeRawVarint32Size(value);
		
		return i;
	}
	
	/**
	 * Varint32编码 写入一个值
	 * 
	 * @param out
	 * @param value
	 * @throws IOException
	 */
	public static void writeRawVarint32(ByteBuf out, int value) throws IOException {
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
	
	/**
	 * Varint32编码 写入多个值
	 * 
	 * @param out
	 * @param value
	 * @throws IOException
	 */
	public static void write(ByteBuf out, int... values) throws IOException {
		for(int value : values)
			writeRawVarint32(out, value);
    }

    
	/**
	 * Varint32解码 读取一个值
	 * 
	 * @param buffer
	 * @return
	 */
	public static int readRawVarint32(ByteBuf buffer) {
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
