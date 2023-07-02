package com.example.memberclient.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.leancloud.LCObject;
import cn.leancloud.LCUser;

public class ConsumeRecordLC extends LCObject implements Parcelable {
    /**
     * 所属什么项目
     */
    public ConsumeProjectLC from;

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
    public String bmId;

    public ConsumeRecordLC() {
    }

    protected ConsumeRecordLC(Parcel in) {
        from = in.readParcelable(ConsumeProjectLC.class.getClassLoader());
        name = in.readString();
        money = in.readDouble();
        count = in.readInt();
        delete = in.readByte() != 0;
        date = in.readString();
        remark = in.readString();
        oldId = in.readString();
        oldCrateTime = in.readString();
        oldUpdateTime = in.readString();
        bmId = in.readString();
        objectId = in.readString();
    }

    public static final Creator<ConsumeRecordLC> CREATOR = new Creator<ConsumeRecordLC>() {
        @Override
        public ConsumeRecordLC createFromParcel(Parcel in) {
            return new ConsumeRecordLC(in);
        }

        @Override
        public ConsumeRecordLC[] newArray(int size) {
            return new ConsumeRecordLC[size];
        }
    };

    public static ConsumeRecordLC toBean(LCObject lcObject) {
        if (lcObject == null) {
            return null;
        }
        ConsumeRecordLC projectLC = new ConsumeRecordLC();
        projectLC.from = ConsumeProjectLC.toBean(lcObject.getLCObject("from"));
        projectLC.delete = lcObject.getBoolean("delete");
        projectLC.bmId = lcObject.getString("bmId");
        projectLC.name = lcObject.getString("name");
        projectLC.date = lcObject.getString("date");
        projectLC.oldCrateTime = lcObject.getString("oldCrateTime");
        projectLC.oldId = lcObject.getString("oldId");
        projectLC.remark = lcObject.getString("remark");
        projectLC.oldUpdateTime = lcObject.getString("oldUpdateTime");
        projectLC.count = lcObject.getInt("count");
        projectLC.objectId = lcObject.getObjectId();
        return projectLC;

    }

    public ConsumeProjectLC getFrom() {
        return from;
    }

    public void setFrom(ConsumeProjectLC from) {
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

    public ConsumeRecordLC setOperator(User operator) {
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

    public ConsumeRecordLC deepCopy() {
        ConsumeRecordLC user2 = new ConsumeRecordLC();
        user2.from = this.from;
        user2.name = this.name;
        user2.date = this.date;
        user2.delete = this.delete;
        user2.operator = this.operator;
        user2.money = this.money;
        user2.count = this.count;
        user2.oldId = getObjectId();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-hh-mm");
        user2.oldCrateTime = simpleDateFormat.format(getCreatedAt());
        user2.oldUpdateTime =simpleDateFormat.format(getUpdatedAt());
        return user2;
    }
    /**
     * 所属什么项目
     */
//    public ConsumeProjectLC from;
//
//    public String name;
//
//    public double money;
//    //    操作人
//    public User operator;
//
//    public int count = 1;
//    public boolean delete;
//
//    public String date;
//    public String remark;
//    public String oldId;
//    public String oldCrateTime;
//    public String oldUpdateTime;
    
    public static ConsumeRecordLC newInstance(ConsumeRecord source) {
        ConsumeRecordLC user2 = new ConsumeRecordLC();
        user2.from =ConsumeProjectLC.newInstance(source.from) ;
       
        user2.delete = source.delete;
        user2.name = source.name;
        user2.money = source.money;
        user2.count = source.count;
//        user2.operator = source.operator;
        user2.date = source.date;
        user2.oldId = source.oldId;
        user2.oldCrateTime = source.oldCrateTime;
        user2.oldUpdateTime = source.oldUpdateTime;
        user2.bmId = source.getObjectId();
//        user2.oldId = source.getObjectId();
        return user2;
    }
    public static ConsumeRecordLC newInstanceV2(ConsumeRecord source) {
        ConsumeRecordLC user2 = new ConsumeRecordLC();
        ConsumeProjectLC consumeProjectLC = new ConsumeProjectLC();
        consumeProjectLC.bmId=source.from.getObjectId();
        consumeProjectLC.user=new UserLC();
        consumeProjectLC.parent=new ProjectLC();
        user2.from = consumeProjectLC;

        user2.delete = source.delete;
        user2.name = source.name;
        user2.money = source.money;
        user2.count = source.count;
//        user2.operator = source.operator;
        user2.date = source.date;
        user2.oldId = source.oldId;
        user2.oldCrateTime = source.oldCrateTime;
        user2.oldUpdateTime = source.oldUpdateTime;
        user2.bmId = source.getObjectId();
//        user2.oldId = source.getObjectId();
        return user2;
    }

    public ConsumeRecordLC saveV2() {
        put("from", from.saveV2());
        put("delete", delete);
        put("date", date);
        put("bmId", bmId);
        put("oldId", oldId);
        put("oldCrateTime", oldCrateTime);
        put("remark", remark);
        put("name", name);
        put("count", count);
        put("oldUpdateTime", oldUpdateTime);
        LCObject operator = LCObject.createWithoutData("_User", LCUser.getCurrentUser().getObjectId());
        put("operator",operator);
        setObjectId(getObjectId());
        setClassName("ConsumeRecordLC");
        return this;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(from, flags);
        dest.writeString(name);
        dest.writeDouble(money);
        dest.writeInt(count);
        dest.writeByte((byte) (delete ? 1 : 0));
        dest.writeString(date);
        dest.writeString(remark);
        dest.writeString(oldId);
        dest.writeString(oldCrateTime);
        dest.writeString(oldUpdateTime);
        dest.writeString(bmId);
        dest.writeString(getObjectId());
    }
}
