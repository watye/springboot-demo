package com.talelife.myproject.web.session;

import org.springframework.session.web.context.AbstractHttpSessionApplicationInitializer;

/**
 * 初始化Session配置
 * @author lwy
 *
 */
public class SessionInitializer extends AbstractHttpSessionApplicationInitializer {
	public SessionInitializer(){
		super(SessionConfig.class);
	}
}
