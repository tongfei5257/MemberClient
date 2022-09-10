package com.example.memberclient.model;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobObject;

public class User2 extends BmobObject {

    public String name = "";
    public String phone = "";
    public String username = "";
    public String password = "";
    /**
     * 备注
     */
    public String remark;
    /**
     * 1 管理员  2 用户
     */
    public String type;

    public String number = "";
    public int newNumber;

    public String pass;


    public boolean delete;

    //    操作人
    public User operator;

    /**
     * 注册时间
     */
    public String date;

    public String oldId = "";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getPass() {
        return pass;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
        try {
            this.newNumber = Integer.parseInt(number);
        } catch (Exception e) {

        }
    }

    public boolean isDelete() {
        return delete;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public User getOperator() {
        return operator;
    }

    public User2 setOperator(User operator) {
        this.operator = operator;
        return this;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getNewNumber() {
        return newNumber;
    }

    public void setNewNumber(int newNumber) {
        this.newNumber = newNumber;
//        this.number = newNumber + "";
    }

    public String getOldId() {
        return oldId;
    }

    public void setOldId(String oldId) {
        this.oldId = oldId;
    }

    public User2 deepCopy() {
        User2 user2 = new User2();
        user2.name = this.name;
        user2.phone = this.phone;
        user2.username = this.username;
        user2.password = this.password;
        user2.remark = this.remark;
        user2.type = this.type;
        user2.number = this.number;
        user2.newNumber = this.newNumber;
        user2.pass = this.pass;
        user2.delete = this.delete;
        user2.operator = this.operator;
        user2.date = this.date;
        user2.oldId = getObjectId();
        return user2;
    }
    public List<ConsumeProject> consumeProjects = new ArrayList<>();


    public void setConsumeProject(ConsumeProject consumeProject) {
        boolean find=false;
        for (ConsumeProject cp:consumeProjects) {
            if (cp.getObjectId().equals(consumeProject.getObjectId())){
                find=true;
                break;
            }
        }
        if (!find){
            consumeProjects.add(consumeProject);
        }
    }

    public void setConsumeRecord(ConsumeRecord consumeRecord) {
        for (ConsumeProject cp:consumeProjects) {
            if (consumeRecord.getFrom().getObjectId().equals(cp.getObjectId())){
                cp.addConsumeRecord(consumeRecord);
                break;
            }
        }
    }

}
