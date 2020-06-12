package net.ahwater.ahwaterCloud.service.identityV3;

import org.apache.http.client.methods.HttpPut;
import org.springframework.stereotype.Service;


/**
 * HttpPatch类，继承HttpPut
 *
 * @author gwh
 */
@Service
public class HttpPatch extends HttpPut {

    public HttpPatch() {
    }

    public HttpPatch(String url) {
        super(url);
    }

    @Override
    public String getMethod() {
        return "PATCH";
    }
}
