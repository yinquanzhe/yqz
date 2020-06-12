package net.ahwater.ahwaterCloud.service.compute;

import net.ahwater.ahwaterCloud.entity.compute.ConsoleLogParam;
import net.ahwater.ahwaterCloud.entity.compute.os_getConsoleOutput;
import net.ahwater.ahwaterCloud.service.identityV3.IdentityOS;
import net.ahwater.ahwaterCloud.util.HttpUtils;
import net.ahwater.ahwaterCloud.util.JsonUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;


/**
 * 控制台日志类
 *
 * @author gwh
 */
@Service
public class ConsoleLog {

    /**
     * 获取控制台日志
     *
     * @param tokenId
     * @param tenantId
     * @param serverId
     * @param numLines
     * @return
     * @throws IOException
     */
    public static String getConsoleLog(String tenantId, String tokenId, String serverId, int numLines) throws IOException {
        String novaAPI = IdentityOS.getAccessAPI().get("nova") + "/" + tenantId;
        String strURL = novaAPI + "/servers/" + serverId + "/action";

        ConsoleLogParam clp = new ConsoleLogParam();
        os_getConsoleOutput ogt = new os_getConsoleOutput();
        ogt.setLength(numLines);
        clp.setOgt(ogt);
        //ObjectMapper mapper = new ObjectMapper();
        //String param = null;
        //try {
        //    param = mapper.writeValueAsString(clp);
        //} catch (JsonProcessingException e1) {
        //    e1.printStackTrace();
        //}
        String param = JsonUtils.toJson(clp).replaceAll("ogt", "os-getConsoleOutput");
        String result = null;
        //try {
        //    URL url = new URL(strURL);
        //    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        //    connection.setDoOutput(true);
        //    connection.setDoInput(true);
        //    connection.setUseCaches(false);
        //    connection.setInstanceFollowRedirects(true);
        //    connection.setRequestMethod("POST");
        //    connection.setRequestProperty("X-Auth-Token", tokenId);
        //    connection.setRequestProperty("Content-Type", "application/json");
        //    connection.setRequestProperty("Content-Length", "21");
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
            result = HttpUtils.post(strURL, tokenId, param);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
