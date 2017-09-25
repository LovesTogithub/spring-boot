package com.application.entity;

import javax.persistence.*;

/**
 * Created by qws on 2017/9/25/025.
 */
@Entity
@Table(name = "t_role")
public class Role implements java.io.Serializable {

    /**
     *权限表
     */
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(name = "role")
    private String role;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}