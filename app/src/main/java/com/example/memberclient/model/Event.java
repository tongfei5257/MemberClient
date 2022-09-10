package com.example.memberclient.model;

import cn.bmob.v3.BmobObject;

public class Event extends BmobObject {
    //    操作人
    private User operator;

    //    操作类型 1增加 2删除 3修改
    private int eventType;

    //    目标  1用户 2 项目 3 子项目
    private int target;

//   1 开始 2 成功 3 失败
    private int processType;

    private String msg;

    private BmobObject targetObj;
    public User getOperator() {
        return operator;
    }

    public void setOperator(User operator) {
        this.operator = operator;
    }

    public int getEventType() {
        return eventType;
    }

    public void setEventType(int eventType) {
        this.eventType = eventType;
    }

    public int getTarget() {
        return target;
    }

    public void setTarget(int target) {
        this.target = target;
    }

    public String getTypeStr() {
      if (eventType==1){
          return "增加";
      }else if (eventType==2){
          return "删除";
      }else if (eventType==3){
          return "修改";
      }
      return "";
    }
    public String getTargetStr() {
        if (eventType==1){
            return "用户";
        }else if (eventType==2){
            return "项目";
        }else if (eventType==3){
            return "子项目";
        }
        return "";
    }

    public int getProcessType() {
        return processType;
    }

    public void setProcessType(int processType) {
        this.processType = processType;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public BmobObject getTargetObj() {
        return targetObj;
    }

    public void setTargetObj(BmobObject targetObj) {
        this.targetObj = targetObj;
    }
}
