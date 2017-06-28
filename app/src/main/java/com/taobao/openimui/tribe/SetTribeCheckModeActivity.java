package com.taobao.openimui.tribe;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.alibaba.mobileim.YWIMKit;
import com.alibaba.mobileim.channel.event.IWxCallback;
import com.alibaba.mobileim.fundamental.widget.WxAlertDialog;
import com.alibaba.mobileim.gingko.model.tribe.YWTribe;
import com.alibaba.mobileim.gingko.model.tribe.YWTribeCheckMode;
import com.alibaba.mobileim.gingko.model.tribe.YWTribeMember;
import com.alibaba.mobileim.gingko.presenter.tribe.IYWTribeChangeListener;
import com.alibaba.mobileim.gingko.presenter.tribe.TribeErrInfo;
import com.alibaba.mobileim.tribe.IYWTribeService;
import com.alibaba.mobileim.utility.AccountInfoTools;
import com.alibaba.mobileim.utility.IMNotificationUtils;
import com.alibaba.openIMUIDemo.R;
import com.taobao.openimui.demo.FragmentTabs;
import com.taobao.openimui.sample.LoginSampleHelper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SetTribeCheckModeActivity extends Activity implements View.OnClickListener {

    private RelativeLayout mNoVerifyLayout; //允许任何人加入群
    private RelativeLayout mPwdVerifyLayout; //需要密码验证
    private RelativeLayout mIdVerifyLayout; //需要身份验证
    private RelativeLayout mNobodyJoinLayout; //不允许任何人主动加入群
    private ImageView mNoVerify; //允许任何人加入群
    private ImageView mPwdVerify; //需要密码验证
    private ImageView mIdVerify; //需要身份验证
    private ImageView mNobodyJoin; //不允许任何人主动加入群

    private AlertDialog mPwdDialog;
    private TextView mDesView;
    private EditText mPasswordView;
    private ImageView mClearButton;

    private int mTribeCheckMode = -1;
    private long mTribeId;
    private YWIMKit mIMKit;
    private IYWTribeService mTribeService;
    private IYWTribeChangeListener mTribeChangeListener;
    private Handler mUIHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_activity_set_tribe_check_mode);
        initView();
        mIMKit = LoginSampleHelper.getInstance().getIMKit();
        mTribeService = mIMKit.getTribeService();
        initTribeChangeListener();
    }

    private void initView(){
        TextView back = (TextView) findViewById(R.id.title_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        TextView title = (TextView) findViewById(R.id.title_text);
        title.setText(getResources().getString(R.string.tribe_check_mode));

        mNoVerifyLayout = (RelativeLayout) findViewById(R.id.no_verification_layout);
        mNoVerifyLayout.setOnClickListener(this);
        mPwdVerifyLayout = (RelativeLayout) findViewById(R.id.pwd_verification_layout);
        mPwdVerifyLayout.setOnClickListener(this);
        mIdVerifyLayout = (RelativeLayout) findViewById(R.id.id_verification_layout);
        mIdVerifyLayout.setOnClickListener(this);
        mNobodyJoinLayout = (RelativeLayout) findViewById(R.id.nobody_join_layout);
        mNobodyJoinLayout.setOnClickListener(this);

        mNoVerify = (ImageView) findViewById(R.id.no_verification);
        mPwdVerify = (ImageView) findViewById(R.id.pwd_verification);
        mIdVerify = (ImageView) findViewById(R.id.id_verification);
        mNobodyJoin = (ImageView) findViewById(R.id.nobody_join);

        Intent intent = getIntent();
        mTribeId = intent.getLongExtra(TribeConstants.TRIBE_ID, 0);
        int checkMode = intent.getIntExtra(TribeConstants.TRIBE_CHECK_MODE, 0);
        updateCheckModeView(checkMode);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.no_verification_layout:
                modifyTribeCheckMode(YWTribeCheckMode.NO_VERIFICATION.type, null);
                break;
            case R.id.pwd_verification_layout:
                if (mTribeCheckMode == YWTribeCheckMode.PWD_VERIFICATION.type){
                    return;
                }
                showPwdDialog();
                break;
            case R.id.id_verification_layout:
                modifyTribeCheckMode(YWTribeCheckMode.ID_VERIFICATION.type, null);
                break;
            case R.id.nobody_join_layout:
                modifyTribeCheckMode(YWTribeCheckMode.NOBODY_JOIN.type, null);
                break;
            default:
                break;
        }
    }

    private void modifyTribeCheckMode(final int checkMode, final String password){
        if (mTribeCheckMode == checkMode){
            return;
        }
        mTribeService.modifyTribeCheckMode(new IWxCallback() {
            @Override
            public void onSuccess(Object... result) {
                mUIHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        updateCheckModeView(checkMode);
                        IMNotificationUtils.getInstance().showToast("修改群验证模式成功", SetTribeCheckModeActivity.this);
                        onBackPressed();
                    }
                });
            }

            @Override
            public void onError(int code, String info) {
                IMNotificationUtils.getInstance().showToast(TribeErrInfo.codeToInfo(code), SetTribeCheckModeActivity.this);
            }

            @Override
            public void onProgress(int progress) {

            }
        }, mTribeId, YWTribeCheckMode.getEnumType(checkMode), password);
    }

    private void updateCheckModeView(final int checkMode){
                mTribeCheckMode = checkMode;
                if (checkMode == YWTribeCheckMode.NO_VERIFICATION.type) {
                    mNoVerify.setVisibility(View.VISIBLE);
                    mPwdVerify.setVisibility(View.GONE);
                    mIdVerify.setVisibility(View.GONE);
                    mNobodyJoin.setVisibility(View.GONE);
                } else if (checkMode == YWTribeCheckMode.ID_VERIFICATION.type) {
                    mNoVerify.setVisibility(View.GONE);
                    mPwdVerify.setVisibility(View.GONE);
                    mIdVerify.setVisibility(View.VISIBLE);
                    mNobodyJoin.setVisibility(View.GONE);
                } else if (checkMode == YWTribeCheckMode.PWD_VERIFICATION.type) {
                    mNoVerify.setVisibility(View.GONE);
                    mPwdVerify.setVisibility(View.VISIBLE);
                    mIdVerify.setVisibility(View.GONE);
                    mNobodyJoin.setVisibility(View.GONE);
                } else {
                    mNoVerify.setVisibility(View.GONE);
                    mPwdVerify.setVisibility(View.GONE);
                    mIdVerify.setVisibility(View.GONE);
                    mNobodyJoin.setVisibility(View.VISIBLE);
                }

    }

    private void showPwdDialog(){
        final View view = getLayoutInflater().inflate(R.layout.demo_dialog_set_tribe_pwd, null);
        mPasswordView = (EditText) view.findViewById(R.id.password);
        mDesView = (TextView) view.findViewById(R.id.description);
        TextView cancel = (TextView) view.findViewById(R.id.cancel);
        final TextView confirm = (TextView) view.findViewById(R.id.confirm);
        mClearButton = (ImageView) view.findViewById(R.id.clear_pwd);
        mPwdDialog = new WxAlertDialog.Builder(this)
                .setView(view)
                .create();
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPwdDialog.dismiss();
            }
        });
        confirm.setOnClickListener(listener);
        mPasswordView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    listener.onClick(confirm);
                    return true;
                }
                return false;
            }
        });
        mClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPasswordView.setText("");
                mDesView.setTextColor(getResources().getColor(R.color.common_text_color2));
                mClearButton.setVisibility(View.GONE);
            }
        });

        if (!mPwdDialog.isShowing()){
            mPwdDialog.show();
        }
    }

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String password = mPasswordView.getText().toString();
            if (isPwdValid(password)){
                modifyTribeCheckMode(YWTribeCheckMode.PWD_VERIFICATION.type, password);
                mPwdDialog.dismiss();
            } else {
                mDesView.setTextColor(getResources().getColor(R.color.red));
                mClearButton.setVisibility(View.VISIBLE);
            }
        }
    };

    private boolean isPwdValid(String  pwd){
        String regEx = "^[A-Za-z0-9]{6,20}+$";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(pwd);
        return matcher.matches();
    }

    private void initTribeChangeListener() {
        mTribeChangeListener = new IYWTribeChangeListener() {
            @Override
            public void onInvite(YWTribe tribe, YWTribeMember user) {

            }

            @Override
            public void onUserJoin(YWTribe tribe, YWTribeMember user) {

            }

            @Override
            public void onUserQuit(YWTribe tribe, YWTribeMember user) {

            }

            @Override
            public void onUserRemoved(YWTribe tribe, YWTribeMember user) {
                String message = "您已被请出“" + tribe.getTribeName() + "”群";
                IMNotificationUtils.getInstance().showToast(message, SetTribeCheckModeActivity.this);
                openTribeListFragment();
            }

            @Override
            public void onTribeDestroyed(YWTribe tribe) {
                if (tribe.getTribeId() != mTribeId) {
                    return;
                }
                String message = tribe.getTribeName() + "”群已被解散";
                IMNotificationUtils.getInstance().showToast(message, SetTribeCheckModeActivity.this);
                openTribeListFragment();
            }

            @Override
            public void onTribeInfoUpdated(YWTribe tribe) {

            }

            @Override
            public void onTribeManagerChanged(YWTribe tribe, YWTribeMember user) {

            }

            @Override
            public void onTribeRoleChanged(YWTribe tribe, YWTribeMember user) {
                if (tribe.getTribeId() != mTribeId) {
                    return;
                }
                if (mIMKit.getIMCore().getLongLoginUserId().equals(AccountInfoTools.getPrefix(user.getAppKey() + user.getUserId()))
                        && user.getTribeRole() == YWTribeMember.ROLE_NORMAL) {
                    IMNotificationUtils.getInstance().showToast("您已被取消管理员", SetTribeCheckModeActivity.this);
                    finish();
                }
            }
        };
        mTribeService.addTribeListener(mTribeChangeListener);
    }

    @Override
    public void onBackPressed() {
        setResult(mTribeCheckMode);
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTribeService.removeTribeListener(mTribeChangeListener);
    }

    private void openTribeListFragment() {
        Intent intent = new Intent(this, FragmentTabs.class);
        intent.putExtra(TribeConstants.TRIBE_OP, TribeConstants.TRIBE_OP);
        startActivity(intent);
        finish();
    }
}
