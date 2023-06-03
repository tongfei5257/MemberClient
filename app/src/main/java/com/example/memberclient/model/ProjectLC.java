package com.example.memberclient.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.leancloud.LCObject;
import cn.leancloud.LCUser;

public class ProjectLC extends LCObject implements Parcelable {
    private String name;
    /**
     * 金额
     */
    private double money;

    /**
     * 类型
     * 鲜花 1
     * 乐园 2
     */
    private int type;

    /**
     * 消费类型  1 使用金额  2 计次
     */
    private int consumeType;

    /**
     * 剩余金额
     */
    private double remainMoney;

    /**
     * 总次数
     */
    private int totalCount;

    /**
     * 剩余次数
     */
    private int remainCount;

    private String remark;

    private String date;
    //    操作人
    private User2LC operator;
    private boolean delete;
    public String oldId;
//    bomb id
    public String bmId;

    public ProjectLC() {
    }

    {
        put("delete", false);
    }

    public ProjectLC(String className) {
        super(className);
    }

    protected ProjectLC(Parcel in) {
        name = in.readString();
        money = in.readDouble();
        type = in.readInt();
        consumeType = in.readInt();
        remainMoney = in.readDouble();
        totalCount = in.readInt();
        remainCount = in.readInt();
        remark = in.readString();
        date = in.readString();
        delete = in.readByte() != 0;
        oldId = in.readString();
        objectId = in.readString();
    }

