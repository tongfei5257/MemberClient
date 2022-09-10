package com.example.memberclient.model;

import java.util.List;

import cn.bmob.v3.BmobObject;

public class Project extends BmobObject {
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
    private User operator;
    private boolean delete;
    public String oldId;

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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getConsumeType() {
        return consumeType;
    }

    public void setConsumeType(int consumeType) {
        this.consumeType = consumeType;
    }

    public double getRemainMoney() {
        return remainMoney;
    }

    public void setRemainMoney(double remainMoney) {
        this.remainMoney = remainMoney;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getRemainCount() {
        return remainCount;
    }

    public void setRemainCount(int remainCount) {
        this.remainCount = remainCount;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public User getOperator() {
        return operator;
    }

    public Project setOperator(User operator) {
        this.operator = operator;
        return this;
    }

    public boolean isDelete() {
        return delete;
    }

    public void setDelete(boolean delete) {
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

    public Project deepCopy() {
        Project user2 = new Project();
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

    public Project deepCopy2() {
        Project user2 = new Project();
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
        user2.setCreatedAt(this.getCreatedAt());
        user2.setUpdatedAt(this.getUpdatedAt());
        return user2;
    }
    public static Project find(List<Project> projects, Project id){
        if (id==null){
            return null;
        }
        for (Project p:projects) {
            if (p.getObjectId().equals(id.getObjectId())){
                return p.deepCopy2();
            }
        }
        return null;
    }
}
