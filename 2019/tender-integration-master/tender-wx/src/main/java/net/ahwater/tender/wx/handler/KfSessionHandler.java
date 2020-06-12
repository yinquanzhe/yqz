package net.ahwater.tender.wx.handler;

import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.util.json.WxMpGsonBuilder;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class KfSessionHandler extends AbstractHandler {

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage, Map<String, Object> context, WxMpService wxMpService, WxSessionManager sessionManager) {
        // 对会话做处理
        logger.error(WxMpGsonBuilder.create().toJson(wxMessage));
        return null;
    }

}
