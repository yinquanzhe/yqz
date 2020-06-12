package net.ahwater.wx.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;


import net.ahwater.wx.message.NewsMaterial;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 微信图文消息文件上传
 *
 * @author Administrator
 */
public class WxSubscribeFileUtil {

    private static Logger log = LoggerFactory.getLogger(WxSubscribeFileUtil.class);

    /**
     * 上传图文消息内的图片获取URL【订阅号与服务号认证后均可用】
     * 请注意，本接口所上传的图片不占用公众号的素材库中图片数量的5000个的限制。图片仅支持jpg/png格式，大小必须在1MB以下。
     * <p>
     * http请求方式: POST
     * https://api.weixin.qq.com/cgi-bin/media/uploadimg?access_token=ACCESS_TOKEN
     * <p>
     * 返回说明 正常情况下的返回结果为：
     * {
     * "url":  "http://mmbiz.qpic.cn/mmbiz/gLO17UPS6FS2xsypf378iaNhWacZ1G1UplZYWEYfwvuU6Ont96b1roYs CNFwaRrSaKTPCUdBK9DgEHicsKwWCBRQ/0"
     * }
     * <p>
     * 异常时返回为null
     *
     * @param filePath
     * @return
     */
    public static String uploadimg(String filePath) {

        String access_token = WxSubscribeUtil.getServerAccessToken();
        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            log.info("file not exists or not a file!");
            return null;
        }
        if (file.length() / 1024 > 1024 || (!file.getName().toLowerCase().endsWith(".jpg") && !file.getName().endsWith(".png"))) {
            log.info("file formart not png||jpg or size more then 1M!");
            return "";
        }

