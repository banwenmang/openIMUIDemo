package com.taobao.openimui.sample;

import com.alibaba.mobileim.YWIMKit;
import com.alibaba.mobileim.conversation.IYWConversationService;
import com.alibaba.mobileim.conversation.YWConversation;
import com.alibaba.mobileim.conversation.YWCustomConversationUpdateModel;

import java.util.Date;


/**
 * 会话列表增加一个自定义会话
 *
 * @author zhaoxu
 */
public class CustomConversationHelper {

    private static YWCustomConversationUpdateModel mCustomConversation;
    private static YWCustomConversationUpdateModel mCustomViewConversation;

    public static void addCustomConversation(String conversationID, String content) {

        mCustomConversation = new YWCustomConversationUpdateModel();
        mCustomConversation.setIdentity(conversationID);
        mCustomConversation.setContent(content);
        mCustomConversation.setLastestTime(new Date().getTime());
        mCustomConversation.setSubType(1);
        mCustomConversation.setExtraData("extraData");
        mCustomConversation.setExtraData1("extraData_1");
        mCustomConversation.setExtraData2("extraData_2");

        YWIMKit imKit = LoginSampleHelper.getInstance().getIMKit();
        IYWConversationService conversationService = imKit.getConversationService();
        YWConversation conversation = conversationService.getCustomConversationByConversationId(conversationID);
        if (conversation != null) {
            conversation.getUnreadCount();
        }

        //更新或者创建一个自定义会话,如果会话已存在则更新 。conversationId作为会话的标识必须传入.unreadCount 属性如果设置了会累加。
        conversationService.updateOrCreateCustomConversation(mCustomConversation);
    }

    public static void addCustomViewConversation(String conversationID, String content) {

        mCustomViewConversation = new YWCustomConversationUpdateModel();
        mCustomViewConversation.setIdentity(conversationID);
        mCustomViewConversation.setContent(content);
        mCustomViewConversation.setLastestTime(new Date().getTime());

        YWIMKit imKit = LoginSampleHelper.getInstance().getIMKit();
        IYWConversationService conversationService = imKit.getConversationService();
        if (conversationService.getCustomViewConversationByConversationId(conversationID) == null) {
            conversationService.updateOrCreateCustomViewConversation(mCustomViewConversation);
        }
    }

    public static void updateCustomConversationContent(String content) {
        if (mCustomConversation == null)
            return;
        mCustomConversation.setContent(content);
        mCustomConversation.setLastestTime(new Date().getTime());

        YWIMKit imKit = LoginSampleHelper.getInstance().getIMKit();
        IYWConversationService conversationService = imKit.getConversationService();
        conversationService.updateOrCreateCustomConversation(mCustomConversation);
    }

    public static void updateCustomViewConversationContent(String content) {
        if (mCustomViewConversation == null)
            return;
        mCustomViewConversation.setContent(content);
        mCustomViewConversation.setLastestTime(new Date().getTime());

        YWIMKit imKit = LoginSampleHelper.getInstance().getIMKit();
        IYWConversationService conversationService = imKit.getConversationService();
        conversationService.updateOrCreateCustomViewConversation(mCustomViewConversation);
    }
}
