package com.allever.security.photo.browser.util;

import android.content.*;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.Toast;
import com.android.absbase.App;
import com.android.absbase.helper.log.DLog;
import com.android.absbase.utils.DeviceUtils;
import com.android.absbase.utils.FileUtils;
import com.videoeditor.R;
import com.videoeditor.function.media.IDeleteListener;
import com.videoeditor.ui.bean.BitmapBean;
import com.videoeditor.ui.bean.ImageFolder;
import com.videoeditor.ui.bean.ThumbnailBean;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

import static android.provider.MediaStore.Video.Thumbnails.FULL_SCREEN_KIND;


/***
 * com.photoeditor.function.common.ImageHelper
 */
public class ImageHelper {

    private ImageHelper() {
    }

    public static int SCREEN_WIDTH = DeviceUtils.getScreenWidthPx();
    public static int SCREEN_HEIGHT = DeviceUtils.getScreenHeightPx();
    //	private static float QUALITY = 0;
    private static final String TAG = ImageHelper.class.getSimpleName();

    private static int HEIGHT = 0;

    private static int mMaxMemory;

    static {
        HEIGHT = SCREEN_HEIGHT - App.getContext().getResources().getDimensionPixelSize(R.dimen.image_eidt_select_bar_height)
                - App.getContext().getResources().getDimensionPixelSize(R.dimen.image_edit_bottom_bar_height);
        mMaxMemory = (int) Runtime.getRuntime().maxMemory();
    }

    /**
     * 获取应该显示的图片大小
     *
     * @param bean
     */
    public static int[] getKnownBitmapSize(BitmapBean bean, int width, int height) {
        int result[] = new int[]{0, 0};
        if (bean.mHeight != 0) {
            int h = bean.mHeight;
            int w = bean.mWidth;
            if (bean.mDegree == 90 || bean.mDegree == 270) {
                int temp = w;
                w = h;
                h = temp;
            }
            if (width / height > w / h) {//w比较小 顶高
                w = w * height / h;
                h = height;
            } else {//h比较小 顶宽
                h = h * width / w;
                w = width;
            }
            result[0] = w;
            result[1] = h;
        }
        return result;
    }

