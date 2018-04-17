package com.talelife.myproject.web.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.talelife.myproject.model.User;
import com.talelife.myproject.service.UserService;
import com.talelife.myproject.web.BaseController;
import com.talelife.util.BusinessException;

@RestController
@RequestMapping("/wechat")
public class WechatApi extends BaseController{
	
	private static final String TOKEN_URL = "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=%s&corpsecret=%s";
	private static final String USER_BASE_INFO_URL = "https://qyapi.weixin.qq.com/cgi-bin/user/getuserinfo?access_token=%s&code=%s";
	private static final String USER_DETAIL_INFO_URL = "https://qyapi.weixin.qq.com/cgi-bin/user/get?access_token=%s&userid=%s";
	private static String accessToken = null;	
	private static final Object lock = new Object();
	private static long expiresTime = -1L;
	
	
	@RequestMapping("/login")
    public void login(HttpServletRequest request,HttpServletResponse response) throws IOException {
		//1.获取code
		String code = request.getParameter("code");
		logger.info("access code->{}", code);
		Objects.requireNonNull(code, "code不能为空");
		
		//2.取微信用户信息
		WechatUserResult userinfo = getWechatUserInfo(code);
		if(userinfo.getErrcode()==0){
			//3.业务系统认证
			User user = getUser(userinfo.getMobile());
			response.sendRedirect("http://116.62.246.148:8082/index/index.html?type=1&loginNo=haojiawei");
			return;
		}else{
			throw new BusinessException(userinfo.getErrmsg());
		}
		
    }

	private User getUser(String mobile) {
		Objects.requireNonNull(mobile, "企业微信绑定的手机号码不能为空");
		User user = new User();
		return user;
	}

	private static WechatUserResult getWechatUserInfo(String code) {
		
		logger.info("expiresTime->{}",JSON.toJSONString(expiresTime));
		
		//1、获取token：1)初次，2)token过期
		synchronized (lock) {
			if(accessToken==null || (expiresTime != -1L && expiresTime<System.currentTimeMillis())){
				getToken();
			}
		}
		
		//2.get base userinfo
		WechatUserResult userBaseinfo = getUserBaseinfo(code);
		
		//3.get user detail
		Objects.requireNonNull(userBaseinfo.getUserid(), "企业微信获取的用户id为空");
		
		return getUserDetail(userBaseinfo);
	}

	private static WechatUserResult getUserDetail(WechatUserResult userBaseinfo) {
		WechatUserResult userDetail = get(String.format(USER_DETAIL_INFO_URL,accessToken,userBaseinfo.getUserid()),WechatUserResult.class);
		if(userDetail.getErrcode()!=0){
			logger.error("wechatApi get userDetail false errorMsg->{}",userDetail.getErrmsg());
		}else{
			logger.info("wechatApi user detail->{}",JSON.toJSONString(userDetail));
			logger.info("wechatApi mobile->{}",userDetail.getMobile());
		}
		return userDetail;
	}

	private static WechatUserResult getUserBaseinfo(String code) {
		Objects.requireNonNull(code, "企业微信获取到的code为空");
		WechatUserResult userBaseinfo = get(String.format(USER_BASE_INFO_URL, accessToken,code),WechatUserResult.class);
		logger.info("wechatApi user baseinfo->{}",JSON.toJSONString(userBaseinfo));
		if(userBaseinfo.getErrcode()==42001){
			//token过期，重新获取token
			synchronized (lock) {
				getToken();
				userBaseinfo = get(String.format(USER_BASE_INFO_URL, accessToken,code),WechatUserResult.class);
			}
		}
		
		if(userBaseinfo.getErrcode()!=0){
			logger.error("wechatApi get user baseinfo false errmsg->{}",userBaseinfo.getErrmsg());
		}
		return userBaseinfo;
	}
	
	private static WechatUserResult getToken(){
		WechatUserResult newToken = get(String.format(TOKEN_URL, "ww894f16a05bf6e59b","eoZcHvtAINp7riMGYKKK366kklG-gLbVDkiQDOEWzTk"), WechatUserResult.class);
		if(newToken.getErrcode()!=0){
			logger.info("wechatApi get access token false->{}",newToken.getErrmsg());
			throw new BusinessException(String.format("获取企业微信token失败：%s",newToken.getErrmsg()));
		}else{
			accessToken = newToken.getAccessToken();
			if(Objects.nonNull(newToken.getExpiresIn()) && newToken.getExpiresIn()>0){
				expiresTime = System.currentTimeMillis() + newToken.getExpiresIn().longValue() - 900;
			}
			logger.info("token expiresTime->{}",expiresTime);
			logger.info("wechatApi get access token->{}",JSON.toJSONString(newToken));
		}
		return newToken;
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
	
	private static class WechatUserResult{
		private String accessToken;
		private int errcode = -1;
		private String errmsg;
		private String userid;
		private String name;
		private String mobile;
		private String email;
		private String avatar;
		private Long expiresIn;
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
		public Long getExpiresIn() {
			return expiresIn;
		}
		public void setExpiresIn(Long expiresIn) {
			this.expiresIn = expiresIn;
		}
		
	}
}
