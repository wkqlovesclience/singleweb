package com.gome.stage.web;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * 因为不能修改t_titles表中的旧数据，须考虑newtopic/yyyyMMdd/titleId的数据
 * 但是移动端和PC端的路径都是newtopic/yyyyMMdd/titleId，只有域名不一致，只能通过域名来区分redirect301的跳转路径，以下两个方法皆是如此。
 * redirect301是重写了springmvc的viewResolver，通过前缀“301redirect：”，实现路径的跳转。
 *
 */

@Controller
@RequestMapping(value = "/newtopicnew")
public class NewTopicController {

    @RequestMapping(value = "/{createTime}/{titleId}", method = RequestMethod.GET)
    public ModelAndView redirectToTopicSummarize(@PathVariable(value = "createTime",required = true)String createTime,@PathVariable(value = "titleId",required = true)String titleId,HttpServletRequest request){
        String host = request.getHeader("host");
        if (host.contains("m.gome.com.cn")){
            return new ModelAndView("redirect301:/mtopic/"+titleId+"/");
        }else {
            return new ModelAndView("redirect301:/topic/"+titleId+"/");
        }

    }

    @RequestMapping(value = "/{createTime}/{titleId}/{condition}", method = RequestMethod.GET)
    public ModelAndView redirectToTopicDetail(@PathVariable(value = "createTime",required = true)String createTime, @PathVariable(value = "titleId",required = true)String titleId, @PathVariable(value = "condition",required = true)String condition, HttpServletRequest request){
        String host = request.getHeader("host");
        if (host.contains("m.gome.com.cn")){
            return new ModelAndView("redirect301:/mtopic/"+titleId+"/"+condition+".html");
        }else {
            return new ModelAndView("redirect301:/topic/"+titleId+"/"+condition+".html");        }


    }

}
