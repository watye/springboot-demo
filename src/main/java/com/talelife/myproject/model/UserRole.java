package com.talelife.myproject.model;
import java.io.Serializable;
import java.util.Date;
/**
 * 用户角色关系实体类
 * date: 2018-04-27 15:44:49
 * 
 * @author Liuweiyao
 * @version 1.0
 */
public class UserRole implements Serializable{
	//
	private Long id;
	
	//
	private Long roleId;
	
	//
	private Long userId;
	

	public Long getId(){
		return id;
	}
	
	public void setId(Long id){
 	    this.id=id;
    }
	public Long getRoleId(){
		return roleId;
	}
	
	public void setRoleId(Long roleId){
 	    this.roleId=roleId;
    }
	public Long getUserId(){
		return userId;
	}
	
	public void setUserId(Long userId){
 	    this.userId=userId;
    }

}