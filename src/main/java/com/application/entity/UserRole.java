package com.application.entity;

import javax.persistence.*;

/**
 * Created by qws on 2017/9/25/025.
 */
@Entity
@Table(name = "t_rt")
public class UserRole implements java.io.Serializable {

    /**
     *关系表
     */
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(name = "userId")
    private Integer userId;

    @Column(name = "roleId")
    private Integer roleId;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }
}