    /**
     * 将图片的Uri中的信息取出为BitmapBean对象
     * 针对Android 版本进行了适配
     * 这个只是从系统数据库获取了
     *
     * @param context
     * @param uri
     * @return
     */
    public static BitmapBean getBitmapBeanFormUri(Context context, Uri uri) {
        BitmapBean result = null;
        Cursor c = null;

        try {
            ContentResolver cr = context.getContentResolver();
            String uriString = uri.toString();
            if (Build.VERSION.SDK_INT >= 16) {
                if (uriString.startsWith(MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString()) || uriString.startsWith("content://0@media/external/images/media/")) {
                    c = cr.query(uri, new String[]{MediaStore.Images.ImageColumns._ID, MediaStore.Images.ImageColumns.ORIENTATION,
                                    MediaStore.Images.ImageColumns.DATE_TAKEN, MediaStore.Images.ImageColumns.WIDTH, MediaStore.Images.ImageColumns.HEIGHT, MediaStore.Images.ImageColumns.DATA},
                            null, null, null);
                    if (c.moveToFirst()) {
                        result = new BitmapBean();
                        result.mDegree = c.getInt(c.getColumnIndex(MediaStore.Images.ImageColumns.ORIENTATION)) % 360;
                        result.mId = c.getInt(c.getColumnIndex(MediaStore.Images.ImageColumns._ID));
                        result.mDate = c.getLong(c.getColumnIndex(MediaStore.Images.ImageColumns.DATE_TAKEN));
                        result.mUri = uri;
                        result.mWidth = c.getInt(c.getColumnIndex(MediaStore.Images.ImageColumns.WIDTH));
                        result.mHeight = c.getInt(c.getColumnIndex(MediaStore.Images.ImageColumns.HEIGHT));
                        result.mPath = c.getString(c.getColumnIndex(MediaStore.Images.ImageColumns.DATA));
                        if (MediaFile.isGifFileType(result.mPath)) {
                            result.mType = com.videoeditor.function.media.MediaTypeUtil.TYPE_GIF;
                        } else if (MediaFile.isJPGFileType(result.mPath)) {
                            result.mType = com.videoeditor.function.media.MediaTypeUtil.TYPE_JPG;
                        } else if (MediaFile.isPNGFileType(result.mPath)) {
                            result.mType = com.videoeditor.function.media.MediaTypeUtil.TYPE_PNG;
                        } else {
                            result.mType = com.videoeditor.function.media.MediaTypeUtil.TYPE_OTHER_IMAGE;
                        }
                    }
                } else if (uriString.startsWith(MediaStore.Video.Media.EXTERNAL_CONTENT_URI.toString()) || uriString.startsWith("content://0@media/external/video/media/")) {
                    c = cr.query(uri, new String[]{MediaStore.Video.VideoColumns._ID, MediaStore.Video.VideoColumns.DATE_TAKEN, MediaStore.Video.VideoColumns.WIDTH, MediaStore.Video.VideoColumns.HEIGHT, MediaStore.Video.VideoColumns.DATA},
                            null, null, null);
                    if (c.moveToFirst()) {
                        result = new BitmapBean();
                        result.mDegree = 0;
                        result.mId = c.getInt(c.getColumnIndex(MediaStore.Video.VideoColumns._ID));
                        result.mDate = c.getLong(c.getColumnIndex(MediaStore.Video.VideoColumns.DATE_TAKEN));
                        result.mUri = uri;
                        result.mWidth = c.getInt(c.getColumnIndex(MediaStore.Video.VideoColumns.WIDTH));
                        result.mHeight = c.getInt(c.getColumnIndex(MediaStore.Video.VideoColumns.HEIGHT));
                        result.mPath = c.getString(c.getColumnIndex(MediaStore.Video.VideoColumns.DATA));
                        result.mType = com.videoeditor.function.media.MediaTypeUtil.TYPE_VIDEO;
                    }
                } else if ("content".equalsIgnoreCase(uri.getScheme())) {
                    c = cr.query(uri, new String[]{MediaStore.Images.Media.DATA},
                            null, null, null);
                    if (c.moveToFirst()) {
                        result = new BitmapBean();
                        result.mUri = uri;
                        result.mPath = c.getString(c.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
                        if (MediaFile.isGifFileType(result.mPath)) {
                            result.mType = com.videoeditor.function.media.MediaTypeUtil.TYPE_GIF;
                        } else if (MediaFile.isJPGFileType(result.mPath)) {
                            result.mType = com.videoeditor.function.media.MediaTypeUtil.TYPE_JPG;
                        } else if (MediaFile.isPNGFileType(result.mPath)) {
                            result.mType = com.videoeditor.function.media.MediaTypeUtil.TYPE_PNG;
                        } else {
                            result.mType = com.videoeditor.function.media.MediaTypeUtil.TYPE_OTHER_IMAGE;
                        }
                    }
                }
            } else {
                if (uriString.startsWith(MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString()) || uriString.startsWith("content://0@media/external/images/media/")) {
                    c = cr.query(uri, new String[]{MediaStore.Images.ImageColumns._ID, MediaStore.Images.ImageColumns.ORIENTATION, MediaStore.Images.ImageColumns.DATE_TAKEN, MediaStore.Images.ImageColumns.DATA},
                            null, null, null);
                    if (c.moveToFirst()) {
                        result = new BitmapBean();
                        result.mDegree = c.getInt(c.getColumnIndex(MediaStore.Images.ImageColumns.ORIENTATION)) % 360;
                        result.mId = c.getInt(c.getColumnIndex(MediaStore.Images.ImageColumns._ID));
                        result.mDate = c.getLong(c.getColumnIndex(MediaStore.Images.ImageColumns.DATE_TAKEN));
                        result.mUri = uri;
                        result.mPath = c.getString(c.getColumnIndex(MediaStore.Images.ImageColumns.DATA));
                        if (MediaFile.isGifFileType(result.mPath)) {
                            result.mType = com.videoeditor.function.media.MediaTypeUtil.TYPE_GIF;
                        } else if (MediaFile.isJPGFileType(result.mPath)) {
                            result.mType = com.videoeditor.function.media.MediaTypeUtil.TYPE_JPG;
                        } else if (MediaFile.isPNGFileType(result.mPath)) {
                            result.mType = com.videoeditor.function.media.MediaTypeUtil.TYPE_PNG;
                        } else {
                            result.mType = com.videoeditor.function.media.MediaTypeUtil.TYPE_OTHER_IMAGE;
                        }
                    }
                } else if (uriString.startsWith(MediaStore.Video.Media.EXTERNAL_CONTENT_URI.toString()) || uriString.startsWith("content://0@media/external/video/media/")) {
                    c = cr.query(uri, new String[]{MediaStore.Video.VideoColumns._ID, MediaStore.Video.VideoColumns.DATE_TAKEN, MediaStore.Video.VideoColumns.DATA},
                            null, null, null);
                    if (c.moveToFirst()) {
                        result = new BitmapBean();
                        result.mDegree = 0;
                        result.mId = c.getInt(c.getColumnIndex(MediaStore.Video.VideoColumns._ID));
                        result.mDate = c.getLong(c.getColumnIndex(MediaStore.Video.VideoColumns.DATE_TAKEN));
                        result.mUri = uri;
                        result.mPath = c.getString(c.getColumnIndex(MediaStore.Video.VideoColumns.DATA));
                        result.mType = com.videoeditor.function.media.MediaTypeUtil.TYPE_VIDEO;
                    }
                } else if ("content".equalsIgnoreCase(uri.getScheme())) {
                    c = cr.query(uri, new String[]{MediaStore.Images.Media.DATA},
                            null, null, null);
                    if (c.moveToFirst()) {
                        result = new BitmapBean();
                        result.mUri = uri;
                        result.mPath = c.getString(c.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
                        if (MediaFile.isGifFileType(result.mPath)) {
                            result.mType = com.videoeditor.function.media.MediaTypeUtil.TYPE_GIF;
                        } else if (MediaFile.isJPGFileType(result.mPath)) {
                            result.mType = com.videoeditor.function.media.MediaTypeUtil.TYPE_JPG;
                        } else if (MediaFile.isPNGFileType(result.mPath)) {
                            result.mType = com.videoeditor.function.media.MediaTypeUtil.TYPE_PNG;
                        } else {
                            result.mType = com.videoeditor.function.media.MediaTypeUtil.TYPE_OTHER_IMAGE;
                        }
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            try {
                if (c != null) {
                    c.close();
                }
            } catch (Throwable e) {
            }
        }

        return result;
    }

    /**
     * 获取最新媒体文件的预览图
     *
     * @param context
     * @param path
     * @return
     */
    public static BitmapBean getTheNewestMedia(Context context, String path) {
        BitmapBean result1 = null;
        BitmapBean result2 = null;
        Cursor c1 = null;
        Cursor c2 = null;
        try {
            ContentResolver cr = context.getContentResolver();

            c1 = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Images.ImageColumns._ID, MediaStore.Images.ImageColumns.ORIENTATION, MediaStore.Images.ImageColumns.DATE_TAKEN, MediaStore.Images.ImageColumns.DATA},
                    MediaStore.Images.ImageColumns.DATA + " like ? ", new String[]{path + File.separator + "%"}, MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC" + ", " + MediaStore.Images.ImageColumns._ID + " ASC" + " limit 1 ");
            if (c1.moveToFirst()) {
                result1 = new BitmapBean();
                result1.mDegree = c1.getInt(c1.getColumnIndex(MediaStore.Images.ImageColumns.ORIENTATION)) % 360;
                result1.mId = c1.getInt(c1.getColumnIndex(MediaStore.Images.ImageColumns._ID));
                result1.mDate = c1.getLong(c1.getColumnIndex(MediaStore.Images.ImageColumns.DATE_TAKEN));
                result1.mPath = c1.getString(c1.getColumnIndex(MediaStore.Images.ImageColumns.DATA));
                if (MediaFile.isGifFileType(result1.mPath)) {
                    result1.mType = com.videoeditor.function.media.MediaTypeUtil.TYPE_GIF;
                } else if (MediaFile.isJPGFileType(result1.mPath)) {
                    result1.mType = com.videoeditor.function.media.MediaTypeUtil.TYPE_JPG;
                } else if (MediaFile.isPNGFileType(result1.mPath)) {
                    result1.mType = com.videoeditor.function.media.MediaTypeUtil.TYPE_PNG;
                } else {
                    result1.mType = com.videoeditor.function.media.MediaTypeUtil.TYPE_OTHER_IMAGE;
                }
            }
            c2 = cr.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Video.VideoColumns._ID, MediaStore.Video.VideoColumns.DATE_TAKEN, MediaStore.Video.VideoColumns.DATA},
                    MediaStore.Video.VideoColumns.DATA + " like ? ", new String[]{path + File.separator + "%"}, MediaStore.Video.VideoColumns.DATE_TAKEN + " DESC" + ", " + MediaStore.Images.ImageColumns._ID + " ASC" + " limit 1 ");
            if (c2.moveToFirst()) {
                result2 = new BitmapBean();
                result2.mDegree = -1;
                result2.mId = c2.getInt(c2.getColumnIndex(MediaStore.Video.VideoColumns._ID));
                result2.mDate = c2.getLong(c2.getColumnIndex(MediaStore.Video.VideoColumns.DATE_TAKEN));
                result2.mType = com.videoeditor.function.media.MediaTypeUtil.TYPE_VIDEO;
                result2.mPath = c2.getString(c2.getColumnIndex(MediaStore.Video.VideoColumns.DATA));
            }
            if (result1 == null) {
                return result2;
            } else if (result2 == null) {
                return result1;
            } else {
                if (result1.mDate > result2.mDate) {
                    return result1;
                } else {
                    return result2;
                }
            }

        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            if (c1 != null) {
                c1.close();
            }
            if (c2 != null) {
                c2.close();
            }
        }
        return null;
    }


    /**
     * 将Video转化为预览图
     *
     * @param bean
     * @return
     */
    public static Bitmap videoToBitmap(BitmapBean bean) {
        if (UriUtil.INSTANCE.isFileUri(bean.mUri)) {
            return MediaThumbnailUtil.createPreViewVideoThumbnail(bean.mPath, MediaStore.Images.Thumbnails.MINI_KIND);
        } else {
            return MediaThumbnailUtil.createPreViewVideoThumbnail(bean.mPath, MediaStore.Images.Thumbnails.MINI_KIND);
        }
    }

    /**
     * 将Image Uri转化为Bitmap  用于预览 不旋转
     * @param bean
     * @param uriErr  和 uiHandler 同时存在
     * @param uiHandler
     * @param v
     * @return
     */
//	public static Bitmap UriToBitmap(final BitmapBean bean, ILoadBitmap uriErr, ImageLoaderInterface.UIHandler uiHandler, View v){
//
//		int insamplesize = 1;
//		Bitmap bitmap = null;
//		ContentResolver mCr = App.getContext().getContentResolver();
//		BitmapFactory.Options option=new BitmapFactory.Options();
//		InputStream in1 = null;
//		InputStream in2 = null;
//		InputStream in3 = null;
//		try {
//			option.inJustDecodeBounds=true;
//			in1 = mCr.openInputStream(bean.mUri);
//			bitmap = BitmapFactory.decodeStream(in1, null, option);
//			if(uriErr != null){
//				if(bean.mHeight == 0 || bean.mHeight == -1){//没有从数据库中拿到宽高信息  通过解码获取
//					uriErr.OnGetSizeInfo(bean, option.outWidth, option.outHeight);
//					if(uiHandler != null){
//						uiHandler.refreashDiaplay(bean, v);
//					}
//				}
//			}
//			/*
//			 * 原来的长宽
//			 */
//			int width;
//			int height;
//			if(bean.mDegree == 90 || bean.mDegree == 270){
//				width = option.outHeight;
//				height = option.outWidth;
//			}else{
//				width = option.outWidth;
//				height = option.outHeight;
//			}
//			option.inJustDecodeBounds =false;
//			option.inPreferredConfig = Bitmap.Config.ARGB_8888;
//			option.inPurgeable = true;
//			option.inInputShareable = true;
//			option.inDither = false;
//			insamplesize = getFitSampleSize(width, height, false);
//			option.inSampleSize = insamplesize;
//
//			in2 = mCr.openInputStream(bean.mUri);
//			bitmap = BitmapFactory.decodeStream(in2, null, option);
//		} catch (Throwable e) {
//			System.gc();
//			e.printStackTrace();
//			/**
//			 * 如果内存溢出 缩小1/4再解析
//			 */
//			option.inSampleSize = insamplesize * 2;
//			try {
//				in3 = mCr.openInputStream(bean.mUri);
//				bitmap = BitmapFactory.decodeStream(in3, null, option);
//			} catch (Throwable e1) {
//				System.gc();
//				e1.printStackTrace();
//			}
//			return bitmap;
//		} finally{
//			try {
//				if(in1 != null){
//					in1.close();
//				}
//				if(in2 != null){
//					in2.close();
//				}
//				if(in3 != null){
//					in3.close();
//				}
//			} catch (Throwable e) {
//			}
//		}
//		return bitmap;
//	}

    /**
     * 将Image Uri转为Bitmap
     *
     * @param bean
     * @param isShare
     * @return
     */
    public static Bitmap UriToBitmap(final BitmapBean bean, boolean isShare) {
        if (bean == null) {
            return null;
        }

        int insamplesize = 1;
        Bitmap bitmap = null;
        ContentResolver mCr = App.getContext().getContentResolver();
        Resources res = App.getContext().getResources();
        BitmapFactory.Options option = new BitmapFactory.Options();
        InputStream in1 = null;
        InputStream in2 = null;
        InputStream in3 = null;
        try {
            option.inJustDecodeBounds = true;
            in1 = mCr.openInputStream(bean.mUri);
            bitmap = BitmapFactory.decodeStream(in1, null, option);
            /*
             * 原来的长宽
             */
            int width;
            int height;

            if (bean.mDegree == 90 || bean.mDegree == 270) {
                width = option.outHeight;
                height = option.outWidth;
            } else {
                width = option.outWidth;
                height = option.outHeight;
            }
            option.inJustDecodeBounds = false;
            option.inPreferredConfig = Bitmap.Config.ARGB_8888;
            option.inPurgeable = true;
            option.inInputShareable = true;
            option.inDither = false;

            float scale;
            if (isShare) {
                scale = getShareFitSampleSize(width, height);
            } else {
                scale = getFitSampleSizeLarger(width, height);
            }
            scale = checkCanvasAndTextureSize(width, height, scale);

            int i = 1;
            while (scale / Math.pow(i, 2) > 1.0f) {
                i *= 2;
            }
            if (i != 1) {
                i = i / 2;
            }
            insamplesize = i;

            int targetDensity = res.getDisplayMetrics().densityDpi;
            option.inScaled = true;
            option.inDensity = (int) (targetDensity * Math.sqrt(scale / Math.pow(i, 2)) + 0.5);
            option.inTargetDensity = targetDensity;
            if (Math.abs(option.inDensity - option.inTargetDensity) < 2) {
                option.inDensity = option.inTargetDensity;
            }

            option.inSampleSize = insamplesize;

            in2 = mCr.openInputStream(bean.mUri);
            bitmap = BitmapFactory.decodeStream(in2, null, option);

//			if(bean.mDegree % 360 != 0){
//				bitmap = rotating(bitmap, bean.mDegree);
//			}
            bitmap = rotatingAndScale(bitmap, bean.mDegree);

//            bitmap = adjustEvenSize(bitmap);
        } catch (Throwable e) {
            System.gc();
            e.printStackTrace();
            /**
             * 如果内存溢出 缩小1/4再解析
             */
            option.inSampleSize = insamplesize * 2;
            try {
                in3 = mCr.openInputStream(bean.mUri);
                bitmap = BitmapFactory.decodeStream(in3, null, option);
                bitmap = rotatingAndScale(bitmap, bean.mDegree);
//                bitmap = adjustEvenSize(bitmap);
            } catch (Throwable e1) {
                System.gc();
                e1.printStackTrace();
            }
            return bitmap;
        } finally {
            try {
                if (in1 != null) {
                    in1.close();
                }
                if (in2 != null) {
                    in2.close();
                }
                if (in3 != null) {
                    in3.close();
                }
            } catch (Throwable e) {
            }
        }
        return bitmap;
    }

    //宽高转换成偶数
//    private static Bitmap adjustEvenSize(Bitmap bitmap) {
//        int bitmapWidth = bitmap.getWidth();
//        int bitmapHeight = bitmap.getHeight();
//        if (bitmapWidth % 2 != 0 || bitmapHeight % 2 != 0) {
//            //偶数
//            Bitmap result = Bitmap.createBitmap(bitmap, 0, 0, GifMaker.verifyVideoOutPutSize(bitmapWidth), GifMaker.verifyVideoOutPutSize(bitmapHeight));
//            if (result != bitmap) {
//                bitmap.recycle();
//            }
//            return result;
//        }
//        return bitmap;
//    }

    /**
     * 拍照后，回传适当大小的图片
     * 用于后暂存页面编辑
     *
     * @param bitmap
     * @param degree
     * @return
     */
    public static Bitmap toFitBitmap(final Bitmap bitmap, float degree) {
        if (bitmap == null) {
            return null;
        }
        Bitmap ret = null;
        try {
            /*
             * 原来的长宽
             */
//			int width;
//			int height;
//
//			if (degree == 90 || degree == 270) {
//				width = bitmap.getHeight();
//				height = bitmap.getWidth();
//			} else {
//				width = bitmap.getWidth();
//				height = bitmap.getHeight();
//			}
//			float scale = getFitSampleSizeLarger(width, height);
//			scale = checkCanvasAndTextureSize(width, height, scale);
//			int i = 1;
//			while(scale / Math.pow(i, 2) > 1.0f){
//				i *= 2;
//			}
//			if(i != 1) {
//				i = i / 2;
//			}

//			scale = (float) (1f / Math.sqrt(scale / Math.pow(i, 2)) + 1f);

            ret = rotatingAndScale(bitmap, degree, 1f);
        } catch (Throwable e) {
            System.gc();
            e.printStackTrace();
        }
        return ret;
    }

    public static BitmapBean toBitmapBean(ThumbnailBean bean) {
        if (bean == null) {
            return null;
        }
        BitmapBean bitmapBean = new BitmapBean();
        bitmapBean.mDate = bean.getDate();
        bitmapBean.mDegree = bean.getDegree();
        bitmapBean.mDuration = bean.getDuration();
        bitmapBean.mPath = bean.getPath();
        bitmapBean.mType = bean.getType();
        bitmapBean.mUri = bean.getUri();
        return bitmapBean;
    }

    /**
     * 将Image Uri转华为Bitmap  用于编辑
     *
     * @param bean
     * @return
     */
    public static Bitmap UriToBitmap(final BitmapBean bean) {
        return UriToBitmap(bean, false);
    }

    /**
     * 用于图片预览 和 编辑的图片压缩
     *
     * @param width
     * @param height
     * @param isHighQuality
     * @return
     */
    public static int getFitSampleSize(int width, int height, boolean isHighQuality) {
        int size = width * height * 4;
        int result = 1;
        int value1, value2;
        if (isHighQuality) {
            value1 = 15;
            value2 = 60;
        } else {
            value1 = 18;
            value2 = 72;
        }
        if (size > mMaxMemory / value1) {
            float f = size * 1.0f / mMaxMemory * value1;
            f = (float) Math.sqrt(f * 4);
            result = (int) (f + 0.5f);
            int i = 1;
            double p = 2;
            while (result > p) {
                i++;
                p = Math.pow(2, i);
            }
            result = (int) p;
        } else if (size < mMaxMemory / value2) {
            result = 1;
        } else {
            result = 2;
        }
        return result;
    }

    /**
     * 新策略用于图片预览 和 编辑的图片压缩
     *
     * @param width
     * @param height
     * @return
     */
    public static int getFitSampleSizeNew(int width, int height) {
        int nw, nh;
        if (width * 1.0f / height >= SCREEN_WIDTH * 1.0f / SCREEN_HEIGHT) {//宽顶着
            nw = SCREEN_WIDTH;
            nh = (int) (height * SCREEN_WIDTH * 1.0f / width + 0.5f);
        } else {
            nh = SCREEN_HEIGHT;
            nw = (int) (width * SCREEN_HEIGHT * 1.0f / height + 0.5f);
        }
        //nw nh是显示在屏幕上的像素宽高
        int size = width * height * 4;
        int minSize = nw * nh * 4;
//		int midSize = SCREEN_WIDTH * SCREEN_HEIGHT * 4;
        int result = 1;
        while ((size / (result * result)) > minSize) {
            result = result * 2;
        }
        if (result != 1) {
            result /= 2;
//			while(result != 1 && (size * 1.0f / (result * result / 4)) < midSize ){
//				result /= 2;
//			}
        }
        return result;
    }

    public static int getFitSampleSize(int width, int height) {
        int size = width * height * 4;
        int result = 1;
        int value1, value2;
        value1 = 15;
        value2 = 60;
        if (size > mMaxMemory / value1) {
            float f = size * 1.0f / mMaxMemory * value1;
            f = (float) Math.sqrt(f * 4);
            result = (int) (f + 0.5f);
            int i = 1;
            double p = 2;
            while (result > p) {
                i++;
                p = Math.pow(2, i);
            }
            result = (int) p;
            float flag = (width * 1.0f / result) * (height * 1.0f / result) * 4;
            if (flag > mMaxMemory / value2) {
                return result;
            } else {
                return result / 2;
            }
        } else {
            result = 1;
        }
        return result;
    }

    /**
     * 放大图片  编辑界面专用
     *
     * @param bitmap
     * @return
     */
    public static Bitmap getScaleBitmap(Bitmap bitmap) {
        try {
            int bw = bitmap.getWidth();
            int bh = bitmap.getHeight();
            if (bw < SCREEN_WIDTH && bh < HEIGHT) {
                float w;
                float h;
                w = SCREEN_WIDTH;
                h = w * ((float) bh / (float) bw);
                if (h > SCREEN_HEIGHT) {
                    h = SCREEN_HEIGHT;
                    w = h * ((float) bw / (float) bh);
                }

                Matrix m = new Matrix();
                m.setScale(w / bw, h / bh);
                Bitmap mBitmap = Bitmap.createBitmap(bitmap, 0, 0, bw, bh, m, true);
                if (bitmap != mBitmap) {
                    bitmap.recycle();
                }

                return mBitmap;
            } else {
                return bitmap;
            }
        } catch (Throwable e) {
            e.printStackTrace();
            System.gc();
        }
        return bitmap;
    }

    /**
     * 编辑界面使用 用于旋转和缩放成合适大小的图片
     *
     * @param bitmap
     * @param degree
     * @return
     */
    public static Bitmap rotatingAndScale(Bitmap bitmap, float degree) {
        try {
            int width = bitmap.getWidth(), height = bitmap.getHeight();
            Matrix m = new Matrix();

            if (degree % 360 != 0) {
                m.postRotate(degree, 0.5f, 0.5f);
            }

            int bw = width;
            int bh = height;
            if (degree == 90 || degree == 270) {
                bw = height;
                bh = width;
            }
            if (bw < SCREEN_WIDTH && bh < HEIGHT) {
                float w;
                float h;
                w = SCREEN_WIDTH;
                h = w * ((float) bh / (float) bw);
                if (h > HEIGHT) {
                    h = HEIGHT;
                    w = h * ((float) bw / (float) bh);
                }
                m.postScale(w / bw, h / bh);
            }
            Bitmap result = Bitmap.createBitmap(bitmap, 0, 0, width, height, m, true);
            if (result != bitmap) {
                bitmap.recycle();
            }
            return result;
        } catch (Throwable e) {
            System.gc();
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap rotatingAndScale(Bitmap bitmap, float degree, float scale) {
        try {
            int width = bitmap.getWidth(), height = bitmap.getHeight();
            Matrix m = new Matrix();

            if (degree % 360 != 0) {
                m.postRotate(degree, 0.5f, 0.5f);
            }

            m.postScale(scale, scale);
            Bitmap result = Bitmap.createBitmap(bitmap, 0, 0, width, height, m, true);
            if (result != bitmap) {
                bitmap.recycle();
            }
            return result;
        } catch (Throwable e) {
            System.gc();
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 旋转
     *
     * @param bitmap
     * @param degree
     * @return
     */
    public static Bitmap rotating(Bitmap bitmap, float degree) {
        try {
            int width = bitmap.getWidth(), height = bitmap.getHeight();
            Matrix m = new Matrix();
            m.postRotate(degree, 0.5f, 0.5f);
            Bitmap result = Bitmap.createBitmap(bitmap, 0, 0, width, height, m, true);
            if (result != bitmap) {
                bitmap.recycle();
            }
            return result;
        } catch (Throwable e) {
            System.gc();
            e.printStackTrace();
        }
        return null;
    }

//	/**
//	 * 选择图片
//	 *
//	 * @param bitmap
//	 * @param degree
//	 * @param flipHorizontal
//	 * @param flipVertical
//	 * @return
//	 */
//    public static Bitmap rotating(Bitmap bitmap, float degree, boolean flipHorizontal,
//            boolean flipVertical) {
//        try {
//            int width = bitmap.getWidth(), height = bitmap.getHeight();
//            Matrix m = new Matrix();
//            if (degree != 0) {
//                m.postRotate(degree, 0.5f, 0.5f);
//            }
//            if (flipHorizontal) {
//                m.postScale(-1, 1);
//            }
//            if (flipVertical) {
//                m.postScale(1, -1);
//            }
//            Bitmap result = Bitmap.createBitmap(bitmap, 0, 0, width, height, m, true);
//            if (result != bitmap) {
//                bitmap.recycle();
//            }
//            return result;
//        } catch (Throwable e) {
//            System.gc();
//            e.printStackTrace();
//        }
//        return null;
//    }

//	/**
//	 * PNG不支持setExif
//	 * @param context
//	 * @param img
//	 * @param path 路径名称
//	 * @param filename 文件名称
//	 * @param listener
//	 * @return
//	 */
//	public static boolean saveBitmapAsPNG(Activity context, Bitmap img, String path, String filename, FolderHelper.OnScanCompletedListener listener){
//		try {
//			if(PhoneInfo.isSupportWriteExtSdCard() && ExtSdcardUtils.isExtSdcardPath(path)){//如果是外置SDCARD
//				String pathName = path + File.separator + filename;
//				OutputStream outputStream = ExtSdcardUtils.getExtCardOutputStream(context, pathName, "");//不传入mimeType防止它直接加入Media数据库
//				img.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
//				outputStream.flush();
//				outputStream.close();
//
//				File f = new File(pathName);
//				FolderHelper.asynAddImage(context, filename, "image/png", System.currentTimeMillis(),
//						null, 0, (int) f.length(), f.getAbsolutePath(), img.getWidth(), img.getHeight(),
//						listener);
//				return true;
//			} else {
//				File file = new File(path);
//				if (!file.exists()) {
//					file.mkdirs();
//				}
//				File f = new File(path + File.separator + filename);
//				FileOutputStream bos = new FileOutputStream(f);
//				img.compress(Bitmap.CompressFormat.PNG, 100, bos);
//				bos.flush();
//				bos.close();
//				FolderHelper.asynAddImage(context, filename, "image/png", System.currentTimeMillis(),
//						null, 0, (int) f.length(), f.getAbsolutePath(), img.getWidth(), img.getHeight(),
//						listener);
//				return true;
//			}
//		} catch (Throwable e) {
//			e.printStackTrace();
//			return false;
//		}
//
//	}

    /**
     * @param context
     * @param img
     * @param path     路径名称
     * @param filename 文件名称
     * @param listener
     * @return
     */
//    public static boolean saveBitmapAsJPG(Activity context, Bitmap img, String path, String filename, FolderHelper.OnScanCompletedListener listener) {
//        return saveBitmapAsJPG(context, img, 100, path, filename, listener);
//    }

//    public static boolean saveBitmapAsJPG(Activity context, Bitmap img, int quality, String path, String filename, FolderHelper.OnScanCompletedListener listener) {
//        try {
//            if (PhoneInfo.isSupportWriteExtSdCard() && ExtSdcardUtils.isExtSdcardPath(path)) {//如果是外置SDCARD  由于EXIF文件只能传入路径 为了防止EXIF文件写入失败 先写入图片至内置SDCARD然后再保存到外置
//
//                //将图片转成byte数组
//                byte[] jpeg = BitmapUtil.bmpToJPGByteArray(img, false);
//                ByteArrayOutputStream output = new ByteArrayOutputStream();
//
//                /**
//                 * 写入需要的字段  在这里不需要写入location
//                 * 这个是写EXIF的相关信息
//                 */
//                com.android.gallery3d.exif.ExifInterface exif = new com.android.gallery3d.exif.ExifInterface();
//                ExifTag widthTg = exif.buildTag(com.android.gallery3d.exif.ExifInterface.TAG_IMAGE_WIDTH, img.getWidth());
//                ExifTag heightTg = exif.buildTag(com.android.gallery3d.exif.ExifInterface.TAG_IMAGE_LENGTH, img.getHeight());
//                ExifTag orientationTg = exif.buildTag(com.android.gallery3d.exif.ExifInterface.TAG_ORIENTATION, com.android.gallery3d.exif.ExifInterface.getOrientationValueForRotation(0));
//                String timeStamp = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss", Locale.US).format(new Date(System.currentTimeMillis()));
//                ExifTag dataTimeTg = exif.buildTag(com.android.gallery3d.exif.ExifInterface.TAG_DATE_TIME, timeStamp);
//                ExifTag makeTg = exif.buildTag(com.android.gallery3d.exif.ExifInterface.TAG_MAKE, "PhotoEditor");
//
//                exif.setTag(widthTg);
//                exif.setTag(heightTg);
//                exif.setTag(orientationTg);
//                exif.setTag(dataTimeTg);
//                exif.setTag(makeTg);
//
//                exif.writeExif(jpeg, output);
//                byte[] buffer = output.toByteArray();
//
//                //外置的文件
//                String pathName = path + File.separator + filename;
//                OutputStream outputStream = ExtSdcardUtils.getExtCardOutputStream(context, pathName, "");//不传入mimeType防止它直接加入Media数据库
//                outputStream.write(buffer);
//
//                outputStream.flush();
//                outputStream.close();
//                output.close();
//
//                File f = new File(pathName);
//                //写入数据库
//                FolderHelper.asynAddImage(context, filename, "image/jpeg", System.currentTimeMillis(),
//                        null, 0, (int) f.length(), f.getAbsolutePath(), img.getWidth(), img.getHeight(),
//                        listener);
//                return true;
//            } else {
//                File file = new File(path);
//                if (!file.exists()) {
//                    file.mkdirs();
//                }
//                File f = new File(path + File.separator + filename);
//                FileOutputStream bos = new FileOutputStream(f);
//                img.compress(Bitmap.CompressFormat.JPEG, quality, bos);
//                bos.flush();
//                bos.close();
//                FolderHelper.setExif(f, img.getWidth(), img.getHeight(), 0, System.currentTimeMillis(), null);
//                FolderHelper.asynAddImage(context, filename, "image/jpeg", System.currentTimeMillis(),
//                        null, 0, (int) f.length(), f.getAbsolutePath(), img.getWidth(), img.getHeight(),
//                        listener);
//                return true;
//            }
//        } catch (Throwable e) {
//            e.printStackTrace();
//            StatisticsUtils.statisticsSaveImgError(e.getMessage());
//            return false;
//        }
//    }

//	public static boolean saveBitmapAsJPGWithoutInsertDb(Activity context, Bitmap img, String path, String filename){
//		try {
//			if(PhoneInfo.isSupportWriteExtSdCard() && ExtSdcardUtils.isExtSdcardPath(path)){//如果是外置SDCARD  由于EXIF文件只能传入路径 为了防止EXIF文件写入失败 先写入图片至内置SDCARD然后再保存到外置
//				//将图片转成byte数组
//				byte[] jpeg = BitmapUtil.bmpToJPGByteArray(img, false);
//				ByteArrayOutputStream output = new ByteArrayOutputStream();
//
//				/**
//				 * 写入需要的字段  在这里不需要写入location
//				 * 这个是写EXIF的相关信息
//				 */
//				com.android.gallery3d.exif.ExifInterface exif = new com.android.gallery3d.exif.ExifInterface();
//				ExifTag widthTg = exif.buildTag(com.android.gallery3d.exif.ExifInterface.TAG_IMAGE_WIDTH, img.getWidth());
//				ExifTag heightTg = exif.buildTag(com.android.gallery3d.exif.ExifInterface.TAG_IMAGE_LENGTH, img.getHeight());
//				ExifTag orientationTg = exif.buildTag(com.android.gallery3d.exif.ExifInterface.TAG_ORIENTATION, com.android.gallery3d.exif.ExifInterface.getOrientationValueForRotation(0));
//				String timeStamp = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss", Locale.US).format(new Date(System.currentTimeMillis()));
//				ExifTag dataTimeTg = exif.buildTag(com.android.gallery3d.exif.ExifInterface.TAG_DATE_TIME, timeStamp);
//				ExifTag makeTg = exif.buildTag(com.android.gallery3d.exif.ExifInterface.TAG_MAKE, "PhotoEditor");
//
//				exif.setTag(widthTg);
//				exif.setTag(heightTg);
//				exif.setTag(orientationTg);
//				exif.setTag(dataTimeTg);
//				exif.setTag(makeTg);
//
//				exif.writeExif(jpeg, output);
//				byte[] buffer = output.toByteArray();
//
//				//外置的文件
//				String pathName = path + File.separator + filename;
//				OutputStream outputStream = ExtSdcardUtils.getExtCardOutputStream(context, pathName, "");//不传入mimeType防止它直接加入Media数据库
//				outputStream.write(buffer);
//
//				outputStream.flush();
//				outputStream.close();
//				output.close();
//				return true;
//			} else {
//				File file = new File(path);
//				if (!file.exists()) {
//					file.mkdirs();
//				}
//				File f = new File(path + File.separator + filename);
//				FileOutputStream bos = new FileOutputStream(f);
//				img.compress(Bitmap.CompressFormat.JPEG, 90, bos);
//				bos.flush();
//				bos.close();
//				FolderHelper.setExif(f, img.getWidth(), img.getHeight(), 0, System.currentTimeMillis(), null);
//				return true;
//			}
//		} catch (Throwable e) {
//			e.printStackTrace();
//			return false;
//		}
//	}

    /**
     * 获取大图的缩略图
     *
     * @param oldBitmap
     * @param rotation
     * @param width
     * @param height
     * @return
     */
    @Nullable
    public static Bitmap getThumbnail(Bitmap oldBitmap, int rotation, int width, int height, boolean recycleOldBitmap) {
        Bitmap result = null;
        try {
            result = ThumbnailUtils.extractThumbnail(oldBitmap, width, height, MediaStore.Images.Thumbnails.MINI_KIND);
        } catch (Exception e) {
            DLog.printStackTrace(e);
            return null;
        }
        if (recycleOldBitmap && oldBitmap != result) {
            oldBitmap.recycle();
        }
        if (rotation == 0) {
            return result;
        }
        return rotating(result, rotation);
    }

    /**
     * 格式化时间
     *
     * @param date
     * @return
     */
    public static String countDate(long date) {
        Date result = new Date(date);
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        return df.format(result);
    }

//	public static float getTotalMemory() {
//         String str1 = "/proc/meminfo";// 系统内存信息文件
//         String str2;
//         String[] arrayOfString;
//         long initial_memory = 0;
//         try {
//                 FileReader localFileReader = new FileReader(str1);
//                 BufferedReader localBufferedReader = new BufferedReader(localFileReader, 8192);
//                 str2 = localBufferedReader.readLine();// 读取meminfo第一行，系统总内存大小
//
//                 arrayOfString = str2.split("\\s+");
//
//                 initial_memory = Integer.valueOf(arrayOfString[1]).intValue() * 1024;// 获得系统总内存，单位是KB，乘以1024转换为Byte
//                 localBufferedReader.close();
//
//                 return initial_memory * 1.0f / 1024 / 1024 / 1024;// Byte转换为KB或者MB，内存大小规格化
//         } catch (Throwable e) {
//
//         }
//         return 1.1f;
//	 }

//	/**
//	 * 针对分享微信的图片处理
//	 * @param bean
//	 * @return
//	 */
//	public static Bitmap get10MLowerBitmap(BitmapBean bean){
//		Bitmap bitmap = null;
//		InputStream in1 = null;
//		InputStream in2 = null;
//		try {
//			ContentResolver mCr = App.getContext().getContentResolver();
//			BitmapFactory.Options option=new BitmapFactory.Options();
//			option.inJustDecodeBounds=true;
//			in1 = mCr.openInputStream(bean.mUri);
//			bitmap = BitmapFactory.decodeStream(in1, null, option);
//			/*
//			 * 原来的长宽
//			 */
//			int width;
//			int height;
//
//			if(bean.mDegree == 90 || bean.mDegree == 270){
//				width = option.outHeight;
//				height = option.outWidth;
//			}else{
//				width = option.outWidth;
//				height = option.outHeight;
//			}
//			option.inJustDecodeBounds = false;
//			option.inPreferredConfig = Bitmap.Config.ARGB_8888;
//			option.inPurgeable = true;
//			option.inInputShareable = true;
//			option.inDither = false;
//
//			option.inSampleSize = getLower10MSampleSize(width, height, 10);
//
//			in2 = mCr.openInputStream(bean.mUri);
//			bitmap = BitmapFactory.decodeStream(in2, null, option);
//
//			if(bean.mDegree % 360 != 0){
//				bitmap = rotating(bitmap, bean.mDegree);
//			}
//		} catch (Throwable e) {
//			System.gc();
//			e.printStackTrace();
//			return null;
//		} finally{
//			try {
//				if(in1 != null){
//					in1.close();
//				}
//				if(in2 != null){
//					in2.close();
//				}
//			} catch (Throwable e) {
//			}
//		}
//		return bitmap;
//	}

    /**
     * 获取合适的SampleSize
     *
     * @param width
     * @param height
     * @param mb     是占用的空间大小  单位是MB
     * @return
     */
    public static int getLower10MSampleSize(int width, int height, int mb) {
        int size = width * height * 4;
        int result = 1;
        int bytes = 1024 * 1024 * mb;
        if (size > bytes) {
            float f = size * 1.0f / bytes;
            f = (float) Math.sqrt(f * 4);
            result = (int) (f + 0.5f);
            int i = 1;
            double p = 2;
            while (result > p) {
                i++;
                p = Math.pow(2, i);
            }
            result = (int) p;
        }
        return result;
    }


    /**
     * 获取某个文件夹下的所有图片和视频的URI
     *
     * @param context
     * @param path
     * @return
     */
    public static ArrayList<ThumbnailBean> getThumbnailBeanFromPath(Context context, String path) {
        return getThumbnailBeanFromPath(context, path, 0);
    }

    /**
     * 获取某个文件夹下的所有图片和视频的URI
     *
     * @param context
     * @param path
     * @param maxDuration 限制视频时长
     * @return
     */
    public static ArrayList<ThumbnailBean> getThumbnailBeanFromPath(Context context, String path, long maxDuration) {
        ArrayList<ThumbnailBean> result1 = getImageThumbnailFromPath(context, path);
        ArrayList<ThumbnailBean> result2 = getVideoThumbnailFromPath(context, path, maxDuration);
        return doThumbnailBeanAlgorithm(result1, result2);
    }

    public static ArrayList<ThumbnailBean> getImageThumbnailFromPath(Context context, String path){
        ContentResolver cr = context.getContentResolver();
        ArrayList<ThumbnailBean> result = new ArrayList<>();
        Cursor cursor = null;
        try {
            if (TextUtils.isEmpty(path)) {
                cursor = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Images.ImageColumns._ID, MediaStore.Images.ImageColumns.DATA, MediaStore.Images.ImageColumns.DATE_TAKEN, MediaStore.Images.ImageColumns.ORIENTATION},
                        null, null, MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC" + ", " + MediaStore.Images.ImageColumns._ID + " ASC");
            } else {
                cursor = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Images.ImageColumns._ID, MediaStore.Images.ImageColumns.DATA, MediaStore.Images.ImageColumns.DATE_TAKEN, MediaStore.Images.ImageColumns.ORIENTATION},
                        MediaStore.Images.ImageColumns.DATA + " like ? ", new String[]{path + File.separator + "%"}, MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC" + ", " + MediaStore.Images.ImageColumns._ID + " ASC");
            }

            if (cursor == null) {
                return result;
            }

            if (cursor.moveToFirst()) {
                int idIndex = cursor.getColumnIndex(MediaStore.Images.ImageColumns._ID);
                int pathIndex = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                int dateIndex = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATE_TAKEN);
                int degreeIndex = cursor.getColumnIndex(MediaStore.Images.ImageColumns.ORIENTATION);
                do {
                    ThumbnailBean bb = new ThumbnailBean();
                    bb.setUri(ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cursor.getInt(idIndex)));

                    String thumbPath = cursor.getString(pathIndex);
                    if (checkImageError(thumbPath)) {
                        continue;
                    }
                    bb.setPath(thumbPath);

                    bb.setDate(cursor.getLong(dateIndex));
                    bb.setDegree(cursor.getInt(degreeIndex));
                    if (MediaFile.isGifFileType(bb.getPath())) {
                        bb.setType(com.videoeditor.function.media.MediaTypeUtil.TYPE_GIF);
                    } else if (MediaFile.isJPGFileType(bb.getPath())) {
                        bb.setType(com.videoeditor.function.media.MediaTypeUtil.TYPE_JPG);
                    } else if (MediaFile.isPNGFileType(bb.getPath())) {
                        bb.setType(com.videoeditor.function.media.MediaTypeUtil.TYPE_PNG);
                    } else {
                        bb.setType(com.videoeditor.function.media.MediaTypeUtil.TYPE_OTHER_IMAGE);
                    }
                    result.add(bb);
                } while (cursor.moveToNext());
            }

        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return result;
    }

    private static ArrayList<ThumbnailBean> getVideoThumbnailFromPath(Context context, String path, long maxDuration){
        ContentResolver cr = context.getContentResolver();
        ArrayList<ThumbnailBean> result = new ArrayList<>();
        Cursor cursor = null;
        try {
            if (TextUtils.isEmpty(path)) {
                cursor = cr.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Video.VideoColumns._ID, MediaStore.Video.VideoColumns.DATA, MediaStore.Video.VideoColumns.DATE_TAKEN, MediaStore.Video.VideoColumns.DURATION},
                        null, null, MediaStore.Video.VideoColumns.DATE_TAKEN + " DESC" + ", " + MediaStore.Video.VideoColumns._ID + " ASC");
            } else {
                cursor = cr.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Video.VideoColumns._ID, MediaStore.Video.VideoColumns.DATA, MediaStore.Video.VideoColumns.DATE_TAKEN, MediaStore.Video.VideoColumns.DURATION},
                        MediaStore.Video.VideoColumns.DATA + " like ? ", new String[]{path + File.separator + "%"}, MediaStore.Video.VideoColumns.DATE_TAKEN + " DESC" + ", " + MediaStore.Video.VideoColumns._ID + " ASC");
            }

            if (cursor == null) {
                return result;
            }

            if (cursor.moveToFirst()) {
                int idIndex = cursor.getColumnIndex(MediaStore.Video.VideoColumns._ID);
                int pathIndex = cursor.getColumnIndex(MediaStore.Video.VideoColumns.DATA);
                int dateIndex = cursor.getColumnIndex(MediaStore.Video.VideoColumns.DATE_TAKEN);
                int durationIndex = cursor.getColumnIndex(MediaStore.Video.VideoColumns.DURATION);
                do {
                    ThumbnailBean bb = new ThumbnailBean();
                    bb.setUri(ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, cursor.getInt(idIndex)));

                    String videoPath = cursor.getString(pathIndex);
                    if (checkVideoError(videoPath)) {
                        continue;
                    }
                    bb.setPath(videoPath);

                    bb.setDate(cursor.getLong(dateIndex));
                    bb.setType(com.videoeditor.function.media.MediaTypeUtil.TYPE_VIDEO);

                    //有些文件后缀为视频格式，却不是视频文件，长度为0， 需要排除
                    long time = cursor.getLong(durationIndex);
                    if (time <= 0) {
                        continue;
                    }
                    bb.setDuration(time);

                    if (maxDuration <= 0 || (time <= maxDuration)) {
                        result.add(bb);
                    }
                } while (cursor.moveToNext());
            }
        }catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return result;
    }

    /**
     * 排序算法
     *
     * @param rs1
     * @param rs2
     * @return
     */
    private static ArrayList<ThumbnailBean> doThumbnailBeanAlgorithm(ArrayList<ThumbnailBean> rs1, ArrayList<ThumbnailBean> rs2) {
        int i = 0, j = 0, k = 0;
        int length1 = rs1 == null ? 0 : rs1.size();
        int length2 = rs2 == null ? 0 : rs2.size();
        int length = length1 + length2;
        ArrayList<ThumbnailBean> result = new ArrayList<ThumbnailBean>(length);
        if (length1 == 0) {
            return rs2;
        } else if (length2 == 0) {
            return rs1;
        }
        while (k < length) {
            if (i == length1) {//rs1已经弄完
                result.add(rs2.get(j));
                j++;
            } else if (j == length2) {//rs2已经弄完
                result.add(rs1.get(i));
                i++;
            } else {
                if (rs1.get(i).getDate() > rs2.get(j).getDate()) {//rs1的放入
                    result.add(rs1.get(i));
                    i++;
                } else {//rs2的放入
                    result.add(rs2.get(j));
                    j++;
                }
            }
            k++;
        }
        return result;
    }


    /**
     * 获取某个文件夹下的所有图片的URI
     *
     * @param context
     * @param path
     * @return
     */
    public static ArrayList<ThumbnailBean> getImageThumbnailBeanFromPath(Context context, String path) {

        ArrayList<ThumbnailBean> result1 = null;
        Cursor c1 = null;
        //Images.ImageColumns.DATA 是图片的绝对路径
        try {
            ContentResolver cr = context.getContentResolver();
            result1 = new ArrayList<ThumbnailBean>();
            if (TextUtils.isEmpty(path)) {
                c1 = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Images.ImageColumns._ID, MediaStore.Images.ImageColumns.DATA, MediaStore.Images.ImageColumns.DATE_TAKEN, MediaStore.Images.ImageColumns.ORIENTATION},
                        null, null, MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC" + ", " + MediaStore.Images.ImageColumns._ID + " ASC");
            } else {
                c1 = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Images.ImageColumns._ID, MediaStore.Images.ImageColumns.DATA, MediaStore.Images.ImageColumns.DATE_TAKEN, MediaStore.Images.ImageColumns.ORIENTATION},
                        MediaStore.Images.ImageColumns.DATA + " like ? ", new String[]{path + File.separator + "%"}, MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC" + ", " + MediaStore.Images.ImageColumns._ID + " ASC");
            }
            if (c1.moveToFirst()) {
                int idIndex = c1.getColumnIndex(MediaStore.Images.ImageColumns._ID);
                int pathIndex = c1.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                int dateIndex = c1.getColumnIndex(MediaStore.Images.ImageColumns.DATE_TAKEN);
                int degreeIndex = c1.getColumnIndex(MediaStore.Images.ImageColumns.ORIENTATION);
                do {
                    ThumbnailBean bb = new ThumbnailBean();
                    bb.setUri(ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, c1.getInt(idIndex)));
                    bb.setPath(c1.getString(pathIndex));
                    bb.setDate(c1.getLong(dateIndex));
                    bb.setDegree(c1.getInt(degreeIndex));
                    if (MediaFile.isGifFileType(bb.getPath())) {
                        bb.setType(com.videoeditor.function.media.MediaTypeUtil.TYPE_GIF);
                    } else if (MediaFile.isJPGFileType(bb.getPath())) {
                        bb.setType(com.videoeditor.function.media.MediaTypeUtil.TYPE_JPG);
                    } else if (MediaFile.isPNGFileType(bb.getPath())) {
                        bb.setType(com.videoeditor.function.media.MediaTypeUtil.TYPE_PNG);
                    } else {
                        bb.setType(com.videoeditor.function.media.MediaTypeUtil.TYPE_OTHER_IMAGE);
                    }
                    result1.add(bb);
                } while (c1.moveToNext());
            }
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            if (c1 != null) {
                c1.close();
            }
        }
        return result1;
    }

    /**
     * 获取某个文件夹下的所有图片的URI
     *
     * @param context
     * @param path
     * @return
     */
    public static ArrayList<ThumbnailBean> getImageThumbnailBeanFromPathExcludeGif(Context context, String path) {

        ArrayList<ThumbnailBean> result1 = null;
        Cursor c1 = null;
        //Images.ImageColumns.DATA 是图片的绝对路径
        try {
            ContentResolver cr = context.getContentResolver();
            result1 = new ArrayList<ThumbnailBean>();
            if (TextUtils.isEmpty(path)) {
                c1 = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Images.ImageColumns._ID, MediaStore.Images.ImageColumns.DATA, MediaStore.Images.ImageColumns.DATE_TAKEN, MediaStore.Images.ImageColumns.ORIENTATION},
                        null, null, MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC" + ", " + MediaStore.Images.ImageColumns._ID + " ASC");
            } else {
                c1 = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Images.ImageColumns._ID, MediaStore.Images.ImageColumns.DATA, MediaStore.Images.ImageColumns.DATE_TAKEN, MediaStore.Images.ImageColumns.ORIENTATION},
                        MediaStore.Images.ImageColumns.DATA + " like ? ", new String[]{path + File.separator + "%"}, MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC" + ", " + MediaStore.Images.ImageColumns._ID + " ASC");
            }
            if (c1.moveToFirst()) {
                int idIndex = c1.getColumnIndex(MediaStore.Images.ImageColumns._ID);
                int pathIndex = c1.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                int dateIndex = c1.getColumnIndex(MediaStore.Images.ImageColumns.DATE_TAKEN);
                int degreeIndex = c1.getColumnIndex(MediaStore.Images.ImageColumns.ORIENTATION);
                do {
                    ThumbnailBean bb = new ThumbnailBean();
                    bb.setUri(ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, c1.getInt(idIndex)));
                    bb.setPath(c1.getString(pathIndex));
                    bb.setDate(c1.getLong(dateIndex));
                    bb.setDegree(c1.getInt(degreeIndex));
                    if (MediaFile.isGifFileType(bb.getPath())) {
                        continue;
                    } else if (MediaFile.isJPGFileType(bb.getPath())) {
                        bb.setType(com.videoeditor.function.media.MediaTypeUtil.TYPE_JPG);
                    } else if (MediaFile.isPNGFileType(bb.getPath())) {
                        bb.setType(com.videoeditor.function.media.MediaTypeUtil.TYPE_PNG);
                    } else {
                        bb.setType(com.videoeditor.function.media.MediaTypeUtil.TYPE_OTHER_IMAGE);
                    }
                    result1.add(bb);
                } while (c1.moveToNext());
            }
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            if (c1 != null) {
                c1.close();
            }
        }
        return result1;
    }

    /**
     * 获取某个文件夹下的所有视频的URI
     *
     * @param context
     * @param path
     * @return
     */
    public static ArrayList<ThumbnailBean> getVideoThumbnailBeanFromPath(Context context, String path) {

        ArrayList<ThumbnailBean> result2 = null;
        Cursor c2 = null;
        //Images.ImageColumns.DATA 是图片的绝对路径
        try {
            ContentResolver cr = context.getContentResolver();
            result2 = new ArrayList<ThumbnailBean>();
            if (TextUtils.isEmpty(path)) {
                c2 = cr.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Video.VideoColumns._ID, MediaStore.Video.VideoColumns.DATA, MediaStore.Video.VideoColumns.DATE_TAKEN},
                        null, null, MediaStore.Video.VideoColumns.DATE_TAKEN + " DESC" + ", " + MediaStore.Video.VideoColumns._ID + " ASC");
            } else {
                c2 = cr.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Video.VideoColumns._ID, MediaStore.Video.VideoColumns.DATA, MediaStore.Video.VideoColumns.DATE_TAKEN},
                        MediaStore.Video.VideoColumns.DATA + " like ? ", new String[]{path + File.separator + "%"}, MediaStore.Video.VideoColumns.DATE_TAKEN + " DESC" + ", " + MediaStore.Video.VideoColumns._ID + " ASC");
            }
            if (c2.moveToFirst()) {
                int idIndex = c2.getColumnIndex(MediaStore.Video.VideoColumns._ID);
                int pathIndex = c2.getColumnIndex(MediaStore.Video.VideoColumns.DATA);
                int dateIndex = c2.getColumnIndex(MediaStore.Video.VideoColumns.DATE_TAKEN);
                do {
                    ThumbnailBean bb = new ThumbnailBean();
                    bb.setUri(ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, c2.getInt(idIndex)));
                    bb.setPath(c2.getString(pathIndex));
                    bb.setDate(c2.getLong(dateIndex));
                    bb.setType(com.videoeditor.function.media.MediaTypeUtil.TYPE_VIDEO);
                    result2.add(bb);
                } while (c2.moveToNext());
            }
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            if (c2 != null) {
                c2.close();
            }
        }
        return result2;
    }

    /**
     * 获取某个文件夹下的所有图片和动态图片的ThumbnailBean
     *
     * @param context
     * @param path
     * @return
     */
    public static ArrayList<ThumbnailBean> getImageAndDynamicThumbnailBeanFromPath(Context context, String path) {

        ArrayList<ThumbnailBean> result1 = null;
        ArrayList<ThumbnailBean> result2 = null;
        Cursor c1 = null;
        Cursor c2 = null;
        //Images.ImageColumns.DATA 是图片的绝对路径
        try {
            ContentResolver cr = context.getContentResolver();
            result1 = new ArrayList<ThumbnailBean>();
            if (TextUtils.isEmpty(path)) {
                c1 = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Images.ImageColumns._ID, MediaStore.Images.ImageColumns.DATA, MediaStore.Images.ImageColumns.DATE_TAKEN, MediaStore.Images.ImageColumns.ORIENTATION},
                        null, null, MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC" + ", " + MediaStore.Images.ImageColumns._ID + " ASC");
            } else {
                c1 = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Images.ImageColumns._ID, MediaStore.Images.ImageColumns.DATA, MediaStore.Images.ImageColumns.DATE_TAKEN, MediaStore.Images.ImageColumns.ORIENTATION},
                        MediaStore.Images.ImageColumns.DATA + " like ? ", new String[]{path + File.separator + "%"}, MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC" + ", " + MediaStore.Images.ImageColumns._ID + " ASC");
            }
            if (c1.moveToFirst()) {
                int idIndex = c1.getColumnIndex(MediaStore.Images.ImageColumns._ID);
                int pathIndex = c1.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                int dateIndex = c1.getColumnIndex(MediaStore.Images.ImageColumns.DATE_TAKEN);
                int degreeIndex = c1.getColumnIndex(MediaStore.Images.ImageColumns.ORIENTATION);
                do {
                    ThumbnailBean bb = new ThumbnailBean();
                    bb.setUri(ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, c1.getInt(idIndex)));
                    bb.setPath(c1.getString(pathIndex));
                    bb.setDate(c1.getLong(dateIndex));
                    bb.setDegree(c1.getInt(degreeIndex));
                    if (MediaFile.isGifFileType(bb.getPath())) {
                        bb.setType(com.videoeditor.function.media.MediaTypeUtil.TYPE_GIF);
                    } else if (MediaFile.isJPGFileType(bb.getPath())) {
                        bb.setType(com.videoeditor.function.media.MediaTypeUtil.TYPE_JPG);
                    } else if (MediaFile.isPNGFileType(bb.getPath())) {
                        bb.setType(com.videoeditor.function.media.MediaTypeUtil.TYPE_PNG);
                    } else {
                        bb.setType(com.videoeditor.function.media.MediaTypeUtil.TYPE_OTHER_IMAGE);
                    }
                    result1.add(bb);
                } while (c1.moveToNext());
            }
            result2 = new ArrayList<ThumbnailBean>();
            if (TextUtils.isEmpty(path)) {
                c2 = cr.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Video.VideoColumns._ID, MediaStore.Video.VideoColumns.DATA, MediaStore.Video.VideoColumns.DATE_TAKEN, MediaStore.Video.VideoColumns.DURATION},
                        MediaStore.Video.VideoColumns.DATA + " like ? ", new String[]{"%" + ".mp4"}, MediaStore.Video.VideoColumns.DATE_TAKEN + " DESC" + ", " + MediaStore.Video.VideoColumns._ID + " ASC");
            } else {
                c2 = cr.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Video.VideoColumns._ID, MediaStore.Video.VideoColumns.DATA, MediaStore.Video.VideoColumns.DATE_TAKEN, MediaStore.Video.VideoColumns.DURATION},
                        MediaStore.Video.VideoColumns.DATA + " like ? ", new String[]{path + File.separator + "%" + ".mp4"}, MediaStore.Video.VideoColumns.DATE_TAKEN + " DESC" + ", " + MediaStore.Video.VideoColumns._ID + " ASC");
            }
            if (c2.moveToFirst()) {
                int idIndex = c2.getColumnIndex(MediaStore.Video.VideoColumns._ID);
                int pathIndex = c2.getColumnIndex(MediaStore.Video.VideoColumns.DATA);
                int dateIndex = c2.getColumnIndex(MediaStore.Video.VideoColumns.DATE_TAKEN);
                int durationIndex = c2.getColumnIndex(MediaStore.Video.VideoColumns.DURATION);
                do {
                    ThumbnailBean bb = new ThumbnailBean();
                    bb.setUri(ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, c2.getInt(idIndex)));
                    bb.setPath(c2.getString(pathIndex));
                    bb.setDate(c2.getLong(dateIndex));
                    bb.setType(com.videoeditor.function.media.MediaTypeUtil.TYPE_VIDEO);
                    long time = c2.getLong(durationIndex);
                    bb.setDuration(time);
                    result2.add(bb);
                } while (c2.moveToNext());
            }
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            if (c1 != null) {
                c1.close();
            }
            if (c2 != null) {
                c2.close();
            }
        }
        return doThumbnailBeanAlgorithm(result1, result2);
    }

    /**
     * 将Image Uri转为Bitmap  用于预览 不旋转
     * @param bean
     * @param uriErr  和 uiHandler 同时存在
     * @param uiHandler
     * @param v
     * @return
     */
