package com.example.memberclient.model;

import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.leancloud.LCObject;

public class ConsumeRecord extends BmobObject {
    /**
     * 所属什么项目
     */
    public ConsumeProject from;

    public String name;

    public double money;
    //    操作人
    public User operator;

    public int count = 1;
    public boolean delete;

    public String date;
    public String remark;
    public String oldId;
    public String oldCrateTime;
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
    public boolean find(List<LCObject> lcObjects) {
        if (lcObjects==null||lcObjects.isEmpty()){
            return false;
        }
        for (LCObject lcObject:lcObjects) {
            if (getObjectId().equals(lcObject.getString("bmId"))){
                return true;
            }
        }
        return false;
    }
}
