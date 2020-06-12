package net.ahwater.ahwaterCloud.service.compute.systemInfo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.ahwater.ahwaterCloud.service.identityV3.IdentityOS;
import net.ahwater.ahwaterCloud.util.HttpUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;


@Service
public class ServiceOS {

    //列出所有系统信息服务
    public String listService(String tokenId) throws Exception {
        String host = IdentityOS.getAccessAPI().get("keystone");
        host = host.replaceAll("2.0", "3");
        String strURL = host + "/services";
        String result = null;
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("X-Auth-Token", tokenId);
            connection.connect();

            result = HttpUtils.readUrlStream(connection);

        } catch (IOException e) {
            e.printStackTrace();
        }

        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(result);
        JsonNode services = rootNode.get("services");
        result = services.toString();
        return result;
    }


    /**
     * 列出所有系统信息计算服务
     *
     * @param tokenId
     * @param tenantId
     * @return
     * @throws Exception
     */
    public String listComputeServices(String tenantId, String tokenId) throws Exception {
        String novaAPI = IdentityOS.getAccessAPI().get("nova") + "/" + tenantId;
        String strURL = novaAPI + "/os-services";
        String result = null;
        try {
            result = HttpUtils.get(strURL, tokenId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(result);
        JsonNode services = rootNode.get("services");

        result = services.toString();
        return result;
    }

}
