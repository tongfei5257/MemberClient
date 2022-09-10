package com.example.memberclient.model;

import cn.bmob.v3.BmobObject;

public class ConsumeRecord extends BmobObject {
    /**
     * 所属什么项目
     */
    private ConsumeProject from;

    private String name;

    private double money;
    //    操作人
    private User operator;

    private int count = 1;
    private boolean delete;

    private String date;
    private String remark;
    public String oldId;
    private String oldCrateTime;
    public String oldUpdateTime;
    public ConsumeProject getFrom() {
        return from;
    }

    public void setFrom(ConsumeProject from) {
        this.from = from;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public User getOperator() {
        return operator;
    }

    public ConsumeRecord setOperator(User operator) {
        this.operator = operator;
        return this;

    }

    public boolean isDelete() {
        return delete;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public ConsumeRecord deepCopy() {
        ConsumeRecord user2 = new ConsumeRecord();
        user2.from = this.from;
        user2.name = this.name;
        user2.date = this.date;
        user2.delete = this.delete;
        user2.operator = this.operator;
        user2.money = this.money;
        user2.count = this.count;
        user2.oldId = getObjectId();
        user2.oldCrateTime = getCreatedAt();
        user2.oldUpdateTime = getUpdatedAt();
        return user2;
    }
}
