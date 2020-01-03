<xml>
	<ToUserName><![CDATA[${toUser}]]></ToUserName>
	<FromUserName><![CDATA[${fromUser}]]></FromUserName>
	<CreateTime>${reateTime}</CreateTime>
	<MsgType><![CDATA[${msgType}]]></MsgType>
	<#if msgType == "text">
	<Content><![CDATA[${content}]]></Content>
	</#if>
</xml>