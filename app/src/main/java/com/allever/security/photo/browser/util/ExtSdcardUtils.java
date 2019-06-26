package com.allever.security.photo.browser.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.*;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.support.v4.provider.DocumentFile;
import android.support.v7.app.AlertDialog;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import com.allever.security.photo.browser.R;
import com.allever.security.photo.browser.bean.ThumbnailBean;
import com.allever.security.photo.browser.media.IDeleteListener;
import com.android.absbase.App;
import com.android.absbase.utils.SpUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;


@TargetApi(21)
public class ExtSdcardUtils {

    //存储的treeUri
    public final static String PREF_SAVED_EXT_SDCARD_URI = "SP_SAVED_URI";

    /**
     * 保存JPG或者视频的CACHE路径
     */
    public final static String SAVE_CACHE_PATH = FileUtil.DICM_ROOT_PATH + File.separator + "Camera";

    /**
     * 是否有外置SD卡的权限
     *
     * @return
     */
    public static boolean hasExtSdcardPermission() {
        boolean result = false;
        Uri uri = getSavedExtSdcardUri();
        if (uri != null) {
            List<UriPermission> uriPermissions = App.getContext().getContentResolver().getPersistedUriPermissions();
            if (uriPermissions != null && uriPermissions.size() > 0) {
                int size = uriPermissions.size();
                for (int i = 0; i < size; i++) {
                    UriPermission permission = uriPermissions.get(i);
                    if (uri.equals(permission.getUri()) && permission.isReadPermission() && permission.isWritePermission()) {
                        result = true;
                        break;
                    }
                }
            }
        }
        return result;
    }

