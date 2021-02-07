package com.wwq.self_shadow.pps;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Log;

import com.wwq.self_shadow.Constant;
import com.wwq.self_shadow.UserInfo;

import static com.wwq.self_shadow.pps.PPSBinder.TRANSACTION_CODE_FAILED_EXCEPTION;
import static com.wwq.self_shadow.pps.PPSBinder.TRANSACTION_CODE_NO_EXCEPTION;

/**
 * 用于控制插件的行为(可以在宿主进程中通过 IBinder 控制插件进程的行为)
 */
public class PpsController {
    // 宿主中用于和插件进程中的插件加载服务进行通讯的 IBinder
    final private IBinder mRemote;

    public PpsController(IBinder ppsBinder) {
        mRemote = ppsBinder;
    }

    /**
     * 测试序列化的数据传输
     * @return
     * @throws RemoteException
     * @throws FailedException
     */
    public UserInfo getUserInfo() throws RemoteException, FailedException {
        Parcel _data = Parcel.obtain();
        Parcel _reply = Parcel.obtain();
        UserInfo _result;
        try {
            _data.writeInterfaceToken(PPSBinder.DESCRIPTOR);
            mRemote.transact(PPSBinder.TRANSACTION_getUserInfo, _data, _reply, 0);
            _reply.readException();
            _result = new UserInfo(_reply);
            int i = _reply.readInt();
            if (i == TRANSACTION_CODE_FAILED_EXCEPTION) {
                throw new FailedException(_reply);
            } else if (i != TRANSACTION_CODE_NO_EXCEPTION) {
                throw new RuntimeException("不认识的Code==" + i);
            }
        } finally {
            _reply.recycle();
            _data.recycle();
        }
        return _result;
    }

    /**
     * 测试赋值
     * @param uuid
     * @throws RemoteException
     * @throws FailedException
     */
    public void setUUID(String uuid) throws RemoteException, FailedException {
        Parcel _data = Parcel.obtain();
        Parcel _reply = Parcel.obtain();
        try {
            _data.writeInterfaceToken(PPSBinder.DESCRIPTOR);
            _data.writeString(uuid);
            mRemote.transact(PPSBinder.TRANSACTION_SET_UUID, _data, _reply, 0);
            int i = _reply.readInt();
            if (i == TRANSACTION_CODE_FAILED_EXCEPTION) {
                throw new FailedException(_reply);
            } else if (i != TRANSACTION_CODE_NO_EXCEPTION) {
                throw new RuntimeException("不认识的Code==" + i);
            }
        } finally {
            _reply.recycle();
            _data.recycle();
        }
    }

    /**
     * 启动service
     * @param uuid
     * @throws RemoteException
     * @throws FailedException
     */
    public void startService(String uuid) throws RemoteException, FailedException{
        Parcel _data = Parcel.obtain();
        Parcel _reply = Parcel.obtain();
        try {
            _data.writeInterfaceToken(PPSBinder.DESCRIPTOR);
            _data.writeString(uuid);
            mRemote.transact(PPSBinder.TRANSACTION_START_SERVICE, _data, _reply, 0);
            int i = _reply.readInt();
            if (i == TRANSACTION_CODE_FAILED_EXCEPTION) {
                throw new FailedException(_reply);
            } else if (i != TRANSACTION_CODE_NO_EXCEPTION) {
                throw new RuntimeException("不认识的Code==" + i);
            }
        } finally {
            _reply.recycle();
            _data.recycle();
        }
    }

    public void exit() throws RemoteException, FailedException{
        Parcel _data = Parcel.obtain();
        Parcel _reply = Parcel.obtain();
        try {
            _data.writeInterfaceToken(PPSBinder.DESCRIPTOR);
            mRemote.transact(PPSBinder.TRANSACTION_EXIT, _data, _reply, 0);
        } finally {
            _reply.recycle();
            _data.recycle();
        }

    }

    /**
     * 由于插件中假的 activity 都在插件 apk 文件中，所以宿主 app 不知道类名，这里只能用字符串了
     * @param shadowActivity
     */
    public void startPluginActivity(String shadowActivity) throws RemoteException {
        Parcel _data = Parcel.obtain();
        Parcel _reply = Parcel.obtain();
        try {
            _data.writeInterfaceToken(PPSBinder.DESCRIPTOR);
            _data.writeString(shadowActivity);
            mRemote.transact(PPSBinder.TRANSACTION_START_ACTIVITY, _data, _reply, 0);
        } finally {
            _reply.recycle();
            _data.recycle();
        }
    }
    public void startPluginActivityForResult() throws RemoteException {
        Parcel _data = Parcel.obtain();
        Parcel _reply = Parcel.obtain();
        try {
            _data.writeInterfaceToken(PPSBinder.DESCRIPTOR);
            mRemote.transact(PPSBinder.TRANSACTION_START_ACTIVITY, _data, _reply, 0);
        } finally {
            _reply.recycle();
            _data.recycle();
        }
    }

    /**
     * 跨进程通讯，向插件进程发送信息
     * @param pluginKey
     * @throws RemoteException
     */
    public void loadPlugin(String pluginKey) throws RemoteException {
        Parcel _data = Parcel.obtain();
        Parcel _reply = Parcel.obtain();
        try {
            _data.writeInterfaceToken(PPSBinder.DESCRIPTOR);
            _data.writeString(pluginKey);
            mRemote.transact(PPSBinder.TRANSACTION_LOADPLUGIN, _data, _reply, 0);
            Log.d(Constant.TAG, "_reply : " + _reply.readString());
        } finally {
            _reply.recycle();
            _data.recycle();
        }
    }

}
