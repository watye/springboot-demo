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
	
	
	@RequestMapping("/login")
    public void login(HttpServletRequest request,HttpServletResponse response) throws IOException {
		//1.access token
		String code = request.getParameter("code");
		logger.info("access code->{}", code);
		Objects.requireNonNull(code, "code不能为空");
		
		//2.取微信用户信息
		WechatUserInfo userinfo = getWechatUserInfo(code);
		if(userinfo.getErrcode()==0){
			logger.info("access mobile->{}", userinfo.getMobile());
			//3.业务系统认证
			if(checkUser(userinfo.getMobile())){
				//登录系统
				response.sendRedirect("http://116.62.246.148:8082/index/index.html?loginNo=s_1351749539628934");
				return;
			}else{
				throw new BusinessException(String.format("找不到手机号为%s的用户",userinfo.getMobile()));
			}
		}else{
			throw new BusinessException(userinfo.getErrmsg());
		}
		
    }

	private boolean checkUser(String mobile) {
		Objects.requireNonNull(mobile, "微信绑定的手机号码不能为空");
		//TODO
		return true;
	}

	private WechatUserInfo getWechatUserInfo(String code) {
		
		WechatUserInfo userDetail = new WechatUserInfo();
		
		synchronized (lock) {
			if(accessToken==null){
				WechatUserInfo userToken = get(String.format(TOKEN_URL, "ww894f16a05bf6e59b","eoZcHvtAINp7riMGYKKK366kklG-gLbVDkiQDOEWzTk"), WechatUserInfo.class);
				if(userToken.getErrcode()!=0){
					logger.info("init access token false->{}",userToken.getErrmsg());
					userDetail.setErrmsg(userToken.getErrmsg());
					return userDetail;
				}else{
					accessToken = userToken.getAccessToken();
					logger.info("init access token->{}",userToken.getAccessToken());
				}
			}
		}
		
		//2.get base userinfo
		WechatUserInfo userBaseinfo = get(String.format(USER_BASE_INFO_URL, accessToken,code),WechatUserInfo.class);
		logger.info("user baseinfo->{}",JSON.toJSONString(userBaseinfo));
		if(userBaseinfo.getErrcode()==42001){
			//token过期，重新获取token
			synchronized (lock) {
				WechatUserInfo newToken = get(String.format(TOKEN_URL, "ww894f16a05bf6e59b","eoZcHvtAINp7riMGYKKK366kklG-gLbVDkiQDOEWzTk"), WechatUserInfo.class);
				if(newToken.getErrcode()!=0){
					logger.info("init access token false->{}",newToken.getErrmsg());
					userDetail.setErrmsg(newToken.getErrmsg());
					return userDetail;
				}else{
					logger.info("get access token->{}",newToken.getAccessToken());
					accessToken = newToken.getAccessToken();
					userBaseinfo = get(String.format(USER_BASE_INFO_URL, newToken.getAccessToken(),code),WechatUserInfo.class);
				}
			}
		}
		
		if(userBaseinfo.getErrcode()!=0){
			logger.error("get user baseinfo false errmsg",userBaseinfo.getErrmsg());
		}
		
		//3.get user detail
		userDetail = get(String.format(USER_DETAIL_INFO_URL,accessToken,userBaseinfo.getUserid()),WechatUserInfo.class);
		if(userDetail.getErrcode()!=0){
			logger.error("get userDetail false errorMsg->{}",userDetail.getErrmsg());
			userDetail.setErrmsg(userDetail.getErrmsg());
			return userDetail;
		}else{
			logger.info("user detail->{}",JSON.toJSONString(userDetail));
			logger.info("mobile->"+userDetail.getMobile());
		}
		
		return userDetail;
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
	
	private static class WechatUserInfo{
		private String accessToken;
		private int errcode = -1;
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
