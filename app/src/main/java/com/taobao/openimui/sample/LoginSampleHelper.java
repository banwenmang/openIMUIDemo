package com.taobao.openimui.sample;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;


import com.alibaba.mobileim.IYWLoginService;
import com.alibaba.mobileim.IYWP2PPushListener;
import com.alibaba.mobileim.IYWTribePushListener;
import com.alibaba.mobileim.YWAPI;
import com.alibaba.mobileim.YWChannel;
import com.alibaba.mobileim.YWConstants;
import com.alibaba.mobileim.YWIMKit;
import com.alibaba.mobileim.YWLoginParam;
import com.alibaba.mobileim.channel.LoginParam;
import com.alibaba.mobileim.channel.constant.B2BConstant;
import com.alibaba.mobileim.channel.event.IWxCallback;
import com.alibaba.mobileim.channel.util.AccountUtils;
import com.alibaba.mobileim.channel.util.WxLog;
import com.alibaba.mobileim.contact.IYWContact;
import com.alibaba.mobileim.contact.IYWContactCacheUpdateListener;
import com.alibaba.mobileim.contact.IYWContactOperateNotifyListener;
import com.alibaba.mobileim.conversation.IYWConversationService;
import com.alibaba.mobileim.conversation.YWConversation;
import com.alibaba.mobileim.conversation.YWCustomMessageBody;
import com.alibaba.mobileim.conversation.YWMessage;
import com.alibaba.mobileim.fundamental.model.YWIMSmiley;
import com.alibaba.mobileim.gingko.model.tribe.YWTribe;
import com.alibaba.mobileim.gingko.model.tribe.YWTribeMember;
import com.alibaba.mobileim.login.YWLoginState;
import com.alibaba.mobileim.login.YWPwdType;
import com.alibaba.mobileim.ui.chat.widget.YWSmilyMgr;
import com.alibaba.mobileim.utility.IMAutoLoginInfoStoreUtil;
import com.alibaba.mobileim.utility.IMNotificationUtils;
import com.alibaba.openIMUIDemo.LoginActivity;
import com.alibaba.openIMUIDemo.R;
import com.alibaba.tcms.env.EnvManager;
import com.alibaba.tcms.env.TcmsEnvType;
import com.alibaba.tcms.env.YWEnvManager;
import com.alibaba.tcms.env.YWEnvType;
import com.alibaba.wxlib.util.SysUtil;
import com.taobao.openimui.contact.ContactCacheUpdateListenerImpl;
import com.taobao.openimui.contact.ContactOperateNotifyListenerImpl;
import com.taobao.openimui.demo.DemoApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * SDK 初始化和登录
 *
 * @author jing.huai
 */
public class LoginSampleHelper {

    private static LoginSampleHelper sInstance = new LoginSampleHelper();

    public static LoginSampleHelper getInstance() {
        return sInstance;
    }

    // 应用APPKEY，这个APPKEY是申请应用时获取的
    public static  String APP_KEY = "23015524";

    //以下两个内容是测试环境使用，开发无需关注
//    public static final String APP_KEY_TEST = "4272";  //60026702

    public static final String APP_KEY_TEST = B2BConstant.APPKEY.APPKEY_B2B;  //60026702    60028148


    public static YWEnvType sEnvType = YWEnvType.TEST;

    // openIM UI解决方案提供的相关API，创建成功后，保存为全局变量使用
    private YWIMKit mIMKit;

    private Application mApp;

    private List<Map<YWTribe, YWTribeMember>> mTribeInviteMessages = new ArrayList<Map<YWTribe, YWTribeMember>>();

    public YWIMKit getIMKit() {
        return mIMKit;
    }

    public void setIMKit(YWIMKit imkit) {
        mIMKit = imkit;
    }

    public void initIMKit(String userId, String appKey) {
        mIMKit = YWAPI.getIMKitInstance(userId, appKey);
        addPushMessageListener();
        //添加联系人通知和更新监听 todo 在初始化后、登录前添加监听，离线的联系人系统消息才能触发监听器
        addContactListeners();

    }

    private YWLoginState mAutoLoginState = YWLoginState.idle;

    public YWLoginState getAutoLoginState() {
        return mAutoLoginState;
    }

    public void setAutoLoginState(YWLoginState state) {
        this.mAutoLoginState = state;
    }

