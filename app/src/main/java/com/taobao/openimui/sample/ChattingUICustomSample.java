package com.taobao.openimui.sample;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.NinePatch;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.alibaba.mobileim.YWChannel;
import com.alibaba.mobileim.YWIMKit;
import com.alibaba.mobileim.aop.Pointcut;
import com.alibaba.mobileim.aop.custom.IMChattingBizService;
import com.alibaba.mobileim.aop.custom.IMChattingPageUI;
import com.alibaba.mobileim.aop.model.YWInputViewPlugin;
import com.alibaba.mobileim.channel.IMChannel;
import com.alibaba.mobileim.channel.constant.YWProfileSettingsConstants;
import com.alibaba.mobileim.channel.event.IWxCallback;
import com.alibaba.mobileim.channel.util.AccountUtils;
import com.alibaba.mobileim.contact.IYWContact;
import com.alibaba.mobileim.conversation.YWConversation;
import com.alibaba.mobileim.conversation.YWConversationType;
import com.alibaba.mobileim.conversation.YWMessage;
import com.alibaba.mobileim.conversation.YWP2PConversationBody;
import com.alibaba.mobileim.conversation.YWTribeConversationBody;
import com.alibaba.mobileim.fundamental.widget.RecordButton;
import com.alibaba.mobileim.fundamental.widget.WxAlertDialog;
import com.alibaba.mobileim.gingko.model.tribe.YWTribe;
import com.alibaba.mobileim.kit.chat.presenter.ChattingDetailPresenter;
import com.alibaba.mobileim.utility.IMNotificationUtils;
import com.alibaba.mobileim.utility.IMPrefsTools;
import com.alibaba.mobileim.utility.YWIMImageUtils;
import com.alibaba.openIMUIDemo.R;
import com.taobao.openimui.tribe.TribeConstants;
import com.taobao.openimui.tribe.TribeInfoActivity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * 聊天界面自带提供两种主题的自定义供用户方便的使用，用户可以通过{@link CustomSampleHelper｝中 实现 AdviceBinder.bindAdvice(PointCutEnum.CHATTING_FRAGMENT_UI_POINTCUT, ChattingUICustomSample.class);
 * 使用该主题的聊天界面自定义风格：文字和图片小猪气泡风格
 *
 * todo 聊天界面的自定义风格1：文字和图片小猪气泡风格
 * Created by mayongge on 15-9-23.
 */
public class ChattingUICustomSample extends IMChattingPageUI {





    public ChattingUICustomSample(Pointcut pointcut) {
        super(pointcut);
    }

    /**
     * 设置消息气泡背景图，需要.9图
     * @param conversation 当前消息所在会话
     * @param message      需要设置背景的消息
     * @param self         是否是自己发送的消息，true：自己发送的消息， false：别人发送的消息
     * @return  0: 默认背景 －1:透明背景（无背景） >0:使用用户设置的背景图
     */
    @Override
    public int getMsgBackgroundResId(YWConversation conversation, YWMessage message, boolean self) {
        if (true)
            return super.getMsgBackgroundResId(conversation, message, self);
        int msgType = message.getSubType();
        if (msgType == YWMessage.SUB_MSG_TYPE.IM_TEXT || msgType == YWMessage.SUB_MSG_TYPE.IM_AUDIO){
            if (self){
                return R.drawable.demo_talk_pop_r_bg;
            } else {
                return R.drawable.demo_talk_pop_l_bg;
            }
        } else if (msgType == YWMessage.SUB_MSG_TYPE.IM_IMAGE){
            if (self){
                return R.drawable.demo_talk_pic_pop_r_bg;
            } else {
                return R.drawable.demo_talk_pic_pop_l_bg;
            }
        } else if (msgType == YWMessage.SUB_MSG_TYPE.IM_VIDEO){
            if (self){
                return R.drawable.demo_talk_pic_pop_r_bg;
            } else {
                return R.drawable.demo_talk_pic_pop_l_bg;
            }
        } else if (msgType == YWMessage.SUB_MSG_TYPE.IM_GEO){
            if (self){
                return R.drawable.aliwx_comment_r_bg;
            } else {
                return R.drawable.aliwx_comment_l_bg;
            }
        } else if (msgType == YWMessage.SUB_MSG_TYPE.IM_P2P_CUS || msgType == YWMessage.SUB_MSG_TYPE.IM_TRIBE_CUS){
            if (self){
                return -1;
            } else {
                return -1;
            }
        }
        return super.getMsgBackgroundResId(conversation, message, self);
    }

    /**
     * 建议使用{@link #processBitmapOfLeftImageMsg｝和{@link #processBitmapOfRightImageMsg｝灵活修改Bitmap，达到对图像进行［圆角处理］,［裁减］等目的,这里建议return false
     * 设置是否需要将聊天界面的图片设置为圆角
     *
     * @return false: 不做圆角处理
     * <br>
     * true：做圆角处理（重要：返回true时不会做{@link #processBitmapOfLeftImageMsg｝和{@link #processBitmapOfRightImageMsg｝二次图片处理，两者互斥！）
     */

    @Override
    public boolean needRoundChattingImage() {
        return true;
    }

    /**
     * 设置聊天界面图片圆角的边角半径的长度(单位：dp)
     *
     * @return
     */
    @Override
    public float getRoundRadiusDps() {
        return 12.6f;
    }

    /**
     * 设置聊天窗口背景
     *
     * @return 聊天窗口背景，默认不显示
     */
    @Override
    public int getChattingBackgroundResId() {
        //聊天窗口背景，默认不显示
        return 0;
        // return R.drawable.demo_chatting_bg;
    }

    /**
     * 用于更灵活地加工［左边图片消息］的Bitmap用于显示，SDK内部会缓存之，后续直接使用缓存的Bitmap显示。例如：对图像进行［裁减］，［圆角处理］等等
     * 重要：使用该方法时：
     * 1.请将 {@link #needRoundChattingImage}设为return false(不裁剪圆角)，两者是互斥关系
     * 2.建议将{@link #getLeftImageMsgBackgroundResId}设为return－1（背景透明）
     *
     * @param input 网络获取的聊天图片
     * @return 供显示的Bitmap
     */
    public Bitmap processBitmapOfLeftImageMsg(Bitmap input) {
        Bitmap output = Bitmap.createBitmap(input.getWidth(),
                input.getHeight(), Bitmap.Config.ARGB_8888);
        //为提高性能，对取得的resource图片做缓存
        Bitmap distBitmap = YWIMImageUtils.getFromCacheOrDecodeResource(R.drawable.left_bubble);
        NinePatch np = new NinePatch(distBitmap, distBitmap.getNinePatchChunk(), null);
        Canvas canvas = new Canvas(output);
        final Paint paint = new Paint();
        final Rect rectSrc = new Rect(0, 0, input.getWidth(), input.getHeight());
        final RectF rectDist = new RectF(0, 0, input.getWidth(), input.getHeight());
        np.draw(canvas, rectDist);
        canvas.drawARGB(0, 0, 0, 0);
        //设置Xfermode
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
        canvas.drawBitmap(input, rectSrc, rectSrc, paint);
        return output;
    }

    /**
     * 用于更灵活地加工［右边图片消息］的Bitmap用于显示，SDK内部会缓存之，后续直接使用缓存的Bitmap显示。例如：对图像进行［裁减］，［圆角处理］等等
     * 重要：使用该方法时：
     * 1.请将 {@link #needRoundChattingImage}设为return false(不裁剪圆角)，两者是互斥关系
     * 2.建议将{@link #getRightImageMsgBackgroundResId}设为return－1（背景透明）
     *
     * @param input 网络获取的聊天图片
     * @return 供显示的Bitmap
     */
    public Bitmap processBitmapOfRightImageMsg(Bitmap input) {
        Bitmap output = Bitmap.createBitmap(input.getWidth(),
                input.getHeight(), Bitmap.Config.ARGB_8888);
        //为提高性能，对取得的resource图片做缓存
        Bitmap distBitmap = YWIMImageUtils.getFromCacheOrDecodeResource(R.drawable.right_bubble);
        NinePatch np = new NinePatch(distBitmap, distBitmap.getNinePatchChunk(), null);
        Canvas canvas = new Canvas(output);
        final Paint paint = new Paint();
        final Rect rectSrc = new Rect(0, 0, input.getWidth(), input.getHeight());
        final RectF rectDist = new RectF(0, 0, input.getWidth(), input.getHeight());
        np.draw(canvas, rectDist);
        canvas.drawARGB(0, 0, 0, 0);
        //设置Xfermode
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
        canvas.drawBitmap(input, rectSrc, rectSrc, paint);
        return output;
    }

    /**
     * 是否隐藏标题栏
     *
     * @param fragment
     * @param conversation
     * @return true: 隐藏标题栏  false：不隐藏标题栏
     */
    @Override
    public boolean needHideTitleView(Fragment fragment, YWConversation conversation) {
//        if (conversation.getConversationType() == YWConversationType.Tribe){
//            return true;
//        }
        //@消息功能需要展示标题栏，暂不隐藏
        return false;
    }

    /**
     * isv需要返回自定义的view. openIMSDK会回调这个方法，获取用户设置的view. Fragment 聊天界面的fragment
     */
    @Override
    public View getCustomTitleView(final Fragment fragment,
                                   final Context context, LayoutInflater inflater,
                                   final YWConversation conversation) {
        // 单聊和群聊都会使用这个方法，所以这里需要做一下区分
        // 本demo示例是处理单聊，如果群聊界面也支持自定义，请去掉此判断

        //TODO 重要：必须以该形式初始化view---［inflate(R.layout.**, new RelativeLayout(context),false)］------，以让inflater知道父布局的类型，否则布局**中的高度和宽度无效，均变为wrap_content
        View view = inflater.inflate(R.layout.demo_custom_chatting_title, new RelativeLayout(context), false);
        view.setBackgroundColor(Color.parseColor("#00b4ff"));
        TextView textView = (TextView) view.findViewById(R.id.title);
        String title = null;
        if (conversation.getConversationType() == YWConversationType.P2P) {
            YWP2PConversationBody conversationBody = (YWP2PConversationBody) conversation
                    .getConversationBody();
            if (!TextUtils.isEmpty(conversationBody.getContact().getShowName())) {
                title = conversationBody.getContact().getShowName();
            } else {

                YWIMKit imKit = LoginSampleHelper.getInstance().getIMKit();
                IYWContact contact = imKit.getContactService().getContactProfileInfo(conversationBody.getContact().getUserId(), conversationBody.getContact().getAppKey());
                //生成showName，According to id。
                if (contact != null && !TextUtils.isEmpty(contact.getShowName())) {
                    title = contact.getShowName();
                }
            }
            //如果标题为空，那么直接使用Id
            if (TextUtils.isEmpty(title)) {
                title = conversationBody.getContact().getUserId();
            }
        } else {
            if (conversation.getConversationBody() instanceof YWTribeConversationBody) {
                title = ((YWTribeConversationBody) conversation.getConversationBody()).getTribe().getTribeName();
                if (TextUtils.isEmpty(title)) {
                    title = "自定义的群标题";
                }
            } else {
                if (conversation.getConversationType() == YWConversationType.SHOP) { //为OpenIM的官方客服特殊定义了下、
                    title = AccountUtils.getShortUserID(conversation.getConversationId());
                }
            }
        }
        textView.setText(title);
        textView.setTextColor(Color.parseColor("#FFFFFF"));
        textView.setTextSize(15);
        TextView backView = (TextView) view.findViewById(R.id.back);
        backView.setTextColor(Color.parseColor("#FFFFFF"));
        backView.setTextSize(15);
        backView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.demo_common_back_btn_white, 0, 0, 0);
        backView.setGravity(Gravity.CENTER_VERTICAL);
        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                fragment.getActivity().finish();
            }
        });

        ImageView btn = (ImageView) view.findViewById(R.id.title_button);
        if (conversation.getConversationType() == YWConversationType.Tribe) {
            btn.setImageResource(R.drawable.aliwx_tribe_info_icon);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String conversationId = conversation.getConversationId();
                    long tribeId = Long.parseLong(conversationId.substring(5));
                    Intent intent = new Intent(fragment.getActivity(), TribeInfoActivity.class);
                    intent.putExtra(TribeConstants.TRIBE_ID, tribeId);
                    fragment.getActivity().startActivity(intent);

                }
            });
            btn.setVisibility(View.VISIBLE);
        } else if (conversation.getConversationType() == YWConversationType.P2P) {
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    YWP2PConversationBody pConversationBody = (YWP2PConversationBody) conversation.getConversationBody();
                    String appKey = pConversationBody.getContact().getAppKey();
                    String userId = pConversationBody.getContact().getUserId();
                    Intent intent = ContactSettingActivity.getContactSettingActivityIntent(context, appKey, userId);
                    context.startActivity(intent);
                }
            });
            btn.setVisibility(View.VISIBLE);

            String feedbackAccount = IMPrefsTools.getStringPrefs(IMChannel.getApplication(), IMPrefsTools.FEEDBACK_ACCOUNT, "");
            if (!TextUtils.isEmpty(feedbackAccount) && feedbackAccount.equals(AccountUtils.getShortUserID(conversation.getConversationId()))) {
                btn.setVisibility(View.GONE);
            }
        }

        //群会话则显示@图标
        if (YWSDKGlobalConfigSample.getInstance().enableTheTribeAtRelatedCharacteristic()) {
            if (conversation.getConversationBody() instanceof YWTribeConversationBody) {
                View atView = view.findViewById(R.id.aliwx_at_content);
                atView.setVisibility(View.VISIBLE);
                atView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = chattingBizService.getIMKit().getAtMsgListActivityIntent(context, conversation);
                        context.startActivity(intent);
                    }
                });
            }
        }
        return view;
    }


    /**
     * 定制图片预览页面titlebar右侧按钮点击事件
     *
     * @param fragment
     * @param message  当前显示的图片对应的ywmessage对象
     * @return true：使用用户定制的点击事件， false：使用默认的点击事件(默认点击事件为保持该图片到本地)
     */
    @Override
    public boolean onImagePreviewTitleButtonClick(Fragment fragment, YWMessage message) {
//        Context context = fragment.getActivity();
//        Toast.makeText(context, "你点击了该按钮~", Toast.LENGTH_SHORT).show();
        return false;
    }

    /**
     * 返回图片保存的目录
     *
     * @param fragment
     * @param message
     * @return 如果为null, 使用SDK默认的目录, 否则使用用户设置的目录
     */
    @Override
    public String getImageSavePath(Fragment fragment, YWMessage message) {
//        return Environment
//                .getExternalStorageDirectory().getAbsolutePath()
//                + "/alibaba/WXOPENI/云旺相册";
        return null;
    }

    /**
     * 返回单聊默认头像资源Id
     *
     * @return 0:使用SDK默认提供的
     */
    @Override
    public int getDefaultHeadImageResId() {
        return 0;
    }

    /**
     * 是否需要圆角矩形的头像
     *
     * @return true:需要圆角矩形
     * <br>
     * false:不需要圆角矩形，默认为圆形
     * <br>
     * 注：如果返回true，则需要使用{@link #getRoundRectRadius()}给出圆角的设置半径，否则无圆角效果
     */
    @Override
    public boolean isNeedRoundRectHead() {
        return true;
    }

    /**
     * 返回设置圆角矩形的圆角半径大小
     *
     * @return 0:如果{@link #isNeedRoundRectHead()}返回true，此处返回0则表示头像显示为直角正方形
     */
    @Override
    public int getRoundRectRadius() {
        return 10;
    }

    /**
     * 聊天界面顶部展示的自定义View,这里的具体场景是当群消息屏蔽时展示的提示条
     *
     * @param fragment 聊天界面的Fragment
     * @param intent   打开聊天界面Activity的Intent
     * @return 返回要展示的View
     */
    @Override
    public View getChattingFragmentCustomViewAdvice(Fragment fragment, Intent intent) {

        if (intent != null && intent.hasExtra("extraTribeId") && intent.hasExtra("conversationType")) {
            final long tribeId = intent.getLongExtra("extraTribeId", 0);
            int conversationType = intent.getIntExtra("conversationType", -1);
            if (tribeId > 0 && conversationType == YWConversationType.Tribe.getValue()) {
                YWIMKit mIMKit = LoginSampleHelper.getInstance().getIMKit();
                if (mIMKit == null) {
                    return null;
                }
                final YWTribe tribe = mIMKit.getTribeService().getTribe(tribeId);

                if (tribe != null && tribe.getMsgRecType() == YWProfileSettingsConstants.TRIBE_MSG_REJ) { //群在屏蔽的时候才显示。
                    final Activity context = fragment.getActivity();
                    final TextView view = new TextView(context);
                    ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            (int) context.getResources().getDimension(R.dimen.hint_text_view_height));
                    lp.setMargins(0, (int) context.getResources().getDimension(R.dimen.title_bar_height), 0, 0);
                    view.setLayoutParams(lp);
                    view.setGravity(Gravity.CENTER);
                    view.setBackgroundResource(R.color.third_text_color);
                    view.setText("你屏蔽了本群的消息，点击接收群消息");
                    view.setTextColor(context.getResources().getColor(R.color.aliwx_common_bg_white_color));
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final AlertDialog alertDialog = new WxAlertDialog.Builder(context)
                                    .setTitle("提示")
                                    .setMessage("接收群消息可能会产生较大数据流量，您确定接收吗？")
                                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(final DialogInterface dialog, int which) {
                                            receiveTribeMsg(tribe, view);
                                            dialog.dismiss();
                                        }
                                    }).create();
                            alertDialog.show();
                        }
                    });

                    return view;
                }
            }
        }
        return null;
    }

    /**
     * 是否需要在聊天界面展示顶部自定义View
     *
     * @param fragment 聊天界面的Fragment
     * @param intent   打开聊天界面Activity的Intent
     * @return
     */
    @Override
    public boolean isUseChattingCustomViewAdvice(Fragment fragment, Intent intent) {
        if (intent != null && intent.hasExtra("extraTribeId") && intent.hasExtra("conversationType")) {
            long tribeId = intent.getLongExtra("extraTribeId", 0);
            int conversationType = intent.getIntExtra("conversationType", -1);
            if (tribeId > 0 && conversationType == YWConversationType.Tribe.getValue()) {
                return true;
            }
        }
        return false;
    }

    private void receiveTribeMsg(YWTribe tribe, final View view) {
        LoginSampleHelper.getInstance().getIMKit().getIMCore().getTribeService().unblockTribe(tribe, new IWxCallback() {
            @Override
            public void onSuccess(Object... result) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        view.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onError(int code, String info) {

            }

            @Override
            public void onProgress(int progress) {

            }
        });
    }

    /**
     * getView方法内，返回View之前，对［聊天界面的右边消息item的View］做最后调整,如调整View的Padding。
     *
     * @param msg
     * @param rightItemParentView
     * @param fragment
     * @param conversation
     */
    @Override
    public void modifyRightItemParentViewAfterSetValue(YWMessage msg, RelativeLayout rightItemParentView, Fragment fragment, YWConversation conversation) {
//        if(msg!=null&&rightItemParentView!=null&&msg.getSubType()==YWMessage.SUB_MSG_TYPE.IM_IMAGE||msg.getSubType()==YWMessage.SUB_MSG_TYPE.IM_GIF){
//            rightItemParentView.setPadding(rightItemParentView.getPaddingLeft(), rightItemParentView.getPaddingTop(), 0, rightItemParentView.getPaddingBottom());
//        }
    }

    /**
     * getView方法内，返回View之前，对［聊天界面的左边消息item的View］做最后调整,如调整View的Padding。
     *
     * @param msg
     * @param leftItemParentView
     * @param fragment
     * @param conversation
     */
    @Override
    public void modifyLeftItemParentViewAfterSetValue(YWMessage msg, RelativeLayout leftItemParentView, Fragment fragment, YWConversation conversation) {

//        if(msg!=null&&leftItemParentView!=null&&msg.getSubType()==YWMessage.SUB_MSG_TYPE.IM_IMAGE||msg.getSubType()==YWMessage.SUB_MSG_TYPE.IM_GIF) {
//            leftItemParentView.setPadding(0, leftItemParentView.getPaddingTop(), leftItemParentView.getPaddingRight(), leftItemParentView.getPaddingBottom());
//        }
    }

    /**
     * 是否隐藏底部ChattingReplyBar
     *
     * @return
     */
    @Override
    public boolean needHideChattingReplyBar() {
        return false;
    }

    /**
     * 是否隐藏底部ChattingReplyBar
     *
     * @return
     */
    @Override
    public boolean needHideChattingReplyBar(YWConversation conversation) {
        return false;
    }

    /**
     * 是否隐藏表情发送入口
     *
     * @return true:隐藏表情按钮
     * false:显示表情按钮
     */
    @Override
    public boolean needHideFaceView() {
        return false;
    }

    /**
     * 是否隐藏语音发送入口
     *
     * @return true:隐藏语音发送按钮
     * false:显示语音发送按钮
     */
    @Override
    public boolean needHideVoiceView() {
        return false;
    }

    /**
     * 返回自定义ChattingReplyBar高度(单位为dp)
     *
     * @return 如果返回值小于等于0, 则使用默认值
     */
    @Override
    public int getCustomChattingReplyBarHeight() {
        return 0;
    }

    /**
     * 返回自定义聊天输入框高度(单位为dp)
     *
     * @return 如果返回值小于等于0, 则使用默认值
     */
    @Override
    public int getCustomChattingInputEditTextHeight() {
        return 0;
    }

    /**
     * 返回表情按钮图标背景资源Id
     * @return
     */
    @Override
    public int getFaceViewBgResId() {
        return 0;
    }

    /**
     * 返回"+号"按钮选中图标背景资源Id
     * @return
     */
    @Override
    public int getExpandViewCheckedBgResId() {
        return 0;
    }

    /**
     * 返回"+号"按钮取消选中图标背景资源Id
     * @return
     */
    @Override
    public int getExpandViewUnCheckedBgResId() {
        return 0;
    }

    /**
     * 返回发送按钮背景资源Id
     * @return
     */
    @Override
    public int getSendButtonBgId() {
        return 0;
    }

    /**
     * 返回语音按钮图标背景资源Id
     * @return
     */
    @Override
    public int getVoiceViewBgResId() {
        return 0;
    }

    /**
     * 返回键盘按钮图标资源Id
     * @return
     */
    @Override
    public int getKeyboardViewBgResId() {
        return 0;
    }

    /**
     * UI仅显示语音发送
     * @return
     */
    @Override
    public boolean onlySupportAudio(){
        return false;
    }

    public void onCustomDrawRecordButton(Canvas canvas, RecordButton button){
        //这里也可以进行自绘的一些操作
//        if (button.isLongPress()){
//            button.setText("松开");
//        }else{
//            button.setText("按住");
//        }
    }

    /**
     * 设置声音播放ImageView
     * @param view
     * @param index 0,1,2,3，分别表示动画第一帧，第二帧，第三帧,及静态的图片
     * @param direction 0，自己，1，对方
     */
    @Override
    public void onSetAudioContentImage(ImageView view, int index, int direction){

    }

    /**
     * 修改ChattingReplyBar上的item，可以修改属性或者新增。如果开发者相同类型的会话plugin一样，则可以缓存pluginItems，
     * 在该方法调用时直接用缓存pluginItems替换掉参数中的pluginItems，替换方式为先removeAll，再addAll
     * <p>可以修改的属性包括：
     *  <p>顺序</p>
     *  <p>是否显示({@link YWInputViewPlugin#setNeedHide(boolean)}),</p>
     *  <p>Item对应的View的基本操作,</p>
     * </p>
     * <p>
     *     SDK默认按钮的id为：
     *     <p>{@link YWChattingPlugin.ReplyBarPlugin#VOICE_VIEW}
     *     <p>{@link YWChattingPlugin.ReplyBarPlugin#INPUT_EDIT_TEXT}
     *     <p>{@link YWChattingPlugin.ReplyBarPlugin#FACE_VIEW}
     *     <p>{@link YWChattingPlugin.ReplyBarPlugin#EXPAND_VIEW}
     *
     * <p/>
     * 如果新增则创建一个{@link YWInputViewPlugin}并添加到replyBarItems。
     * @param conversation ChattingReplyBar所在会话,如果开发者需要可以根据会话类型对ChattingReplyBar的item进行调整
     * @param pluginItems item列表，初始是包含sdk默认提供的4个item：语音按钮、输入框、表情按钮、”+号“按钮，顺序索引为0,1,2,3
     * @return 使用sdk默认顺序则无需任何操作
     */
    @Override
    public List<YWInputViewPlugin> adjustCustomInputViewPlugins(final Fragment fragment, YWConversation conversation, List<YWInputViewPlugin> pluginItems) {
//        if (pluginItems != null && pluginItems.size() > 0) {
//            //对默认项进行操作，可以使用id判断具体是哪一个
//            for (YWInputViewPlugin plugin : pluginItems) {
//                if (plugin.getId() == YWChattingPlugin.ReplyBarPlugin.VOICE_VIEW) {
//                    plugin.setNeedHide(true);//隐藏语音输入按钮
//                }
//            }
//            //自定义新增的plugin只需要在布局中设置marginLeft即可
//            final View plugin = LayoutInflater.from(fragment.getActivity()).inflate(R.layout.demo_extra_item_layout, null);
//            //TODO 新增项id必须从4开始
//            final YWInputViewPlugin pluginToAdd = new YWInputViewPlugin(plugin, 4);
//            plugin.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Toast.makeText(fragment.getActivity(), "你点击了Id为:" + pluginToAdd.getId() + "的新增item", Toast.LENGTH_LONG).show();
//                }
//            });
//            pluginToAdd.setIndex(0);//在设置index时，如果新增项和默认pluginItem一样则开发者添加的显示在前
//            pluginItems.add(pluginToAdd);
//        }
        return pluginItems;
    }

    private IMChattingBizService chattingBizService;

    @Override
    public void onInitFinished(final IMChattingBizService bizService){
        chattingBizService = bizService;
//        final Context context = chattingBizService.getFragment().getContext();
//        IYWChattingReplyBar replyBar = chattingBizService.getChattingReplyBar();
//        replyBar.setInputEditTextRightDrawable(context.getResources().getDrawable(R.drawable.ww_chat_voice), new OnEditTextDrawableClickListener() {
//            @Override
//            public void onClick() {
//                IMNotificationUtils.getInstance().showToast(context, "啦啦啦啦啦啦啦！");
//            }
//        });
    }


    /**
     * 获取文本消息中的文字颜色，该接口可以设置文本、链接和数字的颜色
     * @param conversation 当前会话
     * @param isSelf       是否是自己发送的消息，true：自己发送的消息，false：别人发送的消息
     * @param type         文本类型，1：普通文本，2：链接，3：数字
     * @return 颜色资源Id
     */
    @Override
    public int getCustomTextColor(YWConversation conversation, boolean isSelf, int type) {
        if (type == 1){
            //普通文字颜色
        } else if (type == 2){
            //超链接颜色
        } else if (type == 3){
            //数字颜色
        }
        return super.getCustomTextColor(conversation, isSelf, type);
    }

    /**
     * 聊天窗口消息item的头像上侧是否需要显示消息发送者昵称
     * @param conversation 当前聊天窗口所在会话
     * @param self         是否是自己发送的消息
     * @return true：显示昵称  false：不显示昵称
     */
    @Override
    public boolean needShowName(YWConversation conversation, boolean self) {
        if (self){  //自己发送的消息，即右侧的消息
            return false;
        } else {
            YWConversationType type = conversation.getConversationType();
            if (type == YWConversationType.SHOP || type == YWConversationType.Tribe){
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * 设置底部工具栏与键盘对齐
     */
    @Override
    public boolean needAlignReplyBar() {
        return true;
    }

    //生命周期 和chatfragment相同
    private boolean mIsMyComputerConv;

    @Override
    public void onStart(Fragment f, Intent intent, ChattingDetailPresenter presenter) {
        super.onStart(f, intent, presenter);
        mIsMyComputerConv = intent.getBooleanExtra(ChattingDetailPresenter.EXTRA_MYCOMPUTER, false);
    }

    public boolean isMyComputerConv() {
        return mIsMyComputerConv;
    }

    @Override
    public List<String> getMenuList(IYWContact loginAccount, YWMessage message) {
        List<String> list = new ArrayList<String>();
        list.add("保存到淘云盘");
        return list;
    }

    @Override
    public void onItemClick(IYWContact loginAccount, YWMessage message, Bitmap bitmap, String item) {
        IMNotificationUtils.getInstance().showToast(item, YWChannel.getApplication());
    }
}
