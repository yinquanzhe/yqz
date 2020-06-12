package org.mintleaf.modules.video.controller;

import org.apache.shiro.authz.annotation.RequiresRoles;
import org.beetl.sql.core.engine.PageQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.mintleaf.modules.video.dao.StShipinBDao;
import org.mintleaf.modules.video.entity.StShipinB;
import org.mintleaf.vo.PageEasyui;
import org.mintleaf.vo.ResultMsg;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Random;
/**
 * 视频监视相关控制器
 * @Author: MengchuZhang
 * @Date: 2018/8/20 14:51
 * @Version 1.0
 */
@Controller
@RequestMapping("stshipinb")
public class StShipinBController {

    @Autowired
    StShipinBDao stShipinBDao;

    /**
     * 进入首页
     * @return
     */
    @RequestMapping(value="index.html")
    public ModelAndView index(){
        ModelAndView view =new ModelAndView("modules/video/stshipinb/index.html");
        return view;
    }

    /**
     * 进入视频管理列表页面
     * @return
     */
    @RequestMapping(value="list.html")
    public ModelAndView list(){
        ModelAndView view =new ModelAndView("modules/video/stshipinb/list.html");
        return view;
    }

    /**
     * 进入播放方式页面
     * @param videoId
     * @return
     */
    @RequestMapping(value="player.html",method = {RequestMethod.GET})
    public ModelAndView player(String videoId){
        ModelAndView view =new ModelAndView("modules/video/stshipinb/player.html");
        view.addObject("videoId", videoId);
        return view;
    }

    /**
     * 进入ckplayer播放页面
     * @param videoId
     * @return
     */
    @RequestMapping(value="ckplayer.html",method = {RequestMethod.GET})
    public ModelAndView ckplayer(String videoId){
        ModelAndView view =new ModelAndView("modules/video/stshipinb/ckplayer.html");
        view.addObject("videoId", videoId);
        return view;
    }

    /**
     * 进入hlslayer播放页面
     * @param videoId
     * @return
     */
    @RequestMapping(value="hlsplayer.html",method = {RequestMethod.GET})
    public ModelAndView hlsplayer(String videoId){
        ModelAndView view =new ModelAndView("modules/video/stshipinb/hlsplayer.html");
        view.addObject("videoId", videoId);
        return view;
    }

    /**
     * 添加操作
     * @param stShipinB
     * @return
     */
    @RequestMapping(value = "add.do",method = {RequestMethod.POST}) //请求类型
    public ResponseEntity<Object> addStShipinB(StShipinB stShipinB) throws Exception{
        stShipinB.setId(Long.toString(new Random().nextLong())); //生成随机数
        String result="1";
        try{
            stShipinBDao.insert(stShipinB);
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.ok(ResultMsg.fail("操作出错："+e.getMessage()));
        }
        return ResponseEntity.ok(result);
    }

    /**
     * 删除操作
     * @param stShipinB
     * @return
     */
    @RequestMapping(value="delete.do",method = {RequestMethod.POST})
    public ResponseEntity<Object> deleteStShipinB(StShipinB stShipinB)throws Exception{
        String result="1";
        try{
            stShipinBDao.deleteById(stShipinB.getId());

        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.ok(ResultMsg.fail("操作出错："+e.getMessage()));
        }
        return ResponseEntity.ok(ResultMsg.ok());
    }

    /**
     * 更新操作
     * @param stShipinB
     * @return
     */
    @RequestMapping(value = "update.do",method = {RequestMethod.POST}) //请求类型
    public ResponseEntity<Object> updateStShipinB(StShipinB stShipinB) throws Exception{
        String result="1";
        try{
            stShipinBDao.updateById(stShipinB);
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.ok(ResultMsg.fail("操作出错："+e.getMessage()));
        }
        return ResponseEntity.ok(result);
    }

    /**
     * 查询全部数据操作
     * @return
     */
    @RequestMapping(value="all.json")
    @ResponseBody
    public List<StShipinB> all(){
        List<StShipinB> stShipins =stShipinBDao.all();
        return  stShipins;
    }

    /**
     * 查询分页数据操作
     * @param request
     * @return
     */
    @RequestMapping(value="page.json")
    public ResponseEntity<Object> page(HttpServletRequest request){
        String page=request.getParameter("page");
        String limit=request.getParameter("rows");
        PageQuery<StShipinB> pageQuery=new PageQuery<StShipinB>();
        pageQuery.setPageSize(Long.valueOf(limit));
        pageQuery.setPageNumber(Long.valueOf(page));
        stShipinBDao.templatePage(pageQuery);
        PageEasyui pageEasyui=new PageEasyui();
        pageEasyui.setTotal(pageQuery.getTotalRow());
        pageEasyui.setRows(pageQuery.getList());
        PageEasyui  stShipins =pageEasyui;
        return  ResponseEntity.ok(stShipins);
    }

}
