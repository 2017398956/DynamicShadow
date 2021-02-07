package com.wwq.self_shadow.pps;

import android.os.Binder;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Log;

import com.wwq.self_shadow.Constant;
import com.wwq.self_shadow.PPService;
import com.wwq.self_shadow.UserInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE;

/**
 * 插件加载服务进程中用于接收宿主进程发送来的信息，然后根据信息内容，控制插件行为
 */
public class PPSBinder extends Binder {

    static final String DESCRIPTOR = PPSBinder.class.getName();
    static final int TRANSACTION_CODE_NO_EXCEPTION = 0;
    static final int TRANSACTION_CODE_FAILED_EXCEPTION = 1;
    static final int TRANSACTION_SET_UUID = 2;
    static final int TRANSACTION_START_SERVICE = 3;
    static final int TRANSACTION_getUserInfo= 4;
    static final int TRANSACTION_EXIT= 5;
    static final int TRANSACTION_START_ACTIVITY= 6;
    public static final int TRANSACTION_LOADPLUGIN = 7;
    // 持有插件加载服务，以便于其它操作
    private PPService ppService;

    public PPSBinder(PPService ps) {
        this.ppService = ps;
    }

    @Override
    protected boolean onTransact(int code, @NonNull Parcel data, @Nullable Parcel reply, int flags) throws RemoteException {
        data.enforceInterface(DESCRIPTOR);
        String _arg0  = data.readString();
        Log.d(Constant.TAG, " code = " + code+" _arg0 ="+_arg0);
        switch (code){
            case TRANSACTION_SET_UUID:
                ppService.setUserName(_arg0);
                break;
            case TRANSACTION_START_SERVICE:
                try {
                    ppService.startService();
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(Constant.TAG,"e = "+e.toString());
                }
                break;
            case TRANSACTION_getUserInfo:
                UserInfo userInfo = ppService.getUserInfo();
                reply.writeNoException();
                userInfo.writeToParcel(reply, PARCELABLE_WRITE_RETURN_VALUE);
                return true;
            case TRANSACTION_EXIT:
                ppService.exit();
                reply.writeNoException();
                break;
            case TRANSACTION_START_ACTIVITY:
                ppService.starPluginActivity(_arg0);
                reply.writeNoException();
                break;
            case TRANSACTION_LOADPLUGIN:
                ppService.loadPlugin(_arg0);
                reply.writeNoException();
                reply.writeString("plugins added success!");
                break;
        }
        return super.onTransact(code, data, reply, flags);
    }
}
