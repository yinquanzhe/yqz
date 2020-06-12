package net.ahwater.ahwaterCloud.service.compute;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.ahwater.ahwaterCloud.dao.VolumeDao;
import net.ahwater.ahwaterCloud.entity.compute.AllServerListEle;
import net.ahwater.ahwaterCloud.entity.compute.VMManager.ComputeServiceDiableEle;
import net.ahwater.ahwaterCloud.entity.compute.VMManager.ComputeServiceEnableEle;
import net.ahwater.ahwaterCloud.service.identityV3.IdentityOS;
import net.ahwater.ahwaterCloud.util.HttpUtils;
import net.ahwater.ahwaterCloud.util.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * 计算域服务类
 *
 * @author gwh
 */
@Service
public class ComputeServiceOS {
    static final String binary = "nova-compute";

    @Autowired
    private VolumeDao volumeDao;

    /**
     * 列出所有计算服务
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
        //URL url;
        //try {
        //    url = new URL(strURL);
        //    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        //    connection.setDoOutput(true);
        //    connection.setDoInput(true);
        //    connection.setUseCaches(false);
        //    connection.setInstanceFollowRedirects(true);
        //    connection.setRequestMethod("GET");
        //    connection.setRequestProperty("X-Auth-Token", tokenId);
        //    connection.connect();
        //
        //    result = HttpUtils.readUrlStream(connection);
        //
        //} catch (IOException e) {
        //    e.printStackTrace();
        //}
        try {
            result = HttpUtils.get(strURL, tokenId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(result);
        JsonNode services = rootNode.get("services");

        List<JsonNode> res = new ArrayList<>();
        for (int i = 0; i < services.size(); i++) {
            JsonNode ch = services.get(i);
            if (ch.get("zone").asText().equals("nova")) {
                res.add(ch);
            }
        }
        result = mapper.writeValueAsString(res);
        return result;
    }


    /**
     * 根据缓存表列出所有计算服务   2017-03-07  蔡国成
     *
     * @param tenantName
     * @return
     */
    public List<AllServerListEle> listComputeServicesByCache(String tenantId, String tenantName) {
        return volumeDao.selectByTenanantName(tenantName);
    }


    /**
     * 关闭服务
     *
     * @param tokenId
     * @param tenantId
     * @param hostName
     * @param reason
     * @return
     * @throws IOException
     */
    public String disableComputeService(String tenantId, String tokenId, String hostName, String reason) throws IOException {
        String novaAPI = IdentityOS.getAccessAPI().get("nova") + "/" + tenantId;
        String strURL = novaAPI + "/os-service/disable-log-reason";
        ComputeServiceDiableEle csde = new ComputeServiceDiableEle();
        csde.setHost(hostName);
        csde.setBinary(binary);
        csde.setDisabled_reason(reason);
        String result = null;
        //ObjectMapper mapper = new ObjectMapper();
        //String param = null;
        //try {
        //    param = mapper.writeValueAsString(csde);
        //} catch (JsonProcessingException e1) {
        //    e1.printStackTrace();
        //}
        //
        //try {
        //    URL url = new URL(strURL);
        //    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        //    connection.setDoOutput(true);
        //    connection.setDoInput(true);
        //    connection.setUseCaches(false);
        //    connection.setInstanceFollowRedirects(true);
        //    connection.setRequestMethod("PUT");
        //    connection.setRequestProperty("X-Auth-Token", tokenId);
        //    connection.setRequestProperty("Content-Type", "application/json");
        //    connection.connect();
        //    OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
        //    out.append(param);
        //    out.flush();
        //    out.close();
        //
        //    result = HttpUtils.readUrlStream(connection);
        //
        //} catch (IOException e) {
        //    e.printStackTrace();
        //}
        try {
            result = HttpUtils.put(strURL, tokenId, JsonUtils.toJson(csde));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }


    /**
     * 打开服务
     *
     * @param tokenId
     * @param tenantId
     * @param hostName
     * @return
     * @throws IOException
     */
    public String enableComputeService(String tenantId, String tokenId, String hostName) throws IOException {
        String novaAPI = IdentityOS.getAccessAPI().get("nova") + "/" + tenantId;
        String strURL = novaAPI + "/os-service/enable";
        ComputeServiceEnableEle csee = new ComputeServiceEnableEle();
        csee.setHost(hostName);
        csee.setBinary(binary);
        String result = null;
        //ObjectMapper mapper = new ObjectMapper();
        //String param = null;
        //try {
        //    param = mapper.writeValueAsString(csee);
        //} catch (JsonProcessingException e1) {
        //    e1.printStackTrace();
        //}
        //
        //try {
        //    URL url = new URL(strURL);
        //    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        //    connection.setDoOutput(true);
        //    connection.setDoInput(true);
        //    connection.setUseCaches(false);
        //    connection.setInstanceFollowRedirects(true);
        //    connection.setRequestMethod("PUT");
        //    connection.setRequestProperty("X-Auth-Token", tokenId);
        //    connection.setRequestProperty("Content-Type", "application/json");
        //    connection.connect();
        //    OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
        //    out.append(param);
        //    out.flush();
        //    out.close();
        //
        //    result = HttpUtils.readUrlStream(connection);
        //
        //} catch (IOException e) {
        //    e.printStackTrace();
        //}
        try {
            result = HttpUtils.put(strURL, tokenId,JsonUtils.toJson(csee));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }

}
