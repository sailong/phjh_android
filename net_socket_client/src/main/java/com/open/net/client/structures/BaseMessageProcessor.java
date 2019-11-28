package com.open.net.client.structures;

/**
 * author       :   long
 * created on   :   2017/11/30
 * description  :   数据回调
 */

public abstract class BaseMessageProcessor {

    //----------------------------------发数据------------------------------------------------
    public final void send(BaseClient mClient, byte[] src) {
        this.send(mClient, src, 0, src.length);
    }

    public final void send(BaseClient mClient, byte[] src, int offset, int length) {
        mClient.onSendMessage(src, offset, length);
    }

    //----------------------------------收数据------------------------------------------------
    public final void onReceiveData(BaseClient mClient, byte[] src, int offset, int length) {
        onReceiveMessagesData(src, offset, length);
    }

    public final void onReceiveDataCompleted(BaseClient mClient) {
        if (mClient.mReadMessageQueen.mReadQueen.size() > 0) {
            mClient.onReceiveMessageClear();
        }
    }

    //接收消息
    public abstract void onReceiveMessagesData(byte[] src, int offset, int length);


}
