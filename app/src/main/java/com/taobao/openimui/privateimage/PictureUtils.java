package com.taobao.openimui.privateimage;

import android.app.Activity;
import android.content.Intent;

import com.alibaba.mobileim.channel.event.IWxCallback;


/**
 * Created by mayongge on 16/4/19.
 */
public class PictureUtils {
    private static final String TAG = "PictureUtils";

    public static final String OPERATION = "operation";
    public static final int CAMERA = 0; //拍照
    public static final int ALBUM = 1;  //从相册选择图片
    public static final int PREVIEW_IMAGE = 1; //预览图片


    private static IWxCallback mCallback;

    public static void getPictureFromAlbum(Activity activity, IWxCallback callback){
        mCallback = callback;
        Intent intent = new Intent(activity, PictureActivity.class);
        intent.putExtra(OPERATION, ALBUM);
        activity.startActivity(intent);
    }

    public static void PreviewImage(Activity activity, String path){

    }

    public static IWxCallback getCallback(){
        return mCallback;
    }

}