    /**
     * 初始化SDK
     *
     * @param context
     */
    public void initSDK_Sample(Application context) {
        mApp = context;
        sEnvType = YWEnvManager.getEnv(context);
        //初始化IMKit
		final String userId = IMAutoLoginInfoStoreUtil.getLoginUserId();
		final String appkey = IMAutoLoginInfoStoreUtil.getAppkey();
        TcmsEnvType type = EnvManager.getInstance().getCurrentEnvType(mApp);
        if(type==TcmsEnvType.ONLINE || type == TcmsEnvType.PRE){
            if(TextUtils.isEmpty(appkey)) {
                YWAPI.init(mApp, APP_KEY);
            } else {
                YWAPI.init(mApp, appkey);
            }
        }
        else if(type==TcmsEnvType.TEST){
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            String appKey = preferences.getString("app_key", "");
            WxLog.i("APPKEY", "appKey = " + appKey);
            if (!TextUtils.isEmpty(appKey)){
                YWAPI.aliInit(mApp, appKey, AccountUtils.SITE_CNALICNH);
            } else {
                YWAPI.aliInit(mApp, B2BConstant.APPKEY.APPKEY_B2B, AccountUtils.SITE_CNALICNH);
            }
//            YWAPI.init(mApp, APP_KEY_TEST);
        }

        if (!TextUtils.isEmpty(userId) && !TextUtils.isEmpty(appkey)){
            LoginSampleHelper.getInstance().initIMKit(userId, appkey);
        }

        //通知栏相关的初始化
        NotificationInitSampleHelper.init();
        initAutoLoginStateCallback();


        //添加自定义表情的初始化
        YWSmilyMgr.setSmilyInitNotify(new YWSmilyMgr.SmilyInitNotify() {
            @Override
            public void onDefaultSmilyInitOk() {
                //隐藏默认表情，必须在添加前调用
//                SmilyCustomSample.hideDefaultSmiley();
//                SmilyCustomSample.addDefaultSmiley();
                //如果开发者想修改顺序（把sdk默认的放在自己添加的表情后面），可以先hide默认的然后再最后添加默认的
                SmilyCustomSample.addNewEmojiSmiley();
                YWIMSmiley smiley = SmilyCustomSample.addNewImageSmiley();
                smiley.setIndicatorIconResId(R.drawable.aliwx_s012);
                SmilyCustomSample.setSmileySize(YWIMSmiley.SMILEY_TYPE_IMAGE, 60);
                //最后要清空通知，防止memory leak
                YWSmilyMgr.setSmilyInitNotify(null);
            }
        });
    }

    //将自动登录的状态广播出去
    private void sendAutoLoginState(YWLoginState loginState) {
        Intent intent = new Intent(LoginActivity.AUTO_LOGIN_STATE_ACTION);
        intent.putExtra("state", loginState.getValue());
        LocalBroadcastManager.getInstance(YWChannel.getApplication()).sendBroadcast(intent);
    }

    /**
     * 登录操作
     *
     * @param userId   用户id
     * @param password 密码
     * @param callback 登录操作结果的回调
     */
    //------------------请特别注意，OpenIMSDK会自动对所有输入的用户ID转成小写处理-------------------
    //所以开发者在注册用户信息时，尽量用小写
    public void login_Sample(String userId, String password, String appKey,
                             IWxCallback callback) {

        if (mIMKit == null) {
            return;
        }

        SysUtil.setCnTaobaoInit(true);
        YWLoginParam loginParam = YWLoginParam.createLoginParam(userId,
                password);
        if (TextUtils.isEmpty(appKey) || appKey.equals(YWConstants.YWSDKAppKey)
                || appKey.equals(YWConstants.YWSDKAppKeyCnHupan)) {
            loginParam.setServerType(LoginParam.ACCOUNTTYPE_WANGXIN);
            loginParam.setPwdType(YWPwdType.pwd);
        }
        // openIM SDK提供的登录服务
        IYWLoginService mLoginService = mIMKit.getLoginService();

        mLoginService.login(loginParam, callback);
    }

    /**
     * 添加新消息到达监听，该监听应该在登录之前调用以保证登录后可以及时收到消息
     */
    private void addPushMessageListener(){
        if (mIMKit == null) {
            return;
        }

        IYWConversationService conversationService = mIMKit.getConversationService();
        //添加单聊消息监听，先删除再添加，以免多次添加该监听
        conversationService.removeP2PPushListener(mP2PListener);
        conversationService.addP2PPushListener(mP2PListener);

        //添加群聊消息监听，先删除再添加，以免多次添加该监听
        conversationService.removeTribePushListener(mTribeListener);
        conversationService.addTribePushListener(mTribeListener);
    }

