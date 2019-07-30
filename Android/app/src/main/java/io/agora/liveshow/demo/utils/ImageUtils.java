package io.agora.liveshow.demo.utils;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageUtils {
    public final static String SDCARD_MNT = "/mnt/sdcard";
    public final static String SDCARD = "/sdcard";
    
    /**
     * 请求相册
     */
    public static final int REQUEST_CODE_GETIMAGE_BYSDCARD = 0;
    /**
     * 请求裁剪
     */
    public static final int REQUEST_CODE_GETIMAGE_BYCROP = 2;
    
    /**
     * 写图片文件 在Android系统中，文件保存在 /data/data/PACKAGE_NAME/files 目录下
     *
     * @throws IOException
     */
    public static void saveImage(Context context, String fileName, Bitmap bitmap) throws IOException {
        saveImage(context, fileName, bitmap, 100);
    }
    
    public static void saveImage(Context context, String fileName, Bitmap bitmap, int quality) throws IOException {
        if (bitmap == null || fileName == null || context == null) {
            return;
        }
        
        FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.JPEG, quality, stream);
        byte[] bytes = stream.toByteArray();
        fos.write(bytes);
        fos.close();
    }
    
    /**
     * 判断当前Url是否标准的content://样式，如果不是，则返回绝对路径
     */
    public static String getAbsolutePathFromNoStandardUri(Uri mUri) {
        String filePath = null;
        
        String mUriString = mUri.toString();
        mUriString = Uri.decode(mUriString);
        
        String pre1 = "file://" + SDCARD + File.separator;
        String pre2 = "file://" + SDCARD_MNT + File.separator;
        
        if (mUriString.startsWith(pre1)) {
            filePath = Environment.getExternalStorageDirectory().getPath() + File.separator + mUriString
                    .substring(pre1.length());
        } else if (mUriString.startsWith(pre2)) {
            filePath = Environment.getExternalStorageDirectory().getPath() + File.separator + mUriString
                    .substring(pre2.length());
        }
        return filePath;
    }
    
    /**
     * 通过uri获取文件的绝对路径
     */
    public static String getAbsoluteImagePath(Activity context, Uri uri) {
        String imagePath = "";
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.managedQuery(uri, proj, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                imagePath = cursor.getString(column_index);
            }
        }
        return imagePath;
    }
}
