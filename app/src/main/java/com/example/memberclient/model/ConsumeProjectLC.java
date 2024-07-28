package com.example.memberclient.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.example.memberclient.utils.DateUtils;
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
public class ConsumeProjectLC extends LCObject implements Parcelable {
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
    public String objectId;
    public String createdAt;
    public String updatedAt;
    public ConsumeProjectLC() {
    }


    protected ConsumeProjectLC(Parcel in) {
        parent = in.readParcelable(ProjectLC.class.getClassLoader());
        user = in.readParcelable(UserLC.class.getClassLoader());
        delete = in.readByte() != 0;
        date = in.readString();
        oldId = in.readString();
        oldCrateTime = in.readString();
        bmId = in.readString();
        objectId = in.readString();
    }

    public static final Creator<ConsumeProjectLC> CREATOR = new Creator<ConsumeProjectLC>() {
        @Override
        public ConsumeProjectLC createFromParcel(Parcel in) {
            return new ConsumeProjectLC(in);
        }

        @Override
        public ConsumeProjectLC[] newArray(int size) {
            return new ConsumeProjectLC[size];
        }
    };

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
        objectId=invdata.getObjectId();
//        setCreatedAt(invdata.getCreatedAt());
//        setUpdatedAt(invdata.getUpdatedAt());

    }
    public List<ConsumeRecordLC> consumeRecords=new ArrayList<>();

    public void addConsumeRecord(ConsumeRecordLC consumeRecord) {
        for (ConsumeRecordLC cr : consumeRecords) {
            if (cr.objectId.equals(consumeRecord.objectId)) {
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
        for (ConsumeRecordLC cr : consumeRecords) {
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
        user2.oldCrateTime = TextUtils.isEmpty(source.oldCrateTime)?source.getCreatedAt():source.oldCrateTime;
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

    public static ConsumeProjectLC toBean(LCObject lcObject) {
        if (lcObject == null) {
            return null;
        }
        ConsumeProjectLC projectLC = new ConsumeProjectLC();
        projectLC.parent = ProjectLC.toBean(lcObject.getLCObject("parent"));
        projectLC.user = UserLC.toBean(lcObject.getLCObject("user"));
        projectLC.delete = lcObject.getBoolean("delete");
        projectLC.bmId = lcObject.getString("bmId");
        projectLC.date = lcObject.getString("date");
        projectLC.oldCrateTime = lcObject.getString("oldCrateTime");
        projectLC.objectId = lcObject.getObjectId();
        if (TextUtils.isEmpty(projectLC.oldCrateTime)) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date createdAt = lcObject.getCreatedAt();
            if (createdAt!=null){
                projectLC.oldCrateTime = simpleDateFormat.format(createdAt);
            }
        }
        projectLC.createdAt = lcObject.getCreatedAtString();
        projectLC.updatedAt = lcObject.getUpdatedAtString();
        return projectLC;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(parent, flags);
        dest.writeParcelable(user, flags);
        dest.writeByte((byte) (delete ? 1 : 0));
        dest.writeString(date);
        dest.writeString(oldId);
        dest.writeString(oldCrateTime);
        dest.writeString(bmId);
        dest.writeString(getObjectId());
    }
    public String getRealCreateTime(){
        if (TextUtils.isEmpty(oldCrateTime)){
            return getCreatedAtString();
        }
        return oldCrateTime;
    }
}
