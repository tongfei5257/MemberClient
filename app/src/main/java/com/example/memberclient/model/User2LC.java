package com.example.memberclient.model;

import cn.leancloud.LCUser;

public class User2LC extends LCUser {

    private String name;
    private String phone;
    /**
     * 备注
     */
    private String remark;
    /**
     * 1 管理员  2 用户
     */
    private String type;

    private String number;

    private String pass;


    private boolean delete;
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        put("name",name);
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
        put("type",type);
    }

    public void setPass(String pass) {
        this.pass = pass;
        put("pass",pass);

    }

    public String getPass() {
        return pass;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public boolean isDelete() {
        return delete;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
    }
}
