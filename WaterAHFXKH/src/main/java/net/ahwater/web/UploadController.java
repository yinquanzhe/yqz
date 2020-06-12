package net.ahwater.web;

import com.alibaba.fastjson.JSON;
import net.ahwater.bean.ResponseResult;
import net.ahwater.service.WeixinService;
import net.ahwater.wx.utils.WxSubscribeFileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.Map;
import java.util.UUID;

/**
 * Created by YYC on 2017/7/4.
 */
@Controller
public class UploadController implements ServletContextAware {

    private String imgUploadDirPath;

    private Logger log = LoggerFactory.getLogger(UploadController.class);

    @Autowired
    private WeixinService weixinService;

    @Override
    public void setServletContext(ServletContext servletContext) {
        imgUploadDirPath = servletContext.getRealPath("/wx_img");
    }

    @ResponseBody
    @RequestMapping(value = "/fileUpload", method = RequestMethod.POST)
    public ResponseResult doUpload(
            @RequestParam MultipartFile file,
            HttpServletRequest request,
            @RequestParam(value = "needWx", required = false, defaultValue = "true") String needWx) {
        String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/";
        ResponseResult result = new ResponseResult(1, "", null);
        try {
            if (!file.isEmpty()) {
                String[] temp = file.getOriginalFilename().split("\\.");
                String fileName = UUID.randomUUID().toString() + "." + temp[temp.length - 1];
                String filePath = this.imgUploadDirPath + File.separator + fileName;
                // 转存文件
                file.transferTo(new File(filePath));
                log.info("保存文件成功: " + filePath);
                result.setErrno(0);
                result.setData(new String[] { basePath + "wx_img/" + fileName });
                if (needWx != null && needWx.equals("true")) {
                    // 转成微信地址
                    String res = WxSubscribeFileUtil.uploadimg(filePath);
                    log.info("res: " + res);
                    Map map = (Map) JSON.parse(res);
                    if (map != null && map.get("url") != null) {
                        result.setMsg(map.get("url").toString());
                    } else {
                        result.setErrno(-1);
                        result.setMsg("获取微信端图片地址失败");
                    }
                }
            } else {
                result.setErrno(-1);
                result.setMsg("文件为空");
            }
        } catch (Exception e) {
            result.setErrno(-2);
            result.setMsg("出现异常");
            log.error(e.toString());
        }
        return result;
    }

}