    public static final Creator<ProjectLC> CREATOR = new Creator<ProjectLC>() {
        @Override
        public ProjectLC createFromParcel(Parcel in) {
            return new ProjectLC(in);
        }

        @Override
        public ProjectLC[] newArray(int size) {
            return new ProjectLC[size];
        }
    };



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        put("name", name);
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
        put("money", money);
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
        put("type", type);

    }

    public int getConsumeType() {
        return consumeType;
    }

    public void setConsumeType(int consumeType) {
        this.consumeType = consumeType;
        put("consumeType", consumeType);

    }

    public double getRemainMoney() {
        return remainMoney;
    }

    public void setRemainMoney(double remainMoney) {
        this.remainMoney = remainMoney;
        put("remainMoney", remainMoney);

    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
        put("totalCount", totalCount);

    }

    public int getRemainCount() {
        return remainCount;
    }

    public void setRemainCount(int remainCount) {
        this.remainCount = remainCount;
        put("remainCount", remainCount);

    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
        put("remark", remark);

    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
        put("date", date);

    }

    public User2LC getOperator() {
        return operator;
    }

    public ProjectLC setOperator(User2LC operator) {
        this.operator = operator;
//        put("operator",operator);
        return this;
    }

    public ProjectLC setOperator(LCUser operator) {
//        this.operator = operator;
//        put("operator",operator);
        return this;
    }

    public boolean isDelete() {
        return delete;
    }

    public void setDelete(boolean delete) {
        put("delete", delete);

        this.delete = delete;
    }

    public String getTypeString() {
        if (type == 1) {
            return "鲜花";
        } else if (type == 2) {
            return "乐园";
        }
        return "无";
    }

    public String getConsumeTypeString() {
        if (consumeType == 1) {
            return "金额消费";
        } else if (consumeType == 2) {
            return "计次消费";
        }
        return "无";
    }

    public String getSimpleName() {
        return "(" + getTypeString() + ")" + getConsumeTypeString() + ",金额=" + money + "元 总" + getRealMoneyOrCount();
    }

    public String getRealMoneyOrCount() {
        if (consumeType == 1) {
            return money + "元";
        } else if (consumeType == 2) {
            return totalCount + "次";
        }
        return "";
    }

    @Override
    public String toString() {
        return "Project{" +
                "name='" + name + '\'' +
                ", money=" + money +
                ", type=" + type +
                ", consumeType=" + consumeType +
                ", remainMoney=" + remainMoney +
                ", totalCount=" + totalCount +
                ", remainCount=" + remainCount +
                ", remark='" + remark + '\'' +
                ", date='" + date + '\'' +
                ", operator=" + operator +
                ", delete=" + delete +
                '}';
    }

    public ProjectLC deepCopy() {
        ProjectLC user2 = new ProjectLC();
        user2.name = this.name;
        user2.money = this.money;
        user2.type = this.type;
        user2.consumeType = this.consumeType;
        user2.remark = this.remark;
        user2.remainMoney = this.remainMoney;
        user2.totalCount = this.totalCount;
        user2.remainCount = this.remainCount;
        user2.delete = this.delete;
        user2.operator = this.operator;
        user2.date = this.date;
        user2.oldId = getObjectId();
        return user2;
    }

    public ProjectLC deepCopy2() {
        ProjectLC user2 = new ProjectLC();
        user2.name = this.name;
        user2.money = this.money;
        user2.type = this.type;
        user2.consumeType = this.consumeType;
        user2.remark = this.remark;
        user2.remainMoney = this.remainMoney;
        user2.totalCount = this.totalCount;
        user2.remainCount = this.remainCount;
        user2.delete = this.delete;
        user2.operator = this.operator;
        user2.date = this.date;
        user2.setObjectId(getObjectId());
//        user2.setCreatedAt(this.getCreatedAt());
//        user2.setUpdatedAt(this.getUpdatedAt());
        return user2;
    }

    public static ProjectLC find(List<ProjectLC> projects, ProjectLC id) {
        if (id == null) {
            return null;
        }
        for (ProjectLC p : projects) {
            if (p.getObjectId().equals(id.getObjectId())) {
                return p.deepCopy2();
            }
        }
        return null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeDouble(money);
        dest.writeInt(type);
        dest.writeInt(consumeType);
        dest.writeDouble(remainMoney);
        dest.writeInt(totalCount);
        dest.writeInt(remainCount);
        dest.writeString(remark);
        dest.writeString(date);
        dest.writeByte((byte) (delete ? 1 : 0));
        dest.writeString(oldId);
        dest.writeString(objectId);
    }

    public static ProjectLC toBean(LCObject lcObject) {
        if (lcObject == null) {
            return null;
        }
        ProjectLC projectLC = new ProjectLC();
        projectLC.delete = lcObject.getBoolean("delete");
        projectLC.money = lcObject.getInt("money");
        projectLC.type = lcObject.getInt("type");
        projectLC.consumeType = lcObject.getInt("consumeType");
        projectLC.remainMoney = lcObject.getInt("remainMoney");
        projectLC.totalCount = lcObject.getInt("totalCount");
        projectLC.remainCount = lcObject.getInt("remainCount");
        projectLC.name = lcObject.getString("name");
        projectLC.remark = lcObject.getString("remark");
        projectLC.date = lcObject.getString("date");
        projectLC.operator = lcObject.getLCObject("operator");
        projectLC.objectId = lcObject.getObjectId();

        return projectLC;
    }

    public static void createWithoutData(LCObject des, ProjectLC source) {
        if (source == null) {
            return;
        }
        des.put("delete", source.delete);
        des.put("money", source.money);
        des.put("type", source.type);
        des.put("consumeType", source.consumeType);
        des.put("remainMoney", source.remainMoney);
        des.put("totalCount", source.totalCount);
        des.put("remainCount", source.remainCount);
        des.put("name", source.name);
        des.put("remark", source.remark);
        des.put("operator", source.operator);
        des.put("date", source.date);
    }

    public ProjectLC saveV2() {
        put("delete", delete);
        put("money", money);
        put("type", type);
        put("consumeType", consumeType);
        put("remainMoney", remainMoney);
        put("totalCount", totalCount);
        put("remainCount", remainCount);
        put("name", name);
        put("remark", remark);
        put("operator", operator);
        put("date", date);
        put("oldId", oldId);
        put("bmId", bmId);
        setObjectId(getObjectId());
        setClassName("ProjectLC");
        return this;
    }
    public static ProjectLC newInstance(Project source){
        ProjectLC user2 = new ProjectLC();
        user2.delete = source.delete;
        user2.money = source.money;
        user2.type = source.type;
        user2.consumeType = source.consumeType;
        user2.remainMoney = source.remainMoney;
        user2.totalCount = source.totalCount;
        user2.remainCount = source.remainCount;
        user2.name = source.name;
        user2.remark = source.remark;
        user2.date = source.date;
//        user2.oldId = source.getObjectId();
        user2.bmId = source.getObjectId();
        return user2;
    }
}
