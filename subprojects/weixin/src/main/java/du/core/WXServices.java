package du.core;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
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
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * 微信信息管理
 * 
 * @author I
 * @version 2016年6月27日 下午12:11:09
 */
//@Service
//@Lazy(false)
//public class MessageManager implements DisposableBean {
public class WXServices{
	
	/**
	 * 微信应用标识
	 */
	public static final String AppID = "wx2af8b76446fbf3ee";
	
	/**
	 * 微信应用密钥
	 */
	public static final String AppSecret = "d0904e802927e13b21f0b881130eb25d";
	
	/**
	 * 用户TOKEN
	 */
	public static final String TOKEN = "kkgame";
	
	/**
	 * 微信访问token
	 */
	private String accessToken;
	
	/**
	 * 定时执行器
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
	
	/**
	 * json
	 */
	private ObjectMapper objectMapper;
	
	public WXServices() {
		
		scheduledThreadPool = new ScheduledThreadPoolExecutor(1);
		
		scheduledThreadPool.scheduleWithFixedDelay(()->{
			try{
				while(!Thread.interrupted() && !refreshAccessToken()){
					TimeUnit.MINUTES.sleep(3);
				}
				refreshMenu();
			}catch(Exception e){}
		}, 0, 7000, TimeUnit.SECONDS); //超时时间是7200秒
		
		
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
		
		objectMapper = new ObjectMapper();
	}
	
	/**
	 * 刷新accessToken
	 * 微信会保证交错期间，旧的accessToken也可用
	 * 
	 * @return
	 */
	private boolean refreshAccessToken() {
		
		CloseableHttpClient closeableHttpClient = httpClientBuilder.build();
		HttpGet HttpGet = new HttpGet("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + AppID + "&secret=" + AppSecret);
		
		try {
			String rs = EntityUtils.toString(closeableHttpClient.execute(HttpGet).getEntity());
			
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
	
	public String getAccessToken() {
		return accessToken;
	}
	
	/**
	 * 微信验证服务器 
	 * 
	 * @param signature
	 * @param timestamp
	 * @param nonce
	 * @param echostr
	 * @return
	 */
	public String authentication(String signature, String timestamp, String nonce, String echostr){
		
		try{
			String[] array = new String[]{timestamp, nonce, TOKEN};
			Arrays.sort(array);
			
			MessageDigest digest = java.security.MessageDigest.getInstance("SHA-1");
			digest.update((array[0]+array[1]+array[2]).getBytes());
			byte messageDigest[] = digest.digest();
			
			StringBuffer str = new StringBuffer();
			
			for (int i = 0; i < messageDigest.length; i++) {
				String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
				if (shaHex.length() < 2) {
					str.append(0);
				}
				str.append(shaHex);
			}
			
			if(str != null && str.toString().equals(signature)){
				Logger.getRootLogger().info("微信服务器验证成功");
				return echostr;
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		Logger.getRootLogger().error("微信服务器验证失败");
		
		return null;
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

	/**
	 * 接受微信消息
	 * 
	 * @param input
	 * @return
	 */
	public Map<String, String> receiveMessage(InputStream input){
		
		SAXReader reader = new SAXReader();
		Element rootElement = null;
		try {
			Document document = reader.read(input);
			rootElement = document.getRootElement();
			
			HashMap<String, String> map = new HashMap<>();
			
			for(Iterator<Element> elementIterator = rootElement.elementIterator();elementIterator.hasNext();){
				Element element = elementIterator.next();
				map.put(element.getName(), element.getText());
			}
			Logger.getRootLogger().info("接受微信消息解析成功：" + rootElement != null?rootElement.asXML():"");
			
			return map;
			
		} catch (Exception e) {
			Logger.getRootLogger().error("接受微信消息解析失败：" + rootElement != null?rootElement.asXML():"");
			e.printStackTrace();
		}finally {
			try{
				input.close();
			}catch(Exception e){};
		}
		
		return null;
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
			Logger.getRootLogger().info("微信公众号回复信息模板文件查找成功");
		} catch (IOException e) {
			Logger.getRootLogger().error("微信公众号回复信息模板文件查找失败");
			e.printStackTrace();
			return "success";
		}
		
		StringWriter writer = new StringWriter();
		try {
			template.process(model, writer);
			Logger.getRootLogger().info("微信公众号回复信息：" + writer.toString());
			return writer.toString();
		} catch (TemplateException | IOException e) {
			Logger.getRootLogger().error("微信公众号回复信息模板文件异常");
			e.printStackTrace();
		}
		
		return "success";
	}
	
	/**
	 * 回复文本信息
	 * 
	 * @return
	 */
	public String replyTextMsg(String openId, String gzhId, String content){
		
		// 回复
		Map<String,String> model = new HashMap<String, String>();
		model.put("toUser", openId);
		model.put("fromUser", gzhId);
		model.put("createTime", String.valueOf(System.currentTimeMillis()/1000));
		model.put("msgType", "text");
		model.put("content", content);
		
		return replyMsg(model);
	}
	
	/**
	 * 发送消息
	 * 
	 * @param dialogueInfo
	 */
	public boolean sendMessag(Map<String, String> model){
		
		Template template;
		try {
			template = templateCfg.getTemplate("sendMsg.ftl");
		} catch (IOException e) {
			Logger.getRootLogger().error("微信公众号发送信息模板文件查找失败");
			e.printStackTrace();
			return false;
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

			JsonNode node = objectMapper.readTree(string);
			int errcode = node.path("errcode").asInt();
			if(errcode != 0){
				Logger.getRootLogger().error(":微信发送信息失败，发送:" + writer.toString() + "返回:" + string);
				return false;
			}
			Logger.getRootLogger().info(":微信发送信息成功，发送:" + writer.toString() + "返回:" + string);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				closeableHttpClient.close();
			} catch (IOException e) {
			}
		}
		
		return true;
	}
	
	/**
	 * 发送文本消息
	 * 
	 * @param dialogueInfo
	 */
	public boolean sendTextMessag(String openId, String content){
		
		HashMap<String, String> model = new HashMap<>();
		model.put("toUser", openId);
		model.put("msgType", "text");
		model.put("content", content);
		
		return sendMessag(model);
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
				if(sendMessag(model) && callBack != null){
					callBack.run();
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
			return true;
		}
		return false;
	}
	
	/**
	 * 获得关注用户信息
	 * 
	 * @param openId 用户公众号标识
	 * @return
	 */
	public Map<String, String> userInfo(String openId){
		
		CloseableHttpClient closeableHttpClient = httpClientBuilder.build();
		HttpPost httpPost = new HttpPost("https://api.weixin.qq.com/cgi-bin/user/info" + "?access_token=" +getAccessToken() + "&openid=" + openId + "&lang=zh_CN");
		try {
			CloseableHttpResponse response = closeableHttpClient.execute(httpPost);
			String string = EntityUtils.toString(response.getEntity(), "utf-8");

			Map<String, String> map = objectMapper.readValue(string, Map.class);
			
			if(map.containsKey("errcode"))
				Logger.getRootLogger().error("微信获取用户信息失败，返回:" + string);
			else
				Logger.getRootLogger().info("微信获取用户信息成功:" + string);
			
			return map;
			
		} catch (IOException e) {
			Logger.getRootLogger().error("微信获取用户信息失败");
			e.printStackTrace();
		} finally {
			try {
				closeableHttpClient.close();
			} catch (IOException e) {
			}
		}
		
		return null;
	}

//	@Override
	public void destroy() throws Exception {
		scheduledThreadPool.shutdown();
	}
}