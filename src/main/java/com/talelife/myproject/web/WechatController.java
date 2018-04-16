package com.talelife.myproject.web;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.talelife.myproject.service.UserService;
import com.talelife.util.BusinessException;

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
		logger.info("access code->{}", code);
		Objects.requireNonNull(code, "code为空");
		
		WechatUserInfo userinfo = getWechatUserInfo(code);
		if(userinfo.getErrcode()==0){
			logger.info("access code->{}", userinfo.getMobile());
		}else{
			throw new BusinessException(userinfo.getErrmsg());
		}
		
    }

	private WechatUserInfo getWechatUserInfo(String code) {
		
		WechatUserInfo userDetail = new WechatUserInfo();
		WechatUserInfo userToken = new WechatUserInfo();
		
		synchronized (lock) {
			if(accessToken==null){
				userToken = get(String.format(TOKEN_URL, "ww894f16a05bf6e59b","eoZcHvtAINp7riMGYKKK366kklG-gLbVDkiQDOEWzTk"), WechatUserInfo.class);
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
		WechatUserInfo userBaseinfo = get(String.format(USER_BASE_INFO_URL, userToken.getAccessToken(),code),WechatUserInfo.class);
		logger.info("user baseinfo->{}",JSON.toJSONString(userBaseinfo));
		if(userBaseinfo.getErrcode()==42001){
			//token过期，重新获取token
			userToken = get(String.format(TOKEN_URL, "ww894f16a05bf6e59b","eoZcHvtAINp7riMGYKKK366kklG-gLbVDkiQDOEWzTk"), WechatUserInfo.class);
			if(userToken.getErrcode()!=0){
				logger.info("init access token false->{}",userToken.getErrmsg());
				userDetail.setErrmsg(userToken.getErrmsg());
				return userDetail;
			}else{
				logger.info("get access token->{}",userToken.getAccessToken());
				userBaseinfo = get(String.format(USER_BASE_INFO_URL, userToken.getAccessToken(),code),WechatUserInfo.class);
			}
		}
		
		if(userBaseinfo.getErrcode()!=0){
			logger.error("get user baseinfo false errmsg",userBaseinfo.getErrmsg());
		}
		
		//3.get user detail
		userDetail = get(String.format(USER_DETAIL_INFO_URL,userToken.getAccessToken(),userBaseinfo.getUserid()),WechatUserInfo.class);
		if(userDetail.getErrcode()!=0){
			logger.error("get userDetail false errorMsg->{}",userDetail.getErrmsg());
			userDetail.setErrmsg(userToken.getErrmsg());
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
