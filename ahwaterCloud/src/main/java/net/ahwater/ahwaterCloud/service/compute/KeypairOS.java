package net.ahwater.ahwaterCloud.service.compute;

import org.openstack4j.api.OSClient.OSClientV2;
import org.openstack4j.model.compute.Keypair;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;


/**
 * 定义秘钥对相关操作
 *
 * @author dell
 */
@Service
public class KeypairOS {

    private Keypair keypair;

    public KeypairOS() {
    }

    public KeypairOS(Keypair keypair) {
        this.keypair = keypair;
    }

    public Keypair createKeypair(OSClientV2 os, String name) {

        keypair = os.compute().keypairs().create(name, null);
        return keypair;
    }

    public Keypair createKeypairByPublicKey(OSClientV2 os, String name, String publicKey) {
        keypair = os.compute().keypairs().create(name, publicKey);
        return keypair;
    }

    public Keypair getKeypair(OSClientV2 os, String name) {
        keypair = os.compute().keypairs().get(name);
        return keypair;
    }

    public String getName() {
        return keypair.getName();
    }

    public Integer getID() {
        return keypair.getId();
    }

    public String getPublicKey() {
        return keypair.getPublicKey();
    }

    public String getPrivateKey(OSClientV2 os) {
        return keypair.getPrivateKey();
    }

    public String getFingerPrint() {
        return keypair.getFingerprint();
    }

    public Date getTimeCreated() {
        return keypair.getCreatedAt();
    }

    public String getUserID() {
        return keypair.getUserId();
    }

    /**
     * 查询并列出所有的密钥对
     *
     * @param os os
     * @return 返回已有密钥对列表
     */
    public List<? extends Keypair> ListAllKeyPairs(OSClientV2 os) {
        List<? extends Keypair> keypairs = os.compute().keypairs().list();
        return keypairs;
    }

    /**
     * 通过名称查询该密钥对是否存在
     *
     * @param name 密钥对名称
     * @return
     */
    //TODO:get函数不存在返回null值有待测试
    public boolean ContainsName(OSClientV2 os, String name) {
        Keypair keypair = os.compute().keypairs().get(name);
        if (keypair == null)
            return false;
        else
            return true;
    }

    /**
     * 删除指定名称对应的密钥对
     *
     * @param os
     * @param name
     */
    public void DeleteKeypair(OSClientV2 os, String name) {
        os.compute().keypairs().delete(name);
    }
}
