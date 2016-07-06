package du.core;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

//public class WXServices implements DisposableBean {
public class WXServices {
	
	/**
	 * 微信应用标识
	 */
	public static final String AppID = "wxe820ac282968d1ee";
	
	/**
	 * 微信应用密钥
	 */
	public static final String AppSecret = "c277b84673ee621b0d908846e9659480";
	
	/**
	 * 用户tonken
	 */
	public static final String TOKEN = "kklbwx";
	
	/**
	 * 微信访问token
	 */
	private String accessToken;
	
	/**
	 * 定时消息执行器
	 */
	private ScheduledThreadPoolExecutor scheduledThreadPool;
	
	/**
	 * HTTP工具
	 */
	public static HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
	
	/**
	 * 模板
	 */
	private Configuration templateCfg;
	
	public WXServices() {
		
		scheduledThreadPool = (ScheduledThreadPoolExecutor)Executors.newScheduledThreadPool(4);
		
//		scheduledThreadPool.scheduleWithFixedDelay(()->{
//			try{
//				while(!Thread.interrupted() && !refreshAccessToken()){
//					TimeUnit.MINUTES.sleep(3);
//				}
//				refreshMenu();
//			}catch(Exception e){}
//		}, 0, 7000, TimeUnit.SECONDS); //超时时间是7200秒
		
		
		String file = this.getClass().getClassLoader().getResource("wx").getFile();
		templateCfg = new Configuration();
		try {
			templateCfg.setDirectoryForTemplateLoading(new File(file));
			templateCfg.setDefaultEncoding("utf-8");
			templateCfg.setClassicCompatible(true);
		} catch (IOException e) {
			Logger.getRootLogger().error("初始化微信信息模板文件失败");
			e.printStackTrace();
		}
	}
	