    /**
     * 调用这个请求权限
     *
     * @param activity
     * @param requestCode
     */
    public static void requestExtSdcardPermission(final Activity activity, final int requestCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                    activity.startActivityForResult(intent, requestCode);
                } catch (Throwable e) {
                }
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setTitle(R.string.request_ext_sdcard_permission_tip);
        Resources res = App.getContext().getResources();
        String msg1 = res.getString(R.string.request_ext_sdcard_permission_msg1);
        String msg2 = res.getString(R.string.request_ext_sdcard_permission_msg2);
        String msg3 = res.getString(R.string.request_ext_sdcard_permission_msg3);
        SpannableString spannableString = new SpannableString(msg1 + msg2 + msg3);
        spannableString.setSpan(new ForegroundColorSpan(Color.RED), msg1.length(), msg1.length() + msg2.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setMessage(spannableString);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * 这个方法需要在onActivityresult中执行
     *
     * @param activity
     * @param data
     * @return 返回值是是否是外置SD卡根目录
     */
    public static boolean dealOnActivityResult(Activity activity, Intent data) {
        try {
            Uri u = data.getData();

            DocumentFile documentFile = DocumentFile.fromTreeUri(activity, u);//根目录文件的
            if (isExtSdcardRootUri(documentFile)) {
                saveExtSdcardUri(u);
                final int takeFlags = data.getFlags()
                        & (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                activity.getContentResolver().takePersistableUriPermission(u, takeFlags);//持久化权限
                return true;
            }
        } catch (Throwable e) {
        }
        return false;
    }

    /**
     * 获取tree URI
     *
     * @return
     */
    public static Uri getSavedExtSdcardUri() {
        String uri = SpUtils.obtainDefault().getString(PREF_SAVED_EXT_SDCARD_URI, null);
        if (uri != null) {
            try {
                return Uri.parse(uri);
            } catch (Throwable ignored) {

            }
        }
        return null;
    }

    public static void saveExtSdcardUri(Uri uri) {
        SpUtils.obtainDefault().save(PREF_SAVED_EXT_SDCARD_URI, uri == null ? "" : uri.toString());
    }

    /**
     * 这个是用于判断是不是外置SD卡根目录
     *
     * @param documentFile
     * @return
     */
    public static boolean isExtSdcardRootUri(DocumentFile documentFile) {
        String documentId = DocumentsContract.getDocumentId(documentFile.getUri());
        if (!TextUtils.isEmpty(documentId)) {
            String[] part = documentId.split(":");
            if (part != null && part.length == 1 && !part[0].equals("primary")) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取根目录的Id
     *
     * @param context
     * @param treeUri
     * @return
     */
    public static String getRootDocumentId(Context context, Uri treeUri) {
        if (treeUri != null) {
            DocumentFile documentFile = DocumentFile.fromTreeUri(context, treeUri);
            return DocumentsContract.getDocumentId(documentFile.getUri());
        }
        return null;
    }

    /**
     * 这个方法如果文件不存在会创建新的文件
     *
     * @param context
     * @param pathName
     * @param treeUri
     * @param mimeType
     * @return 返回的都是SingleDocumentFile不能进行文件创建
     */
    public static DocumentFile getDocumentFile(Context context, String pathName, Uri treeUri, String mimeType) {
        DocumentFile documentFile = getDocumentFile(context, pathName, treeUri);
        if (documentFile == null) {
            return null;
        } else if (documentFile.exists()) {
            return documentFile;
        } else {
            createDocumentFile(context, documentFile, treeUri, pathName, mimeType);
            return documentFile;
        }
    }

    /**
     * 获取一个documentFile的父目录的DocumentFile
     *
     * @param documentFile
     * @return SingleDocumentFile
     */
    private static DocumentFile getParentDocument(Context context, DocumentFile documentFile, Uri treeUri) {
        if (documentFile == null) {
            return null;
        }
        DocumentFile parentDocumentFile = documentFile.getParentFile();
        if (parentDocumentFile == null) {
            String documentId = DocumentsContract.getDocumentId(documentFile.getUri());
            String parentDocumentId;
            int index = documentId.lastIndexOf(File.separator);
            if (index == -1) {//没有/则代表是root目录了
                parentDocumentFile = DocumentFile.fromTreeUri(context, treeUri);
            } else {
                parentDocumentId = documentId.substring(0, index);
                parentDocumentFile = DocumentFile.fromSingleUri(context, DocumentsContract.buildDocumentUriUsingTree(treeUri, parentDocumentId));
//                parentDocumentFile = DocumentFile.fromTreeUri(context, DocumentsContract.buildTreeDocumentUri(treeUri.getAuthority(), parentDocumentId));
            }
        }
        return parentDocumentFile;
    }

    /**
     * 创建
     *
     * @param context
     * @param documentFile
     * @param treeUri
     * @param pathName
     * @param mimeType
     * @return
     */
    private static Uri createDocumentFile(Context context, DocumentFile documentFile, Uri treeUri, String pathName, String mimeType) {
        DocumentFile parentDocument = getParentDocument(context, documentFile, treeUri);
        String displayName = FileUtil.getFileName(pathName);
        if (parentDocument == null) return null;
        if (parentDocument.exists()) {
            try {
                return DocumentsContract.createDocument(context.getContentResolver(), parentDocument.getUri(), mimeType, displayName);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            mkdirExtSdcardPath(context, FileUtil.getParentFilePath(pathName), treeUri);//创建目录然后创建文件
            try {
                return DocumentsContract.createDocument(context.getContentResolver(), parentDocument.getUri(), mimeType, displayName);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    /**
     * 这个方法如果文件不存在不会创建新的文件
     *
     * @param context
     * @param pathName
     * @param treeUri
     * @Return SingleDocumentFile
     */
    public static DocumentFile getDocumentFile(Context context, String pathName, Uri treeUri) {
        if (treeUri != null) {
            List<String> sdcardPath = FolderUtil.getAllSDPath(context);
            if (sdcardPath != null && sdcardPath.size() >= 2) {
                String extSdcardPath = sdcardPath.get(1);
                String truePath = pathName.substring(extSdcardPath.length() + 1, pathName.length());//除去root之外的
                String rootDocumentId = getRootDocumentId(context, treeUri);
                String documentId = rootDocumentId + truePath;
                //这里存在两种方式 一种会生成singleDocumentFile 另一种生成TreeDocumentFile
//            DocumentFile documentFile = DocumentFile.fromTreeUri(context, DocumentsContract.buildTreeDocumentUri(treeUri.getAuthority(), documentId));
                DocumentFile documentFile = DocumentFile.fromSingleUri(context, DocumentsContract.buildDocumentUriUsingTree(treeUri, documentId));
                return documentFile;
            }
        }
        return null;
    }


    /**
     * 会创建新的文件夹
     *
     * @param context
     * @param path    必须是一个目录  不能是文件
     * @param treeUri
     */
    public static DocumentFile getDocumentDirectoryFile(Context context, String path, Uri treeUri) {
        DocumentFile documentFile = getDocumentFile(context, path, treeUri);
        if (documentFile == null) {
            return null;
        } else if (documentFile.exists()) {
            return documentFile;
        } else {
            mkdirExtSdcardPath(context, path, treeUri);
            return documentFile;
        }
    }


    /**
     * @param context
     * @param path    必须是一个目录  不能是文件
     * @param treeUri
     */
    public static boolean mkdirExtSdcardPath(Context context, String path, Uri treeUri) {
        DocumentFile documentFile = getDocumentFile(context, path, treeUri);
        DocumentFile parentDocument = getParentDocument(context, documentFile, treeUri);//拿到父文件夹
        if (parentDocument == null) {
            return false;
        } else if (parentDocument.exists()) {
            //直接这样创建是有错误的 SingleDocumentFile不能创建文件或者文件夹
//            documentFile =  parentDocument.createDirectory(FileUtil.getFileName(path));
            try {
                DocumentsContract.createDocument(context.getContentResolver(), parentDocument.getUri(), DocumentsContract.Document.MIME_TYPE_DIR, FileUtil.getFileName(path));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            if (documentFile.exists()) {
                return true;
            } else {
                return false;
            }
        } else {
            boolean flag = mkdirExtSdcardPath(context, FileUtil.getParentFilePath(path), treeUri);
            if (flag) {
//                documentFile =  parentDocument.createDirectory(FileUtil.getFileName(path));
                try {
                    DocumentsContract.createDocument(context.getContentResolver(), parentDocument.getUri(), DocumentsContract.Document.MIME_TYPE_DIR, FileUtil.getFileName(path));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                if (documentFile.exists()) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
    }


    /**
     * 获取某个路径下文件的OutputStream
     *
     * @param context
     * @param pathName
     * @param mimeType
     * @return
     */
    public static OutputStream getExtCardOutputStream(Context context, String pathName, String mimeType) {
        try {
            DocumentFile documentFile = getDocumentFile(context, pathName, getSavedExtSdcardUri(), mimeType);
            OutputStream result = context.getContentResolver().openOutputStream(documentFile.getUri());
            return result;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取外置SDCARD文件的大小
     *
     * @param context
     * @param pathName
     * @return
     */
    public static long getExtCardFileLength(Context context, String pathName) {
        DocumentFile documentFile = getDocumentFile(context, pathName, getSavedExtSdcardUri());
        if (documentFile != null && documentFile.exists()) {
            return documentFile.length();
        }
        return 0;
    }

    /**
     * 判断原理是如果不是内置的就是外置的
     *
     * @param path
     * @return
     */
    public static boolean isExtSdcardPath(String path) {
        return (!(path.startsWith(Environment.getExternalStorageDirectory().getAbsolutePath())));
    }

    /**
     * 删除外置SDCARD的单个文件
     *
     * @param context
     * @param path
     * @return
     */
    public static boolean deleteExtFile(Context context, String path) {
        boolean hasPermission = hasExtSdcardPermission();
        if (hasPermission) {
            DocumentFile documentFile = getDocumentFile(context, path, getSavedExtSdcardUri());
            if (documentFile != null) {
                if (documentFile.exists()) {
                    return documentFile.delete();
                } else {//不存在认为删除成功了
                    return true;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * 用于删除directoryPath目录下的某些文件
     *
     * @param context  //     * @param directoryPath  父目录的地址
     * @param files    父目录下需要删除的图片
     * @param listener
     * @return
     */
    public static int deleteDirectoryFile(Context context/*, String directoryPath*/, ArrayList<ThumbnailBean> files, IDeleteListener<ThumbnailBean> listener) {
        if (files == null || files.size() == 0) return 0;
        ContentResolver cr = context.getContentResolver();
        int size = files.size();
        int successed = 0;
        int failed = 0;
        Uri treeUri = getSavedExtSdcardUri();
        boolean hasPermission = hasExtSdcardPermission();
        for (int i = 0; i < size; i++) {
            ThumbnailBean file = files.get(i);
            if (hasPermission) {
                DocumentFile childDocumentFile = getDocumentFile(context, file.getPath(), treeUri);
                if (childDocumentFile != null) {
                    if (childDocumentFile.exists()) {//存在
                        if (childDocumentFile.delete()) {//删除成功的时候才进行删除数据库记录的操作
                            cr.delete(file.getUri(), null, null);
                            successed++;
                            if (listener != null) {
                                listener.onDeleteFile(file, true);
                            }
                        } else {//删除文件失败 不处理数据库记录
                            failed++;
                            if (listener != null) {
                                listener.onDeleteFile(file, false);
                            }
                        }
                    } else {//不存在 认为成功了
                        cr.delete(file.getUri(), null, null);
                        successed++;
                        if (listener != null) {
                            listener.onDeleteFile(file, true);
                        }
                    }
                } else {
                    failed++;
                    if (listener != null) {
                        listener.onDeleteFile(file, false);
                    }
                }
            } else {
                failed++;
                if (listener != null) {
                    listener.onDeleteFile(file, false);
                }
            }
        }
        return successed;
    }
}
