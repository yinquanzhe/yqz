package net.ahwater.ahwaterCloud.identityV3;

import org.apache.http.client.methods.HttpPut;

/**
 * HttpPatch类，继承HttpPut
 * @author gwh
 *
 */

public class HttpPatch extends HttpPut{
	public HttpPatch(String url) {
		super(url);
	}
	@Override
	public String getMethod(){
		return "PATCH";
	}
}
