package com.talelife.myproject.controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.talelife.myproject.model.UserRole;
import com.talelife.myproject.service.UserRoleService;

@RestController
@RequestMapping("/user_role")
public class UserRoleController extends BaseController{
	@Resource
	private UserRoleService userRoleService;
	
	@RequestMapping("/all")
    public List<UserRole> all() {
        return userRoleService.findAll();
    }
	
	
	
	
}