        HttpURLConnection connection = null;
        try {
            URL url = new URL("https://api.weixin.qq.com/cgi-bin/media/uploadimg?access_token=" + access_token);// 获取accessToken地址的url
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.1; zh-CN; rv:1.9.2.6)");
            String boundary = "----------" + System.currentTimeMillis();
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            //请求正文部分
            StringBuilder sb = new StringBuilder();
            sb.append("--");
            sb.append(boundary);
            sb.append("\r\n");
            sb.append("Content-Disposition: form-data;filename=\"" + file.getName() + "\";filelength=" + file.length() + "\r\n");
            sb.append("Content-Type:image/png\r\n\r\n");
            connection.connect();
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
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
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line = in.readLine();
            in.close();
            connection.disconnect();
            return line;
        } catch (IOException e) {
            if (connection != null) {
                connection.disconnect();
            }
            log.info("image upload error" + e.getMessage());
        }

        return null;
    }

    /**
     * 新增临时素材
     * 临时素材media_id是可复用的。
     * 媒体文件在微信后台保存时间为3天，即3天后media_id失效。
     * 图片（image）: 2M，支持PNG\JPEG\JPG\GIF格式
     * 语音（voice）：2M，播放长度不超过60s，支持AMR\MP3格式
     * 视频（video）：10MB，支持MP4格式
     * 缩略图（thumb）：64KB，支持JPG格式
     * http请求方式: POST
     * https://api.weixin.qq.com/cgi-bin/media/upload?access_token=ACCESS_TOKEN&type=TYPE
     * <p>
     * 正确情况下的返回JSON数据包结果如下：
     * <p>
     * {"type":"TYPE","media_id":"MEDIA_ID","created_at":123456789}
     * <p>
     * 请求异常时将返回null
     *
     * @param filePath
     * @return type
     */
    public static String upload(String filePath, String type) {

        String access_token = WxSubscribeUtil.getServerAccessToken();
        File file = new File(filePath);
        String fileType = file.getName().substring(file.getName().lastIndexOf(".")).toLowerCase();
        if (!file.exists() || !file.isFile()) {
            log.info("file is not exists! or  not a file!");
            return null;
        }
        log.info("type:" + type + "\tfile:" + file.getAbsolutePath() + "\t" + fileType);
        if (type.equals("image") && ".png.jpeg.jpg.gif".indexOf(fileType) != -1 && file.length() / 1024 < 2048) {
            log.info("upload material type: image!");
        } else if (type.equals("voice") && ".mp3.amr".indexOf(fileType) != -1 && file.length() / 1024 < 2048) {
            log.info("upload material type: voice!");
        } else if (type.equals("video") && ".mp4".indexOf(fileType) != -1 && file.length() / 1024 < 10240) {
            log.info("upload material type: video!");
        } else if (type.equals("thumb") && ".jpg".indexOf(fileType) != -1 && file.length() / 1024 < 64) {
            log.info("upload material type: thumb!");
        } else {
            log.info("upload material type: not exists or! more then limit size!");
            return null;
        }
        HttpURLConnection connection = null;
        try {
            URL url = new URL("https://api.weixin.qq.com/cgi-bin/media/upload?access_token=" + access_token + "&type=" + type);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.1; zh-CN; rv:1.9.2.6)");
            String boundary = "----------" + System.currentTimeMillis();
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            //请求正文部分
            StringBuilder sb = new StringBuilder();
            sb.append("--");
            sb.append(boundary);
            sb.append("\r\n");
            sb.append("Content-Disposition: form-data;filename=\"" + file.getName() + "\";filelength=" + file.length() + "\r\n");
            sb.append("Content-Type:image/*\r\n\r\n");
            connection.connect();
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            byte[] imgeBytes = new byte[(int) file.length()];
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
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line = in.readLine();
            in.close();
            connection.disconnect();
            return line;
        } catch (IOException e) {
            if (connection != null) {
                connection.disconnect();
            }
            log.info("image uplaod exception!" + e.getMessage());
        }
        return null;
    }

    public static void main(String[] args) {
        System.out.println(uploadimg("/Users/YYC/Downloads/01.jpg"));
    }


    /**
     * 新增其他类型永久素材
     * http请求方式: POST，需使用https
     * https://api.weixin.qq.com/cgi-bin/material/add_material?access_token=ACCESS_TOKEN&type=TYPE
     * 返回说明
     * {
     * "media_id":MEDIA_ID,
     * "url":URL
     * }
     * 错误情况下的返回JSON数据包示例如下（示例为无效媒体类型错误）：
     * {"errcode":40007,"errmsg":"invalid media_id"}
     */
    public static String add_material(String filePath, String type) {

        String access_token = WxSubscribeUtil.getServerAccessToken();
        File file = new File(filePath);
        String fileType = file.getName().substring(file.getName().lastIndexOf(".")).toLowerCase();
        if (!file.exists() || !file.isFile()) {
            log.info("file is not exists! or  not a file!");
            return null;
        }
        if (type.equals("image") && ".png.jpeg.jpg.gif".indexOf(fileType) != -1 && file.length() / 1024 < 2048) {
            log.info("add_material type: image!");
        } else if (type.equals("voice") && ".mp3.amr".indexOf(fileType) != -1 && file.length() / 1024 < 2048) {
            log.info("add_material type: voice!");
        } else if (type.equals("video") && ".mp4".indexOf(fileType) != -1 && file.length() / 1024 < 10240) {
            log.info("add_material type: video!");
        } else if (type.equals("thumb") && ".jpg".indexOf(fileType) != -1 && file.length() / 1024 < 64) {
            log.info("add_material type: thumb!");
        } else {
            log.info("add_material type: not exists or! more then limit size!");
            return null;
        }
        HttpURLConnection connection = null;
        try {
            URL url = new URL("https://api.weixin.qq.com/cgi-bin/media/upload?access_token=" + access_token + "&type=" + type);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.1; zh-CN; rv:1.9.2.6)");
            String boundary = "----------" + System.currentTimeMillis();
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            //请求正文部分
            StringBuilder sb = new StringBuilder();
            sb.append("--");
            sb.append(boundary);
            sb.append("\r\n");
            sb.append("Content-Disposition: form-data;filename=\"" + file.getName() + "\";filelength=" + file.length() + "\r\n");
            sb.append("Content-Type:image/png\r\n\r\n");
            connection.connect();
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            byte[] imgeBytes = new byte[(int) file.length()];
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
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line = in.readLine();
            in.close();
            connection.disconnect();
            return line;
        } catch (IOException e) {
            if (connection != null) {
                connection.disconnect();
            }
            log.info("image uplaod exception!" + e.getMessage());
        }
        return null;
    }


    /**
     * 新增永久图文素材
     * http请求方式: POST，https协议
     * https://api.weixin.qq.com/cgi-bin/material/add_news?access_token=ACCESS_TOKEN
     * <p>
     * {
     * "articles": [{
     * "title": TITLE,
     * "thumb_media_id": THUMB_MEDIA_ID,
     * "author": AUTHOR,
     * "digest": DIGEST,
     * "show_cover_pic": SHOW_COVER_PIC(0 / 1),
     * "content": CONTENT,
     * "content_source_url": CONTENT_SOURCE_URL
     * },
     * //若新增的是多图文素材，则此处应还有几段articles结构
     * ]
     * }
     * <p>
     * 返回说明
     * {
     * "media_id":MEDIA_ID
     * }
     */
    public static String add_news(String jsonNews) {
        String access_token = WxSubscribeUtil.getServerAccessToken();
        HttpURLConnection connection = null;
        try {
            URL url = new URL("https://api.weixin.qq.com/cgi-bin/material/add_news?access_token=" + access_token);// 获取accessToken地址的url
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestProperty("Content-type", "application/json;charset=UTF-8");
            connection.connect();
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            out.write(jsonNews.getBytes("UTF-8"));
            out.flush();
            out.close();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line = in.readLine();
            in.close();
            connection.disconnect();
            return line;
        } catch (IOException e) {
            if (connection != null) {
                connection.disconnect();
            }
            log.info("add_news  fail " + e.getMessage());
        }
        return null;
    }


    /**
     * 获取永久素材
     * 在新增了永久素材后，开发者可以根据media_id通过本接口下载永久素材。公众号在公众平台官网素材管理模块中新建的永久素材，可通过"获取素材列表"获知素材的media_id。
     * 请注意：临时素材无法通过本接口获取
     * http请求方式: POST,https协议
     * https://api.weixin.qq.com/cgi-bin/material/get_material?access_token=ACCESS_TOKEN
     * <p>
     * 调用示例
     * {
     * "media_id":MEDIA_ID
     * }
     * 接口返回说明
     * 图文素材:
     * {
     * "news_item":
     * [
     * {
     * "title":TITLE,
     * "thumb_media_id"::THUMB_MEDIA_ID,
     * "show_cover_pic":SHOW_COVER_PIC(0/1),
     * "author":AUTHOR,
     * "digest":DIGEST,
     * "content":CONTENT,
     * "url":URL,
     * "content_source_url":CONTENT_SOURCE_URL
     * },
     * //多图文消息有多篇文章
     * ]
     * }
     * 视频消息素材：
     * {
     * "title":TITLE,
     * "description":DESCRIPTION,
     * "down_url":DOWN_URL,
     * }
     * 错误情况下的返回JSON数据包示例如下（示例为无效媒体类型错误）：
     * {"errcode":40007,"errmsg":"invalid media_id"}
     */
    public static String get_material(String jsonMedia_id) {
        String access_token = WxSubscribeUtil.getServerAccessToken();
        HttpURLConnection connection = null;
        try {
            URL url = new URL("https://api.weixin.qq.com/cgi-bin/material/get_material?access_token=" + access_token);// 获取accessToken地址的url
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestProperty("Content-type", "application/json;charset=UTF-8");
            connection.connect();
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            out.write(jsonMedia_id.getBytes("UTF-8"));
            out.flush();
            out.close();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line = in.readLine();
            in.close();
            connection.disconnect();
            return line;
        } catch (IOException e) {
            if (connection != null) {
                connection.disconnect();
            }
            log.info("get_material fail!" + e.getMessage());
        }
        return null;
    }


    /**
     * 删除永久素材
     * 在新增了永久素材后，开发者可以根据本接口来删除不再需要的永久素材，节省空间。
     * http请求方式: POST
     * https://api.weixin.qq.com/cgi-bin/material/del_material?access_token=ACCESS_TOKEN
     * 调用示例
     * {
     * "media_id":MEDIA_ID
     * }
     * 返回说明:正常情况下调用成功时，errcode将为0。
     * {
     * "errcode":ERRCODE,
     * "errmsg":ERRMSG
     * }
     *
     * @param jsonMedia_id
     */
    public static String del_material(String jsonMedia_id) {
        String access_token = WxSubscribeUtil.getServerAccessToken();
        HttpURLConnection connection = null;
        try {
            URL url = new URL("https://api.weixin.qq.com/cgi-bin/material/del_material?access_token=" + access_token);// 获取accessToken地址的url
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestProperty("Content-type", "application/json;charset=UTF-8");
            connection.connect();
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            out.write(jsonMedia_id.getBytes("UTF-8"));
            out.flush();
            out.close();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line = in.readLine();
            in.close();
            connection.disconnect();
            return line;
        } catch (IOException e) {
            if (connection != null) {
                connection.disconnect();
            }
            log.info("del_material fail!" + e.getMessage());
        }
        return null;
    }


    /**
     * 修改永久图文素材
     * 也可以在公众平台官网素材管理模块中保存的图文消息（永久图文素材）
     * http请求方式: POST
     * https://api.weixin.qq.com/cgi-bin/material/update_news?access_token=ACCESS_TOKEN
     * 调用示例
     * {
     * "media_id":MEDIA_ID,
     * "index":INDEX,
     * "articles": {
     * "title": TITLE,
     * "thumb_media_id": THUMB_MEDIA_ID,
     * "author": AUTHOR,
     * "digest": DIGEST,
     * "show_cover_pic": SHOW_COVER_PIC(0 / 1),
     * "content": CONTENT,
     * "content_source_url": CONTENT_SOURCE_URL
     * }
     * }
     * 返回说明
     * {
     * "errcode": ERRCODE,
     * "errmsg": ERRMSG
     * }
     * 正确时errcode的值应为0。
     */
    public static String update_news(String jsonNews) {
        String access_token = WxSubscribeUtil.getServerAccessToken();
        HttpURLConnection connection = null;
        try {
            URL url = new URL("https://api.weixin.qq.com/cgi-bin/material/update_news?access_token=" + access_token);// 获取accessToken地址的url
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestProperty("Content-type", "application/json;charset=UTF-8");
            connection.connect();
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            out.write(jsonNews.getBytes("UTF-8"));
            out.flush();
            out.close();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line = in.readLine();
            in.close();
            connection.disconnect();
            return line;
        } catch (IOException e) {
            if (connection != null) {
                connection.disconnect();
            }
            log.info("update_news fail!" + e.getMessage());
        }
        return null;
    }


    /**
     * 获取素材列表
     * 在新增了永久素材后，开发者可以分类型获取永久素材的列表。
     * http请求方式: POST
     * https://api.weixin.qq.com/cgi-bin/material/batchget_material?access_token=ACCESS_TOKEN
     * 调用示例
     * {
     * "type":TYPE, 	//素材的类型
     * "offset":OFFSET, //从全部素材的该偏移位置开始返回0表示从第一个素材 返回
     * "count":COUNT 	//返回素材的数量，取值在1到20之间
     * }
     * <p>
     * 永久图文消息素材列表的响应如下：
     * {
     * "total_count": TOTAL_COUNT, //该类型的素材的总数
     * "item_count": ITEM_COUNT,   //本次调用获取的素材的数量
     * "item": [{
     * "media_id": MEDIA_ID,
     * "content": {
     * "news_item": [{
     * "title": TITLE,
     * "thumb_media_id": THUMB_MEDIA_ID,
     * "show_cover_pic": SHOW_COVER_PIC(0 / 1),
     * "author": AUTHOR,
     * "digest": DIGEST,
     * "content": CONTENT,
     * "url": URL,
     * "content_source_url": CONTETN_SOURCE_URL
     * },
     * //多图文消息会在此处有多篇文章
     * ]
     * },
     * "update_time": UPDATE_TIME //这篇图文消息素材的最后更新时间
     * },
     * //可能有多个图文消息item结构
     * ]
     * }
     * <p>
     * <p>
     * 其他类型（图片、语音、视频）的返回如下：
     * {
     * "total_count": TOTAL_COUNT,
     * "item_count": ITEM_COUNT,
     * "item": [{
     * "media_id": MEDIA_ID,
     * "name": NAME,//文件名称
     * "update_time": UPDATE_TIME,
     * "url":URL
     * },
     * //可能会有多个素材
     * ]
     * }
     * 错误情况下的返回JSON数据包示例如下（示例为无效媒体类型错误）：
     * {"errcode":40007,"errmsg":"invalid media_id"}
     *
     * @param jsonBatchget_material
     */
    public static String batchget_material(String jsonBatchget_material) {
        String access_token = WxSubscribeUtil.getServerAccessToken();
        HttpURLConnection connection = null;
        try {
            URL url = new URL("https://api.weixin.qq.com/cgi-bin/material/batchget_material?access_token=" + access_token);// 获取accessToken地址的url
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestProperty("Content-type", "application/json;charset=UTF-8");
            connection.connect();
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            out.write(jsonBatchget_material.getBytes("UTF-8"));
            out.flush();
            out.close();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line = in.readLine();
            in.close();
            connection.disconnect();
            return line;
        } catch (IOException e) {
            if (connection != null) {
                connection.disconnect();
            }
            log.info("batchget_material fail!" + e.getMessage());
        }
        return null;
    }


    /**
     * 上传图文消息素材【订阅号与服务号认证后均可用】
     * http请求方式: POST
     * https://api.weixin.qq.com/cgi-bin/media/uploadnews?access_token=ACCESS_TOKEN
     * <p>
     * POST数据示例如下：
     * {
     * "articles": [
     * {
     * "thumb_media_id":"qI6_Ze_6PtV7svjolgs-rN6stStuHIjs9_DidOHaj0Q-mwvBelOXCFZiq2OsIU-p",
     * "author":"xxx",
     * "title":"Happy Day",
     * "content_source_url":"www.qq.com",
     * "content":"content",
     * "digest":"digest",
     * "show_cover_pic":1
     * },
     * {
     * "thumb_media_id":"qI6_Ze_6PtV7svjolgs-rN6stStuHIjs9_DidOHaj0Q-mwvBelOXCFZiq2OsIU-p",
     * "author":"xxx",
     * "title":"Happy Day",
     * "content_source_url":"www.qq.com",
     * "content":"content",
     * "digest":"digest",
     * "show_cover_pic":0
     * }
     * ]
     * }
     * <p>
     * 返回数据示例（正确时的JSON返回结果）：
     * {
     * "type":"news",
     * "media_id":"CsEf3ldqkAYJAU6EJeIkStVDSvffUJ54vqbThMgplD-VJXXof6ctX5fI6-aYyUiQ",
     * "created_at":1391857799
     * }
     * <p>
     * 请求异常时将返回null
     *
     * @param jsonNews
     * @return
     */
    public static String uploadnews(String jsonNews) {
        String access_token = WxSubscribeUtil.getServerAccessToken();
        HttpURLConnection connection = null;
        try {
            URL url = new URL("https://api.weixin.qq.com/cgi-bin/media/uploadnews?access_token=" + access_token);// 获取accessToken地址的url
            connection = (HttpURLConnection) url.openConnection();

            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestProperty("Content-type", "application/json;charset=UTF-8");
            connection.connect();
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            out.write(jsonNews.getBytes("UTF-8"));
            out.flush();
            out.close();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line = in.readLine();
            in.close();
            connection.disconnect();
            return line;
        } catch (IOException e) {
            if (connection != null) {
                connection.disconnect();
            }
            log.info("uploadnews  fail " + e.getMessage());
        }
        return null;
    }


    /**
     * 上传图文消息素材格式字符串
     *
     * @param newsMaterial
     * @return
     */
    public static String parseNewsMaterial(List<NewsMaterial> newsMaterial) {
        StringBuffer sb = new StringBuffer();
        sb.append("{");
        sb.append("\"articles\":[");
        for (int i = 0; i < newsMaterial.size(); i++) {
            sb.append("{");
            sb.append("	\"thumb_media_id\":\"" + newsMaterial.get(i).getThumb_media_id() + "\",");
            sb.append("	\"author\":\"" + newsMaterial.get(i).getAuthor() + "\",");
            sb.append("	\"title\":\"" + newsMaterial.get(i).getTitle() + "\",");
            sb.append("	\"content_source_url\":\"" + newsMaterial.get(i).getContent_source_url() + "\",");
            sb.append("	\"content\":\"" + newsMaterial.get(i).getContent() + "\",");
            sb.append("	\"digest\":\"" + newsMaterial.get(i).getDigest() + "\",");
            sb.append("	\"show_cover_pic\":" + newsMaterial.get(i).getShow_cover_pic());
            sb.append("}");
        }
        sb.append("]");
        sb.append("}");
        log.info("消息json: " + sb.toString());
        return sb.toString();

    }
}