//	public static Bitmap privateUriToBitmap(final BitmapBean bean, ILoadBitmap uriErr, ImageLoaderInterface.UIHandler uiHandler, View v){
//
//		Bitmap bitmap = null;
//		EntryptFileInputStream in1 = null;
//		EntryptFileInputStream in2 = null;
//		EntryptFileInputStream in3 = null;
//		ParcelFileDescriptor pfd1 = null;
//		ParcelFileDescriptor pfd2 = null;
//		ParcelFileDescriptor pfd3 = null;
//		ContentResolver mCr = App.getContext().getContentResolver();
//		BitmapFactory.Options option=new BitmapFactory.Options();
//		int insamplesize = 1;
//		try {
//			option.inJustDecodeBounds=true;
//			pfd1 = mCr.openFileDescriptor(bean.mUri, "r");
//			in1 = EncryptUtil.decrypt(pfd1.getFileDescriptor());
//
//			bitmap = BitmapFactory.decodeStream(in1, null, option);
//			if(uriErr != null){
//				if(bean.mHeight == 0 || bean.mHeight == -1){//没有从数据库中拿到宽高信息  通过解码获取
//					uriErr.OnGetSizeInfo(bean, option.outWidth, option.outHeight);
//					if(uiHandler != null){
//						uiHandler.refreashDiaplay(bean, v);
//					}
//				}
//			}
//			/*
//			 * 原来的长宽
//			 */
//			int width;
//			int height;
//			if(bean.mDegree == 90 || bean.mDegree == 270){
//				width = option.outHeight;
//				height = option.outWidth;
//			}else{
//				width = option.outWidth;
//				height = option.outHeight;
//			}
//			option.inJustDecodeBounds =false;
//			option.inPreferredConfig = Bitmap.Config.ARGB_8888;
//			option.inPurgeable = true;
//			option.inInputShareable = true;
//			option.inDither = false;
//			insamplesize = getFitSampleSize(width, height, false);
//			option.inSampleSize = insamplesize;
//			pfd2 = mCr.openFileDescriptor(bean.mUri, "r");
//			in2 = EncryptUtil.decrypt(pfd2.getFileDescriptor());
//			bitmap = BitmapFactory.decodeStream(in2, null, option);
//		} catch (Throwable e) {
//			System.gc();
//			e.printStackTrace();
//			/**
//			 * 如果内存溢出 缩小1/4再解析
//			 */
//			option.inSampleSize = insamplesize * 2;
//			try {
//				pfd3 = mCr.openFileDescriptor(bean.mUri, "r");
//				in3 = EncryptUtil.decrypt(pfd3.getFileDescriptor());
//				bitmap = BitmapFactory.decodeStream(in3, null, option);
//			} catch (Throwable e1) {
//				System.gc();
//				e1.printStackTrace();
//			}
//			return bitmap;
//		} finally{
//			try {
//				if(in1 != null){
//					in1.close();
//				}
//				if(in2 != null){
//					in2.close();
//				}
//				if(in3 != null){
//					in3.close();
//				}
//				if(pfd1 != null){
//					pfd1.close();
//				}
//				if(pfd2 != null){
//					pfd2.close();
//				}
//				if(pfd3 != null){
//					pfd3.close();
//				}
//			} catch (Throwable e) {
//			}
//		}
//		return bitmap;
//	}

    /**
     * 启动播放视频的Intent
     *
     * @param context
     * @param u
     */
    public static void startVideoPlayer(Context context, Uri u) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(u, "video/*");
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            context.startActivity(intent);
        } catch (Throwable e) {
            Toast.makeText(context, R.string.no_suitable_player, Toast.LENGTH_SHORT).show();
        }
    }

    public static ArrayList<ImageFolder> getAllFolderData(Context context) {
        return getAllFolderData(context, -1);
    }

    /**
     * 获取所有存在视频和图片文件夹信息
     *
     * @param context
     * @return
     */
    public static ArrayList<ImageFolder> getAllFolderData(Context context, long maxDuring) {

        ArrayList<ImageFolder> data1 = getAllImageFolderData(context, true);
        ArrayList<ImageFolder> data2 = getAllVideoFolderData(context);
        HashMap<String, Integer> data1Path = new HashMap<>();

        if (data1 != null){
            for (int i = 0; i< data1.size(); i++) {
                ThumbnailBean bean = data1.get(i).getFirstThumbnailBean();
                String dir = bean.getPath().substring(0, bean.getPath().lastIndexOf(File.separator));
                data1Path.put(dir, i);
            }
        }

        return  doUniformData(data1, data2, data1Path);
    }

    public static ArrayList<ImageFolder> getAllImageFolderData(Context context) {
        return getAllImageFolderData(context, true);
    }

    /**
     * 获取所有存在图片文件夹信息
     *
     * @param context
     * @return
     */
    public static ArrayList<ImageFolder> getAllImageFolderData(Context context, boolean includeGif) {

        ArrayList<ImageFolder> data1 = new ArrayList<>();
        Cursor c1 = null;
        try {
            String sql1;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                sql1 = "1=1) group by (" + MediaStore.Images.ImageColumns.BUCKET_ID + ") having " + MediaStore.Images.ImageColumns.DATE_TAKEN + "=max(" + MediaStore.Images.ImageColumns.DATE_TAKEN;
            } else {
                sql1 = "1=1) group by (" + MediaStore.Images.ImageColumns.BUCKET_ID;
            }
            ContentResolver cr = context.getContentResolver();
            c1 = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{"count(" + MediaStore.Images.ImageColumns.BUCKET_ID + ") as count", MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                            MediaStore.Images.ImageColumns._ID, MediaStore.Images.ImageColumns.DATA, MediaStore.Images.ImageColumns.DATE_TAKEN,
                            MediaStore.Images.ImageColumns.ORIENTATION, MediaStore.Images.ImageColumns.BUCKET_ID},
                    sql1, null, "max(" + MediaStore.Images.ImageColumns.DATE_TAKEN + ") DESC");
            if (c1 == null) {
                return data1;
            }
            if (c1.moveToFirst()) {
                int index = 0;
                int idIndex = c1.getColumnIndex(MediaStore.Images.ImageColumns._ID);
                int pathIndex = c1.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                int dateIndex = c1.getColumnIndex(MediaStore.Images.ImageColumns.DATE_TAKEN);
                int degreeIndex = c1.getColumnIndex(MediaStore.Images.ImageColumns.ORIENTATION);
                int nameIndex = c1.getColumnIndex(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME);
                int numIndex = c1.getColumnIndex("count");
                int bucketIdIndex = c1.getColumnIndex(MediaStore.Images.ImageColumns.BUCKET_ID);
                do {
                    ImageFolder imageFloder = new ImageFolder();
                    imageFloder.setCount(c1.getInt(numIndex));
                    String name = c1.getString(nameIndex);
                    ThumbnailBean bean = new ThumbnailBean();
                    bean.setDate(c1.getLong(dateIndex));
                    bean.setDegree(c1.getInt(degreeIndex));
                    bean.setPath(c1.getString(pathIndex));
                    if (MediaFile.isGifFileType(bean.getPath())) {
                        bean.setType(com.videoeditor.function.media.MediaTypeUtil.TYPE_GIF);
                    } else if (MediaFile.isJPGFileType(bean.getPath())) {
                        bean.setType(com.videoeditor.function.media.MediaTypeUtil.TYPE_JPG);
                    } else if (MediaFile.isPNGFileType(bean.getPath())) {
                        bean.setType(com.videoeditor.function.media.MediaTypeUtil.TYPE_PNG);
                    } else {
                        bean.setType(com.videoeditor.function.media.MediaTypeUtil.TYPE_OTHER_IMAGE);
                    }
                    bean.setUri(ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, c1.getInt(idIndex)));
                    String dir = bean.getPath().substring(0, bean.getPath().lastIndexOf(File.separator));
                    if (name == null) {
                        imageFloder.setDirAndName(dir);
                    } else {
                        imageFloder.setDir(dir);
                        imageFloder.setName(name);
                    }
                    imageFloder.setBucketId(c1.getString(bucketIdIndex));
                    if (!includeGif) {
                        ArrayList<ThumbnailBean> imgExcludeGif = getImageThumbnailBeanFromPathExcludeGif(context, dir);
                        if (imgExcludeGif != null && imgExcludeGif.size() > 0) {
                            imageFloder.setFirstImageBean(imgExcludeGif.get(0));
                            imageFloder.setCount(imgExcludeGif.size());
                            data1.add(imageFloder);
                            index++;
                        }
                    } else {
                        imageFloder.setFirstImageBean(bean);
                        data1.add(imageFloder);
                        index++;
                    }
                } while (c1.moveToNext());
            }
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            if (c1 != null) {
                c1.close();
            }
        }
        return data1;
    }

    /**
     * 获取所有存在视频文件夹信息
     *
     * @param context
     * @return
     */
    public static ArrayList<ImageFolder> getAllVideoFolderData(Context context) {

        ArrayList<ImageFolder> data2 = new ArrayList<>();
        Cursor c2 = null;
        try {
            String sql2;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                sql2 = "1=1) group by (" + MediaStore.Video.VideoColumns.BUCKET_ID + ") having " + MediaStore.Video.VideoColumns.DATE_TAKEN + "=max(" + MediaStore.Video.VideoColumns.DATE_TAKEN;
            } else {
                sql2 = "1=1) group by (" + MediaStore.Video.VideoColumns.BUCKET_ID;
            }
            ContentResolver cr = context.getContentResolver();
            c2 = cr.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, new String[]{"count(" + MediaStore.Video.VideoColumns.BUCKET_ID + ") as count", MediaStore.Video.VideoColumns.BUCKET_DISPLAY_NAME,
                            MediaStore.Video.VideoColumns._ID, MediaStore.Video.VideoColumns.DATA, MediaStore.Video.VideoColumns.DATE_TAKEN, MediaStore.Video.VideoColumns.BUCKET_ID},
                    sql2, null, "max(" + MediaStore.Video.VideoColumns.DATE_TAKEN + ") DESC");
            if (c2 == null) {
                return data2;
            }
            if (c2.moveToFirst()) {
                int idIndex = c2.getColumnIndex(MediaStore.Video.VideoColumns._ID);
                int pathIndex = c2.getColumnIndex(MediaStore.Video.VideoColumns.DATA);
                int dateIndex = c2.getColumnIndex(MediaStore.Video.VideoColumns.DATE_TAKEN);
                int nameIndex = c2.getColumnIndex(MediaStore.Video.VideoColumns.BUCKET_DISPLAY_NAME);
                int numIndex = c2.getColumnIndex("count");
                int bucketIdIndex = c2.getColumnIndex(MediaStore.Video.VideoColumns.BUCKET_ID);
                do {
                    ImageFolder imageFloder = new ImageFolder();
                    int count = c2.getInt(numIndex);
                    DLog.d(TAG, "count = " + count);
                    imageFloder.setCount(count);
                    String name = c2.getString(nameIndex);
                    ThumbnailBean bean = new ThumbnailBean();
                    bean.setType(com.videoeditor.function.media.MediaTypeUtil.TYPE_VIDEO);
                    bean.setDate(c2.getLong(dateIndex));
                    bean.setDegree(0);
                    bean.setPath(c2.getString(pathIndex));
                    bean.setUri(ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, c2.getInt(idIndex)));
                    String dir = bean.getPath().substring(0, bean.getPath().lastIndexOf(File.separator));
                    if (name == null) {
                        imageFloder.setDirAndName(dir);
                    } else {
                        imageFloder.setDir(dir);
                        imageFloder.setName(name);
                    }
                    imageFloder.setFirstImageBean(bean);
                    imageFloder.setBucketId(c2.getString(bucketIdIndex));
                    data2.add(imageFloder);
                } while (c2.moveToNext());
            }
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            if (c2 != null) {
                c2.close();
            }
        }
        return data2;
    }

    /**
     * 获取所有存在图片和动态图片文件夹信息
     * 如果直接拼接SQL的话  字符串需要加‘’（单引号）
     *
     * @param context
     * @return
     */
    public static ArrayList<ImageFolder> getImageAndDynamicFolderData(Context context) {

        ArrayList<ImageFolder> data1 = null;
        ArrayList<ImageFolder> data2 = null;
        HashMap<String, Integer> data1Path = null;
        Cursor c1 = null;
        Cursor c2 = null;
        try {
            String sql1;
            String sql2;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                sql1 = "1=1) group by (" + MediaStore.Images.ImageColumns.BUCKET_ID + ") having " + MediaStore.Images.ImageColumns.DATE_TAKEN + "=max(" + MediaStore.Images.ImageColumns.DATE_TAKEN;
                sql2 = MediaStore.Video.VideoColumns.DATA + " like ? ) group by (" + MediaStore.Video.VideoColumns.BUCKET_ID + ") having " + MediaStore.Video.VideoColumns.DATE_TAKEN + "=max(" + MediaStore.Video.VideoColumns.DATE_TAKEN;
            } else {
                sql1 = "1=1) group by (" + MediaStore.Images.ImageColumns.BUCKET_ID;
                sql2 = MediaStore.Video.VideoColumns.DATA + " like ? ) group by (" + MediaStore.Video.VideoColumns.BUCKET_ID;
            }
            ContentResolver cr = context.getContentResolver();
            c1 = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{"count(" + MediaStore.Images.ImageColumns.BUCKET_ID + ") as count", MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                            MediaStore.Images.ImageColumns._ID, MediaStore.Images.ImageColumns.DATA, MediaStore.Images.ImageColumns.DATE_TAKEN,
                            MediaStore.Images.ImageColumns.ORIENTATION, MediaStore.Images.ImageColumns.BUCKET_ID},
                    sql1, null, "max(" + MediaStore.Images.ImageColumns.DATE_TAKEN + ") DESC");
            int count = c1.getCount();
            data1Path = new HashMap<String, Integer>(count);
            data1 = new ArrayList<ImageFolder>(count);
            if (c1.moveToFirst()) {
                int index = 0;
                int idIndex = c1.getColumnIndex(MediaStore.Images.ImageColumns._ID);
                int pathIndex = c1.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                int dateIndex = c1.getColumnIndex(MediaStore.Images.ImageColumns.DATE_TAKEN);
                int degreeIndex = c1.getColumnIndex(MediaStore.Images.ImageColumns.ORIENTATION);
                int nameIndex = c1.getColumnIndex(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME);
                int numIndex = c1.getColumnIndex("count");
                int bucketIdIndex = c1.getColumnIndex(MediaStore.Images.ImageColumns.BUCKET_ID);
                do {
                    ImageFolder imageFloder = new ImageFolder();
                    imageFloder.setCount(c1.getInt(numIndex));
                    String name = c1.getString(nameIndex);
                    ThumbnailBean bean = new ThumbnailBean();
                    bean.setDate(c1.getLong(dateIndex));
                    bean.setDegree(c1.getInt(degreeIndex));
                    bean.setPath(c1.getString(pathIndex));
                    if (MediaFile.isGifFileType(bean.getPath())) {
                        bean.setType(com.videoeditor.function.media.MediaTypeUtil.TYPE_GIF);
                    } else if (MediaFile.isJPGFileType(bean.getPath())) {
                        bean.setType(com.videoeditor.function.media.MediaTypeUtil.TYPE_JPG);
                    } else if (MediaFile.isPNGFileType(bean.getPath())) {
                        bean.setType(com.videoeditor.function.media.MediaTypeUtil.TYPE_PNG);
                    } else {
                        bean.setType(com.videoeditor.function.media.MediaTypeUtil.TYPE_OTHER_IMAGE);
                    }
                    bean.setUri(ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, c1.getInt(idIndex)));
                    String dir = bean.getPath().substring(0, bean.getPath().lastIndexOf(File.separator));
                    if (name == null) {
                        imageFloder.setDirAndName(dir);
                    } else {
                        imageFloder.setDir(dir);
                        imageFloder.setName(name);
                    }
                    imageFloder.setFirstImageBean(bean);
                    imageFloder.setBucketId(c1.getString(bucketIdIndex));
                    data1.add(imageFloder);
                    data1Path.put(dir, index);
                    index++;
                } while (c1.moveToNext());
            }
            c2 = cr.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, new String[]{"count(" + MediaStore.Video.VideoColumns.BUCKET_ID + ") as count", MediaStore.Video.VideoColumns.BUCKET_DISPLAY_NAME,
                            MediaStore.Video.VideoColumns._ID, MediaStore.Video.VideoColumns.DATA, MediaStore.Video.VideoColumns.DATE_TAKEN, MediaStore.Video.VideoColumns.BUCKET_ID},
                    sql2, new String[]{"%" + File.separator + "%.mp4"}, "max(" + MediaStore.Video.VideoColumns.DATE_TAKEN + ") DESC");
            data2 = new ArrayList<ImageFolder>(c2.getCount());
            if (c2.moveToFirst()) {
                int idIndex = c2.getColumnIndex(MediaStore.Video.VideoColumns._ID);
                int pathIndex = c2.getColumnIndex(MediaStore.Video.VideoColumns.DATA);
                int dateIndex = c2.getColumnIndex(MediaStore.Video.VideoColumns.DATE_TAKEN);
                int nameIndex = c2.getColumnIndex(MediaStore.Video.VideoColumns.BUCKET_DISPLAY_NAME);
                int numIndex = c2.getColumnIndex("count");
                int bucketIdIndex = c2.getColumnIndex(MediaStore.Video.VideoColumns.BUCKET_ID);
                do {
                    ImageFolder imageFloder = new ImageFolder();
                    imageFloder.setCount(c2.getInt(numIndex));
                    String name = c2.getString(nameIndex);
                    ThumbnailBean bean = new ThumbnailBean();
                    bean.setType(com.videoeditor.function.media.MediaTypeUtil.TYPE_VIDEO);
                    bean.setDate(c2.getLong(dateIndex));
                    bean.setDegree(0);
                    bean.setPath(c2.getString(pathIndex));
                    bean.setUri(ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, c2.getInt(idIndex)));
                    String dir = bean.getPath().substring(0, bean.getPath().lastIndexOf(File.separator));
                    if (name == null) {
                        imageFloder.setDirAndName(dir);
                    } else {
                        imageFloder.setDir(dir);
                        imageFloder.setName(name);
                    }
                    imageFloder.setFirstImageBean(bean);
                    imageFloder.setBucketId(c2.getString(bucketIdIndex));
                    data2.add(imageFloder);
                } while (c2.moveToNext());
            }
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            if (c1 != null) {
                c1.close();
            }
            if (c2 != null) {
                c2.close();
            }
        }
        return doUniformData(data1, data2, data1Path);
    }


    /**
     * 视频文件夹和图片文件夹合并
     *
     * @param data1
     * @param data2
     * @param data1Path
     * @return
     */
    private static ArrayList<ImageFolder> doUniformData(ArrayList<ImageFolder> data1, ArrayList<ImageFolder> data2, HashMap<String, Integer> data1Path) {
        if (data2 == null) {
            return data1;
        }
        if (data1 == null) {
            return data2;
        }
        int count = data2.size();
        Set<String> keySet = data1Path.keySet();
        //这个用于保存修改过的ImageFolder
        ArrayList<ImageFolder> data3 = new ArrayList<ImageFolder>();
        for (int i = 0; i < count; ) {//执行合并的操作
            ImageFolder imageFD2 = data2.get(i);
            ThumbnailBean bean2 = imageFD2.getFirstThumbnailBean();
            String path = imageFD2.getDir();
            if (keySet.contains(path)) {
                int index = data1Path.get(path);
                ImageFolder imageFD1 = data1.get(index);
                ThumbnailBean bean1 = imageFD1.getFirstThumbnailBean();
                if (bean2.getDate() > bean1.getDate()) {//bean2的时间比较新
                    imageFD1.setFirstImageBean(bean2);
                    data3.add(imageFD1);
                }
                imageFD1.setCount(imageFD1.getCount() + imageFD2.getCount());
                data2.remove(i);
                count--;
            } else {
                i++;
            }
        }
        if (data3.size() != 0) {
            data1.removeAll(data3);
            quickSort(data3, 0, data3.size() - 1);
            return doFolderDataAlgorithm(doFolderDataAlgorithm(data1, data3), data2);
        } else {
            return doFolderDataAlgorithm(data1, data2);
        }
    }

    /**
     * 排序算法
     *
     * @param data1
     * @param data2
     * @return
     */
    private static ArrayList<ImageFolder> doFolderDataAlgorithm(ArrayList<ImageFolder> data1, ArrayList<ImageFolder> data2) {
        int i = 0, j = 0, k = 0;
        int length1 = data1 == null ? 0 : data1.size();
        int length2 = data2 == null ? 0 : data2.size();
        int length = length1 + length2;
        ArrayList<ImageFolder> result = new ArrayList<ImageFolder>(length);
        if (length1 == 0) {
            return data2;
        } else if (length2 == 0) {
            return data1;
        }
        while (k < length) {
            if (i == length1) {//rs1已经弄完
                result.add(data2.get(j));
                j++;
            } else if (j == length2) {//rs2已经弄完
                result.add(data1.get(i));
                i++;
            } else {
                if (data1.get(i).getFirstThumbnailBean().getDate() > data2.get(j).getFirstThumbnailBean().getDate()) {//rs1的放入
                    result.add(data1.get(i));
                    i++;
                } else {//rs2的放入
                    result.add(data2.get(j));
                    j++;
                }
            }
            k++;
        }
        return result;
    }

    /**
     * 先按照数组为数据原型写出算法，再写出扩展性算法。数组{49,38,65,97,76,13,27}
     */
    public static void quickSort(ArrayList<ImageFolder> data, int left, int right) {
        int pivot;
        if (left < right) {
            //pivot作为枢轴，较之小的元素在左，较之大的元素在右
            pivot = partition(data, left, right);
            //对左右数组递归调用快速排序，直到顺序完全正确
            quickSort(data, left, pivot - 1);
            quickSort(data, pivot + 1, right);
        }
    }

    public static int partition(ArrayList<ImageFolder> data, int left, int right) {
        ImageFolder pivotkey = data.get(left);
        //枢轴选定后永远不变，最终在中间，前小后大
        while (left < right) {
            while (left < right && data.get(right).getFirstThumbnailBean().getDate() <= pivotkey.getFirstThumbnailBean().getDate()) {
                --right;
            }
            //将比枢轴小的元素移到低端，此时right位相当于空，等待低位比pivotkey大的数补上
            data.set(left, data.get(right));
            while (left < right && data.get(left).getFirstThumbnailBean().getDate() >= pivotkey.getFirstThumbnailBean().getDate()) {
                ++left;
            }
            //将比枢轴大的元素移到高端，此时left位相当于空，等待高位比pivotkey小的数补上
            data.set(right, data.get(left));
        }
        //当left == right，完成一趟快速排序，此时left位相当于空，等待pivotkey补上
        data.set(left, pivotkey);
        return left;
    }

    /**
     * 新策略用于图片预览 和 编辑的图片压缩
     * 向下取
     *
     * @param width
     * @param height
     * @return
     */
    public static float getFitSampleSizeLarger(int width, int height) {
        int size = width * height * 4;//原来的大小

        float maxSize;
        if (is1080PResolution()) {//高分辨率
            if (isHighMemory()) {
                maxSize = 1920 * 1080 * 4 * 2;
                if (size > maxSize) {//这时候才需要压缩
                    return (size / maxSize);
                }
            } else {
                maxSize = 1920 * 1080 * 4 * 1.5f;
                if (size > maxSize) {//这时候才需要压缩
                    return (size / maxSize);
                }
            }
        } else if (is720PResolution()) {//中分辨率
            if (isHighMemory()) {
                maxSize = 1280 * 720 * 4 * 3.5f;
                if (size > maxSize) {//这时候才需要压缩
                    return (size / maxSize);
                }
            } else {
                maxSize = 1280 * 720 * 4 * 2.5f;
                if (size > maxSize) {//这时候才需要压缩
                    return (size / maxSize);
                }
            }
        } else {//低分辨率
            if (isHighMemory()) {
                maxSize = SCREEN_WIDTH * SCREEN_HEIGHT * 4 * 4;
                if (size > maxSize) {//这时候才需要压缩
                    return (size / maxSize);
                }
            } else {
                maxSize = SCREEN_WIDTH * SCREEN_HEIGHT * 4 * 2f;
                if (size > maxSize) {//这时候才需要压缩
                    return (size / maxSize);
                }
            }
        }

        return 1;
    }

    private static boolean is720PResolution() {
        int modeSize = 1280 * 720;
        int size = SCREEN_WIDTH * SCREEN_HEIGHT;
        if (size >= modeSize * 0.7) {
            return true;
        } else {
            return false;
        }
    }

    private static boolean is1080PResolution() {
        int modeSize = 1920 * 1080;
        int size = SCREEN_WIDTH * SCREEN_HEIGHT;
        if (size >= modeSize * 0.7) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断是不是
     *
     * @return
     */
    private static boolean isHighMemory() {
        if (mMaxMemory >= 536870912) {//512
            return true;
        } else {
            return false;
        }
    }

    /**
     * 用于解析图片
     *
     * @param width
     * @param height
     * @param scale
     * @return
     */
    public static float checkCanvasAndTextureSize(int width, int height, float scale) {
        float result = scale;
        Canvas canvas = new Canvas();
        int maxW = canvas.getMaximumBitmapWidth() / 8;
        int maxH = canvas.getMaximumBitmapHeight() / 8;
        int max2 = SPDataManager.getMaxTextureSize();
        int max = 0;
        if (max2 != 0) {
            max = Math.min(Math.min(maxW, maxH), max2);
        } else {
            max = Math.min(maxW, maxH);
        }
        if (width * width / scale >= max * max || height * height / scale > max * max) {
            result = Math.max(width * 1.0f / max, height * 1.0f / max);
            result = result * result;
        }
        return result;
    }

    /**
     * 删除android5.0以下外置sdcard的Media文件
     * android 5.0以上可以申请权限使用DocumentFile的方式删除，这种直接删除
     * 先删除  如果成功则清除掉数据库中的
     *
     * @param context
     * @param beans
     * @param listener
     * @return 成功的个数
     */
    public static int deleteExtSdcardMeidaUnderAndroidM(Context context, ArrayList<ThumbnailBean> beans, IDeleteListener<ThumbnailBean> listener) {
        ContentResolver cr = context.getContentResolver();
        int length = beans.size();
        int successed = 0;
        int failed = 0;
        for (int j = 0; j < length; j++) {
            ThumbnailBean bean = beans.get(j);
            File f = new File(bean.getPath());
            if (f.delete()) {
                cr.delete(bean.getUri(), null, null);
                //删除成功 文件删除成功就算成功了
                successed++;
                if (listener != null) {
                    listener.onDeleteFile(bean, true);
                }
            } else {//删除失败
                failed++;
                if (listener != null) {
                    listener.onDeleteFile(bean, false);
                }
            }
        }
        return successed;
    }

    /**
     * 从程序内部获取BitmapBean  处理了私密照片  还有 视频等问题
     *
     * @param context
     * @param uri
     * @return
     */
    public static BitmapBean getInterBitmapBeanFormUri(Context context, @NonNull Uri uri) {
        if (uri == null) {
            return null;
        }
        BitmapBean imageBean = null;
        if (UriUtil.INSTANCE.isFileUri(uri)) {
            imageBean = new BitmapBean();
            imageBean.mUri = uri;
            imageBean.mPath = uri.getPath();
            if (MediaFile.isGifFileType(imageBean.mPath)) {
                imageBean.mType = com.videoeditor.function.media.MediaTypeUtil.TYPE_GIF;
            } else if (MediaFile.isPNGFileType(imageBean.mPath)) {
                imageBean.mType = com.videoeditor.function.media.MediaTypeUtil.TYPE_PNG;
            } else if (MediaFile.isJPGFileType(imageBean.mPath)) {
                imageBean.mType = com.videoeditor.function.media.MediaTypeUtil.TYPE_JPG;
            } else if (MediaFile.getFileType(imageBean.mPath) != null && MediaFile.isVideoFileType(MediaFile.getFileType(imageBean.mPath).fileType)) {
                imageBean.mType = com.videoeditor.function.media.MediaTypeUtil.TYPE_VIDEO;
            } else {
                imageBean.mType = com.videoeditor.function.media.MediaTypeUtil.TYPE_OTHER_IMAGE;
            }
            imageBean.mDegree = MediaThumbnailUtil.readPictureDegree(imageBean.mPath);
        } else {
            imageBean = ImageHelper.getBitmapBeanFormUri(context, uri);
        }
        return imageBean;
    }

    /**
     * 新策略用于社区上传的压缩
     * 向下取
     *
     * @param width
     * @param height
     * @return
     */
    public static float getShareFitSampleSize(int width, int height) {
        //nw nh是显示在屏幕上的像素宽高
        int size = width * height * 4;//原来的大小

        float maxSize = 1920 * 1080 * 4;
        if (size > maxSize) {//这时候才需要压缩
            return (size / maxSize);
        }
        return 1;
    }

    public static ArrayList<ThumbnailBean> getThumbnailBeanFromBucketId(Context context, String bucketId) {
        return getThumbnailBeanFromBucketId(context, bucketId, -1);
    }

    /**
     * 获取某个文件夹下的所有图片和视频的URI
     *
     * @param context
     * @param bucketId
     * @return
     */
    public static ArrayList<ThumbnailBean> getThumbnailBeanFromBucketId(Context context, String bucketId, long maxDuring) {

        ArrayList<ThumbnailBean> result1 = getImageThumbnailBeanFromBucketId(context, bucketId);
        ArrayList<ThumbnailBean> result2 = getVideoThumbnailBeanFromBucketId(context, bucketId);

        return doThumbnailBeanAlgorithm(result1, result2);
    }

    public static ArrayList<ThumbnailBean> getImageThumbnailBeanFromBucketId(Context context, String bucketId) {
        return getImageThumbnailBeanFromBucketId(context, bucketId, true);
    }

    /**
     * 获取某个文件夹下的所有图片的URI
     *
     * @param context
     * @param bucketId
     * @return
     */
    public static ArrayList<ThumbnailBean> getImageThumbnailBeanFromBucketId(Context context, String bucketId, boolean includeGif) {
        ArrayList<ThumbnailBean> result1 = new ArrayList<>();

        if (TextUtils.isEmpty(bucketId)) {
            return result1;
        }

        Cursor c1 = null;
        //Images.ImageColumns.DATA 是图片的绝对路径
        try {
            ContentResolver cr = context.getContentResolver();

            c1 = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Images.ImageColumns._ID, MediaStore.Images.ImageColumns.DATA, MediaStore.Images.ImageColumns.DATE_TAKEN, MediaStore.Images.ImageColumns.ORIENTATION},
                    MediaStore.Images.ImageColumns.BUCKET_ID + " = ? ", new String[]{bucketId}, MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC" + ", " + MediaStore.Images.ImageColumns._ID + " ASC");
            if (c1 == null) {
                return result1;
            }

            if (c1.moveToFirst()) {
                int idIndex = c1.getColumnIndex(MediaStore.Images.ImageColumns._ID);
                int pathIndex = c1.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                int dateIndex = c1.getColumnIndex(MediaStore.Images.ImageColumns.DATE_TAKEN);
                int degreeIndex = c1.getColumnIndex(MediaStore.Images.ImageColumns.ORIENTATION);
                do {
                    ThumbnailBean bb = new ThumbnailBean();
                    bb.setUri(ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, c1.getInt(idIndex)));

                    String thumbPath = c1.getString(pathIndex);
                    if (checkImageError(thumbPath)) {
                        continue;
                    }
                    bb.setPath(thumbPath);

                    bb.setDate(c1.getLong(dateIndex));
                    bb.setDegree(c1.getInt(degreeIndex));
                    if (MediaFile.isGifFileType(bb.getPath())) {
                        if (!includeGif) {
                            continue;
                        }
                        bb.setType(com.videoeditor.function.media.MediaTypeUtil.TYPE_GIF);
                    } else if (MediaFile.isJPGFileType(bb.getPath())) {
                        bb.setType(com.videoeditor.function.media.MediaTypeUtil.TYPE_JPG);
                    } else if (MediaFile.isPNGFileType(bb.getPath())) {
                        bb.setType(com.videoeditor.function.media.MediaTypeUtil.TYPE_PNG);
                    } else {
                        bb.setType(com.videoeditor.function.media.MediaTypeUtil.TYPE_OTHER_IMAGE);
                    }
                    result1.add(bb);
                } while (c1.moveToNext());
            }
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            if (c1 != null) {
                c1.close();
            }
        }
        return result1;
    }

    /**
     * 获取某个文件夹下的所有图片和视频的URI
     *
     * @param context
     * @param bucketId
     * @return
     */
    public static ArrayList<ThumbnailBean> getVideoThumbnailBeanFromBucketId(Context context, String bucketId) {
        ArrayList<ThumbnailBean> result2 = new ArrayList<ThumbnailBean>();

        if (TextUtils.isEmpty(bucketId)){
            return result2;
        }

        Cursor c2 = null;
        //Images.ImageColumns.DATA 是图片的绝对路径
        try {
            ContentResolver cr = context.getContentResolver();

            c2 = cr.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Video.VideoColumns._ID, MediaStore.Video.VideoColumns.DATA, MediaStore.Video.VideoColumns.DATE_TAKEN, MediaStore.Video.VideoColumns.DURATION},
                    MediaStore.Video.VideoColumns.BUCKET_ID + " = ? ", new String[]{bucketId}, MediaStore.Video.VideoColumns.DATE_TAKEN + " DESC" + ", " + MediaStore.Video.VideoColumns._ID + " ASC");

            if (c2 == null) {
                return result2;
            }

            if (c2.moveToFirst()) {
                int idIndex = c2.getColumnIndex(MediaStore.Video.VideoColumns._ID);
                int pathIndex = c2.getColumnIndex(MediaStore.Video.VideoColumns.DATA);
                int dateIndex = c2.getColumnIndex(MediaStore.Video.VideoColumns.DATE_TAKEN);
                int durationIndex = c2.getColumnIndex(MediaStore.Video.VideoColumns.DURATION);
                do {
                    ThumbnailBean bb = new ThumbnailBean();
                    bb.setUri(ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, c2.getInt(idIndex)));

                    String videoPath = c2.getString(pathIndex);
                    if (checkVideoError(videoPath)) {
                        DLog.d(TAG, "error video path = " + videoPath);
                        continue;
                    }
                    bb.setPath(videoPath);

                    //有些文件后缀为视频格式，却不是视频文件，长度为0， 需要排除
                    long time = c2.getLong(durationIndex);
                    if (time <= 0) {
                        continue;
                    }
                    bb.setDuration(time);

                    bb.setDate(c2.getLong(dateIndex));
                    bb.setType(com.videoeditor.function.media.MediaTypeUtil.TYPE_VIDEO);
                    result2.add(bb);
                } while (c2.moveToNext());
            }
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            if (c2 != null) {
                c2.close();
            }
        }
        return result2;
    }

    /**
     * 获取某个bucketId下的图片和动态图片的URI
     *
     * @param context
     * @param bucketId
     * @return
     */
    public static ArrayList<ThumbnailBean> getImageAndDynamicThumbnailBeanFromBucketId(Context context, String bucketId, String path) {

        ArrayList<ThumbnailBean> result1 = null;
        ArrayList<ThumbnailBean> result2 = null;
        Cursor c1 = null;
        Cursor c2 = null;
        //Images.ImageColumns.DATA 是图片的绝对路径
        try {
            ContentResolver cr = context.getContentResolver();
            result1 = new ArrayList<ThumbnailBean>();
            c1 = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Images.ImageColumns._ID, MediaStore.Images.ImageColumns.DATA, MediaStore.Images.ImageColumns.DATE_TAKEN, MediaStore.Images.ImageColumns.ORIENTATION},
                    MediaStore.Images.ImageColumns.BUCKET_ID + " = ? ", new String[]{bucketId}, MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC" + ", " + MediaStore.Images.ImageColumns._ID + " ASC");
            if (c1.moveToFirst()) {
                int idIndex = c1.getColumnIndex(MediaStore.Images.ImageColumns._ID);
                int pathIndex = c1.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                int dateIndex = c1.getColumnIndex(MediaStore.Images.ImageColumns.DATE_TAKEN);
                int degreeIndex = c1.getColumnIndex(MediaStore.Images.ImageColumns.ORIENTATION);
                do {
                    ThumbnailBean bb = new ThumbnailBean();
                    bb.setUri(ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, c1.getInt(idIndex)));
                    bb.setPath(c1.getString(pathIndex));
                    bb.setDate(c1.getLong(dateIndex));
                    bb.setDegree(c1.getInt(degreeIndex));
                    if (MediaFile.isGifFileType(bb.getPath())) {
                        bb.setType(com.videoeditor.function.media.MediaTypeUtil.TYPE_GIF);
                    } else if (MediaFile.isJPGFileType(bb.getPath())) {
                        bb.setType(com.videoeditor.function.media.MediaTypeUtil.TYPE_JPG);
                    } else if (MediaFile.isPNGFileType(bb.getPath())) {
                        bb.setType(com.videoeditor.function.media.MediaTypeUtil.TYPE_PNG);
                    } else {
                        bb.setType(com.videoeditor.function.media.MediaTypeUtil.TYPE_OTHER_IMAGE);
                    }
                    result1.add(bb);
                } while (c1.moveToNext());
            }
            result2 = new ArrayList<ThumbnailBean>();
            c2 = cr.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Video.VideoColumns._ID, MediaStore.Video.VideoColumns.DATA, MediaStore.Video.VideoColumns.DATE_TAKEN},
                    MediaStore.Video.VideoColumns.BUCKET_ID + " = ? AND " + MediaStore.Video.VideoColumns.DATA + " like ? ", new String[]{bucketId, path + File.separator + "%" + ".mp4"}, MediaStore.Video.VideoColumns.DATE_TAKEN + " DESC" + ", " + MediaStore.Video.VideoColumns._ID + " ASC");
            if (c2.moveToFirst()) {
                int idIndex = c2.getColumnIndex(MediaStore.Video.VideoColumns._ID);
                int pathIndex = c2.getColumnIndex(MediaStore.Video.VideoColumns.DATA);
                int dateIndex = c2.getColumnIndex(MediaStore.Video.VideoColumns.DATE_TAKEN);
                do {
                    ThumbnailBean bb = new ThumbnailBean();
                    bb.setUri(ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, c2.getInt(idIndex)));
                    bb.setPath(c2.getString(pathIndex));
                    bb.setDate(c2.getLong(dateIndex));
                    bb.setType(com.videoeditor.function.media.MediaTypeUtil.TYPE_VIDEO);
                    result2.add(bb);
                } while (c2.moveToNext());
            }
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            if (c1 != null) {
                c1.close();
            }
            if (c2 != null) {
                c2.close();
            }
        }
        return doThumbnailBeanAlgorithm(result1, result2);
    }

    /**
     * 根据BucketId删除图片和视频
     *
     * @param context
     * @param folder
     * @return 完全删除Return true否则返回false
     */
    public static int deleteMediaByBucketId(Context context, ImageFolder folder, IDeleteListener listener) {
        if (folder == null) {
            if (listener != null) {
                listener.onDeleteFiles(false);
            }
            return 0;
        }
        if (ExtSdcardUtils.isExtSdcardPath(folder.getDir())) {//这种情况删除时需要特殊处理  要先删除文件 如果是外置路径 且支持外置SDCARD
            ArrayList<ThumbnailBean> thumbnailBeanArrayList = folder.getData();
            if (thumbnailBeanArrayList == null) {//如果为空则重新获取
                thumbnailBeanArrayList = getThumbnailBeanFromBucketId(context, folder.getBucketId());
            }
            //没有获取到任何的 thumbnailBean
            if (thumbnailBeanArrayList == null || thumbnailBeanArrayList.size() == 0) {
                return 0;
            }

            if (PhoneInfo.isSupportWriteExtSdCard()) {
                //相同的buckedId 一定是同一个父文件夹
//				String path = FileUtil.getParentFilePath(thumbnailBeanArrayList.get(0).getPath());
                return ExtSdcardUtils.deleteDirectoryFile(context, thumbnailBeanArrayList, listener);
            } else {//5.0以下且是外置的路径  这时候很可能无法删除 一个删除失败就说明无法操作这个文件夹 则终止
                return deleteExtSdcardMeidaUnderAndroidM(context, thumbnailBeanArrayList, listener);
            }
        } else {
            //下面部分是删除Media数据库中的东西 如果外置SDCARD没有特殊权限 同时也会删除文件
            String bucketId = folder.getBucketId();
            ContentResolver cr = context.getContentResolver();
            int i = cr.delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.ImageColumns.BUCKET_ID + " = ?", new String[]{bucketId});
            int j = cr.delete(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.ImageColumns.BUCKET_ID + " = ?", new String[]{bucketId});

            if (i == -1 && j == -1) {
                if (listener != null) {
                    listener.onDeleteFiles(false);
                }
                return 0;
            } else if (i == -1) {
                if (listener != null) {
                    listener.onDeleteFiles(false);
                }
                return j;
            } else if (j == -1) {
                if (listener != null) {
                    listener.onDeleteFiles(false);
                }
                return i;
            }
            if (listener != null) {
                listener.onDeleteFiles(true);
            }
            return (i + j);
        }
    }

    /**
     * 根据Uri删除图片或者视频
     *
     * @param context
     * @param thumbnailBeanArrayList
     * @param needCategory           是否需要分类 根据需求
     * @return
     */
    public static int deleteMedia(Context context, ArrayList<ThumbnailBean> thumbnailBeanArrayList, IDeleteListener listener, boolean needCategory) {
        if (thumbnailBeanArrayList == null || thumbnailBeanArrayList.size() == 0) {
            return 0;
        }
        ArrayList<ArrayList> divideResult;
        if (needCategory) {//分类
            divideResult = divideMediaByParent(thumbnailBeanArrayList);
        } else {
            divideResult = new ArrayList<>();
            divideResult.add(thumbnailBeanArrayList);
        }
        int size = divideResult.size();
        int successedNumber = 0;
        for (int i = 0; i < size; i++) {
            ArrayList<ThumbnailBean> thumbnailBeens = divideResult.get(i);
            if (thumbnailBeens != null && thumbnailBeens.size() > 0) {
                //父目录的Path
                String path = FileUtil.getParentFilePath(thumbnailBeens.get(0).getPath());
                if (ExtSdcardUtils.isExtSdcardPath(path)) {//如果是外置路径
                    if (PhoneInfo.isSupportWriteExtSdCard()) {//支持外置SDCARD 这种情况删除时需要特殊处理  要先删除文件
                        int success = ExtSdcardUtils.deleteDirectoryFile(context, thumbnailBeens, listener);
                        successedNumber += success;
                    } else {
                        int success = deleteExtSdcardMeidaUnderAndroidM(context, thumbnailBeens, listener);
                        successedNumber += success;
                    }
                } else {//内置SDCARD的文件  直接删除即可
                    int success = deleteInterSdcardMedia(context, thumbnailBeens, listener);
                    successedNumber += success;
                }
            }
        }
        return successedNumber;
    }

    /**
     * 根据父文件夹分类
     *
     * @param thumbnailBeanArrayList
     * @return
     */
    public static ArrayList<ArrayList> divideMediaByParent(ArrayList<ThumbnailBean> thumbnailBeanArrayList) {
        ArrayList<ThumbnailBean> copyData = ArrayUtil.copyArray(thumbnailBeanArrayList);
        ArrayList<ArrayList> result = new ArrayList<>();
        String path;
        ArrayList<ThumbnailBean> beans;
        while (true) {
            path = FileUtil.getParentFilePath(copyData.get(0).getPath());
            beans = new ArrayList<>();//一个类别
            result.add(beans);//加入到类别中
            beans.add(copyData.get(0));//文件加入这个类别
            copyData.remove(0);//移除掉第一个
            int size = copyData.size();
            for (int i = 0; i < size; ) {//分类
                ThumbnailBean bean = copyData.get(i);
                String parentPath = FileUtil.getParentFilePath(bean.getPath());
                if (parentPath.equals(path)) {//遍历完将一个类别的加入
                    beans.add(bean);
                    //移除掉已经添加的
                    copyData.remove(i);
                    --size;//总的大小减少 i不变 因为移除掉了一个
                } else {
                    ++i;
                }
            }
            if (copyData.size() == 0) {
                break;
            }
        }
        return result;
    }

    /**
     * 删除内置SDCARD上的Media文件
     *
     * @param context
     * @param beans
     * @param listener
     * @return 成功的个数
     */
    public static int deleteInterSdcardMedia(Context context, ArrayList<ThumbnailBean> beans, IDeleteListener<ThumbnailBean> listener) {
        ContentResolver cr = context.getContentResolver();
        int length = beans.size();
        int successed = 0;
        int failed = 0;
        for (int j = 0; j < length; j++) {
            ThumbnailBean bean = beans.get(j);
            int num = cr.delete(bean.getUri(), null, null);
            if (num == -1) {
                failed++;
                if (listener != null) {
                    listener.onDeleteFile(bean, false);
                }
            } else {
                successed++;
                if (listener != null) {
                    listener.onDeleteFile(bean, true);
                }
            }
        }
        return successed;
    }

    /**
     * 用于拼图那边, 生成合适的图片
     *
     * @param width
     * @param height
     * @param scale
     * @return
     */
    public static float checkCanvasAndTextureSizeHalf(int width, int height, float scale) {
        float result = scale;
        Canvas canvas = new Canvas();
        int maxW = canvas.getMaximumBitmapWidth() / 8;
        int maxH = canvas.getMaximumBitmapHeight() / 8;
        int max2 = SPDataManager.getMaxTextureSize();
        int max = 0;
        if (max2 != 0) {
            max = Math.min(Math.min(maxW, maxH), max2);
        } else {
            max = Math.min(maxW, maxH);
        }
        if (width / scale > max || height / scale > max) {
            result = Math.max(width * 1.0f / max, height * 1.0f / max);
        }
        return result;
    }

    /**
     * 获取当前最大的内存值
     *
     * @return
     */
    public static float getCurrentMaxSize() {
        float maxSize;
        if (is1080PResolution()) {//高分辨率
            if (isHighMemory()) {
                maxSize = 1920 * 1080 * 4 * 2;
            } else {
                maxSize = 1920 * 1080 * 4 * 1.5f;
            }
        } else if (is720PResolution()) {//中分辨率
            if (isHighMemory()) {
                maxSize = 1280 * 720 * 4 * 3.5f;
            } else {
                maxSize = 1280 * 720 * 4 * 2.5f;
            }
        } else {//低分辨率
            if (isHighMemory()) {
                maxSize = SCREEN_WIDTH * SCREEN_HEIGHT * 4 * 4;
            } else {
                maxSize = SCREEN_WIDTH * SCREEN_HEIGHT * 4 * 2f;
            }
        }
        return maxSize;
    }

    //获取缩放图
    public static Bitmap getScaleBitmap(RectF rectf, Bitmap regineBitmap) {
        Bitmap resultBitmap = null;
        int width = regineBitmap.getWidth();
        int height = regineBitmap.getHeight();
        float realW = rectf.width();
        float realH = rectf.height();
        float scaleW = realW / width;
        float scaleH = realH / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleW, scaleH);
        resultBitmap = Bitmap.createBitmap(regineBitmap, 0, 0, width,
                height, matrix, true);
        return resultBitmap;
    }

    /**
     * 获取Collage bitmap
     *
     * @param bitmapBeans
     * @return
     */
    public static ArrayList<Bitmap> getCollageBitmap(ArrayList<BitmapBean> bitmapBeans) {

        int insamplesize = 1;
        Bitmap bitmap = null;
        ContentResolver mCr = App.getContext().getContentResolver();
        Resources res = App.getContext().getResources();

        try {
            int size = bitmapBeans.size();
            ArrayList<Bitmap> bitmaps = new ArrayList<Bitmap>(size);

            for (int j = 0; j < size; j++) {
                BitmapBean bean = bitmapBeans.get(j);
                String uriString = bean.mUri.toString();
                int sourceType = bean.getSourceType();
                if (uriString.startsWith(MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString())) {
                    InputStream in1 = null;
                    InputStream in2 = null;

                    BitmapFactory.Options option = new BitmapFactory.Options();
                    option.inJustDecodeBounds = true;
                    if (sourceType == ThumbnailBean.SYSTEM) {
                        in1 = mCr.openInputStream(bean.mUri);
                    } else {
                        in1 = new FileInputStream(bean.tempPath);
                    }
                    bitmap = BitmapFactory.decodeStream(in1, null, option);

                    /*
                     * 原来的长宽
                     */
                    int width;
                    int height;

                    if (bean.mDegree == 90 || bean.mDegree == 270) {
                        width = option.outHeight;
                        height = option.outWidth;
                    } else {
                        width = option.outWidth;
                        height = option.outHeight;
                    }
                    option.inJustDecodeBounds = false;
                    option.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    option.inPurgeable = true;
                    option.inInputShareable = true;
                    option.inDither = false;
                    float scale = getFitSampleSizeLarger(width, height * size);
                    scale = checkCanvasAndTextureSize(width, height, scale);

                    int i = 1;
                    while (scale / Math.pow(i, 2) > 1.0f) {
                        i *= 2;
                    }
                    if (i != 1) {
                        i = i / 2;
                    }
                    insamplesize = i;

                    int targetDensity = res.getDisplayMetrics().densityDpi;
                    option.inScaled = true;
                    option.inDensity = (int) (targetDensity * Math.sqrt(scale / Math.pow(i, 2)) + 1);
                    option.inTargetDensity = targetDensity;

                    option.inSampleSize = insamplesize;
                    if (sourceType == ThumbnailBean.SYSTEM) {
                        in2 = mCr.openInputStream(bean.mUri);
                    } else {
                        in2 = new FileInputStream(bean.tempPath);
                    }
                    bitmap = BitmapFactory.decodeStream(in2, null, option);

                    if (bean.mDegree % 360 != 0) {
                        bitmap = rotating(bitmap, bean.mDegree);
                    }

                    if (in1 != null) {
                        in1.close();
                    }
                    if (in2 != null) {
                        in2.close();
                    }
                } else if (uriString.startsWith(MediaStore.Video.Media.EXTERNAL_CONTENT_URI.toString())) {
                    bitmap = MediaThumbnailUtil.createPreViewVideoThumbnail(bean.mPath, FULL_SCREEN_KIND);
                } else {//加密图片解析
                    EntryptFileInputStream in1 = null;
                    EntryptFileInputStream in2 = null;
                    ParcelFileDescriptor pfd1 = null;
                    ParcelFileDescriptor pfd2 = null;
                    BitmapFactory.Options option = new BitmapFactory.Options();
                    option.inJustDecodeBounds = true;
                    pfd1 = mCr.openFileDescriptor(bean.mUri, "r");
                    in1 = EncryptUtil.decrypt(pfd1.getFileDescriptor());
                    bitmap = BitmapFactory.decodeStream(in1, null, option);

                    /*
                     * 原来的长宽
                     */
                    int width;
                    int height;
                    if (bean.mDegree == 90 || bean.mDegree == 270) {
                        width = option.outHeight;
                        height = option.outWidth;
                    } else {
                        width = option.outWidth;
                        height = option.outHeight;
                    }
                    option.inJustDecodeBounds = false;
                    option.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    option.inPurgeable = true;
                    option.inInputShareable = true;
                    option.inDither = false;

                    float scale = getFitSampleSizeLarger(width, height * size);
                    scale = checkCanvasAndTextureSize(width, height, scale);
                    int i = 1;
                    while (scale / Math.pow(i, 2) > 1.0f) {
                        i *= 2;
                    }
                    if (i != 1) {
                        i = i / 2;
                    }
                    insamplesize = i;

                    int targetDensity = res.getDisplayMetrics().densityDpi;
                    option.inScaled = true;
                    option.inDensity = (int) (targetDensity * Math.sqrt(scale / Math.pow(i, 2)) + 1);
                    option.inTargetDensity = targetDensity;

                    option.inSampleSize = insamplesize;
                    pfd2 = mCr.openFileDescriptor(bean.mUri, "r");
                    in2 = EncryptUtil.decrypt(pfd2.getFileDescriptor());
                    bitmap = BitmapFactory.decodeStream(in2, null, option);

                    if (bean.mDegree % 360 != 0) {
                        bitmap = rotating(bitmap, bean.mDegree);
                    }

                    if (in1 != null) {
                        in1.close();
                    }
                    if (in2 != null) {
                        in2.close();
                    }

                    if (pfd1 != null) {
                        pfd1.close();
                    }
                    if (pfd2 != null) {
                        pfd2.close();
                    }
                }
                bitmaps.add(bitmap);
            }
            return bitmaps;
        } catch (Throwable e) {

        }
        return null;
    }

    /**
     * 获取Collage bitmap
     *
     * @param bitmapBean
     * @param size       当前可能存在的图片数目  用于压缩的
     * @return
     */
    public static Bitmap getCollageBitmap(BitmapBean bitmapBean, int size) {

        int insamplesize = 1;
        Bitmap bitmap = null;
        ContentResolver mCr = App.getContext().getContentResolver();
        Resources res = App.getContext().getResources();
        try {
            String uriString = bitmapBean.mUri.toString();
            if (uriString.startsWith(MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString())) {
                InputStream in1 = null;
                InputStream in2 = null;

                BitmapFactory.Options option = new BitmapFactory.Options();
                option.inJustDecodeBounds = true;
                in1 = mCr.openInputStream(bitmapBean.mUri);
                bitmap = BitmapFactory.decodeStream(in1, null, option);

                /*
                 * 原来的长宽
                 */
                int width;
                int height;

                if (bitmapBean.mDegree == 90 || bitmapBean.mDegree == 270) {
                    width = option.outHeight;
                    height = option.outWidth;
                } else {
                    width = option.outWidth;
                    height = option.outHeight;
                }
                option.inJustDecodeBounds = false;
                option.inPreferredConfig = Bitmap.Config.ARGB_8888;
                option.inPurgeable = true;
                option.inInputShareable = true;
                option.inDither = false;
                float scale = getFitSampleSizeLarger(width, height * size);
                scale = checkCanvasAndTextureSize(width, height, scale);

                int i = 1;
                while (scale / Math.pow(i, 2) > 1.0f) {
                    i *= 2;
                }
                if (i != 1) {
                    i = i / 2;
                }
                insamplesize = i;

                int targetDensity = res.getDisplayMetrics().densityDpi;
                option.inScaled = true;
                option.inDensity = (int) (targetDensity * Math.sqrt(scale / Math.pow(i, 2)) + 1);
                option.inTargetDensity = targetDensity;

                option.inSampleSize = insamplesize;

                in2 = mCr.openInputStream(bitmapBean.mUri);
                bitmap = BitmapFactory.decodeStream(in2, null, option);

                if (bitmapBean.mDegree % 360 != 0) {
                    bitmap = rotating(bitmap, bitmapBean.mDegree);
                }

                if (in1 != null) {
                    in1.close();
                }
                if (in2 != null) {
                    in2.close();
                }
            } else {
                EntryptFileInputStream in1 = null;
                EntryptFileInputStream in2 = null;
                ParcelFileDescriptor pfd1 = null;
                ParcelFileDescriptor pfd2 = null;
                BitmapFactory.Options option = new BitmapFactory.Options();
                option.inJustDecodeBounds = true;
                pfd1 = mCr.openFileDescriptor(bitmapBean.mUri, "r");
                in1 = EncryptUtil.decrypt(pfd1.getFileDescriptor());
                bitmap = BitmapFactory.decodeStream(in1, null, option);

                /*
                 * 原来的长宽
                 */
                int width;
                int height;
                if (bitmapBean.mDegree == 90 || bitmapBean.mDegree == 270) {
                    width = option.outHeight;
                    height = option.outWidth;
                } else {
                    width = option.outWidth;
                    height = option.outHeight;
                }
                option.inJustDecodeBounds = false;
                option.inPreferredConfig = Bitmap.Config.ARGB_8888;
                option.inPurgeable = true;
                option.inInputShareable = true;
                option.inDither = false;

                float scale = getFitSampleSizeLarger(width, height);
                scale = checkCanvasAndTextureSize(width, height, scale);
                int i = 1;
                while (scale / Math.pow(i, 2) > 1.0f) {
                    i *= 2;
                }
                if (i != 1) {
                    i = i / 2;
                }
                insamplesize = i;

                int targetDensity = res.getDisplayMetrics().densityDpi;
                option.inScaled = true;
                option.inDensity = (int) (targetDensity * Math.sqrt(scale / Math.pow(i, 2)) + 1);
                option.inTargetDensity = targetDensity;

                option.inSampleSize = insamplesize;
                pfd2 = mCr.openFileDescriptor(bitmapBean.mUri, "r");
                in2 = EncryptUtil.decrypt(pfd2.getFileDescriptor());
                bitmap = BitmapFactory.decodeStream(in2, null, option);

                if (bitmapBean.mDegree % 360 != 0) {
                    bitmap = rotating(bitmap, bitmapBean.mDegree);
                }

                if (in1 != null) {
                    in1.close();
                }
                if (in2 != null) {
                    in2.close();
                }

                if (pfd1 != null) {
                    pfd1.close();
                }
                if (pfd2 != null) {
                    pfd2.close();
                }
            }
            return bitmap;
        } catch (Throwable e) {

        }
        return null;
    }

    //缩放drawable
    public static Drawable zoomDrawable(Drawable drawable, int w, int h) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap oldbmp = drawableToBitmap(drawable);// drawable转换成bitmap
        Matrix matrix = new Matrix();   // 创建操作图片用的Matrix对象
        float scaleWidth = ((float) w / width);   // 计算缩放比例
        float scaleHeight = ((float) h / height);
        matrix.postScale(scaleWidth, scaleHeight);         // 设置缩放比例
        Bitmap newbmp = Bitmap.createBitmap(oldbmp, 0, 0, width, height, matrix, true);       // 建立新的bitmap，其内容是对原bitmap的缩放后的图
        return new BitmapDrawable(newbmp);       // 把bitmap转换成drawable并返回
    }

    /**
     * drawable 转换成bitmap
     *
     * @param drawable
     * @param left
     * @param top
     * @param right
     * @param bottom
     * @return
     */
    public static Bitmap drawableToBitmap(Drawable drawable, int left, int top, int right, int bottom, float scaleWidth, float scaleHeight) {
        int width = drawable.getIntrinsicWidth();   // 取drawable的长宽
        int height = drawable.getIntrinsicHeight();
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;         // 取drawable的颜色格式
        Bitmap oldbmp = Bitmap.createBitmap(width, height, config);     // 建立对应bitmap
        Canvas canvas = new Canvas(oldbmp);         // 建立对应bitmap的画布
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);      // 把drawable内容画到画布中
        Matrix matrix = new Matrix();   // 创建操作图片用的Matrix对象
        matrix.postScale(scaleWidth, scaleHeight);         // 设置缩放比例
        Bitmap newbmp = Bitmap.createBitmap(oldbmp, left, top, right - left, bottom - top, matrix, true);
        return newbmp;
    }

    /**
     * 获取某个文件夹下的所有图片的URI
     *
     * @param context
     * @param path
     * @return
     */
    public static ArrayList<BitmapBean> getImageURIFromPath_SDK16(Context context, String path) {
        ArrayList<BitmapBean> result1 = null;
        ArrayList<BitmapBean> result2 = null;
        Cursor c1 = null;
        Cursor c2 = null;
        //Images.ImageColumns.DATA 是图片的绝对路径
        try {
            ContentResolver cr = context.getContentResolver();
            if (TextUtils.isEmpty(path)) {
                c1 = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Images.ImageColumns._ID, MediaStore.Images.ImageColumns.ORIENTATION, MediaStore.Images.ImageColumns.DATE_TAKEN,
                                MediaStore.Images.ImageColumns.WIDTH, MediaStore.Images.ImageColumns.HEIGHT, MediaStore.Images.ImageColumns.DATA},
                        null, null, MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC" + ", " + MediaStore.Images.ImageColumns._ID + " ASC");
            } else {
                c1 = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Images.ImageColumns._ID, MediaStore.Images.ImageColumns.ORIENTATION, MediaStore.Images.ImageColumns.DATE_TAKEN,
                                MediaStore.Images.ImageColumns.WIDTH, MediaStore.Images.ImageColumns.HEIGHT, MediaStore.Images.ImageColumns.DATA},
                        MediaStore.Images.ImageColumns.DATA + " like ? ", new String[]{path + File.separator + "%"}, MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC" + ", " + MediaStore.Images.ImageColumns._ID + " ASC");
            }
            if (c1.moveToFirst()) {
                result1 = new ArrayList<BitmapBean>();
                int idIndex = c1.getColumnIndex(MediaStore.Images.ImageColumns._ID);
                int orientationIndex = c1.getColumnIndex(MediaStore.Images.ImageColumns.ORIENTATION);
                int dateIndex = c1.getColumnIndex(MediaStore.Images.ImageColumns.DATE_TAKEN);
                int widthIndex = c1.getColumnIndex(MediaStore.Images.ImageColumns.WIDTH);
                int heightIndex = c1.getColumnIndex(MediaStore.Images.ImageColumns.HEIGHT);
                int pathIndex = c1.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                do {
                    BitmapBean bb = new BitmapBean();
                    bb.mDegree = c1.getInt(orientationIndex) % 360;
                    bb.mId = c1.getInt(idIndex);
                    bb.mUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, bb.mId);
                    bb.mDate = c1.getLong(dateIndex);
                    bb.mWidth = c1.getInt(widthIndex);
                    bb.mHeight = c1.getInt(heightIndex);
                    bb.mPath = c1.getString(pathIndex);
                    if (MediaFile.isGifFileType(bb.mPath)) {
                        bb.mType = com.videoeditor.function.media.MediaTypeUtil.TYPE_GIF;
                    } else if (MediaFile.isJPGFileType(bb.mPath)) {
                        bb.mType = com.videoeditor.function.media.MediaTypeUtil.TYPE_JPG;
                    } else if (MediaFile.isPNGFileType(bb.mPath)) {
                        bb.mType = com.videoeditor.function.media.MediaTypeUtil.TYPE_PNG;
                    } else {
                        bb.mType = com.videoeditor.function.media.MediaTypeUtil.TYPE_OTHER_IMAGE;
                    }
                    result1.add(bb);
                } while (c1.moveToNext());
            }
            if (TextUtils.isEmpty(path)) {
                c2 = cr.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Video.VideoColumns._ID, MediaStore.Video.VideoColumns.DATE_TAKEN,
                                MediaStore.Video.VideoColumns.WIDTH, MediaStore.Video.VideoColumns.HEIGHT, MediaStore.Video.VideoColumns.DATA, MediaStore.Video.VideoColumns.DURATION},
                        null, null, MediaStore.Video.VideoColumns.DATE_TAKEN + " DESC" + ", " + MediaStore.Video.VideoColumns._ID + " ASC");
            } else {
                c2 = cr.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Video.VideoColumns._ID, MediaStore.Video.VideoColumns.DATE_TAKEN,
                                MediaStore.Video.VideoColumns.WIDTH, MediaStore.Video.VideoColumns.HEIGHT, MediaStore.Video.VideoColumns.DATA, MediaStore.Video.VideoColumns.DURATION},
                        MediaStore.Video.VideoColumns.DATA + " like ? ", new String[]{path + File.separator + "%"}, MediaStore.Video.VideoColumns.DATE_TAKEN + " DESC" + ", " + MediaStore.Video.VideoColumns._ID + " ASC");
            }
            if (c2.moveToFirst()) {
                result2 = new ArrayList<BitmapBean>();
                int idIndex = c2.getColumnIndex(MediaStore.Video.VideoColumns._ID);
                int dateIndex = c2.getColumnIndex(MediaStore.Video.VideoColumns.DATE_TAKEN);
                int widthIndex = c2.getColumnIndex(MediaStore.Video.VideoColumns.WIDTH);
                int heightIndex = c2.getColumnIndex(MediaStore.Video.VideoColumns.HEIGHT);
                int pathIndex = c2.getColumnIndex(MediaStore.Video.VideoColumns.DATA);
                int durationIndex = c2.getColumnIndex(MediaStore.Video.VideoColumns.DURATION);
                do {
                    BitmapBean bb = new BitmapBean();
                    bb.mId = c2.getInt(idIndex);
                    bb.mUri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, bb.mId);
                    bb.mDate = c2.getLong(dateIndex);
                    bb.mType = com.videoeditor.function.media.MediaTypeUtil.TYPE_VIDEO;
                    bb.mWidth = c2.getInt(widthIndex);
                    bb.mHeight = c2.getInt(heightIndex);
                    bb.mPath = c2.getString(pathIndex);
                    bb.mDuration = c2.getLong(durationIndex);
                    result2.add(bb);
                } while (c2.moveToNext());
            }
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            if (c1 != null) {
                c1.close();
            }
            if (c2 != null) {
                c2.close();
            }
        }
        return doAlgorithm(result1, result2);
    }

    /**
     * 获取某个bucketId下的所有图片的URI
     *
     * @param context
     * @param bucket_id
     * @return
     */
    public static ArrayList<BitmapBean> getImageURIFromBucketID_SDK16(Context context, String bucket_id) {
        ArrayList<BitmapBean> result1 = null;
        ArrayList<BitmapBean> result2 = null;
        Cursor c1 = null;
        Cursor c2 = null;
        //Images.ImageColumns.DATA 是图片的绝对路径
        try {
            ContentResolver cr = context.getContentResolver();

            c1 = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Images.ImageColumns._ID, MediaStore.Images.ImageColumns.ORIENTATION, MediaStore.Images.ImageColumns.DATE_TAKEN,
                            MediaStore.Images.ImageColumns.WIDTH, MediaStore.Images.ImageColumns.HEIGHT, MediaStore.Images.ImageColumns.DATA},
                    MediaStore.Images.ImageColumns.BUCKET_ID + " = ? ", new String[]{bucket_id}, MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC" + ", " + MediaStore.Images.ImageColumns._ID + " ASC");
            if (c1.moveToFirst()) {
                result1 = new ArrayList<BitmapBean>();
                int idIndex = c1.getColumnIndex(MediaStore.Images.ImageColumns._ID);
                int orientationIndex = c1.getColumnIndex(MediaStore.Images.ImageColumns.ORIENTATION);
                int dateIndex = c1.getColumnIndex(MediaStore.Images.ImageColumns.DATE_TAKEN);
                int widthIndex = c1.getColumnIndex(MediaStore.Images.ImageColumns.WIDTH);
                int heightIndex = c1.getColumnIndex(MediaStore.Images.ImageColumns.HEIGHT);
                int pathIndex = c1.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                do {
                    BitmapBean bb = new BitmapBean();
                    bb.mDegree = c1.getInt(orientationIndex) % 360;
                    bb.mId = c1.getInt(idIndex);
                    bb.mUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, bb.mId);
                    bb.mDate = c1.getLong(dateIndex);
                    bb.mWidth = c1.getInt(widthIndex);
                    bb.mHeight = c1.getInt(heightIndex);
                    bb.mPath = c1.getString(pathIndex);
                    if (MediaFile.isGifFileType(bb.mPath)) {
                        bb.mType = com.videoeditor.function.media.MediaTypeUtil.TYPE_GIF;
                    } else if (MediaFile.isJPGFileType(bb.mPath)) {
                        bb.mType = com.videoeditor.function.media.MediaTypeUtil.TYPE_JPG;
                    } else if (MediaFile.isPNGFileType(bb.mPath)) {
                        bb.mType = com.videoeditor.function.media.MediaTypeUtil.TYPE_PNG;
                    } else {
                        bb.mType = com.videoeditor.function.media.MediaTypeUtil.TYPE_OTHER_IMAGE;
                    }
                    result1.add(bb);
                } while (c1.moveToNext());
            }
            c2 = cr.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Video.VideoColumns._ID, MediaStore.Video.VideoColumns.DATE_TAKEN,
                            MediaStore.Video.VideoColumns.WIDTH, MediaStore.Video.VideoColumns.HEIGHT, MediaStore.Video.VideoColumns.DATA, MediaStore.Video.VideoColumns.DURATION},
                    MediaStore.Video.VideoColumns.BUCKET_ID + " = ? ", new String[]{bucket_id}, MediaStore.Video.VideoColumns.DATE_TAKEN + " DESC" + ", " + MediaStore.Video.VideoColumns._ID + " ASC");
            if (c2.moveToFirst()) {
                result2 = new ArrayList<BitmapBean>();
                int idIndex = c2.getColumnIndex(MediaStore.Video.VideoColumns._ID);
                int dateIndex = c2.getColumnIndex(MediaStore.Video.VideoColumns.DATE_TAKEN);
                int widthIndex = c2.getColumnIndex(MediaStore.Video.VideoColumns.WIDTH);
                int heightIndex = c2.getColumnIndex(MediaStore.Video.VideoColumns.HEIGHT);
                int pathIndex = c2.getColumnIndex(MediaStore.Video.VideoColumns.DATA);
                int durationIndex = c2.getColumnIndex(MediaStore.Video.VideoColumns.DURATION);
                do {
                    BitmapBean bb = new BitmapBean();
                    bb.mId = c2.getInt(idIndex);
                    bb.mUri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, bb.mId);
                    bb.mDate = c2.getLong(dateIndex);
                    bb.mType = com.videoeditor.function.media.MediaTypeUtil.TYPE_VIDEO;
                    bb.mWidth = c2.getInt(widthIndex);
                    bb.mHeight = c2.getInt(heightIndex);
                    bb.mPath = c2.getString(pathIndex);
                    bb.mDuration = c2.getLong(durationIndex);
                    result2.add(bb);
                } while (c2.moveToNext());
            }
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            if (c1 != null) {
                c1.close();
            }
            if (c2 != null) {
                c2.close();
            }
        }
        return doAlgorithm(result1, result2);
    }

    /**
     * 获取某个文件夹下的所有图片的URI
     *
     * @param context
     * @param path
     * @return
     */
    public static ArrayList<BitmapBean> getImageURIFromPath_SDK15(Context context, String path) {
        ArrayList<BitmapBean> result1 = null;
        ArrayList<BitmapBean> result2 = null;
        Cursor c1 = null;
        Cursor c2 = null;
        //Images.ImageColumns.DATA 是图片的绝对路径
        try {
            ContentResolver cr = context.getContentResolver();
            if (TextUtils.isEmpty(path)) {
                c1 = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Images.ImageColumns._ID, MediaStore.Images.ImageColumns.ORIENTATION, MediaStore.Images.ImageColumns.DATE_TAKEN, MediaStore.Images.ImageColumns.DATA},
                        null, null, MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC" + ", " + MediaStore.Images.ImageColumns._ID + " ASC");
            } else {
                c1 = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Images.ImageColumns._ID, MediaStore.Images.ImageColumns.ORIENTATION, MediaStore.Images.ImageColumns.DATE_TAKEN, MediaStore.Images.ImageColumns.DATA},
                        MediaStore.Images.ImageColumns.DATA + " like ? ", new String[]{path + File.separator + "%"}, MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC" + ", " + MediaStore.Images.ImageColumns._ID + " ASC");
            }
            if (c1.moveToFirst()) {
                result1 = new ArrayList<BitmapBean>();
                int idIndex = c1.getColumnIndex(MediaStore.Images.ImageColumns._ID);
                int orientationIndex = c1.getColumnIndex(MediaStore.Images.ImageColumns.ORIENTATION);
                int dateIndex = c1.getColumnIndex(MediaStore.Images.ImageColumns.DATE_TAKEN);
                int pathIndex = c1.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                do {
                    BitmapBean bb = new BitmapBean();
                    bb.mDegree = c1.getInt(orientationIndex) % 360;
                    bb.mId = c1.getInt(idIndex);
                    bb.mUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, bb.mId);
                    bb.mDate = c1.getLong(dateIndex);
                    bb.mPath = c1.getString(pathIndex);
                    if (MediaFile.isGifFileType(bb.mPath)) {
                        bb.mType = com.videoeditor.function.media.MediaTypeUtil.TYPE_GIF;
                    } else if (MediaFile.isJPGFileType(bb.mPath)) {
                        bb.mType = com.videoeditor.function.media.MediaTypeUtil.TYPE_JPG;
                    } else if (MediaFile.isPNGFileType(bb.mPath)) {
                        bb.mType = com.videoeditor.function.media.MediaTypeUtil.TYPE_PNG;
                    } else {
                        bb.mType = com.videoeditor.function.media.MediaTypeUtil.TYPE_OTHER_IMAGE;
                    }
                    result1.add(bb);
                } while (c1.moveToNext());
            }
            if (TextUtils.isEmpty(path)) {
                c2 = cr.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Video.VideoColumns._ID, MediaStore.Video.VideoColumns.DATE_TAKEN, MediaStore.Video.VideoColumns.DATA, MediaStore.Video.VideoColumns.DURATION},
                        null, null, MediaStore.Video.VideoColumns.DATE_TAKEN + " DESC" + ", " + MediaStore.Video.VideoColumns._ID + " ASC");
            } else {
                c2 = cr.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Video.VideoColumns._ID, MediaStore.Video.VideoColumns.DATE_TAKEN, MediaStore.Video.VideoColumns.DATA, MediaStore.Video.VideoColumns.DURATION},
                        MediaStore.Video.VideoColumns.DATA + " like ? ", new String[]{path + File.separator + "%"}, MediaStore.Video.VideoColumns.DATE_TAKEN + " DESC" + ", " + MediaStore.Video.VideoColumns._ID + " ASC");
            }
            if (c2.moveToFirst()) {
                result2 = new ArrayList<BitmapBean>();
                int idIndex = c2.getColumnIndex(MediaStore.Video.VideoColumns._ID);
                int dateIndex = c2.getColumnIndex(MediaStore.Video.VideoColumns.DATE_TAKEN);
                int pathIndex = c2.getColumnIndex(MediaStore.Video.VideoColumns.DATA);
                int durationIndex = c2.getColumnIndex(MediaStore.Video.VideoColumns.DURATION);
                do {
                    BitmapBean bb = new BitmapBean();
                    bb.mId = c2.getInt(idIndex);
                    bb.mUri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, bb.mId);
                    bb.mDate = c2.getLong(dateIndex);
                    bb.mType = com.videoeditor.function.media.MediaTypeUtil.TYPE_VIDEO;
                    bb.mPath = c2.getString(pathIndex);
                    bb.mDuration = c2.getLong(durationIndex);
                    result2.add(bb);
                } while (c2.moveToNext());
            }
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            if (c1 != null) {
                c1.close();
            }
            if (c2 != null) {
                c2.close();
            }
        }
        return doAlgorithm(result1, result2);
    }

    /**
     * 获取某个BucketId下的所有图片的URI
     *
     * @param context
     * @param bucketId
     * @return
     */
    public static ArrayList<BitmapBean> getImageURIFromBucketID_SDK15(Context context, String bucketId) {
        ArrayList<BitmapBean> result1 = null;
        ArrayList<BitmapBean> result2 = null;
        Cursor c1 = null;
        Cursor c2 = null;
        //Images.ImageColumns.DATA 是图片的绝对路径
        try {
            ContentResolver cr = context.getContentResolver();

            c1 = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Images.ImageColumns._ID, MediaStore.Images.ImageColumns.ORIENTATION, MediaStore.Images.ImageColumns.DATE_TAKEN, MediaStore.Images.ImageColumns.DATA},
                    MediaStore.Images.ImageColumns.BUCKET_ID + " = ? ", new String[]{bucketId}, MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC" + ", " + MediaStore.Images.ImageColumns._ID + " ASC");
            if (c1.moveToFirst()) {
                result1 = new ArrayList<BitmapBean>();
                int idIndex = c1.getColumnIndex(MediaStore.Images.ImageColumns._ID);
                int orientationIndex = c1.getColumnIndex(MediaStore.Images.ImageColumns.ORIENTATION);
                int dateIndex = c1.getColumnIndex(MediaStore.Images.ImageColumns.DATE_TAKEN);
                int pathIndex = c1.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                do {
                    BitmapBean bb = new BitmapBean();
                    bb.mDegree = c1.getInt(orientationIndex) % 360;
                    bb.mId = c1.getInt(idIndex);
                    bb.mUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, bb.mId);
                    bb.mDate = c1.getLong(dateIndex);
                    bb.mPath = c1.getString(pathIndex);
                    if (MediaFile.isGifFileType(bb.mPath)) {
                        bb.mType = com.videoeditor.function.media.MediaTypeUtil.TYPE_GIF;
                    } else if (MediaFile.isJPGFileType(bb.mPath)) {
                        bb.mType = com.videoeditor.function.media.MediaTypeUtil.TYPE_JPG;
                    } else if (MediaFile.isPNGFileType(bb.mPath)) {
                        bb.mType = com.videoeditor.function.media.MediaTypeUtil.TYPE_PNG;
                    } else {
                        bb.mType = com.videoeditor.function.media.MediaTypeUtil.TYPE_OTHER_IMAGE;
                    }
                    result1.add(bb);
                } while (c1.moveToNext());
            }
            c2 = cr.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Video.VideoColumns._ID, MediaStore.Video.VideoColumns.DATE_TAKEN, MediaStore.Video.VideoColumns.DATA, MediaStore.Video.VideoColumns.DURATION},
                    MediaStore.Video.VideoColumns.BUCKET_ID + " = ? ", new String[]{bucketId}, MediaStore.Video.VideoColumns.DATE_TAKEN + " DESC" + ", " + MediaStore.Video.VideoColumns._ID + " ASC");
            if (c2.moveToFirst()) {
                result2 = new ArrayList<BitmapBean>();
                int idIndex = c2.getColumnIndex(MediaStore.Video.VideoColumns._ID);
                int dateIndex = c2.getColumnIndex(MediaStore.Video.VideoColumns.DATE_TAKEN);
                int pathIndex = c2.getColumnIndex(MediaStore.Video.VideoColumns.DATA);
                int durationIndex = c2.getColumnIndex(MediaStore.Video.VideoColumns.DURATION);
                do {
                    BitmapBean bb = new BitmapBean();
                    bb.mId = c2.getInt(idIndex);
                    bb.mUri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, bb.mId);
                    bb.mDate = c2.getLong(dateIndex);
                    bb.mType = com.videoeditor.function.media.MediaTypeUtil.TYPE_VIDEO;
                    bb.mPath = c2.getString(pathIndex);
                    bb.mDuration = c2.getLong(durationIndex);
                    result2.add(bb);
                } while (c2.moveToNext());
            }
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            if (c1 != null) {
                c1.close();
            }
            if (c2 != null) {
                c2.close();
            }
        }
        return doAlgorithm(result1, result2);
    }

    /**
     * 排序算法
     *
     * @param rs1
     * @param rs2
     * @return
     */
    private static ArrayList<BitmapBean> doAlgorithm(ArrayList<BitmapBean> rs1, ArrayList<BitmapBean> rs2) {
        int i = 0, j = 0, k = 0;
        int length1 = rs1 == null ? 0 : rs1.size();
        int length2 = rs2 == null ? 0 : rs2.size();
        int length = length1 + length2;
        ArrayList<BitmapBean> result = new ArrayList<BitmapBean>(length);
        if (length1 == 0) {
            return rs2;
        } else if (length2 == 0) {
            return rs1;
        }
        while (k < length) {
            if (i == length1) {//rs1已经弄完
                result.add(rs2.get(j));
                j++;
            } else if (j == length2) {//rs2已经弄完
                result.add(rs1.get(i));
                i++;
            } else {
                if (rs1.get(i).mDate > rs2.get(j).mDate) {//rs1的放入
                    result.add(rs1.get(i));
                    i++;
                } else {//rs2的放入
                    result.add(rs2.get(j));
                    j++;
                }
            }
            k++;
        }
        return result;
    }

    /**
     * 根据Uri删除图片或者视频
     *
     * @param context
     * @param uri
     * @param path
     * @return
     */
    public static boolean deleteMediaByUri(Context context, Uri uri, String path) {
        if (uri != null) {
            if (UriUtil.INSTANCE.isFileUri(uri)) {
                path = uri.getPath();
                File f = new File(path);
                if (MediaFile.getFileType(path) != null && f.delete()) {
                    ContentResolver cr = context.getContentResolver();
                    String where;
                    if (MediaFile.isImageFileType(MediaFile.getFileType(path).fileType)) {//Image
                        where = MediaStore.Images.ImageColumns.DATA + " = ? ";
                        cr.delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, where, new String[]{path});
                    } else {
                        where = MediaStore.Video.VideoColumns.DATA + " = ? ";
                        cr.delete(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, where, new String[]{path});
                    }
                    return true;
                } else {
                    return false;
                }
            } else {
                if (TextUtils.isEmpty(path)) {//路径为空时
                    BitmapBean bean = getBitmapBeanFormUri(context, uri);
                    path = bean.mPath;
                }

                if (ExtSdcardUtils.isExtSdcardPath(path)) {//如果是外置路径 且支持外置SDCARD
                    if (PhoneInfo.isSupportWriteExtSdCard()) {//这种情况删除时需要特殊处理  要先删除文件
                        boolean success = ExtSdcardUtils.deleteExtFile(context, path);
                        if (success) {//如果删除文件成功 再去删除数据库文件
                            ContentResolver cr = context.getContentResolver();
                            int i = cr.delete(uri, null, null);
                            if (i == -1) {
                                return false;
                            } else {
                                return true;
                            }
                        } else {//删除文件失败  不进行处理  直接返回失败
                            return false;
                        }
                    } else {
                        File f = new File(path);
                        if (f.delete()) {
                            ContentResolver cr = context.getContentResolver();
                            int i = cr.delete(uri, null, null);
                            if (i == -1) {
                                return false;
                            } else {
                                return true;
                            }
                        } else {
                            return false;
                        }
                    }
                } else {
                    ContentResolver cr = context.getContentResolver();
                    int i = cr.delete(uri, null, null);
                    if (i == -1) {
                        return false;
                    } else {
                        return true;
                    }
                }
            }
        } else {
            return false;
        }
    }

    /**
     * 获取普通图片的信息
     *
     * @param context
     * @param bean
     * @return
     */
    public static ContentValues getImageInfo(Context context, BitmapBean bean) {
        ContentValues cv = new ContentValues();
        Cursor c = null;
        String longitude = null;
        String latitude = null;
        String type = null;
        try {
            if (UriUtil.INSTANCE.isFileUri(bean.mUri)) {//对Action view的特殊Uri做处理
                String name = bean.mPath.substring(bean.mPath.lastIndexOf(File.separator) + 1, bean.mPath.length());
                File myFile = new File(bean.mPath);
                cv.put("name", name);
                cv.put("album", myFile.getParentFile().getName());
                cv.put("type", MediaFile.getFileType(bean.mPath).mimeType);

                ExifInterface exif = new ExifInterface(bean.mPath);

                long time = MediaThumbnailUtil.readPictureDateTime(exif, false);
                if (time != 0) {
                    cv.put("date", SimpleDateFormat.getDateTimeInstance().format(time));
                }

                int width = exif.getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH, 0);
                int height = exif.getAttributeInt(ExifInterface.TAG_IMAGE_LENGTH, 0);
                if (width != -1 && width != 0 && height != -1 && height != 0) {
                    if (bean.mDegree == 90 || bean.mDegree == 270) {
                        cv.put("resolution", height + "×" + width);
                    } else {
                        cv.put("resolution", width + "×" + height);
                    }
                }

                cv.put("size", FileUtil.formetFileSize(myFile.length()));
                cv.put("path", bean.mPath);

                float output[] = new float[2];
                boolean isExit = exif.getLatLong(output);
                if (isExit) {
                    latitude = output[0] + "";
                    longitude = output[1] + "";
                }
                if (!TextUtils.isEmpty(longitude) && !TextUtils.isEmpty(latitude)) {
                    cv.put("location", latitude + ", " + longitude);
                }

            } else {
                if (com.videoeditor.function.media.MediaTypeUtil.isVideo(bean.mType)) {
                    c = context.getContentResolver().query(bean.mUri, new String[]{MediaStore.Video.VideoColumns.LONGITUDE, MediaStore.Video.VideoColumns.LATITUDE, MediaStore.Video.VideoColumns.MIME_TYPE}, null, null, null);
                    if (c.moveToNext()) {
                        longitude = c.getString(c.getColumnIndex(MediaStore.Video.VideoColumns.LONGITUDE));
                        latitude = c.getString(c.getColumnIndex(MediaStore.Video.VideoColumns.LATITUDE));
                        type = c.getString(c.getColumnIndex(MediaStore.Video.VideoColumns.MIME_TYPE));
                    }
                } else {
                    c = context.getContentResolver().query(bean.mUri, new String[]{MediaStore.Images.ImageColumns.LONGITUDE, MediaStore.Images.ImageColumns.LATITUDE, MediaStore.Images.ImageColumns.MIME_TYPE}, null, null, null);
                    if (c.moveToNext()) {
                        longitude = c.getString(c.getColumnIndex(MediaStore.Images.ImageColumns.LONGITUDE));
                        latitude = c.getString(c.getColumnIndex(MediaStore.Images.ImageColumns.LATITUDE));
                        type = c.getString(c.getColumnIndex(MediaStore.Images.ImageColumns.MIME_TYPE));
                    }
                }
                String name = bean.mPath.substring(bean.mPath.lastIndexOf(File.separator) + 1, bean.mPath.length());
                File myFile = new File(bean.mPath);
                cv.put("name", name);
                cv.put("album", myFile.getParentFile().getName());
                if (!TextUtils.isEmpty(type)) {
                    cv.put("type", type);
                }

                cv.put("date", SimpleDateFormat.getDateTimeInstance().format(bean.mDate));
                if (bean.mWidth != -1 && bean.mWidth != 0 && bean.mHeight != -1 && bean.mHeight != 0) {
                    if (bean.mDegree == 90 || bean.mDegree == 270) {
                        cv.put("resolution", bean.mHeight + "×" + bean.mWidth);
                    } else {
                        cv.put("resolution", bean.mWidth + "×" + bean.mHeight);
                    }
                }
                cv.put("size", FileUtil.formetFileSize(myFile.length()));
                cv.put("path", bean.mPath);
                if (!TextUtils.isEmpty(longitude) && !TextUtils.isEmpty(latitude)) {
                    cv.put("location", latitude + ", " + longitude);
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return cv;
    }

    // drawable 转换成bitmap
    public static Bitmap drawableToBitmap(Drawable drawable) {
        int width = drawable.getIntrinsicWidth();   // 取drawable的长宽
        int height = drawable.getIntrinsicHeight();
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;         // 取drawable的颜色格式
        Bitmap bitmap = Bitmap.createBitmap(width, height, config);     // 建立对应bitmap
        Canvas canvas = new Canvas(bitmap);         // 建立对应bitmap的画布
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);      // 把drawable内容画到画布中
        return bitmap;
    }

    /***
     *
     * @param path
     * @return error -> true
     */
    private static boolean checkImageError(String path) {
        if (!FileUtils.isExistFile(path)) {
            return true;
        }

        return !MediaFile.isImageFile(path);
    }

    private static boolean checkVideoError(String path) {
        if (!FileUtils.isExistFile(path)) {
            return true;
        }

        return !MediaFile.isVideoFile(path);
    }

}
