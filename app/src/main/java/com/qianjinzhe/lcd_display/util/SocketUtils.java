package com.qianjinzhe.lcd_display.util;


import com.qianjinzhe.lcd_display.bean.SocketMsg;

/******************************************************
 * Copyrights @ 2018，Qianjinzhe Technology Co., Ltd.
 *               深圳市前进者科技有限公司
 * All rights reserved.
 *
 * Filename：
 *              SocketUtils.java
 * Description：
 *              socket工具
 * Author:
 *              youngHu
 * Finished：
 *              2018年05月31日
 ********************************************************/

public class SocketUtils {
    /**服务器ip,默认192.168.0.100*/
    public static String SERCIVER_IP = "172.16.37.105";
    /**数据库ip*/
    public static String DATABASE_IP = "172.16.37.202";
    /**数据库ip*/
    public static String SERCIVER_IP_OLD = "172.16.37.202";
    /**数据库名称*/
    public static String DATABASE_NAME = "qqs6";
    /**窗口号*/
    public static int COUNTER_NO = 1;
    /**服务器端口*/
    public static final int SERCIVER_PORT = 2402;
    /**Socket接收缓冲区大小（必须等于SocketMsg的大小，不能大于它，否则Socket接收数据机制需要修改*/
    public static final int BUFFER_SIZE = 344;


    /**
     * int转小端byte
     * @param i
     * @param len
     * @return
     */
    public static byte[] little_intToByte(int i, int len) {
        byte[] abyte = new byte[len];
        if (len == 1) {
            abyte[0] = (byte) (0xff & i);
        } else if (len == 2) {
            abyte[0] = (byte) (0xff & i);
            abyte[1] = (byte) ((0xff00 & i) >> 8);
        } else {
            abyte[0] = (byte) (0xff & i);
            abyte[1] = (byte) ((0xff00 & i) >> 8);
            abyte[2] = (byte) ((0xff0000 & i) >> 16);
            abyte[3] = (byte) ((0xff000000 & i) >> 24);
        }
        return abyte;
    }


