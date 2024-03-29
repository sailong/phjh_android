package com.open.net.client.impl.tcp.nio;

import com.open.net.client.GClient;
import com.open.net.client.structures.BaseClient;
import com.open.net.client.structures.BaseMessageProcessor;
import com.open.net.client.structures.IConnectListener;
import com.open.net.client.structures.TcpAddress;
import com.open.net.client.structures.message.Message;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * author       :   long
 * created on   :   2017/11/30
 * description  :   NioClient
 */

public final class NioClient extends BaseClient {

    static {
        GClient.init();
    }

    //-------------------------------------------------------------------------------------------
    private NioConnector mConnector;

    public NioClient(BaseMessageProcessor mMessageProcessor, IConnectListener mConnectListener) {
        super(mMessageProcessor);
        mConnector = new NioConnector(this, mConnectListener);
    }

    //-------------------------------------------------------------------------------------------
    public void setConnectAddress(TcpAddress[] tcpArray) {
        mConnector.setConnectAddress(tcpArray);
    }

    public void setConnectTimeout(long connect_timeout) {
        mConnector.setConnectTimeout(connect_timeout);
    }

    public void connect() {
        mConnector.connect();
    }

    public void disconnect() {
        mConnector.disconnect();
    }

    public void reconnect() {
        mConnector.reconnect();
    }

    public boolean isConnected() {
        return mConnector.isConnected();
    }

    public boolean isColsed() {
        return mConnector.isClosed();
    }

    //-------------------------------------------------------------------------------------------
    private SocketChannel mSocketChannel;
    private ByteBuffer mReadByteBuffer = ByteBuffer.allocate(64 * 1024);
    private ByteBuffer mWriteByteBuffer = ByteBuffer.allocate(64 * 1024);

    public void init(SocketChannel socketChannel) {
        this.mSocketChannel = socketChannel;
    }

    @Override
    public void onCheckConnect() {
        mConnector.checkConnect();
    }

    @Override
    public void onClose() {
        mSocketChannel = null;
    }

    public boolean onRead() {
        boolean readRet = true;
        try {
            mReadByteBuffer.clear();
            int readTotalLength = 0;
            int readReceiveLength = 0;
            while (true) {
                int readLength = mSocketChannel.read(mReadByteBuffer);//客户端关闭连接后，此处将抛出异常/或者返回-1
                if (readLength == -1) {
                    readRet = false;
                    break;
                }
                readReceiveLength += readLength;
                //如果一次性读满了，则先回调一次，然后接着读剩下的，目的是为了一次性读完单个通道的数据
                if (readReceiveLength == mReadByteBuffer.capacity()) {
                    mReadByteBuffer.flip();
                    if (mReadByteBuffer.remaining() > 0) {
                        this.mMessageProcessor.onReceiveData(this, mReadByteBuffer.array(), 0, mReadByteBuffer.remaining());
                    }
                    mReadByteBuffer.clear();
                    readReceiveLength = 0;
                }

                if (readLength > 0) {
                    readTotalLength += readLength;
                } else {
                    break;
                }
            }

            mReadByteBuffer.flip();
            if (mReadByteBuffer.remaining() > 0) {
                this.mMessageProcessor.onReceiveData(this, mReadByteBuffer.array(), 0, mReadByteBuffer.remaining());
            }
            mReadByteBuffer.clear();

        } catch (Exception e) {
            e.printStackTrace();
            readRet = false;
        }

        if (null != mMessageProcessor) {
            mMessageProcessor.onReceiveDataCompleted(this);
        }
        //退出客户端的时候需要把要写给该客户端的数据清空
        if (!readRet) {
            Message msg = pollWriteMessage();
            while (null != msg) {
                removeWriteMessage(msg);
                msg = pollWriteMessage();
            }
        }
        return readRet;
    }

    public boolean onWrite() {
        boolean writeRet = true;
        Message msg = pollWriteMessage();
        try {
            while (null != msg) {
                //如果消息块的大小超过缓存的最大值，则需要分段写入后才丢弃消息，不能在数据未完全写完的情况下将消息丢弃;avoid BufferOverflowException
                if (mWriteByteBuffer.capacity() < msg.length) {

                    int offset = 0;
                    int leftLength = msg.length;
                    int writtenTotalLength;

                    while (true) {

                        int putLength = leftLength > mWriteByteBuffer.capacity() ? mWriteByteBuffer.capacity() : leftLength;
                        mWriteByteBuffer.put(msg.data, offset, putLength);
                        mWriteByteBuffer.flip();
                        offset += putLength;
                        leftLength -= putLength;

                        int writtenLength = mSocketChannel.write(mWriteByteBuffer);//客户端关闭连接后，此处将抛出异常
                        writtenTotalLength = writtenLength;

                        while (writtenLength > 0 && mWriteByteBuffer.hasRemaining()) {
                            writtenLength = mSocketChannel.write(mWriteByteBuffer);
                            writtenTotalLength += writtenLength;
                        }
                        mWriteByteBuffer.clear();

                        if (leftLength <= 0) {
                            break;
                        }
                    }
                } else {
                    mWriteByteBuffer.put(msg.data, msg.offset, msg.length);
                    mWriteByteBuffer.flip();

                    int writtenLength = mSocketChannel.write(mWriteByteBuffer);//客户端关闭连接后，此处将抛出异常
                    int writtenTotalLength = writtenLength;

                    while (writtenLength > 0 && mWriteByteBuffer.hasRemaining()) {
                        writtenLength = mSocketChannel.write(mWriteByteBuffer);
                        writtenTotalLength += writtenLength;
                    }
                    mWriteByteBuffer.clear();
                }

                removeWriteMessage(msg);
                msg = pollWriteMessage();
            }

        } catch (IOException e) {
            e.printStackTrace();
            writeRet = false;
        }

        //退出客户端的时候需要把要写给该客户端的数据清空
        if (!writeRet) {
            if (null != msg) {
                removeWriteMessage(msg);
            }
            msg = pollWriteMessage();
            while (null != msg) {
                removeWriteMessage(msg);
                msg = pollWriteMessage();
            }
        }

        return writeRet;
    }
}
