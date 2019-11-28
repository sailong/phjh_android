package com.qianjinzhe.lcd_display.bean;

import android.os.Parcel;
import android.os.Parcelable;

/******************************************************
 * Copyrights @ 2018，Qianjinzhe Technology Co., Ltd.
 *               深圳市前进者科技有限公司
 * All rights reserved.
 *
 * Filename：
 *              SocketMsg.java
 * Description：
 *              消息实体
 * Author:
 *              youngHu
 * Finished：
 *              2018年03月29日
 ********************************************************/
public class SocketMsg implements Parcelable {
    public int MsgType;   //消息类型
    public int CounterNo;       //窗口编号
    public int ServiceNo;       //业务编号
    public String TicketNo = "";     //票号，长度32
    public int WaitingNum;      //等候人数
    public String StaffId = "";      //员工工号，长度32
    public int Arg1;            //参数1
    public int Arg2;            //参数2
    public String Arg3 = "";         //参数3，长度128
    public String Arg4 = "";         //参数4，长度128


    protected SocketMsg(Parcel in) {
        MsgType = in.readInt();
        CounterNo = in.readInt();
        ServiceNo = in.readInt();
        TicketNo = in.readString();
        WaitingNum = in.readInt();
        StaffId = in.readString();
        Arg1 = in.readInt();
        Arg2 = in.readInt();
        Arg3 = in.readString();
        Arg4 = in.readString();
    }


    public SocketMsg(int MsgType, int CounterNo, int ServiceNo, String TicketNo,
                     int WaitingNum, String StaffId, int Arg1, int Arg2, String Arg3,
                     String Arg4) {
        this.MsgType = MsgType;
        this.CounterNo = CounterNo;
        this.ServiceNo = ServiceNo;
        this.TicketNo = TicketNo;
        this.WaitingNum = WaitingNum;
        this.StaffId = StaffId;
        this.Arg1 = Arg1;
        this.Arg2 = Arg2;
        this.Arg3 = Arg3;
        this.Arg4 = Arg4;
    }


    public SocketMsg() {
    }


    /**
     * 这个方法是自定义的，stub对象中会用到，不定义会报错
     *
     * @param in
     */
    public void readFromParcel(Parcel in) {
        this.MsgType = in.readInt();
        this.CounterNo = in.readInt();
        this.ServiceNo = in.readInt();
        this.TicketNo = in.readString();
        this.WaitingNum = in.readInt();
        this.StaffId = in.readString();
        this.Arg1 = in.readInt();
        this.Arg2 = in.readInt();
        this.Arg3 = in.readString();
        this.Arg4 = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(MsgType);
        dest.writeInt(CounterNo);
        dest.writeInt(ServiceNo);
        dest.writeString(TicketNo);
        dest.writeInt(WaitingNum);
        dest.writeString(StaffId);
        dest.writeInt(Arg1);
        dest.writeInt(Arg2);
        dest.writeString(Arg3);
        dest.writeString(Arg4);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SocketMsg> CREATOR = new Creator<SocketMsg>() {
        @Override
        public SocketMsg createFromParcel(Parcel in) {
            return new SocketMsg(in);
        }

        @Override
        public SocketMsg[] newArray(int size) {
            return new SocketMsg[size];
        }
    };

    @Override
    public String toString() {
        return "SocketMsg{" +
                "MsgType=" + MsgType +
                ", CounterNo=" + CounterNo +
                ", ServiceNo=" + ServiceNo +
                ", TicketNo='" + TicketNo + '\'' +
                ", WaitingNum=" + WaitingNum +
                ", StaffId='" + StaffId + '\'' +
                ", Arg1=" + Arg1 +
                ", Arg2=" + Arg2 +
                ", Arg3='" + Arg3 + '\'' +
                ", Arg4='" + Arg4 + '\'' +
                '}';
    }


}
