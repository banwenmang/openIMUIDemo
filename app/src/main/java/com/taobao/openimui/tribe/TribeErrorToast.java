package com.taobao.openimui.tribe;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import com.alibaba.mobileim.channel.constant.WXConstant;
import com.alibaba.mobileim.utility.IMNotificationUtils;

/**
 *
 */
public class TribeErrorToast {

    private static Handler mUIHandler = new Handler(Looper.getMainLooper());

    public static String codeToInfo(int code) {
        String info = "";
        switch (code) {
            case WXConstant.TRIBE_MANAGE_TYPE.TRB_TRIBE_NOT_EXIST:
                info = "该群不存在";
                break;
            case WXConstant.TRIBE_MANAGE_TYPE.TRB_USER_NOT_EXIST:
                info = "该用户不存在";
                break;
            case WXConstant.TRIBE_MANAGE_TYPE.TRB_NO_PRIVILEGE:
                info = "没有权限, 请联系群主或管理员";
                break;
            case WXConstant.TRIBE_MANAGE_TYPE.TRB_TRIBE_MEMBER_LIMIT:
                info = "群成员数量已达到上限";
                break;
            case WXConstant.TRIBE_MANAGE_TYPE.TRB_USER_TRIBES_LIMIT:
                info = "加入群的数量已达到上限";
                break;
            case WXConstant.TRIBE_MANAGE_TYPE.TRB_DUP_MEMBER:
                info = "群内已有该成员";
                break;
            case WXConstant.TRIBE_MANAGE_TYPE.TRB_BLACK_MEMBER:
                info = "该成员已加入黑名单";
                break;
            case WXConstant.TRIBE_MANAGE_TYPE.TRB_VERIFY_FAIL:
                info = "验证失败";
                break;
            case WXConstant.TRIBE_MANAGE_TYPE.TRB_MEMBER_NOT_EXIST:
                info = "该群成员不存在";
                break;
            case WXConstant.TRIBE_MANAGE_TYPE.TRB_NEED_VERIFY:
                info = "需要验证";
                break;
            case WXConstant.TRIBE_MANAGE_TYPE.TRB_MANAGER_LIMIT:
                info = "群管理员数量已达到上限";
                break;
            case WXConstant.TRIBE_MANAGE_TYPE.TRB_NO_ALLOCATED_TID:
                info = "拥有的群数量已超过上限";
                break;
            case WXConstant.TRIBE_MANAGE_TYPE.TRB_DUP_TRIBE:
                info = "该群已存在";
                break;
            case WXConstant.TRIBE_MANAGE_TYPE.TRB_EXAM_PROCESSED:
                info = "无法处理，已有管理员(或群主)处理过了该请求";
                break;
            default:
                break;
        }
        return info;
    }

    public static void show(final Context context, final String operation, final int code) {
        mUIHandler.post(new Runnable() {
            @Override
            public void run() {
                IMNotificationUtils.getInstance().showToast(context, operation, codeToInfo(code));
            }
        });
    }

}
