package net.ahwater.web;


import net.ahwater.bean.*;
import net.ahwater.service.WeixinService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by Administrator on 2017/11/10.
 * Implemented by Reeye on 2017/11/11
 * 微信的相关接口
 */
@RestController
@RequestMapping("/wx")
@CrossOrigin
public class WxController {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Value("${reportImgFolderPath}")
    private String reportImgFolderPath;

    @Value("${boardImgFolderPath}")
    private String boardImgFolderPath;

    @Autowired
    private WeixinService weixinService;

    /**
     * 查询安徽省下所有的市
     */
    @RequestMapping("/listAddvcdOfCity")
    public List<Hh_addvcd> listAddvcdOfCity() {
        return weixinService.listAddvcdOfCity();
    }

    /**
     * 根据市级别区划码 查找该市下所有的区县
     */
    @RequestMapping("/listAddvcdOfCountyByAddvcd")
    public List<Hh_addvcd> listAddvcdOfCountyByAddvcd(@RequestParam String addvcd) {
        return weixinService.listAddvcdOfCountyByAddvcd(addvcd);
    }

    /**
     * 根据区划码查找该区划下 所有的河段
     * @param addvcd
     * @return
     */
    @RequestMapping("/listRiverHeadByAddvcd")
    public List<Hh_river_head> listRiverHeadByAddvcd(@RequestParam String addvcd) {
        return weixinService.listRiverHeadByAddvcd(addvcd);
    }

