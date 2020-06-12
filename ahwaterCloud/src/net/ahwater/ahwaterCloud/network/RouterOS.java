/**
 * 
 */
package net.ahwater.ahwaterCloud.network;

import java.util.List;

import org.openstack4j.api.OSClient.OSClientV2;
import org.openstack4j.model.network.ExternalGateway;
import org.openstack4j.model.network.HostRoute;
import org.openstack4j.model.network.Router;
import org.openstack4j.model.network.State;

import com.sun.org.apache.bcel.internal.generic.RETURN;
import com.sun.org.apache.xml.internal.resolver.helpers.PublicId;

import net.ahwater.ahwaterCloud.network.entity.RouterBriefInfo;

/**
 * 定义了路由的创建、删除和查询操作
 * @author dell
 *
 */
public class RouterOS {
	Router  router;
	
	
	public RouterOS(){}
	public RouterOS(Router router)
	{
		this.router=router;
	}
	public RouterOS(OSClientV2 os,String routerId)
	{
		router=os.networking().router().get(routerId);
	}
	/**
	 * 获取路由列表
	 */
	 public static List< ? extends Router> ListAllRouter(OSClientV2 os)
	 {
		 return os.networking().router().list();
	 }
	 public ExternalGateway getExternalGateway()
	 {
		 return router.getExternalGatewayInfo();
	 }
	 public String getId()
	 {
		 return router.getId();
	 }
	 public String getName()
	 {
		 return router.getName();
	 }
	 public State getStatus()
	 {
		 return router.getStatus();
	 }
	 public String getTenantId()
	 {
		 return router.getTenantId();
	 }
	 public boolean isAdminStateUp()
	 {
		 return router.isAdminStateUp();
	 }
	 public List<?extends HostRoute> getRoutes()
	 {
		 return router.getRoutes();
	 }
	 
}