    private IYWContactOperateNotifyListener mContactOperateNotifyListener = new ContactOperateNotifyListenerImpl();

    private IYWContactCacheUpdateListener mContactCacheUpdateListener = new ContactCacheUpdateListenerImpl();

    /**
     * 联系人相关操作通知回调，SDK使用方可以实现此接口来接收联系人操作通知的监听
     * 所有方法都在UI线程调用
     * SDK会自动处理这些事件，一般情况下，用户不需要监听这个事件
     * @author shuheng
     *
     */
    private void addContactListeners(){
        //添加联系人通知和更新监听，先删除再添加，以免多次添加该监听
        removeContactListeners();
        if(mIMKit!=null){
            if(mContactOperateNotifyListener!=null)
                mIMKit.getContactService().addContactOperateNotifyListener(mContactOperateNotifyListener);
            if(mContactCacheUpdateListener!=null)
                mIMKit.getContactService().addContactCacheUpdateListener(mContactCacheUpdateListener);

        }
    }

    private void removeContactListeners(){
        if(mIMKit!=null){
            if(mContactOperateNotifyListener!=null)
                mIMKit.getContactService().removeContactOperateNotifyListener(mContactOperateNotifyListener);
            if(mContactCacheUpdateListener!=null)
                mIMKit.getContactService().removeContactCacheUpdateListener(mContactCacheUpdateListener);

        }
    }

    private IYWP2PPushListener mP2PListener = new IYWP2PPushListener() {
        @Override
        public void onPushMessage(IYWContact contact, List<YWMessage> messages) {
            for (YWMessage message : messages) {
                if (message.getSubType() == YWMessage.SUB_MSG_TYPE.IM_P2P_CUS) {
                    if (message.getMessageBody() instanceof YWCustomMessageBody) {
                        YWCustomMessageBody messageBody = (YWCustomMessageBody) message.getMessageBody();
                        if (messageBody.getTransparentFlag() == 1) {
                            String content = messageBody.getContent();
                            try {
                                JSONObject object = new JSONObject(content);
                                if (object.has("text")) {
                                    String text = object.getString("text");
                                    IMNotificationUtils.getInstance().showToast(DemoApplication.getContext(), "透传消息，content = " + text);
                                } else if (object.has("customizeMessageType")) {
                                    String customType = object.getString("customizeMessageType");
                                    if (!TextUtils.isEmpty(customType) && customType.equals(ChattingOperationCustomSample.CustomMessageType.READ_STATUS)) {
                                        YWConversation conversation = mIMKit.getConversationService().getConversationByConversationId(message.getConversationId());
                                        long msgId = Long.parseLong(object.getString("PrivateImageRecvReadMessageId"));
                                        conversation.updateMessageReadStatus(conversation, msgId);
                                    }
                                }
                            } catch (JSONException e) {
                            }
                        }
                    }
                }
            }
        }
    };

    private IYWTribePushListener mTribeListener = new IYWTribePushListener() {
        @Override
        public void onPushMessage(YWTribe tribe, List<YWMessage> messages) {
            //TODO 收到群消息
        }

    };

    /**
     * 登出
     */
    public void loginOut_Sample() {
        if (mIMKit == null) {
            return;
        }


        // openIM SDK提供的登录服务
        IYWLoginService mLoginService = mIMKit.getLoginService();
        mLoginService.logout(new IWxCallback() {

            @Override
            public void onSuccess(Object... arg0) {

            }

            @Override
            public void onProgress(int arg0) {

            }

            @Override
            public void onError(int arg0, String arg1) {

            }
        });
    }

    /**
     * 开发者不需要关注此方法，纯粹是DEMO自动登录的需要
     */
    private void initAutoLoginStateCallback() {
        YWChannel.setAutoLoginCallBack(new IWxCallback() {
            @Override
            public void onSuccess(Object... result) {
                mAutoLoginState = YWLoginState.success;
                sendAutoLoginState(mAutoLoginState);
            }

            @Override
            public void onError(int code, String info) {
                mAutoLoginState = YWLoginState.fail;
                sendAutoLoginState(mAutoLoginState);
            }

            @Override
            public void onProgress(int progress) {
                mAutoLoginState = YWLoginState.logining;
                sendAutoLoginState(mAutoLoginState);
            }
        });
    }

    public List<Map<YWTribe, YWTribeMember>> getTribeInviteMessages() {
        return mTribeInviteMessages;
    }
}
