package net.ahwater.ahwaterCloud.service.network;

import net.ahwater.ahwaterCloud.service.identityV3.IdentityOS;
import net.ahwater.ahwaterCloud.util.HttpUtils;
import org.openstack4j.api.OSClient.OSClientV2;
import org.openstack4j.model.identity.v2.Token;
import org.openstack4j.model.network.Network;
import org.openstack4j.model.network.Subnet;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;


/**
 * 子网的相关操作
 *
 * @author zdf
 */
@Service
public class SubnetOS {

    private Subnet subnet;

    public List<? extends Subnet> listAllSubnet(OSClientV2 os) {
        List<? extends Subnet> subnets = os.networking().subnet().list();
        return subnets;
    }

    /**
     * 列出一个网络下的所有的子网
     *
     * @param os
     * @param networkId
     * @return
     */
    public List<String> listSubnet(OSClientV2 os, String networkId) {
        Network network = os.networking().network().get(networkId);
        List<String> subnet = network.getSubnets();
        return subnet;
    }


    /**
     * 列出子网的详细信息
     *
     * @param token
     * @param os
     * @param subnetId
     * @return
     * @throws IOException
     */
    public String subnetDetail(Token token, OSClientV2 os, String subnetId) throws IOException {
        String result = "";
        //String host = "http://ahwater-cloud-controller:9696/v2.0";
        //String urlst = host+"/"+"subnets/"+subnetId;
        String networkAPI = IdentityOS.getAccessAPI().get("neutron");
        String urlst = networkAPI + "/v2.0" + "/subnets/" + subnetId;
        //URL url;
        //try {
        //    url = new URL(urlst);
        //    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        //    connection.setDoOutput(true);
        //    connection.setDoInput(true);
        //    connection.setInstanceFollowRedirects(true);
        //    connection.setRequestMethod("GET");
        //    connection.setRequestProperty("X-Auth-Token", token.getId());
        //    connection.setRequestProperty("Content-Type", "application/json");
        //    connection.setRequestProperty("charset", "UTF-8");
        //    connection.connect();
        //    BufferedReader in = null;
        //    in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        //
        //    result = HttpUtils.readUrlStream(connection);
        //
        //} catch (MalformedURLException e) {
        //    e.printStackTrace();
        //}
        try {
            result = HttpUtils.get(urlst, token.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
