package net.ahwater.utils;

import net.ahwater.bean.FileTypeEnum;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/** 
 * @author huoyufei 
 * @date 2017-07-17 11:48:31 
 * @version 1.0  
 */
public class WxHttpUtil {
	
	public static final Logger log = LoggerFactory.getLogger(WxHttpUtil.class);
	
	/**
	 * formatedUrl参数替换
	 * @param formatUrl
	 * @param param
	 * @return
	 */
	protected static String reformUrl(String formatUrl ,Map<String,String> param) {
		Set<String> keys = param.keySet();
		if(keys == null) {
			return null;
		}
		Iterator<String> it = keys.iterator();
		while(it.hasNext()) {
			String  key = it.next();
			formatUrl = formatUrl.replace(key, param.get(key));
		}
		return formatUrl;
	}
	
	/**
     * 向指定URL发送GET方法的请求
     * @param formatedUrl  发送请求的URL
     * @return 远程资源的响应结果
     */
	protected static String sendGet(String formatedUrl) {
        BufferedReader in = null;
        try {
            String formatedUrlNameString = formatedUrl;
            URL realUrl = new URL(formatedUrlNameString);
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent","Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            connection.connect();
            // 获取所有响应头字段
            /* Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : map.keySet()) {
                System.out.println(key + "--->" + map.get(key));
            }*/
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String result = "";
            String line ;
            while ((line = in.readLine()) != null) {
                result += line;
            }
            return result;
        } catch (Exception e) {
            log.error("WxHttpUtil send Get exception:{}" , e);
        }
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception ex) {
            	log.error("WxHttpUtil send Get exception:{}" , ex);
            }
        }
        return null;
    }

    /**
     * 向指定 URL 发送POST方法的请求
     * 
     * @param formatedUrl 发送请求的 URL
     * @param jsonParam 请求体内容
     * @return  远程资源的响应结果
     */
	protected static String sendPost(String formatedUrl, String jsonParam) {
        OutputStreamWriter out = null;
        BufferedReader in = null;
        try {
            URL realUrl = new URL(formatedUrl);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new OutputStreamWriter(conn.getOutputStream(),"UTF-8");
            // 发送请求参数
            out.write(jsonParam);
            out.flush();
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String result = "";
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
            return result;
        } catch (Exception e) {
        	  log.error("WxHttpUtil sendPost exception:{}" ,e);
        }
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException ex){
            	log.error("WxHttpUtil sendPost exception:{}" ,ex);
            }
        }
        return null;
    }    
   
    /**
     * 发送multipart/form-data表单请求
     * @param formatedUrl
     * @param file
     * @return 远程资源的响应结果
     */
	protected static String sendPostFile(String formatedUrl, FileTypeEnum contType, File file) {
    	DataOutputStream out = null;
        BufferedReader in = null;
        try {
            URL realUrl = new URL(formatedUrl);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            String boundary = "----------"+System.currentTimeMillis();
            conn.setRequestProperty("Content-Type","multipart/form-data; boundary="+boundary);
			//请求正文部分
            StringBuilder sb = new StringBuilder();
            sb.append("--");
            sb.append(boundary);
            sb.append("\r\n");
            sb.append("Content-Disposition: form-data;filename=\""+file.getName()+"\";filelength="+file.length()+"\r\n");
            sb.append("Content-Type:"+contType.getType()+"/*\r\n\r\n");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            //获取流对象
            out = new DataOutputStream( conn.getOutputStream());
	        byte[] imgeBytes = new byte[1024];
	        FileInputStream fis = new FileInputStream(file);
	        int len = 0;
	        out.write(sb.toString().getBytes("UTF-8"));
	        while ((len = fis.read(imgeBytes)) != -1) {  
	            out.write(imgeBytes, 0, len);  
	        }
	        fis.close();
	        byte[] endData = ("\r\n--" + boundary + "--\r\n").getBytes();  
	        out.write(endData);  
	        out.flush();  
	        out.close();
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String result = "";
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
            return result;
        } catch (Exception e) {
        	  log.error("WxHttpUtil sendPostFile exception:{}" ,e);
        }
        //使用finally块来关闭输出流、输入流
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException ex){
            	log.error("WxHttpUtil sendPostFile exception:{}" ,ex);
            }
        }
        return null;
    } 
    
    /**
     * 向指定 URL请求资源
     * 
     * @param formatedUrl 发送请求的 URL
     * @param jsonParam 请求体内容
     * @param accepter 文件接收对象
     * @return 远程资源的响应结果
     */
	protected static Object resPost(String formatedUrl, String jsonParam,Object accepter) {
        OutputStreamWriter out = null;
        BufferedReader in = null;
        try {
            URL realUrl = new URL(formatedUrl);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new OutputStreamWriter(conn.getOutputStream(),"UTF-8");
            // 发送请求参数
            out.write(jsonParam);
            out.flush();
            if(accepter instanceof File) {
            	File file = (File) accepter;
            	FileUtils.copyInputStreamToFile(conn.getInputStream(), file);
            	return file;
            }else if(accepter instanceof String){
            	in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String result = "";
                String line;
                while ((line = in.readLine()) != null) {
                    result += line;
                }
                return result;
            }else {
            	log.warn("WxHttpUtil resPost accepterType error");
            	return null;
            }
        } catch (Exception e) {
        	  log.error("WxHttpUtil resPost exception:{}" ,e);
        }finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException ex){
            	log.error("WxHttpUtil resPost exception:{}" ,ex);
            }
        }
        return null;
    }    

    /**
     * 重定向定向请求地址 
     * @param request
     * @param response
     * @param url 重定向地址
     */
	protected static void redirector(HttpServletRequest request,HttpServletResponse response,String url) {
    	try {
			response.sendRedirect(url);
		} catch (IOException e) {
			log.warn("WxHttpUtil redirector exception:{}",e);
		}
    }
    

}
