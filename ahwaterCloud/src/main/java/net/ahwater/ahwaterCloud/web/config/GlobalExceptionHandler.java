package net.ahwater.ahwaterCloud.web.config;

import net.ahwater.ahwaterCloud.util.ErrorCode;
import net.ahwater.ahwaterCloud.util.RespResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * 捕获异常统一处理
 * Created by TECHMAN on 2018/2/24.
 */

@ControllerAdvice(annotations = {Controller.class})
public class GlobalExceptionHandler {

    private static final Logger logger = LogManager.getLogger(GlobalExceptionHandler.class);

    /**
     * 系统异常处理，比如：404,500
     *
     * @param req
     * @param e
     * @return
     * @throws Exception
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public RespResult<?> defaultErrorHandler(HttpServletRequest req, Exception e) throws Exception {
        logger.error("", e);
        RespResult baseResp = new RespResult();
        baseResp.setMsg(e.getCause().getMessage());
        if (e instanceof org.springframework.web.servlet.NoHandlerFoundException) {
            baseResp.setCode(ErrorCode.http_status_not_found);
        } else {
            baseResp.setCode(ErrorCode.http_status_internal_server_error);
        }
        baseResp.setData("");
        return baseResp;
    }
}