    /**
     * 公众拍照上报信息
     */
    @PostMapping("/doReport")
    @Transactional
    public ResponseResult doReport(@RequestParam MultipartFile[] files, Hh_repprob repprob) {
        System.out.println("上报开始");
        int rptRes = 0;
        int picRes = 0;
        try {
            repprob.setReptm(new Date());
            System.out.println("插入时间");
            rptRes = weixinService.insertReport(repprob);
            System.out.println("插入一条记录成功");
            for (int i = 0; i < files.length; i++) {
                MultipartFile file = files[i];
                String[] temp = file.getOriginalFilename().split("\\.");
                String suffix = temp[temp.length - 1];
                if (suffix == null || suffix.trim().equals("")) {
                    break;
                }
                String fileName = UUID.randomUUID().toString() + "." + suffix;
                String filePath = this.reportImgFolderPath + File.separator + fileName;
                // 转存文件
                File dir = new File(reportImgFolderPath);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                file.transferTo(new File(filePath));
                log.info("保存文件成功: " + filePath);
                picRes += weixinService.insertReportPic(new Hh_reppic(repprob.getRepno(), "/wx/reportPic/" + fileName, new Date()));
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.wrong(e.getMessage());
        }
        String resMsg = ((rptRes == 1) ? "上报信息插入成功" : "上报信息插入失败")
                + ";"
                + ((picRes > 0) ? "上报照片插入成功" + picRes + "张" : "上报信息插入失败");
        return ResponseResult.correct(resMsg);
    }

    /**
     * 根据河段(湖段)编号查找对应的上报记录
     * @param rlrcnos 河段(湖段)的编号 数组类型
     * @param stm 开始时间
     * @param etm 结束时间
     * @return
     */
    @RequestMapping("/listReport")
    public List<Hh_repprob> listReport(
            @RequestParam(value = "rlrcnos[]", required = false) List<Integer> rlrcnos,
            @RequestParam(value = "stm", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date stm,
            @RequestParam(value = "etm", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date etm) {
        return weixinService.listReport(rlrcnos, stm, etm);
    }

    /**
     * 获取照片
     */
    @RequestMapping(value = "/reportPic/{picFileName}.{picFileSuffix}", method = RequestMethod.GET)
    public ResponseResult reportPic(
            @PathVariable String picFileName,
            @PathVariable String picFileSuffix,
            HttpServletResponse response) {
        String filePath = reportImgFolderPath + File.separator + picFileName + "." + picFileSuffix;
        log.info("Reeye>" + filePath);
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        try {
            File file = new File(filePath);
            if (file.exists()) {
                // 设置强制下载不打开
                // response.setContentType("application/force-download");
                // 设置文件名
                // response.addHeader("Content-Disposition", "attachment;fileName=" + name);
                response.addHeader("Accept-Ranges", "bytes"); // 设置大小
                response.addHeader("Content-Length", (new Long(file.length())).toString()); // 设置大小
                response.addHeader("Content-Type", "image/jpeg;image/png;image/jpg;image/gif");
                byte[] buffer = new byte[1024];
                fis = new FileInputStream(file);
                bis = new BufferedInputStream(fis);
                OutputStream os = response.getOutputStream();
                int i = bis.read(buffer);
                while (i != -1) {
                    os.write(buffer, 0, i);
                    i = bis.read(buffer);
                }
            } else {
                return ResponseResult.wrong("照片路径不存在");
            }
            return null;
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseResult.wrong(e.getMessage());
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 查询在用的上报类型
     */
    @RequestMapping("/listReportType")
    public List<Hh_reptp> listReportType() {
        return weixinService.listReportType();
    }

    /**
     * 新增公示牌上报记录
     * @param billboard
     * @return
     */
    @PostMapping("/insertBillboard")
    public ResponseResult insertOne(@RequestParam MultipartFile[] files, Hh_billboard billboard) {
        int res = 0;
        try {
            String uuid = UUID.randomUUID().toString();
            for (int i = 0; i < files.length; i++) {
                MultipartFile file = files[i];
                String[] temp = file.getOriginalFilename().split("\\.");
                String suffix = temp[temp.length - 1];
                if (suffix == null || suffix.trim().equals("")) {
                    break;
                }
                String fileName = uuid + "_" + i + "." + suffix;
                String filePath = this.boardImgFolderPath + File.separator + fileName;
                // 转存文件
                File dir = new File(boardImgFolderPath);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                file.transferTo(new File(filePath));
                log.info("保存文件成功: " + filePath);
                if (i == 0) {
                    billboard.setApath("/wx/boardPic/" + fileName);
                }
                if (i == 1) {
                    billboard.setBpath("/wx/boardPic/" + fileName);
                }
            }
            res = weixinService.insertBillboard(billboard);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res > 0 ? ResponseResult.correct("提交成功") : ResponseResult.wrong("出错了, 提交失败");
    }

    /**
     * 获取公示牌照片
     */
    @RequestMapping(value = "/boardPic/{picFileName}.{picFileSuffix}", method = RequestMethod.GET)
    public ResponseResult boardPic(
            @PathVariable String picFileName,
            @PathVariable String picFileSuffix,
            HttpServletResponse response) {
        String filePath = boardImgFolderPath + File.separator + picFileName + "." + picFileSuffix;

        FileInputStream fis = null;
        BufferedInputStream bis = null;
        try {
            File file = new File(filePath);
            if (file.exists()) {
                // 设置强制下载不打开
                // response.setContentType("application/force-download");
                // 设置文件名
                // response.addHeader("Content-Disposition", "attachment;fileName=" + name);
                response.addHeader("Accept-Ranges", "bytes"); // 设置大小
                response.addHeader("Content-Length", (new Long(file.length())).toString()); // 设置大小
                response.addHeader("Content-Type", "image/jpeg;image/png;image/jpg;image/gif");
                byte[] buffer = new byte[1024];
                fis = new FileInputStream(file);
                bis = new BufferedInputStream(fis);
                OutputStream os = response.getOutputStream();
                int i = bis.read(buffer);
                while (i != -1) {
                    os.write(buffer, 0, i);
                    i = bis.read(buffer);
                }
            } else {
                return ResponseResult.wrong("照片路径不存在");
            }
            return null;
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseResult.wrong(e.getMessage());
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }


    /**
     * 根据区划码和行政等级 查询公示牌上报记录
     * @param addvcdStartWith
     * @param level
     * @return
     */
    @RequestMapping("/listAllByAddvcdAndLevel")
    public List<Hh_billboard> listAllByAddvcdAndLevel(@RequestParam String addvcdStartWith, @RequestParam String level) {
        return weixinService.listAllByAddvcdAndLevel(addvcdStartWith, level);
    }

}
