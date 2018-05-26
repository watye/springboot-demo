package com.talelife.myproject.service;
import com.talelife.myproject.model.UserRole;
import java.util.List;
/**
 * 用户角色关系业务接口
 * date: 2018-04-27 15:44:49
 * 
 * @author Liuweiyao
 * @version 1.0
 */
public interface UserRoleService{
	List<UserRole> findAll();
	
	List<UserRole> findList(UserRole userRole);
	
	int add(UserRole userRole);
	
	int delete(Long id);
	
	int update(UserRole userRole);
	
	UserRole findByPK(Long id);
}