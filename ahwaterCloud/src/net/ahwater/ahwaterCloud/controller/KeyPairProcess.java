/**
 * 
 */
package net.ahwater.ahwaterCloud.controller;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.openstack4j.api.OSClient.OSClientV2;
import org.openstack4j.model.compute.Keypair;
import org.openstack4j.model.identity.v2.Access;
import org.openstack4j.openstack.OSFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.ahwater.ahwaterCloud.compute.KeypairOS;
import net.ahwater.ahwaterCloud.compute.entity.CreateKeyMsg;
import net.ahwater.ahwaterCloud.compute.entity.KeyPairDetailInfo;
import net.ahwater.ahwaterCloud.compute.entity.KeyPairListElements;


@Controller
@RequestMapping("/contr")

/**
 * 定义密钥对的相关操作
 * @author dell
 * 
 */
public class KeyPairProcess {
	
	/**
	 * 列举所有密钥对的简要信息
	 */
	@RequestMapping("/ListAllKeyPairs")
	public void ListAllKeyPairs(HttpServletResponse response,HttpSession session) throws IOException{
		OSClientV2 os=OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
		List<? extends Keypair> keypairs=KeypairOS.ListAllKeyPairs(os);
		List<KeyPairListElements> keyPairElementList=new ArrayList<KeyPairListElements>();
		for(Keypair kp:keypairs)
		{
			KeyPairListElements keyPairListElements=new KeyPairListElements();
			KeypairOS keypairOS=new KeypairOS(kp);
			keyPairListElements.setKeyPairName(keypairOS.getName());
			keyPairListElements.setFingerPrint(keypairOS.getFingerPrint());
			keyPairElementList.add(keyPairListElements);
		}
		ObjectMapper mapper = new ObjectMapper();
		String msg = mapper.writeValueAsString(keyPairElementList);
		PrintWriter out = null;
		response.setContentType("application/json");
		out = response.getWriter();
		out.write(msg);
	}
	/**
	 * 显示单个秘钥对的详细信息
	 * @throws IOException
	 */
	@RequestMapping("/DisplayKeyPairDetails")
	public void DisplayKeyPairDetails(HttpServletRequest request,HttpServletResponse response,HttpSession session) throws IOException
	{
		String keypairName=request.getParameter("keypairName");
		OSClientV2 os=OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
		KeyPairDetailInfo keyPairDetailInfo=new KeyPairDetailInfo();
		KeypairOS keypairOS=new KeypairOS();
		keypairOS.getKeypair(os, keypairName);
		keyPairDetailInfo.setKeypairName(keypairOS.getName());
		keyPairDetailInfo.setId(keypairOS.getID().toString());
		keyPairDetailInfo.setFingerPrint(keypairOS.getFingerPrint());
		keyPairDetailInfo.setTimeCreated(keypairOS.getTimeCreated().toString());
		keyPairDetailInfo.setUserID(keypairOS.getUserID());
		keyPairDetailInfo.setPublicKey(keypairOS.getPublicKey());
		ObjectMapper mapper=new ObjectMapper();
		String msg = mapper.writeValueAsString(keyPairDetailInfo);
		System.out.println(msg);
		PrintWriter out=null;
		response.setContentType("appllication/json");
		out=response.getWriter();
		out.write(msg);
	}
	/**
	 * 创建指定名称的秘钥对，若名称尚不存在，则成功创建并返回私钥，若名称已存在，则创建失败并返回错误信息“Existed Name!”
	 * @param request
	 * @param response
	 * @param session
	 * @throws IOException
	 */
	@RequestMapping("/CreateKeyPair")
	public void CreateKeyPair(HttpServletRequest request,HttpServletResponse response,HttpSession session) throws IOException
	{
		String keypairName=request.getParameter("keypairName");
		OSClientV2 os=OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
		KeypairOS keypairOS=new KeypairOS();
		String errorMsg="success";
		String privateKey="";
		if(keypairOS.ContainsName(os,keypairName))
			errorMsg="Existed Name!";
		else
		{
			keypairOS.createKeypair(os, keypairName);
			privateKey=keypairOS.getPrivateKey(os);
		}
		CreateKeyMsg ckMsg=new CreateKeyMsg();
		ckMsg.setErrorMsg(errorMsg);
		ckMsg.setPrivateKey(privateKey);
		ObjectMapper mapper=new ObjectMapper();
		String msg = mapper.writeValueAsString(ckMsg);
		PrintWriter out=null;
		response.setContentType("appllication/json");
		out=response.getWriter();
		out.write(msg);
	}
	/**
	 * 删除给定名称的密钥对
	 * @param request
	 * @param response
	 * @param session
	 * @throws IOException
	 */
	@RequestMapping("/DeleteKeyPair")
	public void DeleteKeyPair(HttpServletRequest request,HttpServletResponse response,HttpSession session) throws IOException
	{
		String keypairNames=request.getParameter("keypairNames");
		ObjectMapper mapper = new ObjectMapper(); 
		List<String> keypairNameList=mapper.readValue(keypairNames, new TypeReference<List<String>>() {});
		OSClientV2 os=OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
		KeypairOS keypairOS=new KeypairOS();
		int success=0,failed=0;
		for(String keypairName:keypairNameList )
		{
			try{
				keypairOS.DeleteKeypair(os, keypairName);
				success++;
			}
			catch(Exception ex)
			{
				failed++;
			}
		}
		String errorMsgs="删除成功 "+success+" 个，失败 "+failed+" 个！";
		String msg = mapper.writeValueAsString(errorMsgs);
		PrintWriter out=null;
		response.setContentType("appllication/json");
		out=response.getWriter();
		out.write(msg);
	}
	/**
	 * 通过导入的公钥创建密钥对
	 * @param request
	 * @param response
	 * @param session
	 * @throws IOException
	 */
	@RequestMapping("/ImportKeyPair")
	public void ImportKeyPair(HttpServletRequest request,HttpServletResponse response,HttpSession session) throws IOException
	{
		String keypairName=request.getParameter("keypairName");
		String publicKey=request.getParameter("publicKey");
		OSClientV2 os=OSFactory.clientFromAccess((Access) session.getAttribute("Access"));
		KeypairOS keypairOS=new KeypairOS();
		String errorMsg="success";
		if( keypairOS.ContainsName(os, keypairName))
		{
			errorMsg="Existed Name";
		}
		else
		{
			try{
				keypairOS.createKeypairByPublicKey(os, keypairName, publicKey);
			}
			catch(Exception e)
			{
				errorMsg="Illegal PublicKey";
			}
		}
		ObjectMapper mapper = new ObjectMapper(); 
		String msg = mapper.writeValueAsString(errorMsg);
		PrintWriter out=null;
		response.setContentType("appllication/json");
		out=response.getWriter();
		out.write(msg);
	}
	
	
}
