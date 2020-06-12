package net.ahwater.ahwaterCloud.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.openstack4j.api.OSClient.OSClientV2;
import org.openstack4j.api.types.Facing;
import org.openstack4j.model.identity.v2.Access;
import org.openstack4j.model.identity.v2.Token;
import org.openstack4j.model.image.Image;
import org.openstack4j.openstack.OSFactory;
import org.openstack4j.openstack.identity.v2.domain.KeystoneToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.ahwater.ahwaterCloud.image.ImageOS;
import net.ahwater.ahwaterCloud.image.entity.ImageDetail;
import net.ahwater.ahwaterCloud.image.entity.ImageElement;
/**
 * 镜像（image）控制类
 * 接收前端对镜像的操作请求，调用相关模块进行处理完成对请求的响应
 * 接收的请求包括：
 *   列出当前租户拥有的镜像;
 *   列出公有镜像;
 *   列出image的name和id;
 *   修改镜像的名字;
 *   删除一个指定的镜像;
 *   批量删除镜像;
 *   列出云平台中所有的镜像.
 *   
 * @author ZhangDaofu
 *
 */
@Controller
@RequestMapping("/cont")
public class ImageProcess {
	
	/**
	 * 列出当前租户拥有的镜像
	 * @param request
	 * @param response
	 * @param session
	 * @throws IOException
	 */
	@RequestMapping("/listAllImages")
	public void listAllImages(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException{
		OSClientV2 os=OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
		KeystoneToken token =(KeystoneToken) os.getAccess().getToken();
		String tenantId = token.getTenant().getId();
//		String tenantId = request.getParameter("tenantId");
		List<? extends Image> images = ImageOS.queryAllImages(os);
		List<ImageElement> imas = new ArrayList<>();
		for(Image i : images){
			if(i.getOwner().equals(tenantId)) //过滤掉非当前租户拥有的镜像
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
		ObjectMapper mapper = new ObjectMapper();
		String msg = mapper.writeValueAsString(imas);
		PrintWriter out = null;
		response.setContentType("application/json");
		out = response.getWriter();
		out.write(msg);
	}
	
	@RequestMapping("/listShareImages")  //列出共享镜像
	public void listShareImages(){
		
	}
	
	/**
	 * 列出公有镜像
	 * @param response
	 * @param session
	 * @throws IOException
	 */
	@RequestMapping("/listPublicImages")  
	public void listPublicImages(HttpServletResponse response, HttpSession session) throws IOException{
		OSClientV2 os=OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
		List<? extends Image> images = ImageOS.queryPublicImage(os);
		List<ImageElement> imas = new ArrayList<>();
		for(Image i : images){
			ImageElement ima = new ImageElement();
			ImageOS ios = new ImageOS(i);
			ima.setImageName(ios.getImageName());
			//ima.setImageType(ios.getImageType());
			ima.setImageType(ios.getIsSnapshot());
			ima.setImageStatus(ios.getImageStatus());
			ima.setImageisPublic(ios.isImageisPublic());
			ima.setImageisProtected(ios.isImageisProtected());
			ima.setImageFormat(ios.getImageFormat());
			ima.setImageSize(ios.getImageSize());
			ima.setImageId(ios.getImageId());
			imas.add(ima);
		}
		ObjectMapper mapper = new ObjectMapper();
		String msg = mapper.writeValueAsString(imas);
		PrintWriter out = null;
		response.setContentType("application/json");
		out = response.getWriter();
		out.write(msg);
	}
	
	/**
	 * 列出image的name和id
	 * @param response
	 * @param session
	 * @throws IOException
	 */
	@RequestMapping("/listImageNameId")  
	public void listImageNameId(HttpServletResponse response, HttpSession session) throws IOException{
		OSClientV2 os=OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
		List<? extends Image> images = ImageOS.queryAllImages(os);
		List<ImageElement> imas = new ArrayList<>();
		for(Image i : images){
			ImageElement ima = new ImageElement();
			ImageOS ios = new ImageOS(i);
			ima.setImageName(ios.getImageName());
			ima.setImageId(ios.getImageId());
			imas.add(ima);
		}
		ObjectMapper mapper = new ObjectMapper();
		String msg = mapper.writeValueAsString(imas);
		PrintWriter out = null;
		response.setContentType("application/json");
		out = response.getWriter();
		out.write(msg);
	}
	
	
	/**
	 * 列出镜像 的详细信息
	 * @param request
	 * @param response
	 * @param session
	 * @throws IOException
	 */
	@RequestMapping("/imageDetail")
	public void imageDetail(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException{
		OSClientV2 os=OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
		String imageId = request.getParameter("imageId");
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
		
		ObjectMapper mapper = new ObjectMapper();
		String msg = mapper.writeValueAsString(imageDetail);
		PrintWriter out = null;
		response.setContentType("application/json");
		out = response.getWriter();
		out.write(msg);
	}
	
	
	/**
	 * 修改镜像的名字
	 * @param request
	 * @param response
	 * @param session
	 * @throws IOException
	 */
	@RequestMapping("/updateImageName")
	public void updateImageName(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException{
		OSClientV2 os=OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
		Token token = os.getAccess().getToken();
		String newName = request.getParameter("newName");
		String imageId = request.getParameter("imageId");
		ImageOS ios = new ImageOS();
		String message = ios.updateImageName(token.getId(), imageId, newName);
//		String message = ios.updateImageName(os, imageId, newName);
		ObjectMapper mapper = new ObjectMapper();
		JsonNode jsonnode = mapper.readTree(message);
		String name = jsonnode.get("name").toString();
		name = name.substring(1, name.length()-1);
		if(name.equals(newName))
			message = "succ";
		else
			message = "error";
		String msg = mapper.writeValueAsString(message);
		PrintWriter out = null;
		response.setContentType("application/json");
		out = response.getWriter();
		out.write(msg);
	}
	
	
	/**
	 * 删除一个指定的镜像
	 * @param request
	 * @param response
	 * @param session
	 * @throws IOException
	 */
	@RequestMapping("/deleteImage")
	public void deleteImage(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException{
		OSClientV2 os=OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
		String imageId = request.getParameter("imageId");
		ImageOS ios = new ImageOS();
		String message = ios.deteteImage(os, imageId);
		ObjectMapper mapper = new ObjectMapper();
		String msg = mapper.writeValueAsString(message);
		PrintWriter out = null;
		response.setContentType("application/json");
		out = response.getWriter();
		out.write(msg);
	}
	
	
	/**
	 * 批量删除镜像
	 * @param request
	 * @param response
	 * @param session
	 * @throws IOException
	 */
	@RequestMapping("/deleteMultiImage")
	public void deleteMultiImage(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException{
		OSClientV2 os=OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
		String imageIdsStr = request.getParameter("imageIds");
		ObjectMapper mapper = new ObjectMapper();
		List<String> imageIds = mapper.readValue(imageIdsStr, new TypeReference<List<String>>() {});
		List<String> messages = new ArrayList<>();
		ImageOS ios = new ImageOS();
//		int succDel = 0;
		for(String imageId:imageIds){
			String message = ios.deteteImage(os, imageId);
			messages.add(message);
//			if(message.equals("succ")){
//				succDel ++;
//				messages.add(message);
//			}else{
//				messages.add(message);
//			}
		}
//		String message = "成功删除"+Integer.toString(succDel)+"个";
		String msg = mapper.writeValueAsString(messages);
		PrintWriter out = null;
		response.setContentType("application/json");
		out = response.getWriter();
		out.write(msg);
	}
	
	
	/**
	 * 列出云平台中所有的镜像
	 * @param request
	 * @param response
	 * @param session
	 * @throws IOException
	 */
	@RequestMapping("/adminListAllImages")
	public void adminListAllImages(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException{
		OSClientV2 os=OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
		os.perspective(Facing.ADMIN);
		ImageOS ios = new ImageOS();
		String result = ios.AdminListAllImage(os);
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.readTree(result);
		JsonNode imagesjson = rootNode.get("images");
		List<ImageElement> imagesElement = new ArrayList<>();
		for(int i=0; i<imagesjson.size(); i++){
			ImageElement imageEle = new ImageElement();
			JsonNode imagejson = imagesjson.get(i);
			String tenantId = imagejson.get("owner").toString();
			tenantId = tenantId.substring(1,tenantId.length()-1);
			String imageId = imagejson.get("id").toString();
			imageId = imageId.substring(1,imageId.length()-1);
			Image image = os.images().get(imageId);
			
			imageEle.setImageFormat(image.getDiskFormat());
			imageEle.setImageId(image.getId());
			imageEle.setImageisProtected(image.isProtected());
			imageEle.setImageisPublic(image.isPublic());
			String iName = imagejson.get("name").toString();
			iName = iName.substring(1, iName.length()-1);
			imageEle.setImageName(iName);
//			imageEle.setImageName(image.getName());
			imageEle.setImageOwner(image.getOwner());
			imageEle.setImageOwnerName(os, tenantId);
			imageEle.setImageSize(image.getSize());
			imageEle.setImageStatus(image.getStatus());
			imageEle.setImageType(image.isSnapshot());
			imageEle.setImageOwnerName(os, tenantId);
			
			imagesElement.add(imageEle);
//			ImageElement image = new ImageElement();
//			JsonNode imagejson = imagesjson.get(i);
//			String tenantId = imagejson.get("owner").toString();
//			tenantId = tenantId.substring(1,tenantId.length()-1);
//			image.setImageOwnerName(os, tenantId);
//			image.setImageOwner(tenantId);
//			image.setImageFormat(imagejson.get("disk_format").toString());
//			image.setImageId(imagejson.get("id").toString());
//			image.setImageisProtected(imagejson.get("protected").toString());
//			image.setImageisPublic(imagejson.get("visibility").toString());
//			image.setImageName(imagejson.get("name").toString());
//			image.seti
		}
		
		String msg = mapper.writeValueAsString(imagesElement);
		PrintWriter out = null;
		response.setContentType("application/json");
		out = response.getWriter();
		out.write(msg);
	}
	
}
