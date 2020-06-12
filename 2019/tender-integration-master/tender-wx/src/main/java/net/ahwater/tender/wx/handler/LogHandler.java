package net.ahwater.tender.wx.handler;

import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.util.json.WxMpGsonBuilder;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class LogHandler extends AbstractHandler {

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage, Map<String, Object> context, WxMpService wxMpService, WxSessionManager sessionManager) {
        this.logger.info("接收到请求消息，内容：\n{}", WxMpGsonBuilder.create().toJson(wxMessage));
//        try {
//            wxMpService.getKefuService().sendKefuMessage(WxMpKefuMessage
//                    .TEXT()
//                    .toUser(wxMessage.getFromUser())
//                    .content("我是客服")
//                    .build());
//        } catch (WxErrorException e) {
//            e.printStackTrace();
//        }
        return null;
    }

}