	/**
	 * 刷新accessToken
	 * 
	 * @return
	 */
	private synchronized boolean refreshAccessToken() {
		
		CloseableHttpClient closeableHttpClient = httpClientBuilder.build();
		HttpGet HttpGet = new HttpGet("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + AppID + "&secret=" + AppSecret);
		
		try {
			String rs = EntityUtils.toString(closeableHttpClient.execute(HttpGet).getEntity());
			
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode node = objectMapper.readTree(rs);
			String text = node.path("access_token").asText();
			if("".equals(text)){
				Logger.getRootLogger().error(node.path("errcode").asInt() + ":微信获取access_tonken失败，重试");
				return false;
			}
			accessToken = text;
			Logger.getRootLogger().info("微信获取access_tonken成功：" + accessToken);
			return true;
		} 
		catch (IOException e) {
			Logger.getRootLogger().error("微信获取access_tonken失败，重试");
			e.printStackTrace();
			return false;
		}
		finally {
			try {
				closeableHttpClient.close();
			} catch (IOException e) {}
		}
	}
	
	/**
	 * 刷新微信菜单
	 * 
	 * @return
	 */
	public boolean refreshMenu(){
		
		Template template;
		try {
			template = templateCfg.getTemplate("menu.ftl");
		} catch (IOException e) {
			Logger.getRootLogger().error("微信公众号菜单模板文件查找失败");
			e.printStackTrace();
			return false;
		}
		
		CloseableHttpClient closeableHttpClient = httpClientBuilder.build();
		HttpPost httpPost = new HttpPost("https://api.weixin.qq.com/cgi-bin/menu/create?access_token=" + getAccessToken());
		
		try {
			StringWriter writer = new StringWriter();
			template.process(null, writer);
			
			httpPost.setEntity(new StringEntity(writer.toString(), Charset.forName("utf-8")));
			HttpEntity rsEntity = closeableHttpClient.execute(httpPost).getEntity();
			String rs = EntityUtils.toString(rsEntity);
			
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode node = objectMapper.readTree(rs);
			int errcode = node.path("errcode").asInt();
			if(errcode != 0){
				Logger.getRootLogger().error(":微信刷新菜单失败，返回:" + rs);
				return false;
			}
			Logger.getRootLogger().info(":微信刷新菜单成功");
			return true;
		} catch (IOException e) {
			Logger.getRootLogger().error("微信获取access_tonken失败，重试");
			e.printStackTrace();
			return false;
		} catch (TemplateException e) {
			Logger.getRootLogger().error("微信公众号菜单模板文件异常");
			e.printStackTrace();
		} finally {
			try {
				closeableHttpClient.close();
			} catch (IOException e) {}
		}
		
		return true;
	}
	
	public synchronized String getAccessToken() {
		return accessToken;
	}
	
	/**
	 * 发送定时消息
	 * 
	 * @param dialogueInfo
	 */
	public ScheduledFuture<?> sendMessageDelay(Map<String, String> model, long milliseconds){
		return sendMessageDelay(model, milliseconds, null);
	}
	/**
	 * 发送定时消息
	 * 
	 * @param dialogueInfo
	 */
	public ScheduledFuture<?> sendMessageDelay(Map<String, String> model, long milliseconds, Runnable callBack){
		
		Runnable task = () -> {
			try {
				Template template;
				try {
					template = templateCfg.getTemplate("sendMsg.ftl");
				} catch (IOException e) {
					Logger.getRootLogger().error("微信公众号发送信息模板文件查找失败");
					e.printStackTrace();
					return;
				}
				
				StringWriter writer = new StringWriter();
				try {
					template.process(model, writer);
				} catch (TemplateException | IOException e) {
					Logger.getRootLogger().error("微信公众号发送信息模板文件异常");
					e.printStackTrace();
				}
				
				CloseableHttpClient closeableHttpClient = httpClientBuilder.build();
				HttpPost httpPost = new HttpPost("https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=" + getAccessToken());
				try {
					httpPost.setEntity(new StringEntity(writer.toString(), Charset.forName("utf-8")));
					CloseableHttpResponse response = closeableHttpClient.execute(httpPost);
					String string = EntityUtils.toString(response.getEntity());

					ObjectMapper objectMapper = new ObjectMapper();
					JsonNode node = objectMapper.readTree(string);
					int errcode = node.path("errcode").asInt();
					if(errcode != 0){
						Logger.getRootLogger().error(":微信发送信息失败，发送:" + writer.toString() + "返回:" + string);
					}
					else if(callBack != null){
						callBack.run();
					}
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						closeableHttpClient.close();
					} catch (IOException e) {
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		};
		
		ScheduledFuture<?> future = scheduledThreadPool.schedule(task, milliseconds, TimeUnit.MILLISECONDS);
		
		return future;
	}
	
	/**
	 * 取消发送定时消息
	 * 
	 * @param dialogueInfo
	 */
	public boolean removeMessageDelay(Object o){
		
		if(o instanceof ScheduledFuture){
			
			ScheduledFuture<?> future = (ScheduledFuture<?>)o;
		
			if(!future.cancel(true)){
				Logger.getRootLogger().error(":微信取消发送信息失败");
				return false;
			}
			Logger.getRootLogger().info(":微信取消发送信息成功");
			return true;
		}
		return false;
	}
	
	/**
	 * 回复信息
	 * 
	 * @return
	 */
	public String replyMsg(Map<String, String> model){
		
		Template template;
		try {
			template = templateCfg.getTemplate("replyMsg.ftl");
		} catch (IOException e) {
			Logger.getRootLogger().error("微信公众号回复信息模板文件查找失败");
			e.printStackTrace();
			return "success";
		}
		
		StringWriter writer = new StringWriter();
		try {
			template.process(model, writer);
			return writer.toString();
		} catch (TemplateException | IOException e) {
			Logger.getRootLogger().error("微信公众号回复信息模板文件异常");
			e.printStackTrace();
		}
		
		return "success";
	}

//	@Override
	public void destroy() throws Exception {
		scheduledThreadPool.shutdown();
	}
}
