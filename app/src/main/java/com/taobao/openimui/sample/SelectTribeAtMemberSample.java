package com.taobao.openimui.sample;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.mobileim.aop.Pointcut;
import com.alibaba.mobileim.aop.custom.IMSelectTribeAtMemeberPageUI;
import com.alibaba.mobileim.appmonitor.tiptool.DensityUtil;
import com.alibaba.openIMUIDemo.R;


/**
 * Created by weiquanyun on 15/12/22.
 */
public class SelectTribeAtMemberSample extends IMSelectTribeAtMemeberPageUI {

    public SelectTribeAtMemberSample(Pointcut pointcut) {
        super(pointcut);
    }

    /**
     * 获取自定义title
     * @param activity
     * @param context
     * @param inflater
     * @return
     */
    @Override
    public View getCustomTitle(final Activity activity, Context context, LayoutInflater inflater) {
        View view = LayoutInflater.from(activity).inflate(R.layout.demo_custom_select_at_member_title, new RelativeLayout(context));
        //TODO 这里设置背景最好用color的方式来设置,因为界面中的一些字体颜色需要跟titleBar主题颜色一致
        view.setBackgroundColor(activity.getResources().getColor(R.color.aliwx_color_blue));
        TextView titleText = (TextView) view.findViewById(R.id.title);
        titleText.setText("成员列表");
        titleText.setTextColor(Color.WHITE);
        TextView back = (TextView) view.findViewById(R.id.back);
        back.setVisibility(View.GONE);
        TextView titleButton = (TextView) view.findViewById(R.id.title_button);
        titleButton.setText("取消");
        titleButton.setTextColor(Color.WHITE);
        titleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
            }
        });
        return view;
    }

    /**
     * 获取自定义搜索view
     * @param context 当前界面的context
     * @return 自定义搜索view，若返回null则使用sdk默认样式
     */
    @Override
    public View getCustomSearchView(Context context) {
        TextView textView = new TextView(context);
        textView.setText("搜索");
        textView.setGravity(Gravity.CENTER);
        textView.setCompoundDrawablesWithIntrinsicBounds(
                null, null, null, null);
        textView.setTextSize(15);
        textView.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.aliwx_btn_search_bar));
        textView.setTextColor(context.getResources().getColor(R.color.aliwx_color_gray_02));
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(context, 27));
        textView.setLayoutParams(layoutParams);
        return textView;
    }

    @Override
    public int getCustomAtOkButtonColor(Context context) {
        return context.getResources().getColor(R.color.aliwx_color_blue);
    }
}
