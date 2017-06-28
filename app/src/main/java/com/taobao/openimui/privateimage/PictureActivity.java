package com.taobao.openimui.privateimage;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.alibaba.mobileim.channel.event.IWxCallback;


public class PictureActivity extends Activity {

    private IWxCallback mCallback;
    private static final int PHOTO_REQUEST_ALBUM = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent != null){
            mCallback = PictureUtils.getCallback();
            int op = intent.getIntExtra(PictureUtils.OPERATION, -1);
            if (op == PictureUtils.ALBUM){
                getPictureFromAlbum();
            }
        }
    }

    private void getPictureFromAlbum(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_GALLERY
        startActivityForResult(intent, PHOTO_REQUEST_ALBUM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PHOTO_REQUEST_ALBUM && data != null){
            Uri uri = data.getData();
            String path = getFilePathFromUri(uri);
            if (mCallback != null){
                mCallback.onSuccess(path);
            }
        }
        finish();
    }

    private String getFilePathFromUri(Uri uri){
        String filePath = uri.toString();
        //红米Note这里会直接返回文件路径，因此这里需要适配一下(红米Note返回的数据格式为：file:///storage/emulated/0/.....)
        if (!TextUtils.isEmpty(filePath) && filePath.startsWith("file:")){
            filePath = filePath.substring(7);
            return filePath;
        }
        String[] filePathColumn = {MediaStore.MediaColumns.DATA};

        Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
        try {
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            filePath = cursor.getString(columnIndex);
        } finally {
            if (cursor != null){
                cursor.close();
            }
        }
        return filePath;
    }

}
