package com.taobao.openimui.sample;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

import com.alibaba.mobileim.aop.Pointcut;
import com.alibaba.mobileim.aop.custom.IMCommonWidget;
import com.alibaba.mobileim.conversation.YWConversation;
import com.alibaba.mobileim.conversation.YWMessage;
import com.alibaba.mobileim.fundamental.widget.IYWAlertParams;


/**
 * Created by mayongge on 16/5/25.
 */
public class CommonWidgetSample extends IMCommonWidget {

    public CommonWidgetSample(Pointcut pointcut) {
        super(pointcut);
    }

    /**
     * 显示自定义toast
     * @param context
     * @param info
     * @param duration
     * @return true：使用自定义toast  false：使用SDK默认toast
     */
    @Override
    public boolean showCustomToast(Context context, String info, int duration) {
        Toast toast = Toast.makeText(context, info, duration);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
        return true;
    }

    /**
     * 自定义对话框
     * @param context       对话框所在页面context
     * @param type          对话框弹出场景，具体见{@link IYWAlertParams.IYWDialogType}
     * @param alertParams   SDK中默认对话框相关参数
     * @param conversation  该对话框对应的会话，可能为null
     * @param message       该对话框对应的消息，可能为null
     * @return              非null：使用自定义dialog  null：使用SDK默认dialog
     */
    @Override
    public AlertDialog getCustomDialog(Context context, int type, IYWAlertParams alertParams, YWConversation conversation, YWMessage message) {
        return null;
    }

    /**
     * 是否需要隐藏下拉刷新动画
     * @param activity
     * @param conversation 当前会话，如果pageId == PAGE_CONVERSATION_FRAGMENT，则该参数为null
     * @param pageId       该listView所在页面，pageId为{@link #PAGE_CHATTING_FRAGMENT}或者{@link #PAGE_CONVERSATION_FRAGMENT}
     * @return  true：隐藏下拉刷新动画  false：不隐藏
     */
    @Override
    public boolean needHidePullToRefreshView(Activity activity, YWConversation conversation, int pageId) {
//        if (pageId == IMCommonWidget.PAGE_CHATTING_FRAGMENT && conversation != null){
//            YWConversationType type = conversation.getConversationType();
//            if (type == YWConversationType.P2P){
//                return true;
//            } else {
//                return false;
//            }
//        }
        return false;
    }
}
