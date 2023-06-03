package com.example.memberclient.model;

import com.example.memberclient.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.leancloud.LCObject;

/**
 *
 */
public class ConsumeProjectLC extends LCObject {
    /**
     * 父项目
     */
    public ProjectLC parent;

    /**
     * 所属用户
     */
    public UserLC user;

    public boolean delete;

    //    操作人
    public User operator;
    /**
     * 消费时间
     */
    public String date;
    public String oldId;
    public String oldCrateTime;
    public String bmId;

    public ConsumeProjectLC() {
    }



    public ProjectLC getParent() {
        return parent;
    }

    public void setParent(ProjectLC parent) {
        this.parent = parent;
    }

    public UserLC getUser() {
        return user;
    }

    public void setUser(UserLC user) {
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

    public ConsumeProjectLC setOperator(User operator) {
        this.operator = operator;
        return this;

    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public ConsumeProjectLC deepCopy() {
        ConsumeProjectLC user2 = new ConsumeProjectLC();
        user2.parent = this.parent;
        user2.user = this.user;
        user2.delete = this.delete;
        user2.operator = this.operator;
        user2.date = this.date;
        user2.oldId = getObjectId();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-hh-mm");
        user2.oldCrateTime = simpleDateFormat.format(getCreatedAt());
        return user2;
    }


    public ConsumeProjectLC(ConsumeProjectLC invdata) {
        this.delete=invdata.delete;
        setObjectId(invdata.getObjectId());
//        setCreatedAt(invdata.getCreatedAt());
//        setUpdatedAt(invdata.getUpdatedAt());

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
    public ConsumeProjectLC saveV2() {
        put("parent", parent.saveV2());
        put("user", user.saveV2());
        put("delete", delete);
        put("date", date);
        put("bmId", bmId);
        put("oldId", oldId);
        put("oldCrateTime", oldCrateTime);
        setObjectId(getObjectId());
        setClassName("ConsumeProjectLC");
        return this;
    }
    public static ConsumeProjectLC newInstance(ConsumeProject source){
        ConsumeProjectLC user2 = new ConsumeProjectLC();
        user2.parent =ProjectLC.newInstance(source.parent) ;
        user2.user = UserLC.newInstance(source.user);
        user2.delete = source.delete;
//        user2.operator = source.operator;
        user2.date = source.date;
        user2.oldId = source.oldId;
        user2.oldCrateTime = source.oldCrateTime;
        user2.bmId = source.getObjectId();
//        user2.oldId = source.getObjectId();
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
