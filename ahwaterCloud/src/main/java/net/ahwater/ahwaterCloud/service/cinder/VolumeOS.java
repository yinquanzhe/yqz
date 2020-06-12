package net.ahwater.ahwaterCloud.service.cinder;

import net.ahwater.ahwaterCloud.dao.VolumeDao;
import net.ahwater.ahwaterCloud.entity.compute.AllServerListEle;
import net.ahwater.ahwaterCloud.service.identityV3.IdentityOS;
import net.ahwater.ahwaterCloud.util.HttpUtils;
import net.ahwater.ahwaterCloud.util.JsonUtils;
import org.openstack4j.api.Builders;
import org.openstack4j.api.OSClient.OSClientV2;
import org.openstack4j.model.common.ActionResponse;
import org.openstack4j.model.identity.v2.Token;
import org.openstack4j.model.storage.block.Volume;
import org.openstack4j.model.storage.block.Volume.Status;
import org.openstack4j.model.storage.block.VolumeUploadImage;
import org.openstack4j.model.storage.block.options.UploadImageData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;


/**
 * 云硬盘操作类
 * 功能：创建云硬盘（没有源）;
 * 创建云硬盘（从image创建）;
 * 创建云硬盘（从快照创建）;
 * 创建云硬盘（从硬盘创建）;
 * 查询当前租户所有的云硬盘;
 * 更新云硬盘的名字和描述;
 * 扩展云硬盘;
 * 将因云硬盘挂载到server;
 * 卸载云硬盘;
 * 更改云硬盘的状态;
 * 删除云硬盘;
 * 列出云平台上的所有云硬盘
 *
 * @author ZhangDaofu
 */
@Service
public class VolumeOS {

    private Volume volume;
    @Autowired
    private VolumeDao volumeDao;

    public VolumeOS() {
    }

    public VolumeOS(Volume volume) {
        this.volume = volume;
    }

    public Volume getVolume() {
        return volume;
    }


    /**
     * 创建云硬盘（没有源）
     *
     * @param os
     * @param volumeName
     * @param volumeDescription
     * @param volumeSize
     * @return
     */
    public String creatVolume(OSClientV2 os, String volumeName, String volumeDescription, int volumeSize) {
        String exceptionMessage = "success";
        try {
            volume = os.blockStorage().volumes()
                    .create(Builders.volume()
                            .name(volumeName)
                            .description(volumeDescription)
                            .size(volumeSize)
                            .build()
                    );
        } catch (Exception e) {
            exceptionMessage = e.getMessage();
        }

        return exceptionMessage;
    }


    /**
     * 创建云硬盘（从image创建）
     *
     * @param os
     * @param volumeName
     * @param volumeDescription
     * @param volumeSize
     * @param imageId
     * @return
     */
    public String creatVolumefromImage(OSClientV2 os, String volumeName, String volumeDescription, int volumeSize, String imageId) {
        String exceptionMessage;
        try {
            volume = os.blockStorage().volumes()
                    .create(Builders.volume()
                            .name(volumeName)
                            .description(volumeDescription)
                            .imageRef(imageId)
                            .size(volumeSize)
                            .build()
                    );
            exceptionMessage = "success";
        } catch (Exception e) {
            exceptionMessage = e.getMessage();
        }
        return exceptionMessage;
    }


    /**
     * 创建云硬盘（从快照创建）
     *
     * @param os
     * @param volumeName
     * @param volumeDescription
     * @param volumeSize
     * @param snapshotId
     * @return
     */
    public String creatVolumefromSnapshot(OSClientV2 os, String volumeName, String volumeDescription, int volumeSize, String snapshotId) {
        String exceptionMessage;
        try {
            volume = os.blockStorage().volumes()
                    .create(Builders.volume()
                            .name(volumeName)
                            .description(volumeDescription)
                            .snapshot(snapshotId)
                            .size(volumeSize)
                            .build()
                    );
            exceptionMessage = "success";
        } catch (Exception e) {
            exceptionMessage = e.getMessage();
        }

        return exceptionMessage;
    }


    /**
     * 创建云硬盘（从硬盘创建）
     *
     * @param os
     * @param volumeName
     * @param volumeDescription
     * @param volumeSize
     * @param volumeId
     * @return
     */
    public String creatVolumefromVolume(OSClientV2 os, String volumeName, String volumeDescription, int volumeSize, String volumeId) {
        String exceptionMessage;
        try {
            volume = os.blockStorage().volumes()
                    .create(Builders.volume()
                            .name(volumeName)
                            .description(volumeDescription)
                            .source_volid(volumeId)
                            .size(volumeSize)
                            .build()
                    );
            exceptionMessage = "success";
        } catch (Exception e) {
            exceptionMessage = e.getMessage();
        }
        return exceptionMessage;
    }


