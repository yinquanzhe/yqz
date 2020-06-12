package net.ahwater.ahwaterCloud.web.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.ahwater.ahwaterCloud.entity.image.ImageDetail;
import net.ahwater.ahwaterCloud.entity.image.ImageElement;
import net.ahwater.ahwaterCloud.service.image.ImageOS;
import org.openstack4j.api.OSClient.OSClientV2;
import org.openstack4j.api.types.Facing;
import org.openstack4j.model.identity.v2.Access;
import org.openstack4j.model.identity.v2.Token;
import org.openstack4j.model.image.Image;
import org.openstack4j.openstack.OSFactory;
import org.openstack4j.openstack.identity.v2.domain.KeystoneToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * 镜像（image）控制类
 * 接收前端对镜像的操作请求，调用相关模块进行处理完成对请求的响应
 * 接收的请求包括：
 * 列出当前租户拥有的镜像;
 * 列出公有镜像;
 * 列出image的name和id;
 * 修改镜像的名字;
 * 删除一个指定的镜像;
 * 批量删除镜像;
 * 列出云平台中所有的镜像.
 *
 * @author ZhangDaofu
 */
@RestController
@RequestMapping("/cont")
public class ImageProcess {

    @Autowired
    private ImageOS imageOS;

    /**
     * 列出当前租户拥有的镜像
     *
     * @param session
     * @throws IOException
     */
    @RequestMapping("/listAllImages")
    public List<ImageElement> listAllImages(HttpSession session) {
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
        KeystoneToken token = (KeystoneToken) os.getAccess().getToken();
        String tenantId = token.getTenant().getId();
        List<? extends Image> images = imageOS.queryAllImages(os);
        List<ImageElement> imas = new ArrayList<>();
        for (Image i : images) {
            if (i.getOwner().equals(tenantId)) //过滤掉非当前租户拥有的镜像
            {
                ImageElement ima = new ImageElement();
                ImageOS ios = new ImageOS(i);
                ima.setImageName(i.getName());
                ima.setImageType(ios.getIsSnapshot());
                ima.setImageStatus(ios.getImageStatus());
                ima.setImageisPublic(ios.isImageisPublic());
                ima.setImageisProtected(ios.isImageisProtected());
                ima.setImageFormat(ios.getImageFormat());
                ima.setImageSize(ios.getImageSize());
                ima.setImageId(ios.getImageId());
                ima.setImageOwner(i.getOwner());

                imas.add(ima);
            }
        }
        return imas;
    }

    @RequestMapping("/listShareImages")  //列出共享镜像
    public void listShareImages() {

    }

    /**
     * 列出公有镜像
     *
     * @param session
     * @throws IOException
     */
    @RequestMapping("/listPublicImages")
    public List<ImageElement> listPublicImages(HttpSession session) {
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
        List<? extends Image> images = imageOS.queryPublicImage(os);
        List<ImageElement> imas = new ArrayList<>();
        for (Image i : images) {
            ImageElement ima = new ImageElement();
            ImageOS ios = new ImageOS(i);
            ima.setImageName(ios.getImageName());
            ima.setImageType(ios.getIsSnapshot());
            ima.setImageStatus(ios.getImageStatus());
            ima.setImageisPublic(ios.isImageisPublic());
            ima.setImageisProtected(ios.isImageisProtected());
            ima.setImageFormat(ios.getImageFormat());
            ima.setImageSize(ios.getImageSize());
            ima.setImageId(ios.getImageId());
            imas.add(ima);
        }
        return imas;
    }

    /**
     * 列出image的name和id
     *
     * @param session
     */
    @RequestMapping("/listImageNameId")
    public List<ImageElement> listImageNameId(HttpSession session) {
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
        List<? extends Image> images = imageOS.queryAllImages(os);
        List<ImageElement> imas = new ArrayList<>();
        for (Image i : images) {
            ImageElement ima = new ImageElement();
            ImageOS ios = new ImageOS(i);
            ima.setImageName(ios.getImageName());
            ima.setImageId(ios.getImageId());
            imas.add(ima);
        }
        return imas;
    }


