package io.agora.liveshow.demo.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.agora.liveshow.demo.activity.AgoraBaseActivity;

public class ImageSelectHelper {
    private final String TAG = "SelectImageHelper";
    private final int REQ_PERMISSION = 100;
    private final String FILE_SAVEPATH;
    private AgoraBaseActivity mContext;
    private boolean mIsCrop;
    private boolean mIsCompressSource = true; // 是否压缩源文件
    private float mCompressMinWidth = 1024.0f; // 源文件压缩的最小宽度
    private int mOutWidth;
    private int mOutHeight;
    private Uri mSoureFileUri;
    private String mSourceFilePath;
    private String mCropFilePath;
    private OnImageSelectedListener mSelectedListener;

    /**
     * 需要将当前activity的onActivityResult和onRequestPermissionsResult代理到本类中
     *
     * @param context
     */
    public ImageSelectHelper(AgoraBaseActivity context) {
        mContext = context;
        FILE_SAVEPATH = context.getFilesDir().getAbsolutePath() + "/AgoraTest/";
        new File(FILE_SAVEPATH).mkdirs();
    }

    public void release() {
        mContext = null;
        mSelectedListener = null;
    }

    public void startSelectImage(OnImageSelectedListener listener) {
        mSelectedListener = listener;
        preStartImagePick();
    }

    private void preStartImagePick() {
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            //摄像头权限检测
            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                //进行权限请求
                ActivityCompat.requestPermissions(mContext, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, REQ_PERMISSION);
                return;
            } else {
                startImagePick();
            }
        } else {
            startImagePick();
        }
    }

    /**
     * 选择图片裁剪
     */
    private void startImagePick() {
        // 判断是否挂载了SD卡
        String storageState = Environment.getExternalStorageState();
        if (storageState.equals(Environment.MEDIA_MOUNTED)) {
            File savedir = new File(FILE_SAVEPATH);
            if (!savedir.exists()) {
                savedir.mkdirs();
            }
        } else {
            // 没有挂载SD卡，无法保存文件
            mContext.showToast("无法保存照片，请检查SD卡是否挂载");
            return;
        }
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
        } else {
            intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
        }
        mContext.startActivityForResult(Intent.createChooser(intent, "选择图片"), ImageUtils.REQUEST_CODE_GETIMAGE_BYCROP);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnIntent) {
        if (resultCode != Activity.RESULT_OK) {
            if (null != mSelectedListener) {
                if (requestCode == ImageUtils.REQUEST_CODE_GETIMAGE_BYSDCARD && null != mSoureFileUri) {
                    mSelectedListener.onImageSelected(compressSourceImage(), "");
                } else {
                    mSelectedListener.onCanceled();
                }
            }
            return;
        }

        switch (requestCode) {
            case ImageUtils.REQUEST_CODE_GETIMAGE_BYCROP:
                mSoureFileUri = imageReturnIntent.getData();
                mSourceFilePath = getOutImageUrl(mSoureFileUri);
                startActionCrop();// 选图后裁剪
                break;
            case ImageUtils.REQUEST_CODE_GETIMAGE_BYSDCARD:
                if (null != mSelectedListener) {
                    mSelectedListener.onImageSelected(compressSourceImage(), mCropFilePath);
                }
                break;
        }
    }

    /**
     * 裁剪
     */
    private void startActionCrop() {
        if (!mIsCrop) {
            if (null != mSelectedListener) {
                mSelectedListener.onImageSelected(compressSourceImage(), "");
            }
            return;
        }

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(mSoureFileUri, "image/*");
        intent.putExtra("output", genCropTempFile(mSoureFileUri));
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", mOutWidth);// 裁剪框比例
        intent.putExtra("aspectY", mOutHeight);
        intent.putExtra("outputX", mOutWidth);// 输出图片大小
        intent.putExtra("outputY", mOutHeight);
        intent.putExtra("scale", true);// 去黑边
        intent.putExtra("scaleUpIfNeeded", true);// 去黑边
        mContext.startActivityForResult(intent, ImageUtils.REQUEST_CODE_GETIMAGE_BYSDCARD);
    }

    // 裁剪头像的绝对路径
    private Uri genCropTempFile(Uri uri) {
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String thePath = ImageUtils.getAbsolutePathFromNoStandardUri(uri);

        // 如果是标准Uri
        if (TextUtils.isEmpty(thePath)) {
            thePath = ImageUtils.getAbsoluteImagePath(mContext, uri);
        }
        String ext = getFileFormat(thePath);
        ext = TextUtils.isEmpty(ext) ? "jpg" : ext;
        // 照片命名
        String cropFileName = "phonelive_crop_" + timeStamp + "." + ext;
        mCropFilePath = FILE_SAVEPATH + cropFileName;
        // 裁剪头像的绝对路径
        return Uri.fromFile(new File(mCropFilePath));
    }

    private String geSourceFileName() {
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        return "tmpImage" + timeStamp + ".jpg";// 照片命名
    }

    private String getOutImageUrl(Uri uri) {
        String thePath = ImageUtils.getAbsolutePathFromNoStandardUri(uri);
        // 如果是标准Uri
        if (TextUtils.isEmpty(thePath)) {
            thePath = ImageUtils.getAbsoluteImagePath(mContext, uri);
        }
        return thePath;
    }

    private String compressSourceImage() {
        if (TextUtils.isEmpty(mSourceFilePath)) {
            mSourceFilePath = getOutImageUrl(mSoureFileUri);
        }
        String outPath = mSourceFilePath;
        if (!mIsCompressSource) {
            return outPath;
        }
        FileInputStream fis = null;
        Bitmap bitmap = null;
        try {
            File file = new File(outPath);
            fis = new FileInputStream(file);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            bitmap = BitmapFactory.decodeStream(fis, null, options);
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            // 压缩大小，去宽高最大的值计算
            float sampleSize = width > height ? width / mCompressMinWidth : height / mCompressMinWidth;
            if (sampleSize > 1) {
                options.inJustDecodeBounds = false;
                options.inSampleSize = (int) sampleSize;
                bitmap = BitmapFactory.decodeStream(fis, null, options);
                outPath = new File(FILE_SAVEPATH, geSourceFileName()).getAbsolutePath();
                ImageUtils.saveImage(mContext, outPath, bitmap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Error e) {
            e.printStackTrace();
        } finally {
            try {
                fis.close();
            } catch (Exception e) {
            }
            try {
                bitmap.recycle();
            } catch (Exception e) {
            }
        }
        return outPath;
    }

    public interface OnImageSelectedListener {
        void onImageSelected(String sourcePath, String cropPath);

        void onCanceled();
    }

    private String getFileFormat(String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            return "";
        }

        int point = fileName.lastIndexOf('.');
        return fileName.substring(point + 1);
    }

    public enum ImageFrom {
        FROM_CAMERA, FROM_ABLUM
    }

}