    //Create a Bootable Volume from an Image
    public Volume creatVolumefrom(OSClientV2 os, String volumeName, String volumeDescription, String imageId, boolean bootableTF) {
        volume = os.blockStorage().volumes()
                .create(Builders.volume()
                        .name(volumeName)
                        .description(volumeDescription)
                        .imageRef(imageId)
                        .bootable(bootableTF)
                        .build()
                );
        return volume;
    }


    /**
     * 查询当前租户所有的volume
     *
     * @param os
     * @return
     */
    public List<? extends Volume> listAllVolumes(OSClientV2 os) {
        List<? extends Volume> volumes = os.blockStorage().volumes().list();
        return volumes;
    }


    /**
     * 更新云硬盘的名字和描述
     *
     * @param os
     * @param volumeId
     * @param newName
     * @param newDescription
     * @return
     */
    public String updateNameDescription(OSClientV2 os, String volumeId, String newName, String newDescription) {
        String exceptionMessage;
        ActionResponse response = os.blockStorage().volumes().update(volumeId, newName, newDescription);
        if (response.isSuccess()) {
            exceptionMessage = "success";
        } else {
            exceptionMessage = response.getFault();
        }
        return exceptionMessage;
    }


    /**
     * 扩展云硬盘
     *
     * @param os
     * @param volumeId
     * @param newSize
     * @return
     */
    public String extendVolume(OSClientV2 os, String volumeId, int newSize) {
        String exceptionMessage;
        ActionResponse response = os.blockStorage().volumes().extend(volumeId, newSize);
        if (response.isSuccess()) {
            exceptionMessage = "success";
        } else {
            exceptionMessage = response.getFault();
        }
        return exceptionMessage;
    }


    /**
     * 将因云硬盘挂载到server
     *
     * @param os
     * @param serverId
     * @param volumeId
     * @param device
     * @return
     */
    public String attachVolumeAndServer(OSClientV2 os, String serverId, String volumeId, String device) {
        String message;
        try {
            os.compute().servers().attachVolume(serverId, volumeId, device);
            message = "success";
        } catch (Exception e) {
            message = e.getMessage();
        }
        return message;
    }


    /**
     * 卸载云硬盘
     *
     * @param os
     * @param serverId
     * @param attachmentId
     * @return
     */
    public String detachVolumeFromServer(OSClientV2 os, String serverId, String attachmentId) {
        String message;
        ActionResponse response = os.compute().servers().detachVolume(serverId, attachmentId);
        if (response.isSuccess()) {
            message = "success";
        } else {
            message = response.getFault();
        }
        return message;
    }


    /**
     * 更改云硬盘的状态
     *
     * @param os
     * @param newState
     * @return
     */
    public String resetVolumeState(OSClientV2 os, String newState) {
        ActionResponse response = null;
        String exceptionMessage;
        if (newState.equals("in_use")) {
            response = os.blockStorage().volumes().resetState(volume.getId(), Status.IN_USE);
        } else if (newState.equals("error")) {
            response = os.blockStorage().volumes().resetState(volume.getId(), Status.ERROR);
        } else if (newState.equals("creating")) {
            response = os.blockStorage().volumes().resetState(volume.getId(), Status.CREATING);
        } else if (newState.equals("deleting")) {
            response = os.blockStorage().volumes().resetState(volume.getId(), Status.DELETING);
        } else if (newState.equals("detaching")) {
            response = os.blockStorage().volumes().resetState(volume.getId(), Status.DETACHING);
        } else if (newState.equals("error_deleting")) {
            response = os.blockStorage().volumes().resetState(volume.getId(), Status.ERROR_DELETING);
        } else if (newState.equals("available")) {
            response = os.blockStorage().volumes().resetState(volume.getId(), Status.AVAILABLE);
        }
        if (response.isSuccess()) {
            exceptionMessage = "succ";
        } else {
            exceptionMessage = response.getFault();
        }
        return exceptionMessage;

    }