    /**
     * 列出镜像 的详细信息
     *
     * @param imageId
     * @param session
     */
    @RequestMapping("/imageDetail")
    public ImageDetail imageDetail(String imageId, HttpSession session) {
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
        Image image = os.images().get(imageId);
        ImageDetail imageDetail = new ImageDetail();
        imageDetail.setImageCheckSum(image.getChecksum());
        imageDetail.setImageContainerFormat(image.getContainerFormat());
        imageDetail.setImageCreatedDate(image.getCreatedAt());
        imageDetail.setImageFormat(image.getDiskFormat());
        imageDetail.setImageId(image.getId());
        imageDetail.setImageisProtected(image.isProtected());
        imageDetail.setImageisPublic(image.isPublic());
        imageDetail.setImageName(image.getName());
        imageDetail.setImageOwner(image.getOwner());
        imageDetail.setImageSize(image.getSize());
        imageDetail.setImageStatus(image.getStatus());
        imageDetail.setImageUpdateDate(image.getUpdatedAt());

        return imageDetail;
    }


    /**
     * 修改镜像的名字
     *
     * @param newName
     * @param imageId
     * @param session
     * @throws IOException
     */
    @RequestMapping("/updateImageName")
    public String updateImageName(String newName, String imageId, HttpSession session) throws IOException {
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
        Token token = os.getAccess().getToken();
        String message = imageOS.updateImageName(token.getId(), imageId, newName);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonnode = mapper.readTree(message);
        String name = jsonnode.get("name").toString();
        name = name.substring(1, name.length() - 1);
        if (name.equals(newName)) {
            message = "succ";
        } else {
            message = "error";
        }
        return message;
    }


    /**
     * 删除一个指定的镜像
     *
     * @param imageId
     * @param session
     * @throws IOException
     */
    @RequestMapping("/deleteImage")
    public String deleteImage(String imageId, HttpSession session) {
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
        return imageOS.deteteImage(os, imageId);
    }


    /**
     * 批量删除镜像
     *
     * @param imageIdsStr
     * @param session
     * @throws IOException
     */
    @RequestMapping("/deleteMultiImage")
    public List<String> deleteMultiImage(@RequestParam("imageIds") String imageIdsStr, HttpSession session) throws IOException {
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
        ObjectMapper mapper = new ObjectMapper();
        List<String> imageIds = mapper.readValue(imageIdsStr, new TypeReference<List<String>>() {
        });
        List<String> messages = new ArrayList<>();
        for (String imageId : imageIds) {
            String message = imageOS.deteteImage(os, imageId);
            messages.add(message);
        }
        return messages;
    }


    /**
     * 列出云平台中所有的镜像
     *
     * @param session
     * @throws IOException
     */
    @RequestMapping("/adminListAllImages")
    public List<ImageElement> adminListAllImages(HttpSession session) throws IOException {
        OSClientV2 os = OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
        os.perspective(Facing.ADMIN);
        String result = imageOS.AdminListAllImage(os);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(result);
        JsonNode imagesjson = rootNode.get("images");
        List<ImageElement> imagesElement = new ArrayList<>();
        for (int i = 0; i < imagesjson.size(); i++) {
            ImageElement imageEle = new ImageElement();
            JsonNode imagejson = imagesjson.get(i);
            String tenantId = imagejson.get("owner").toString();
            tenantId = tenantId.substring(1, tenantId.length() - 1);
            String imageId = imagejson.get("id").toString();
            imageId = imageId.substring(1, imageId.length() - 1);
            Image image = os.images().get(imageId);

            imageEle.setImageFormat(image.getDiskFormat());
            imageEle.setImageId(image.getId());
            imageEle.setImageisProtected(image.isProtected());
            imageEle.setImageisPublic(image.isPublic());
            String iName = imagejson.get("name").toString();
            iName = iName.substring(1, iName.length() - 1);
            imageEle.setImageName(iName);
            imageEle.setImageOwner(image.getOwner());
            imageEle.setImageOwnerName(os, tenantId);
            imageEle.setImageSize(image.getSize());
            imageEle.setImageStatus(image.getStatus());
            imageEle.setImageType(image.isSnapshot());
            imageEle.setImageOwnerName(os, tenantId);

            imagesElement.add(imageEle);
        }

        return imagesElement;
    }

}
