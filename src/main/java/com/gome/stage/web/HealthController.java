package com.gome.stage.web;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.gome.stage.logger.GomeLogger;
import com.gome.stage.logger.GomeLoggerFactory;

/**
 * 
 * 健康页
 * @author zhoutianting
 */
@Controller
@RequestMapping(value="/health")
public class HealthController {
	protected GomeLogger logger = GomeLoggerFactory.getLogger(HealthController.class);
	
	@RequestMapping(value = "/state", method = RequestMethod.GET)
	public void getHealthyState(HttpServletRequest request,HttpServletResponse response){
		
		try {
			response.getWriter().print(true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			response.setStatus(404);
		}
		
	}
	
}
