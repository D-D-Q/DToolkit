package d.cgunit.core;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import org.junit.runner.JUnitCore;

import groovy.lang.GroovyClassLoader;

/**
 * 接受连接, 运行测试文件
 * 
 * @author D
 */
public class CGUnitServer extends Thread {

	private GroovyClassLoader groovyClassLoader;
	
	private ServerSocketChannel serverSocketChannel;
	
	/**
	 * nio端口
	 */
	private int port = 8888;
	
	public CGUnitServer() {
		groovyClassLoader = new GroovyClassLoader();
	}
	
	public CGUnitServer(int port) {
		this();
		this.port = port;
	}
	
	/**
	 * 启动测试服务
	 */
	public void startServer(){
		this.start();
	}
	
	/**
	 * 停止测试服务
	 */
	public void stopServer(){
		this.interrupt();
		try{
			serverSocketChannel.close();
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		
		StringBuilder message = new StringBuilder(); // 只是有一个message，不支持多个客户端连接。多个连接message保存的内容就串了
		
		ByteBuffer byteBuffer = ByteBuffer.allocate(32);
		Selector selector = null;
		try{
			serverSocketChannel = ServerSocketChannel.open();
			serverSocketChannel.socket().bind(new InetSocketAddress("127.0.0.1", port));
			serverSocketChannel.configureBlocking(false);
			
			selector = Selector.open();
			serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT); // 监听连接
		}
		catch(Exception e){
			e.printStackTrace();
			return;
		}
		
		System.out.println("--------CGUnit Test Server Start--------");
		do{
			try{
				selector.select();
				
				Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
				while(iterator.hasNext()){
					
					SelectionKey selectionKey = iterator.next();
					iterator.remove();
					if(!selectionKey.isValid())
						continue;
					
					if(selectionKey.isAcceptable()){
						ServerSocketChannel serverChannel = (ServerSocketChannel)selectionKey.channel();
						SocketChannel socketChannel = serverChannel.accept();
						socketChannel.configureBlocking(false);
						socketChannel.register(selector, SelectionKey.OP_READ);
					}
					else if(selectionKey.isReadable()){
						SocketChannel socketChannel = (SocketChannel)selectionKey.channel();
						
						byteBuffer.clear();
						int i = socketChannel.read(byteBuffer);
						while(i != 0 && i != -1){ // 读本次字节流
							message.append(new String(byteBuffer.array(), 0, i, "utf-8"));
							byteBuffer.clear();
							i = socketChannel.read(byteBuffer);
						}
						if(message.lastIndexOf("#") == -1){ // 没有读到message结尾
							socketChannel.register(selector, SelectionKey.OP_READ);
							break;
						}
						
						message.deleteCharAt(message.length()-1); // 删除结尾符号#
						runTest(message.toString());
						message.setLength(0); // 清空message消息
						
						selectionKey.cancel();
						socketChannel.close();
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		while(serverSocketChannel.isOpen() && !this.isInterrupted());
	}
	
	/**
	 *  groovy生成类对象，junit执行测试
	 * 
	 * @param message
	 */
	private void runTest(String message){
		
		File file = new File(message);
		try{
			groovyClassLoader.clearCache();
			Class<?> parseClass = groovyClassLoader.parseClass(file);
			
			JUnitCore jUnitCore = new JUnitCore();
			jUnitCore.addListener(new TestRunListener());
			jUnitCore.run(parseClass);
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
