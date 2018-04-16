package com.talelife.myproject.web;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.talelife.myproject.service.UserService;
import com.talelife.util.BusinessException;

import ch.qos.logback.classic.Level;

@RestController
@RequestMapping("/wechat")
public class WechatController extends BaseController{
	
	private static final String TOKEN_URL = "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=%s&corpsecret=%s";
	private static final String USER_BASE_INFO_URL = "https://qyapi.weixin.qq.com/cgi-bin/user/getuserinfo?access_token=%s&code=%s";
	private static final String USER_DETAIL_INFO_URL = "https://qyapi.weixin.qq.com/cgi-bin/user/get?access_token=%s&userid=%s";
	private static String accessToken = null;	
	private static final Object lock = new Object();
	
	@Resource
	private UserService userService;
	
	
	
	@RequestMapping("/login")
    public void login(HttpServletRequest request) {
		//1.access token
		String code = request.getParameter("code");
		logger.info("access code->%s", code);
		
		UserInfo userToken = new UserInfo();
		
		synchronized (lock) {
			if(accessToken==null){
				userToken = get(String.format(TOKEN_URL, "ww894f16a05bf6e59b","eoZcHvtAINp7riMGYKKK366kklG-gLbVDkiQDOEWzTk"), UserInfo.class);
				if(userToken.getErrcode()!=0){
					logger.info("init access token false->%s",userToken.getErrmsg());
				}else{
					logger.info("init access token->%s",userToken.getAccessToken());
				}
			}
		}
		
		//2.get base userinfo
		UserInfo userBaseinfo = get(String.format(USER_BASE_INFO_URL, userToken.getAccessToken(),code),UserInfo.class);
		logger.info("user baseinfo->%s",JSON.toJSONString(userBaseinfo));
		if(userBaseinfo.getErrcode()==42001){
			//token过期，重新获取token
			userToken = get(String.format(TOKEN_URL, "ww894f16a05bf6e59b","eoZcHvtAINp7riMGYKKK366kklG-gLbVDkiQDOEWzTk"), UserInfo.class);
			if(userToken.getErrcode()!=0){
				logger.info("init access token false->%s",userToken.getErrmsg());
			}else{
				logger.info("get access token->%s",userToken.getAccessToken());
			}
		}
		
		//3.get user detail
		UserInfo userDetail = get(String.format(USER_DETAIL_INFO_URL,userToken.getAccessToken(),userBaseinfo.getUserid()),UserInfo.class);
		if(userDetail.getErrcode()!=0){
			logger.error("get userDetail false errorMsg->"+userDetail.getErrmsg());
		}else{
			logger.info("user detail->%s",JSON.toJSONString(userDetail));
			logger.info("mobile->"+userDetail.getMobile());
		}
		
		
    }
	
	private static <T> T get(String url,Class<T> clazz){
		StringBuilder r = new StringBuilder();
		try {
			
			HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
			connection.setRequestMethod("GET");
			connection.connect();
			int responseCode = connection.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				InputStream inputStream = connection.getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
				for (String line = null; (line = reader.readLine()) != null;) {
					r.append(line);
				}
			} 
		} catch (Exception e) {
			throw new BusinessException(e);
		}
		return JSON.parseObject(r.toString(),clazz);
	}
	
	private static class UserInfo{
		private String accessToken;
		private int errcode;
		private String errmsg;
		private String userid;
		private String name;
		private String mobile;
		private String email;
		private String avatar;
		public int getErrcode() {
			return errcode;
		}
		public void setErrcode(int errcode) {
			this.errcode = errcode;
		}
		public String getErrmsg() {
			return errmsg;
		}
		public void setErrmsg(String errmsg) {
			this.errmsg = errmsg;
		}
		public String getUserid() {
			return userid;
		}
		public void setUserid(String userid) {
			this.userid = userid;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getMobile() {
			return mobile;
		}
		public void setMobile(String mobile) {
			this.mobile = mobile;
		}
		public String getEmail() {
			return email;
		}
		public void setEmail(String email) {
			this.email = email;
		}
		public String getAvatar() {
			return avatar;
		}
		public void setAvatar(String avatar) {
			this.avatar = avatar;
		}
		public String getAccessToken() {
			return accessToken;
		}
		public void setAccessToken(String accessToken) {
			this.accessToken = accessToken;
		}
		
	}
}
