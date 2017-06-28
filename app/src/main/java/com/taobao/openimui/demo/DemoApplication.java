package com.taobao.openimui.demo;

import android.content.Context;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.alibaba.sdk.android.AlibabaSDK;
import com.alibaba.sdk.android.callback.InitResultCallback;
import com.alibaba.sdk.android.media.MediaService;
import com.alibaba.wxlib.util.SysUtil;
import com.taobao.openimui.sample.InitHelper;

public class DemoApplication extends MultiDexApplication {

    private static final String TAG = "DemoApplication";
    public static final String NAMESPACE = "openimdemo";
	//云旺OpenIM的DEMO用到的Application上下文实例
	private static Context sContext;
	public static Context getContext(){
		return sContext;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();

		//todo Application.onCreate中，首先执行这部分代码，以下代码固定在此处，不要改动，这里return是为了退出Application.onCreate！！！
		if(mustRunFirstInsideApplicationOnCreate()){
			//todo 如果在":TCMSSevice"进程中，无需进行openIM和app业务的初始化，以节省内存
			return;
		}

		//初始化云旺SDK
		InitHelper.initYWSDK(this);

		//初始化反馈功能(未使用反馈功能的用户无需调用该初始化)

		InitHelper.initFeedBack(this);

        //初始化多媒体SDK，小视频和阅后即焚功能需要使用多媒体SDK
        AlibabaSDK.asyncInit(this, new InitResultCallback() {
            @Override
            public void onSuccess() {
                Log.e(TAG, "-----initTaeSDK----onSuccess()-------" );
                MediaService mediaService = AlibabaSDK.getService(MediaService.class);
                mediaService.enableHttpDNS(); //果用户为了避免域名劫持，可以启用HttpDNS
                mediaService.enableLog(); //在调试时，可以打印日志。正式上线前可以关闭
            }

            @Override
            public void onFailure(int code, String msg) {
                Log.e(TAG, "-------onFailure----msg:" + msg + "  code:" + code);
            }
        });

    }



	private boolean mustRunFirstInsideApplicationOnCreate() {
		//必须的初始化
		SysUtil.setApplication(this);
		sContext = getApplicationContext();
		return SysUtil.isTCMSServiceProcess(sContext);
	}

}