    /**
     * 删除云硬盘
     *
     * @param os
     * @param volumeId
     * @return
     */
    public String deleteVolume(OSClientV2 os, String volumeId) {
        String exceptionMessage;
        ActionResponse response = os.blockStorage().volumes().delete(volumeId);
        if (response.isSuccess()) {
            exceptionMessage = "succ";
        } else {
            exceptionMessage = response.getFault();
        }
        return exceptionMessage;
    }

    //上传镜像
    public VolumeUploadImage uploadImage(OSClientV2 os, UploadImageData data) {
        VolumeUploadImage volumeImage = os.blockStorage().volumes().uploadToImage(volume.getId(), data);
        return volumeImage;

    }


    /**
     * 列出云平台上的所有云硬盘
     *
     * @param token
     * @param os
     * @param tenantId
     * @return
     * @throws IOException
     */
    public String AdminListAllImage(Token token, OSClientV2 os, String tenantId) throws IOException {

        String volumeAPI = IdentityOS.getAccessAPI().get("cinderv2");
        String urlst = volumeAPI + "/" + tenantId + "/volumes/detail?all_tenants=1";
        String result = null;
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
        //
        //    result = HttpUtils.readUrlStream(connection);
        //} catch (MalformedURLException e) {
        //    e.printStackTrace();
        //} catch (IOException e) {
        //    throw new RuntimeException(e);
        //}
        //CloseableHttpClient httpClient = null;
        //CloseableHttpResponse httpResponse = null;
        //try {
        //    httpClient = HttpClients.createDefault();
        //    URI uri = new URIBuilder(urlst)// 设置请求路径
        //            .setCharset(Charset.forName("UTF-8"))// 设置编码（默认为项目编码），一般不用设置
        //            //.addParameter("k1", "v1")// 连续调用此方法可以设置多个请求参数
        //            .build();// 构建并返回URI对象
        //    HttpGet httpGet = new HttpGet(uri);
        //    httpGet.setHeader("X-Auth-Token",token.getId());
        //    httpGet.setHeader("Content-Type","application/json");
        //    httpResponse = httpClient.execute(httpGet);
        //    HttpEntity entity = httpResponse.getEntity();
        //    result = EntityUtils.toString(entity);
        //    // 处理result的代码...
        //} catch (URISyntaxException e) {
        //    e.printStackTrace();
        //} finally {
        //    if (httpResponse != null) {
        //        httpResponse.close();
        //    }
        //    if (httpClient != null) {
        //        httpClient.close();
        //    }
        //}
        try {
            //Map<String,String> map = new LinkedHashMap<>();
            //map.put("X-Auth-Token",token.getId());
            //map.put("Content-Type","application/json");
            //map.put("charset","UTF-8");
            result = HttpUtils.get(urlst,token.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 根据缓存表列出所有计算服务   2017-03-07  蔡国成
     *
     * @param tenantName
     * @return
     * @throws Exception
     */
    public String AdminListAllImageByCache(String tenantName) throws Exception {

        //region Description
		/*Connection conn = JdbcUtil.getConnection();
		String sql="select * from  ahwater_cloud_instances where tenanantName=?"; //
		List<AllServerListEle> lm=new ArrayList<>();
		PreparedStatement pstmt=conn.prepareStatement(sql);
		ResultSet rs=null;
		pstmt.setString(1,tenantName);
		rs=pstmt.executeQuery();// 
		while (rs.next()){
			  AllServerListEle m=new AllServerListEle();
				m.setTenanantName(rs.getString(2));
				m.setHost(rs.getString(3));
				m.setServerId(rs.getString(4));
				m.setServerName(rs.getString(5));
				m.setImageName(rs.getString(6));
				m.setImagePersonalName(rs.getString(7));
				m.setImageOStype(rs.getString(8));
				m.setImageOSVersion(rs.getString(9));
				m.setImageOSBit(rs.getString(10));
				
				List<String> ips=new ArrayList<>();
				ips.add(rs.getString(11));
				m.setIpAddr(ips);
				m.setVcpus(rs.getInt(12));
				m.setRam(rs.getInt(13));
				m.setDisk(rs.getInt(14));
				m.setStatus(rs.getString(15));
				m.setTimeFromCreated(rs.getString(16)); 
				lm.add(m);
		}		
		JdbcUtil.close(conn, pstmt, rs);*/
        //endregion

        List<AllServerListEle> lm = volumeDao.selectByTenanantName(tenantName);
        //处理 成 json
        return JsonUtils.toJson(lm);
    }


}
