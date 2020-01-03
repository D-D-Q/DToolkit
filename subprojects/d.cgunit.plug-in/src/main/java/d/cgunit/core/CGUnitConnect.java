package d.cgunit.core;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class CGUnitConnect {

	private int port = 8888;
	
	public void run(String str){
		
		ByteBuffer byteBuffer = ByteBuffer.allocate(32);
		
		SocketChannel socketChannel = null;
		Selector selector = null;
		try{
			socketChannel = SocketChannel.open();
			socketChannel.configureBlocking(false);
			
			selector = Selector.open();
			socketChannel.register(selector, SelectionKey.OP_CONNECT);
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		try{
			socketChannel.connect(new InetSocketAddress("127.0.0.1", port));
			selector.select();
			
			Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
			while(iterator.hasNext()){
				
				SelectionKey selectionKey = iterator.next();
				iterator.remove();
				if(!selectionKey.isValid())
					continue;
				
				if(selectionKey.isConnectable()){
					SocketChannel channel = (SocketChannel)selectionKey.channel();
					channel.finishConnect();
					channel.configureBlocking(false);
					
					byte[] bytes = str.getBytes("utf-8");
					for(int i = 0; i < bytes.length; i+=byteBuffer.capacity()){ // 不能超过最大长度
						byteBuffer.clear();
						if(bytes.length - i < byteBuffer.capacity()) // 剩余字节不够byteBuffer大小
							byteBuffer.put(bytes, i, bytes.length - i);
						else
							byteBuffer.put(bytes, i, byteBuffer.capacity());
						byteBuffer.flip();
						channel.write(byteBuffer); // 写入字节流
					}
					byteBuffer.clear();
					byteBuffer.put("#".getBytes()); // 写入表示message结尾符
					byteBuffer.flip();
					channel.write(byteBuffer);
					
					selectionKey.cancel();
					channel.close();
					break;
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			try {
				socketChannel.close();
			} catch (IOException e) {}
		}
	}
	
//	public static void main(String[] args) {
//		new CGUnitConnect().run("file");
//	}
}