    /**
     * 对象转c#小端字节序
     * @param socketMsg
     * @return
     */
    public static byte[] objectToBytes(SocketMsg socketMsg) {
        byte[] buf = new byte[344];
        byte[] temp;
        int index = 0;
        //类型
        byte[] msgtype_byte = little_intToByte(socketMsg.MsgType, 4);
        System.arraycopy(msgtype_byte, 0, buf, index, msgtype_byte.length);
        index += msgtype_byte.length;

        //窗口号
        byte[] counter_no_byte = little_intToByte(socketMsg.CounterNo, 4);
        System.arraycopy(counter_no_byte, 0, buf, index, counter_no_byte.length);
        index += counter_no_byte.length;

        //业务编号
        byte[] service_no_byte = little_intToByte(socketMsg.ServiceNo, 4);
        System.arraycopy(service_no_byte, 0, buf, index, service_no_byte.length);
        index += service_no_byte.length;

        //票号
        byte[] ticket_no_byte = new byte[32];
        if (!TextUtils.isEmpty(socketMsg.TicketNo)) {
            try {
                temp = socketMsg.TicketNo.getBytes("GBK");
                System.arraycopy(temp, 0, ticket_no_byte, 0, temp.length);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.arraycopy(ticket_no_byte, 0, buf, index, ticket_no_byte.length);
        index += ticket_no_byte.length;

        //等候人数
        byte[] waiting_num_byte = little_intToByte(socketMsg.WaitingNum, 4);
        System.arraycopy(waiting_num_byte, 0, buf, index, waiting_num_byte.length);
        index += waiting_num_byte.length;

        //工号
        byte[] staff_id_byte = new byte[32];
        if (!TextUtils.isEmpty(socketMsg.StaffId)) {
            try {
                temp = socketMsg.StaffId.getBytes("GBK");
                System.arraycopy(temp, 0, staff_id_byte, 0, temp.length);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.arraycopy(staff_id_byte, 0, buf, index, staff_id_byte.length);
        index += staff_id_byte.length;

        //参数1
        byte[] arg1_byte = little_intToByte(socketMsg.Arg1, 4);
        System.arraycopy(arg1_byte, 0, buf, index, arg1_byte.length);
        index += arg1_byte.length;

        //参数2
        byte[] arg2_byte = little_intToByte(socketMsg.Arg2, 4);
        System.arraycopy(arg2_byte, 0, buf, index, arg2_byte.length);
        index += arg2_byte.length;

        //参数3
        byte[] arg3_byte = new byte[128];
        if (!TextUtils.isEmpty(socketMsg.Arg3)) {
            try {
                temp = socketMsg.Arg3.getBytes("GBK");
                System.arraycopy(temp, 0, arg3_byte, 0, temp.length);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.arraycopy(arg3_byte, 0, buf, index, arg3_byte.length);
        index += arg3_byte.length;

        //参数4
        byte[] arg4_byte = new byte[128];
        if (!TextUtils.isEmpty(socketMsg.Arg4)) {
            try {
                temp = socketMsg.Arg4.getBytes("GBK");
                System.arraycopy(temp, 0, arg4_byte, 0, temp.length);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.arraycopy(arg4_byte, 0, buf, index, arg4_byte.length);

        return buf;
    }

    /**
     * 字节数组转对象
     * @param bytes
     * @return 消息对象
     */
    public static SocketMsg bytesToObject(byte[] bytes) {
        if (bytes.length != 344) {
            return null;
        }
        SocketMsg socketMsg = new SocketMsg();
        try {
            //类型
            byte[] msgtype_byte = new byte[4];
            //窗口号
            byte[] counter_no_byte = new byte[4];
            //业务编号
            byte[] service_no_byte = new byte[4];
            //票号
            byte[] ticket_no_byte = new byte[32];
            //等候人数
            byte[] waiting_num_byte = new byte[4];
            //工号
            byte[] staff_id_byte = new byte[32];
            //参数1
            byte[] arg1_byte = new byte[4];
            //参数2
            byte[] arg2_byte = new byte[4];
            //参数3
            byte[] arg3_byte = new byte[128];
            //参数4
            byte[] arg4_byte = new byte[128];
            int po = 0;
            //循环获取个字段字节
            for (int i = 0; i < bytes.length; i++) {

                if ((po == 4 && i == 4)
                        || (po == 4 && i == 8)
                        || (po == 4 && i == 12)
                        || (po == 32 && i == 44)
                        || (po == 4 && i == 48)
                        || (po == 32 && i == 80)
                        || (po == 4 && i == 84)
                        || (po == 4 && i == 88)
                        || (po == 128 && i == 216)
                        || (po == 128 && i == 344)) {
                    po = 0;
                }
                if (i < 4) {
                    msgtype_byte[po] = bytes[i];
                } else if (i < 8) {
                    counter_no_byte[po] = bytes[i];
                } else if (i < 12) {
                    service_no_byte[po] = bytes[i];
                } else if (i < 44) {
                    ticket_no_byte[po] = bytes[i];
                } else if (i < 48) {
                    waiting_num_byte[po] = bytes[i];
                } else if (i < 80) {
                    staff_id_byte[po] = bytes[i];
                } else if (i < 84) {
                    arg1_byte[po] = bytes[i];
                } else if (i < 88) {
                    arg2_byte[po] = bytes[i];
                } else if (i < 216) {
                    arg3_byte[po] = bytes[i];
                } else if (i < 344) {
                    arg4_byte[po] = bytes[i];
                }
                po++;
            }
            //设置消息类型
            socketMsg.MsgType = little_bytesToInt(msgtype_byte);
            //设置窗口号
            socketMsg.CounterNo = little_bytesToInt(counter_no_byte);
            //设置业务编号
            socketMsg.ServiceNo = little_bytesToInt(service_no_byte);
            //票号
            if (getNewBytes(ticket_no_byte).length > 0) {
                String ticket_no = new String(getNewBytes(ticket_no_byte), "GBK");
                socketMsg.TicketNo = ticket_no;
            }
            //等候人数
            socketMsg.WaitingNum = little_bytesToInt(waiting_num_byte);
            //员工工号
            if (getNewBytes(staff_id_byte).length > 0) {
                socketMsg.StaffId = new String(getNewBytes(staff_id_byte), "GBK");
            }
            //参数1
            socketMsg.Arg1 = little_bytesToInt(arg1_byte);
            //参数2
            socketMsg.Arg2 = little_bytesToInt(arg2_byte);
            //参数3
            if (getNewBytes(arg3_byte).length > 0) {
                socketMsg.Arg3 = new String(getNewBytes(arg3_byte), "GBK");
            }
            //参数4
            if (getNewBytes(arg4_byte).length > 0) {
                socketMsg.Arg4 = new String(getNewBytes(arg4_byte), "GBK");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        return socketMsg;
    }


    /**
     * 获取有效的byte数组
     * @param mBytes
     * @return
     */
    public static byte[] getNewBytes(byte[] mBytes) {
        int num = getBytesNum(mBytes);
        byte[] newByte = new byte[num];
        for (int i = 0; i < mBytes.length; i++) {
            if (i < num) {
                newByte[i] = mBytes[i];
            }
        }
        return newByte;
    }


    /**
     * 获取有效字节个数
     * @param buf
     * @return
     */
    public static int getBytesNum(byte[] buf) {
        int i = 0;
        for (; i < buf.length; i++) {
            //如果为0
            if (buf[i] == (byte) 0) {
                break;
            }
        }
        return i;
    }

    /**
     * 小端字节转int
     * @param bytes
     * @return
     */
    public static int little_bytesToInt(byte[] bytes) {
        int addr = 0;
        if (bytes.length == 1) {
            addr = bytes[0] & 0xFF;
        } else if (bytes.length == 2) {
            addr = bytes[0] & 0xFF;
            addr |= (((int) bytes[1] << 8) & 0xFF00);
        } else {
            addr = bytes[0] & 0xFF;
            addr |= (((int) bytes[1] << 8) & 0xFF00);
            addr |= (((int) bytes[2] << 16) & 0xFF0000);
            addr |= (((int) bytes[3] << 24) & 0xFF000000);
        }
        return addr;
    }


}
