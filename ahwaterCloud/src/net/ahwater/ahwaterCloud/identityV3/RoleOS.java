package net.ahwater.ahwaterCloud.identityV3;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.openstack4j.api.OSClient.OSClientV2;
import org.openstack4j.model.common.ActionResponse;
import org.openstack4j.model.identity.v2.Role;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.ahwater.ahwaterCloud.identityV3.entity.RoleParam;
import net.ahwater.ahwaterCloud.identityV3.entity.UpdateRole;

/**
 * 角色类
 * @author gwh
 *
 */

public class RoleOS {
	
	Role role;
	
	public RoleOS(){}
	
	public RoleOS(Role role){
		this.role=role;
	}
	
	/**
	 * 创建角色
	 * @param os
	 * @param roleName
	 * @return
	 */
	public String createRole(OSClientV2 os,String roleName){
		String excepStr=null;
		try{
			role=os.identity().roles().create(roleName);
		}catch(Exception e){
			excepStr=e.getMessage();
		}
		return excepStr;
	}
	
	/**
	 * 编辑角色
	 * @param os
	 * @param newRoleName
	 * @throws IOException 
	 */
	public static String editRole(OSClientV2 os,String tokenId,String roleId, String newRoleName) throws IOException{
		String keystone=IdentityOS.getAccessAPI().get("keystone");
		keystone=keystone.replaceAll("v2.0", "v3");	
		String strURL=keystone+"/roles/"+roleId;
		
		UpdateRole uptateRoleParam=new UpdateRole();
		RoleParam rp=new RoleParam();
		rp.setName(newRoleName);
		uptateRoleParam.setRole(rp);
		
		ObjectMapper mapper = new ObjectMapper();
		String param=null;
		try {
			param = mapper.writeValueAsString(uptateRoleParam);
		} catch (JsonProcessingException e1) {
			e1.printStackTrace();
		}
		
		String result=null;
		HttpClient httpClient = new DefaultHttpClient();
		HttpPatch httpPatch = new HttpPatch(strURL); 
		httpPatch.setHeader("Content-type", "application/json");  
        httpPatch.setHeader("Charset", HTTP.UTF_8);  
        httpPatch.setHeader("Accept", "application/json");  
        httpPatch.setHeader("Accept-Charset", HTTP.UTF_8);  
        httpPatch.setHeader("X-Auth-Token", tokenId);
        try {
        	 StringEntity entity = new StringEntity(param,HTTP.UTF_8);  
             httpPatch.setEntity(entity);  
 
            HttpResponse response = httpClient.execute(httpPatch);  
            result =EntityUtils.toString(response.getEntity());  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return result;
	}
	
	/**
	 * 删除角色
	 * @param os
	 * @return
	 */
	public ActionResponse deleteRole(OSClientV2 os){
		ActionResponse ac= os.identity().roles().delete(role.getId());
		return ac;
	}
	
	/**
	 * 批量删除角色
	 * @param os
	 * @param roleIds
	 */
	public static void patchDeleteRole(OSClientV2 os,List<String> roleIds){
		for(String id:roleIds){
			os.identity().roles().delete(id);
		}
	}
	
	/**
	 * 角色列表
	 * @param os
	 * @return
	 */
	public static List<?extends Role> listRoles(OSClientV2 os){
		return os.identity().roles().list();
	}
}
