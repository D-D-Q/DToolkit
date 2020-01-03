package du.core;

import java.security.MessageDigest;
import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * 微信Controller
 * 
 * @author I
 * @version 2016-06-24
 */
public class WXController {
	
	private WXServices messageManager;
	
	/**
	 * 接受微信验证
	 * 
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
//	@RequestMapping(value = "", method = RequestMethod.GET)
//	@ResponseBody
	public Object get(HttpServletRequest request, HttpServletResponse response) {
		
		String signature = request.getParameter("signature");
		String timestamp = request.getParameter("timestamp");
		String nonce = request.getParameter("nonce");
		String echostr = request.getParameter("echostr");
		
		String[] array = new String[]{timestamp, nonce, WXServices.TOKEN};
		Arrays.sort(array);
		
		try{
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
				Logger.getRootLogger().error("微信服务器验证成功");
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
	 * 接受微信消息
	 * 
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
//	@RequestMapping(value = "", method = RequestMethod.POST)
//	@ResponseBody
	public Object post(HttpServletRequest request, HttpServletResponse response) {
		
		SAXReader reader = new SAXReader();
		Element rootElement = null;
		try {
			Document document = reader.read(request.getInputStream());
			rootElement = document.getRootElement();
			
			String msgType = rootElement.elementText("MsgType");
			
			switch (msgType) {
				case "event":
					String event = rootElement.elementText("Event");
					String eventKey = rootElement.elementText("EventKey");
					if("CLICK".equals(event) && "b_riddle".equals(eventKey)){
						
						
						
						return "";
					}
					break;
					
				case "text":
					
					
					return "";
	
				default:
					break;
			}
		} catch (Exception e) {
			Logger.getRootLogger().error("微信消息解析失败：" + rootElement != null?rootElement.asXML():"");
			e.printStackTrace();
		}finally {
			
		}
		return "success";
	}
}
