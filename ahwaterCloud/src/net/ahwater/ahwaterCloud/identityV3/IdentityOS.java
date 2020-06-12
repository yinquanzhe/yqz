package net.ahwater.ahwaterCloud.identityV3;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.openstack4j.api.OSClient.OSClientV2;
import org.openstack4j.model.identity.v2.Endpoint;
import org.openstack4j.model.identity.v2.Role;
import org.openstack4j.model.identity.v2.Tenant;
import org.openstack4j.openstack.OSFactory;

import net.ahwater.ahwaterCloud.util.JdbcUtil;
import sun.misc.PerformanceLogger;

/**
 * 登录认证类
 * @author gwh
 *
 */
public class IdentityOS {
	private String userName;
	private String passWord;
	private OSClientV2 os;
	private boolean isAdmin=false;
	private Tenant defaultTenant;
	private List<? extends Tenant> tenants;
	
	
	public IdentityOS(String userName,String passWord){
		this.userName=userName;
		this.passWord=passWord;
	}
	
	/**
	 * 验证初始化
	 * @return
	 * @throws IOException 
	 */
	public boolean init() throws IOException{
		try{
			String endpoint=getAccessAPI().get("keystone");
			
			os=OSFactory.builderV2()
					.endpoint(endpoint)
					.credentials(userName, passWord)
					.authenticate();
		}catch (Exception e) {
			return false;
		}
		
		tenants = os.identity().tenants().list();
		
		defaultTenant=tenants.get(0);
		
		reIdentity(defaultTenant.getName());
		
		List<? extends Role> rl=os.getAccess().getUser().getRoles();
		
		if(rl.size()==1){
			Iterator<? extends Role> it=rl.iterator();
			if(it.next().getName().equals("admin")){
				isAdmin=true;
			}
		}else{
			for(Role r:rl){
				if(r.getName().equals("admin")){
					isAdmin=true;
				}
			}
		}
		
		return true;
	}
	
	public OSClientV2 getOSClientV2(){
		return os;
	}
	
	/**
	 * 是否是admin
	 * @return
	 */
	public boolean isAdmin() {
		return isAdmin;
	}
	
	/**
	 * 获取登录的默认租户
	 * @return
	 */
	public Tenant getDefaultTenant() {
		return defaultTenant;
	}


	public List<? extends Tenant> getTenants() {
		return tenants;
	}
	
	/**
	 * 重新认证
	 * @param tenantName
	 * @return
	 * @throws IOException 
	 */
	public OSClientV2 reIdentity(String tenantName) throws IOException{
		String endpoint=getAccessAPI().get("keystone");
		os=OSFactory.builderV2()
				.endpoint(endpoint)
				.credentials(userName, passWord)
				.tenantName(tenantName)
				.authenticate();
		return os;
	}
	
	/**
	 *  获取访问API
	 * @return
	 * @throws IOException
	 */
	public static Map<String,String> getAccessAPI() throws IOException{
		
		Properties props = new Properties();
        InputStream in = IdentityOS.class.getResourceAsStream("/config/API.properties");
        props.load(in);
        
        Map<String,String> m=new HashMap<String, String>();
        m.put("nova", props.getProperty("nova"));
        m.put("neutron", props.getProperty("neutron"));
        m.put("cinderv2", props.getProperty("cinderv2"));
        m.put("glance", props.getProperty("glance"));
        m.put("cinder", props.getProperty("cinder"));
        m.put("keystone", props.getProperty("keystone"));
		
		return m;
	}
	
	
}
