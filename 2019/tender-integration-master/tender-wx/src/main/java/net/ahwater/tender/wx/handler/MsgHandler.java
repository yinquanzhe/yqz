package net.ahwater.tender.wx.handler;

import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Map;

import static me.chanjar.weixin.common.api.WxConsts.XmlMsgType;

@Component
public class MsgHandler extends AbstractHandler {

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage, Map<String, Object> context, WxMpService wxMpService, WxSessionManager sessionManager) {
        if (!wxMessage.getMsgType().equals(XmlMsgType.EVENT)) {
            // 可以选择将消息保存到本地
        }
        //当用户输入关键词如“你好”，“客服”等，并且有客服在线时，把消息转发给在线客服
        try {
            if (StringUtils.startsWithAny(wxMessage.getContent(), "你好", "客服") && wxMpService.getKefuService().kfOnlineList().getKfOnlineList().size() > 0) {
                return WxMpXmlOutMessage.TRANSFER_CUSTOMER_SERVICE()
                        .fromUser(wxMessage.getToUser())
                        .toUser(wxMessage.getFromUser()).build();
            }
        } catch (WxErrorException e) {
            logger.error(e.getError().getJson());
        }

        // 组装回复消息
        String content = "收到信息内容：<a href='http://www.baidu.com'>百度</a>";
//        String content = "收到信息内容：" + WxMpGsonBuilder.create().toJson(wxMessage);
//        return new TextBuilder().build(content, wxMessage, wxMpService);
        return null;
    }

}
