/**
 *
 */
package net.ahwater.ahwaterCloud.web.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.ahwater.ahwaterCloud.entity.compute.CreateKeyMsg;
import net.ahwater.ahwaterCloud.entity.compute.KeyPairDetailInfo;
import net.ahwater.ahwaterCloud.entity.compute.KeyPairListElements;
import net.ahwater.ahwaterCloud.service.compute.KeypairOS;
import org.openstack4j.api.OSClient.OSClientV2;
import org.openstack4j.model.compute.Keypair;
import org.openstack4j.model.identity.v2.Access;
import org.openstack4j.openstack.OSFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * 定义密钥对的相关操作
 *
 * @author dell
 */
@RestController
@RequestMapping("/contr")
public class KeyPairProcess {

    @Autowired
    private KeypairOS keypairOS;

    /**
     * 列举所有密钥对的简要信息
     */
    @RequestMapping("/ListAllKeyPairs")
    public List<KeyPairListElements> ListAllKeyPairs(HttpSession session) {
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
        List<? extends Keypair> keypairs = keypairOS.ListAllKeyPairs(os);
        List<KeyPairListElements> keyPairElementList = new ArrayList<>();
        for (Keypair kp : keypairs) {
            KeyPairListElements keyPairListElements = new KeyPairListElements();
            KeypairOS keypairOS = new KeypairOS(kp);
            keyPairListElements.setKeyPairName(keypairOS.getName());
            keyPairListElements.setFingerPrint(keypairOS.getFingerPrint());
            keyPairElementList.add(keyPairListElements);
        }
        return keyPairElementList;
    }

    /**
     * 显示单个秘钥对的详细信息
     *
     * @param keypairName
     * @throws IOException
     */
    @RequestMapping("/DisplayKeyPairDetails")
    public KeyPairDetailInfo DisplayKeyPairDetails(String keypairName, HttpSession session) {
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
        KeyPairDetailInfo keyPairDetailInfo = new KeyPairDetailInfo();
        KeypairOS keypairOS = new KeypairOS();
        keypairOS.getKeypair(os, keypairName);
        keyPairDetailInfo.setKeypairName(keypairOS.getName());
        keyPairDetailInfo.setId(keypairOS.getID().toString());
        keyPairDetailInfo.setFingerPrint(keypairOS.getFingerPrint());
        keyPairDetailInfo.setTimeCreated(keypairOS.getTimeCreated().toString());
        keyPairDetailInfo.setUserID(keypairOS.getUserID());
        keyPairDetailInfo.setPublicKey(keypairOS.getPublicKey());

        return keyPairDetailInfo;
    }

    /**
     * 创建指定名称的秘钥对，若名称尚不存在，则成功创建并返回私钥，若名称已存在，则创建失败并返回错误信息“Existed Name!”
     *
     * @param keypairName
     * @param session
     * @throws IOException
     */
    @RequestMapping("/CreateKeyPair")
    public CreateKeyMsg CreateKeyPair(String keypairName, HttpSession session) {
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
        KeypairOS keypairOS = new KeypairOS();
        String errorMsg = "success";
        String privateKey = "";
        if (keypairOS.ContainsName(os, keypairName))
            errorMsg = "Existed Name!";
        else {
            keypairOS.createKeypair(os, keypairName);
            privateKey = keypairOS.getPrivateKey(os);
        }
        CreateKeyMsg ckMsg = new CreateKeyMsg();
        ckMsg.setErrorMsg(errorMsg);
        ckMsg.setPrivateKey(privateKey);
        return ckMsg;
    }

    /**
     * 删除给定名称的密钥对
     *
     * @param keypairNames
     * @param session
     * @throws IOException
     */
    @RequestMapping("/DeleteKeyPair")
    public String DeleteKeyPair(String keypairNames, HttpSession session) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        List<String> keypairNameList = mapper.readValue(keypairNames, new TypeReference<List<String>>() {
        });
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
        KeypairOS keypairOS = new KeypairOS();
        int success = 0, failed = 0;
        for (String keypairName : keypairNameList) {
            try {
                keypairOS.DeleteKeypair(os, keypairName);
                success++;
            } catch (Exception ex) {
                failed++;
            }
        }
        String errorMsgs = "删除成功 " + success + " 个，失败 " + failed + " 个！";
        return errorMsgs;
    }

    /**
     * 通过导入的公钥创建密钥对
     *
     * @param keypairName
     * @param publicKey
     * @param session
     * @throws IOException
     */
    @RequestMapping("/ImportKeyPair")
    public String ImportKeyPair(String keypairName, String publicKey, HttpSession session) {
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
        KeypairOS keypairOS = new KeypairOS();
        String errorMsg = "success";
        if (keypairOS.ContainsName(os, keypairName)) {
            errorMsg = "Existed Name";
        } else {
            try {
                keypairOS.createKeypairByPublicKey(os, keypairName, publicKey);
            } catch (Exception e) {
                errorMsg = "Illegal PublicKey";
            }
        }
        return errorMsg;
    }
}
