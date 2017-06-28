package com.taobao.openimui.sample;

import android.content.Context;
import android.content.Intent;

import com.alibaba.mobileim.aop.Pointcut;
import com.alibaba.mobileim.aop.custom.IMTribeAtPageOperation;
import com.alibaba.mobileim.conversation.YWConversationType;
import com.alibaba.mobileim.conversation.YWMessage;
import com.alibaba.mobileim.kit.chat.presenter.ChattingDetailPresenter;
import com.alibaba.mobileim.ui.WxChattingActvity;
import com.alibaba.mobileim.utility.UserContext;


/**
 * Created by weiquanyun on 16/5/24.
 */
public class AtMsgListOperationSample extends IMTribeAtPageOperation {

    public AtMsgListOperationSample(Pointcut pointcut) {
        super(pointcut);
    }

    /**
     * 如果开发者使用自己的Activity作为聊天界面Activity，并且使用了@功能，需要实现该方法，返回自己的Activity.class。
     * 否则点击收到的@消息会无法跳转到聊天界面
     * @return
     * @see this.getStartChatActivityIntent(Context, YWMessage)
     */
    @Deprecated
    @Override
    public Class getChattingActivityClass() {
        return WxChattingActvity.class;
    }

    /**
     * 返回跳转聊天界面的Intent
     * @param context 当前界面所在的Activity
     * @param appKey 当前登录用户的appKey
     * @param userId 当前登录用户的userId
     * @param atMessage 被点击的@消息
     * @return 跳转聊天界面的Intent
     */
    @Override
    public Intent getStartChatActivityIntent(Context context, String appKey, String userId, YWMessage atMessage) {
        Intent intent = new Intent(context, WxChattingActvity.class);
        intent.putExtra(ChattingDetailPresenter.EXTRA_CVS_TYPE, YWConversationType.Tribe);
        intent.putExtra(ChattingDetailPresenter.EXTRA_CVSID, atMessage.getConversationId());
        intent.putExtra(UserContext.EXTRA_USER_CONTEXT_KEY, new UserContext(userId, appKey));
        intent.putExtra(WxChattingActvity.AT_MSG, atMessage);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return intent;
    }
}
