package org.mintleaf.modules.core.controller;

import org.beetl.sql.core.engine.PageQuery;
import org.mintleaf.modules.core.dao.CoreButtonDao;
import org.mintleaf.modules.core.entity.CoreButton;
import org.mintleaf.utils.UUIDUtils;
import org.mintleaf.vo.PageFrame;
import org.mintleaf.vo.ResultMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * @Author: MengchuZhang
 * @Date: 2018/9/2 11:11
 * @Version 1.0
 */
@Controller
@RequestMapping("coreButton")
public class CoreButtonController {
    @Autowired
    CoreButtonDao coreButtonDao;

    /**
     * 进入列表页面
     * @return
     */
    @RequestMapping(value="index.html")
    public ModelAndView index(){
        ModelAndView view =new ModelAndView("modules/core/corebutton/index.html");
        return view;
    }

    /**
     * 进入编辑页面
     * @return
     */
    @RequestMapping(value="edit.html")
    public ModelAndView edit(){
        ModelAndView view =new ModelAndView("modules/core/corebutton/edit.html");
        return view;
    }

    /**
     * 进入新增页面
     * @return
     */
    @RequestMapping(value="add.html")
    public ModelAndView add(){
        ModelAndView view =new ModelAndView("modules/core/corebutton/add.html");
        return view;
    }

    /**
     * 添加操作
     * @param coreButton
     * @return
     */
    @RequestMapping(value = "add.do",method = {RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public ResultMsg add(CoreButton coreButton) {
//        String uuid=UUIDUtils.getUUID();
        coreButton.setId(new Random().nextInt());
        coreButtonDao.insert(coreButton);
        ResultMsg result=new ResultMsg();
        result.setData(coreButton);
        return result;
    }

    /**
     * 删除操作
     * @param coreButton
     * @return
     */
    @RequestMapping(value = "delete.do",method = {RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public ResultMsg delete(CoreButton coreButton) {
        coreButtonDao.deleteById(coreButton.getId());
        ResultMsg result=new ResultMsg();
        return result;
    }

    /**
     * 批量删除操作
     * @param id
     * @return
     */
    @RequestMapping(value = "deleteBatch.do",method = {RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public ResultMsg deleteBatch(String id) {
        coreButtonDao.deleteByIds(id.split(","));
        ResultMsg result=new ResultMsg();
        return result;
    }

    /**
     * 编辑操作
     * @param coreButton
     * @return
     */
    @RequestMapping(value = "edit.do",method = {RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public ResultMsg update(CoreButton coreButton) {
        coreButtonDao.updateTemplateById(coreButton);
        ResultMsg result=new ResultMsg();
        result.setData(coreButton);
        return result;
    }

    /**
     * 查询单条数据操作
     * @param coreButton
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "findById.json",method = {RequestMethod.POST,RequestMethod.GET}) //请求类型
    @ResponseBody
    public ResultMsg updateCoreMenu(CoreButton coreButton) throws Exception{
        CoreButton menu=coreButtonDao.single(coreButton.getId());
        ResultMsg result=new ResultMsg();
        result.setData(menu);
        return result;

    }

    /**
     * 查询全部数据操作
     * @return
     */
    @RequestMapping(value="all.json")
    @ResponseBody
    public ResultMsg all(){
        List<CoreButton> coreButtons =coreButtonDao.all();
        ResultMsg result=new ResultMsg();
        result.setData(coreButtons);
        return  result;
    }

    /**
     * 查询分页数据操作
     * @param coreButton
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping(value="page.json",method = {RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public ResultMsg page(@ModelAttribute CoreButton coreButton, Long pageNum, Long pageSize){
        PageQuery<CoreButton> pageQuery=new PageQuery<CoreButton>();
        pageQuery.setPageSize(Long.valueOf(pageSize));
        pageQuery.setPageNumber(Long.valueOf(pageNum));
        pageQuery.setParas(coreButton);
        coreButtonDao.templatePage(pageQuery);
        PageFrame pageFrame=new PageFrame();
        pageFrame.setList(pageQuery.getList());
        pageFrame.setPageNum(Long.valueOf(pageNum));
        pageFrame.setPageSize(Long.valueOf(pageSize));
        pageFrame.setPages(pageQuery.getTotalPage());
        pageFrame.setTotal(pageQuery.getTotalRow());
        ResultMsg result=new ResultMsg();
        result.setData(pageFrame);
        return  result;
    }
}
