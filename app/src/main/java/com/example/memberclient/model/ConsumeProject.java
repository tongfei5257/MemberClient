package com.example.memberclient.model;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 *
 */
public class ConsumeProject extends BmobObject {
    /**
     * 父项目
     */
    private Project parent;

    /**
     * 所属用户
     */
    public User2 user;

    private boolean delete;

    //    操作人
    private User operator;
    /**
     * 消费时间
     */
    private String date;
    public String oldId;
    public String oldCrateTime;

    public ConsumeProject() {
    }

    public Project getParent() {
        return parent;
    }

    public void setParent(Project parent) {
        this.parent = parent;
    }

    public User2 getUser() {
        return user;
    }

    public void setUser(User2 user) {
        this.user = user;
    }

    public boolean isDelete() {
        return delete;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
    }

    public User getOperator() {
        return operator;
    }

    public ConsumeProject setOperator(User operator) {
        this.operator = operator;
        return this;

    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public ConsumeProject deepCopy() {
        ConsumeProject user2 = new ConsumeProject();
        user2.parent = this.parent;
        user2.user = this.user;
        user2.delete = this.delete;
        user2.operator = this.operator;
        user2.date = this.date;
        user2.oldId = getObjectId();
        user2.oldCrateTime = getCreatedAt();
        return user2;
    }


    public ConsumeProject(ConsumeProject invdata) {
        this.delete=invdata.delete;
        setObjectId(invdata.getObjectId());
        setCreatedAt(invdata.getCreatedAt());
        setUpdatedAt(invdata.getUpdatedAt());
    }
    public List<ConsumeRecord> consumeRecords=new ArrayList<>();

    public void addConsumeRecord(ConsumeRecord consumeRecord) {
        for (ConsumeRecord cr : consumeRecords) {
            if (cr.getObjectId().equals(consumeRecord.getObjectId())) {
                return;
            }
        }
        consumeRecords.add(consumeRecord);
    }
    public int getRemainCount() {
        int totalCount = parent.getTotalCount();
        if (consumeRecords.size()==0){
            return totalCount;
        }
        for (ConsumeRecord cr : consumeRecords) {
            totalCount-=cr.getCount();
        }
        return totalCount;
    }

}
