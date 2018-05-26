package com.talelife.myproject.service.impl;
import org.springframework.stereotype.Service;
import com.talelife.myproject.service.UserRoleService;
import com.talelife.myproject.mapper.UserRoleMapper;
import com.talelife.myproject.model.UserRole;
import javax.annotation.Resource;
import java.util.List;
/**
 * 用户角色关系业务实现类
 * date: 2018-04-27 15:44:49
 * 
 * @author Liuweiyao
 * @version 1.0
 */
@Service
public class UserRoleServiceImpl implements UserRoleService{
	
	@Resource
	private UserRoleMapper userRoleMapper;
	
	public List<UserRole> findAll(){
		return userRoleMapper.findAll();
	}
	
	public List<UserRole> findList(UserRole userRole){
		return userRoleMapper.findList(userRole);
	}
	
	public int add(UserRole userRole){
		return userRoleMapper.add(userRole);
	}
	
	public int delete(Long id){
		return userRoleMapper.delete(id);
	}
	
	public int update(UserRole userRole){
		return userRoleMapper.update(userRole);
	}
	
	public UserRole findByPK(Long id){
		return userRoleMapper.findByPK(id);
	}
	
	
	
	
	
}