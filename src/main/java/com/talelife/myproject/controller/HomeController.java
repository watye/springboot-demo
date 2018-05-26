package com.talelife.myproject.controller;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {
	
	@RequestMapping("/")
    public String home() {
        return "this is home!";
    }

	@RequestMapping("/login")
    public String login(HttpServletRequest request) {
		request.getSession().setAttribute("loginUserName", request.getParameter("name"));
        return "login success";
    }
	
	@RequestMapping("/get_login_user")
    public String getLoginUser(HttpServletRequest request) {
		String userName = (String)request.getSession().getAttribute("loginUserName");
        return userName;
    }
	
	
	@RequestMapping("/suite/receive")
    public String suiteReceive(HttpServletRequest request) {
		Enumeration<String> names = request.getParameterNames();
		while(names.hasMoreElements()){
			System.out.println("============>"+names.nextElement());
		}
		
		Map<String, String[]> maps = request.getParameterMap();
		Iterator<Entry<String, String[]>> it = maps.entrySet().iterator();
		while(it.hasNext()){
			Entry<String, String[]> item =	it.next();
			System.out.println("============>key="+item.getKey()+",value="+Arrays.toString(item.getValue()));
		}
		
		return "success";
    }
}
