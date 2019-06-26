package com.allever.security.photo.browser.util;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.Toast;
import com.allever.security.photo.browser.R;
import com.allever.security.photo.browser.bean.ThumbnailBean;

import java.io.*;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

//import com.photoeditor.bean.FileBean;

public class FolderUtil {

    /**
     * 新建文件夹 成功 失败 和已经存在
     */
    public static final int NEW_FOLDER_SUCCESS = 0;
    public static final int NEW_FOLDER_FIAL = 1;
    public static final int NEW_FOLDER_EXIST = 2;
    public static final int NEW_FILE_EXIST = 3;

    public static boolean isBackupFile(File file) {
        String name = file.getName();
        int index = name.lastIndexOf(".");
        if (index >= 0) {
            String houzui = name.substring(index, name.length());
            if (TextUtils.isEmpty(houzui)) {
                return false;
            } else {
                return EncryptUtil.BACKUP_FILE_TYPE.equals(houzui.toLowerCase());
            }
        } else {
            return false;
        }
    }

    /**
     * 数据拉入
     * 按照文件名称排序的
     * @param path
     */
//	public int getDirectoryFile(ArrayList<FileBean> fileMap, String path, String lastName, boolean isNeedZip) {
//		File f = new File(path);
//		if (f.exists()) {
//			fileMap.clear();
//			File[] son = f.listFiles();
//			FileBean lastBean = null;
//			if (son != null) {
//				for (File fi : son) {
//					if (fi.isDirectory() && !fi.isHidden()) {// 目录且非隐藏
//						fileMap.add(new FileBean(fi.getName(), FileBean.TYPE_FOLDER));
//					}else if(isNeedZip && fi.isFile() && isBackupFile(fi)){
//						fileMap.add(new FileBean(fi.getName(), FileBean.TYPE_ZIP));
//					}
//
//					if(!TextUtils.isEmpty(lastName) && fi.getName().equals(lastName)){
//						lastBean = fileMap.get(fileMap.size() - 1);
//					}
//				}
//				// 文件夹排序！！！
//				for (int i = 0; i < fileMap.size(); i++) {
//					for (int j = 0; j < fileMap.size(); j++) {
//						FileBean temp;
//						if (fileMap.get(i).getName().toLowerCase(Locale.getDefault()).compareTo(
//									fileMap.get(j).getName().toLowerCase(Locale.getDefault())) < 1) {
//							temp = fileMap.get(j);
//							fileMap.set(j, fileMap.get(i));
//							fileMap.set(i, temp);
//						}
//					}
//				}
//				if(lastBean != null){
//					return fileMap.indexOf(lastBean);
//				}
//			}
//		}
//		return 0;
//	}

    /**
     * 新建文件夹
     *
     * @param folderPath
     * @return
     */
    private int newFolder(String folderPath) {
        try {
            File f = new File(folderPath);
            if (!f.exists()) {
                boolean flag = f.mkdirs();
                if (flag) {
                    return NEW_FOLDER_SUCCESS;
                } else {
                    return NEW_FOLDER_FIAL;
                }
            } else {
                if (f.isFile()) {
                    return NEW_FILE_EXIST;
                } else {
                    return NEW_FOLDER_EXIST;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return NEW_FOLDER_FIAL;
        }
    }

    /**
     * 将路径写入缓存
     *
     * @param context
     */
    public static List<String> getAllSDPath(Context context) {
        if (PhoneInfo.isNotSupportReadExtSdcardPathFromMount()) {
            return getVolumePaths(context);
        }
        List<String> result = getExtSDCardPaths();
        if (result.size() <= 1) {
            return getVolumePaths(context);
        }
        return result;
    }

    private static List<String> getVolumePaths(Context context) {
        List<String> result = null;
        try {
            StorageManager mStorageManager = (StorageManager) context
                    .getSystemService(Activity.STORAGE_SERVICE);
            //下面这个方法有问题  获取到的Path不一定是挂载上了的
//        	Method mMethodGetPaths = mStorageManager.getClass()
//                    .getMethod("getVolumePaths");
//        	mMethodGetPaths.setAccessible(true);
//        	String[] paths = (String[]) mMethodGetPaths.invoke(mStorageManager);
//        	result = new ArrayList<String>();
//        	for(String str : paths){
//        		result.add(str);
//        	}
            result = new ArrayList<String>();
            Method mMethodGetPrimaryVolume = mStorageManager.getClass().getMethod("getVolumeList");
            mMethodGetPrimaryVolume.setAccessible(true);
            Object[] storageVolumes = (Object[]) mMethodGetPrimaryVolume.invoke(mStorageManager);
            if (storageVolumes != null) {
                for (int i = 0; i < storageVolumes.length; i++) {
                    Object volume = storageVolumes[i];
                    Method getState = volume.getClass().getMethod("getState");
                    getState.setAccessible(true);
                    String state = (String) getState.invoke(volume);

                    if (!TextUtils.isEmpty(state) && Environment.MEDIA_MOUNTED.equals(state)) {
                        Method getPath = volume.getClass().getMethod("getPath");
                        getPath.setAccessible(true);
                        String path = (String) getPath.invoke(volume);
                        result.add(path);
                    }
                }
            }
        } catch (Throwable e) {
            return null;
        }
        return result;
    }


    private static List<String> getExtSDCardPaths() {
        List<String> paths = new ArrayList<String>();
        String extFileStatus = Environment.getExternalStorageState();
        File extFile = Environment.getExternalStorageDirectory();
        if (extFileStatus.equals(Environment.MEDIA_MOUNTED)
                && extFile.exists() && extFile.isDirectory()
                && extFile.canWrite()) {
            paths.add(extFile.getAbsolutePath());
        }
        Process process = null;
        BufferedReader br = null;
        try {
            Runtime runtime = Runtime.getRuntime();
            process = runtime.exec("mount");
            InputStream is = process.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            br = new BufferedReader(isr);
            String line = null;
            int mountPathIndex = 1;
            while ((line = br.readLine()) != null) {
                if ((!line.contains("fat") && !line.contains("fuse") && !line.contains("storage")) || line.contains("secure") || line.contains("asec") || line.contains("firmware")
                        || line.contains("shell")
                        || line.contains("obb")
                        || line.contains("legacy") || line.contains("data")) {
                    continue;
                }
                String[] parts = line.split(" ");
                int length = parts.length;
                if (mountPathIndex >= length) {
                    continue;
                }
                String mountPath = parts[mountPathIndex];
                if (!mountPath.contains("/") || mountPath.contains("data")
                        || mountPath.contains("Data")) {
                    continue;
                }
                File mountRoot = new File(mountPath);
                if (!mountRoot.exists() || !mountRoot.isDirectory()
                        || !mountRoot.canWrite()) {
                    continue;
                }
                boolean equalsToPrimarySD = mountPath.equals(extFile.getAbsolutePath());
                if (equalsToPrimarySD) {
                    continue;
                }
                paths.add(mountPath);
            }
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (process != null) {
                    process.destroy();
                }
                if (br != null) {
                    br.close();
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        return paths;
    }

    /**
     * 删除一个目录下下的所有文件
     *
     * @param dic
     */
    public static void deleteAllFileInDirectory(File dic) {
        try {
            if (dic.exists() && dic.isDirectory()) {
                File[] files = dic.listFiles();
                int count = files.length;
                for (int i = 0; i < count; i++) {
                    if (files[i].isDirectory()) {
                        deleteAllFileInDirectory(files[i]);
                    } else {
                        files[i].delete();
                    }
                }
            }
        } catch (Throwable e) {
        }
    }

    //50M
    public static final float TAKE_PHOTO_MINI_SIZE = 50 * 1024 * 1024;

    public static long getCurrentSelectedSdCardSize(String path) {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            File file = new File(path);
            if (file.exists()) {
                return file.getUsableSpace();
            } else {
                return -1L;
            }
        } else {
            return -1L;
        }
    }

    /**
     * 检查SDCARD的状态  输出提示用户的Toast
     */
    public static void checkSdCardState(Activity activity) {
        long size = getCurrentSelectedSdCardSize(FolderHelper.getOrCreateSaveLocation());
        if (size == -1L) {//不可用的情况
            Toast.makeText(activity, R.string.storage_not_ready, Toast.LENGTH_SHORT).show();
        } else {
            if (size <= TAKE_PHOTO_MINI_SIZE) {
                Toast.makeText(activity, R.string.little_sdcard_tips, Toast.LENGTH_SHORT).show();
            }
        }
    }


    /**
     * 检查SDCARD的状态  输出提示用户的Toast
     */
    public static boolean isSdcardPathCanWrite(Activity activity, String path) {
        if (PhoneInfo.isSupportWriteExtSdCard() && ExtSdcardUtils.isExtSdcardPath(path)) {//如果是外置的路径 且是5.0以上
            if (ExtSdcardUtils.hasExtSdcardPermission()) {
                return true;
            }
        } else {
            File extFile = new File(path);
            if (extFile.canWrite()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取压缩包中的单个文件InputStream
     *
     * @param zipFilePath    压缩文件的完整路径
     * @param singleFileName 压缩包中要解压的文件名 <B>（该目录下的路径)</B>
     * @return InputStream
     * @throws Exception
     */
    public static InputStream unzipSingleFile(String zipFilePath,
                                              String singleFileName) throws Exception {
        ZipFile zipFile = new ZipFile(zipFilePath);
        ZipEntry zipEntry = zipFile.getEntry(singleFileName);

        return zipFile.getInputStream(zipEntry);
    }


    /**
     * 导出ZIP文件的注释
     *
     * @param filename 指定的ZIP文件的完全路径
     * @return ZIP文件的注释，如果找不到任何注释将返回null
     */
    public static String extractZipComment(String filename) {
        if (TextUtils.isEmpty(filename)) {
            return null;
        }

        String retStr = null;
        try {
            File file = new File(filename);
            int fileLen = (int) file.length();

            FileInputStream in = new FileInputStream(file);

            byte[] buffer = new byte[Math.min(fileLen, 2048)];
            int len;

            in.skip(fileLen - buffer.length);

            if ((len = in.read(buffer)) > 0) {
                retStr = getZipCommentFromBuffer(buffer, len);
                if (retStr != null) {
                    retStr = retStr.trim();
                }
            }

            in.close();
        } catch (Exception e) {
        }
        return retStr;
    }

    private static String getZipCommentFromBuffer(byte[] buffer, int len) {
        byte[] magicDirEnd = {0x50, 0x4b, 0x05, 0x06};
        int buffLen = Math.min(buffer.length, len);

        // Check the buffer from the end
        for (int i = buffLen - magicDirEnd.length - 22; i >= 0; i--) {
            boolean isMagicStart = true;
            for (int k = 0; k < magicDirEnd.length; k++) {
                if (buffer[i + k] != magicDirEnd[k]) {
                    isMagicStart = false;
                    break;
                }
            }

            if (isMagicStart) {
                // Magic Start found!
                int commentLen = buffer[i + 20] + buffer[i + 21] * 256;
                int realLen = buffLen - i - 22;
                if (commentLen != realLen) {
                }
                String comment = new String(
                        buffer, i + 22, Math.min(commentLen, realLen));
                return comment;
            }
        }

        return null;
    }


    /**
     * 删除指定文件夹中的所有文件
     *
     * @param folderPath 目标文件夹的路径
     * @return 是否成功删除
     */
    public static boolean deleteFilesInFolder(String folderPath) {
        File folder = new File(folderPath);
        try {
            // 如果图片存放的路径存在并且是目录，则循环删除里面的所有文件
            if (folder.exists() && folder.isDirectory()) {
                File[] files = folder.listFiles();
                for (File file : files) {
                    file.delete();
                }
                return true;
            } else {
            }
        } catch (Exception e) {

        }

        return false;
    }

    /**
     * 检查文件是否存在
     *
     * @param path 文件的路径
     * @return 文件是否存在
     */
    public static boolean checkExist(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }

        try {
            File file = new File(path);
            if (file.exists()) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }

        return false;
    }

    /**
     * 检查并建立指定的文件夹
     *
     * @param folderPath 文件夹的路径
     * @return 是否建立了文件夹
     */
    public static boolean buildFolderIfNotFound(String folderPath) {
        try {
            File folder = new File(folderPath);
            if (!folder.exists()) {
                if (folder.mkdirs() == false) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } catch (Exception ex) {
            return false;
        }
    }


    public static void copyFile(File sourceFile, File targetFile) throws IOException {
        // 新建文件输入流并对它进行缓冲
        FileInputStream input = new FileInputStream(sourceFile);
        BufferedInputStream inBuff = new BufferedInputStream(input);

        // 新建文件输出流并对它进行缓冲
        FileOutputStream output = new FileOutputStream(targetFile);
        BufferedOutputStream outBuff = new BufferedOutputStream(output);

        // 缓冲数组
        byte[] b = new byte[1024 * 5];
        int len;
        while ((len = inBuff.read(b)) != -1) {
            outBuff.write(b, 0, len);
        }

        // 刷新此缓冲的输出流
        outBuff.flush();

        // 关闭流
        inBuff.close();
        outBuff.close();
        output.close();
        input.close();
    }

    /**
     * 移动文件
     *
     * @param sourceFile 源文件
     * @param targetFile 目标文件
     * @return 是否成功移动文件
     */
    public static boolean moveFile(File sourceFile, File targetFile) {
        if (sourceFile == null) {
            return false;
        }
        if (targetFile == null) {
            return false;
        }

        try {
            copyFile(sourceFile, targetFile);
            deleteFile(sourceFile.getAbsolutePath());
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    /**
     * 删除指定的路径的文件
     *
     * @param filePath 指定文件的完全路径
     */
    public static void deleteFile(String filePath) {
        try {
            File file = new File(filePath);
            file.delete();
        } catch (Exception e) {
        }
    }

    /**
     * 删除一个文件夹
     *
     * @param folderPath
     */
    public static void deleteFolder(String folderPath) {
        try {
            File file = new File(folderPath);
            if (!file.exists()) {
                return;
            }
            deleteAllFiles(folderPath);
            file.delete();
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    private static void deleteAllFiles(String folderPath) {
        try {
            File file = new File(folderPath);
            File[] listFiles = file.listFiles();
            if (listFiles != null) {
                for (File item : listFiles) {
                    if (item.isFile()) {
                        item.delete();
                    } else {
                        deleteFolder(item.getAbsolutePath());
                    }
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    /**
     * 删除文件后更新数据库  通知媒体库更新文件夹
     *
     * @param context
     * @param filepath
     */
    public static void updateFileFromDatabase(Context context, String filepath) {
        context.getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Audio.Media.DATA + "=?", new String[]{filepath});
        context.getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Media.DATA + "=?", new String[]{filepath});
    }

    /**
     * 判断SD卡是否可用
     *
     * @return SD卡是否可用
     */
    public static boolean isSDCardMounted() {
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * 调用媒体扫描服务扫描整个外部存储器</br>
     * 建议采用{@link #callToScanMediaFile(Context, String)}来扫描指定文件以提高效率
     *
     * @param context 上下文对象
     */
    public static void callToScanMediaFile(Context context) {
        if (context == null) {
            return;
        }

        context.sendBroadcast(new Intent(
                Intent.ACTION_MEDIA_MOUNTED,
                Uri.parse("file://" + Environment.getExternalStorageDirectory())));
    }

    /**
     * 调用媒体扫描服务扫描指定的图像文件
     *
     * @param context  上下文对象
     * @param filePath 指定的图像文件的完全路径，不需要包含"<code>file://</code>"的前缀
     */
    public static void callToScanMediaFile(Context context, String filePath) {
        if (context == null) {
            return;
        }
        if (filePath == null || filePath.length() == 0) {
            return;
        }

        Uri uri = Uri.parse("file://" + filePath);
        if (uri == null) {
            return;
        }

        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
    }


    private static final String SCHEME_FILE = "file";
    private static final String[] IMAGE_FILE_EXT = {"jpg", "jpeg", "gif", "png", "bmp"};


    /**
     * 获取GO短信在sd卡上的目录
     *
     * @return 目录全路径，例如：/mnt/sdcard/GOSMS/
     */
    public static String getGOSMSDir() {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + "/GOSMS/";
    }


    /****************Properties操作*************************/
    public static final String GOSMS_FILE_PATH = Environment.getExternalStorageDirectory() + "/GOSMS/";
    public static final String PROPERTIES_PATH = GOSMS_FILE_PATH + "properties.cfg";

    /**
     * 加载配置文件
     *
     * @return
     */
    public static Properties loadConfig() {
        Properties properties = new Properties();
        try {
            getOrMakeNomediaDir(GOSMS_FILE_PATH);
            FileInputStream s = new FileInputStream(PROPERTIES_PATH);
            properties.load(s);
            s.close();
        } catch (Exception e) {

        }
        return properties;
    }

    /**
     * 获取Properties值
     *
     * @return
     */
    public static String getPropertiesValue(String key) {
        Properties properties = loadConfig();
        try {
            return properties.get(key).toString();
        } catch (Exception e) {

        }
        return null;
    }


    /**
     * @param byteData
     * @param fileName
     * @return
     */
    public static boolean saveByteToCommonIconSDFile(final byte[] byteData, final String fileName) {
        String filePathName = GOSMS_FILE_PATH + "download/";
        filePathName += fileName;
        return saveByteToSDFile(byteData, filePathName);
    }

    /**
     * 保存数据到指定文件
     *
     * @param byteData
     * @param filePathName
     * @return true for save successful, false for save failed.
     */
    public static boolean saveByteToSDFile(final byte[] byteData, final String filePathName) {
        boolean result = false;
        try {
            File newFile = createNewFile(filePathName, false);
            FileOutputStream fileOutputStream = new FileOutputStream(newFile);
            fileOutputStream.write(byteData);
            fileOutputStream.flush();
            fileOutputStream.close();
            result = true;
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SecurityException e) {
            // TODO: handle exception
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return result;
    }

    /**
     * @param path：文件路径
     * @param append：若存在是否插入原文件
     * @return
     */
    public static File createNewFile(String path, boolean append) {
        File newFile = new File(path);
        if (!append) {
            if (newFile.exists()) {
                newFile.delete();
            }
        }
        if (!newFile.exists()) {
            try {
                File parent = newFile.getParentFile();
                if (parent != null && !parent.exists()) {
                    parent.mkdirs();
                }
                newFile.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return newFile;
    }

    /**
     * 获取媒体文件路径
     *
     * @param context
     * @param uri
     * @param c
     * @return
     */
    public static String getMediaFilePath(Context context, Uri uri, Cursor c) {
        String filePath = null;
        try {
            filePath = c.getString(c.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
        } catch (IllegalArgumentException e) {
            try {
                filePath = c.getString(c.getColumnIndexOrThrow("_data"));
            } catch (IllegalArgumentException ex) {
                filePath = uri.getPath();
            }
        }
        return filePath;
    }

    /**
     * 获取媒体文件类型
     *
     * @param context
     * @param uri
     * @param c
     * @return
     */
    public static String getMediaFileMimeType(Context context, Uri uri, Cursor c) {
        String contentType = null;
        try {
            contentType = c.getString(
                    c.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE)); // mime_type
        } catch (IllegalArgumentException e) {
            try {
                contentType = c.getString(c.getColumnIndexOrThrow("mimetype"));
            } catch (IllegalArgumentException ex) {
                contentType = context.getContentResolver().getType(uri);
            }
        }
        return contentType;
    }

    public static void saveMediaFile(Context context, Uri uri, String filePath) {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = context.getContentResolver().openInputStream(uri);
            os = new FileOutputStream(filePath);
            byte[] buffer = new byte[8192];
            for (int len = 0; (len = is.read(buffer)) != -1; ) {
                os.write(buffer, 0, len);
            }
        } catch (Throwable e) {
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (Throwable e) {
                }
            }
            if (os != null) {
                try {
                    os.close();
                } catch (Throwable e) {
                }
            }
        }
    }

    public static void takePersistableUriPermission(Context context, Uri uri, int takeFlags) {
        try {
            Method takePersistableUriPermissionMethod = ContentResolver.class.getMethod(
                    "takePersistableUriPermission", new Class<?>[]{
                            Uri.class, int.class
                    });
            takePersistableUriPermissionMethod.invoke(context.getContentResolver(), uri, takeFlags);
        } catch (Throwable tr) {
        }
    }

    private static final String[] wordList = {"doc", "docx", "docm", "dotx", "dotm",
            "dot", "odt", "wps", "wpt"};
    private static final String[] pptList = {"ppt", "pptx", "pptm", "potx", "potm",
            "pot", "ppsx", "ppsm", "pps", "ppam", "ppa", "odp", "dps", "dpt"};
    private static final String[] excelList = {"xls", "xlsx", "xlsm", "xlsb", "xltx",
            "xltm", "xlt", "ods", "xlam", "et", "ett"};
    private static final String[] pdfList = {"pdf"};
    private static final String[] zipList = {"rar", "zip", "7z", "CAB", "ARJ", "LZH",
            "TAR", "GZ", "ACE", "UUE", "BZ2", "JAR", "ISO"};
    private static final String[] apkList = {"apk"};

    private static boolean isHave(String[] list, String name) {
        int size = list.length;
        for (int i = 0; i < size; i++) {
            if (list[i].indexOf(name) != -1) {
                return true;
            }
        }
        return false;
    }


    /**
     * 保存数据到文件
     *
     * @param filePath 要保存到的文件路径
     * @param files    要保存的二进制数据
     */
    public static void savePicToFile(String filePath, byte[] files) {
        FileOutputStream out = null;
        try {
            File newFile = new File(filePath);
            if (!newFile.exists()) {
                newFile.createNewFile();
            }
            out = new FileOutputStream(newFile);
            out.write(files);

        } catch (IOException e) {

        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                }
            }
        }
    }

    /**
     * 彩信附件文件名处理
     *
     * @param src
     * @return
     */
    public static String getMmsPartSrc(String src) {
        if (TextUtils.isEmpty(src)) {
            return src;
        }

        src = src.replace(' ', '_');
        src = src.replace('=', '_');

        if (src.length() > 20) {
            src = src.substring(src.length() - 20);
        }

        return src;
    }

    /**
     * 移动文件或者目录
     *
     * @param oldPath
     * @param newPath
     * @return
     */
    public static boolean moveFile(String oldPath, String newPath) {// 移动文件或者目录
        try {

            File src = new File(oldPath);
            if (src.isFile()) {// 文件
                File dest = new File(newPath);
                src.renameTo(dest);
            } else if (src.isDirectory()) {// 目录
                File dest = new File(newPath);
//				File newFile = new File(dest.getAbsoluteFile() + "");
                src.renameTo(dest);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 移动一个目录下的所有文件或者文件夹到另一个目录
     *
     * @param oldPath
     * @param newPath
     * @return
     */
    public static boolean moveDirectoryFile(String oldPath, String newPath) {// 移动文件或者目录
        try {

            File src = new File(oldPath);
            File files[] = src.listFiles();
            int len = files.length;
            for (int i = 0; i < len; i++) {
                if (files[i].isFile()) {// 文件
                    File dest = new File(newPath + File.separator + files[i].getName());
                    files[i].renameTo(dest);
                } else if (files[i].isDirectory()) {// 目录
                    File dest = new File(newPath + File.separator + files[i].getName());
//				File newFile = new File(dest.getAbsoluteFile() + "");
                    files[i].renameTo(dest);
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * @param path
     * @return
     */
    public static long getSdCardSize(String path) {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            /**
             * 获取所有的SD卡的路径
             */
            File file = new File(path);
            if (file.exists()) {
                return file.getUsableSpace();
            } else {
                return -1L;
            }
        } else {
            return -1L;
        }
    }

    /**
     * 获取文件总的大小
     *
     * @param thumbnailBeanArrayList
     * @return
     */
    public static long getFilesSize(ArrayList<ThumbnailBean> thumbnailBeanArrayList) {
        if (thumbnailBeanArrayList == null || thumbnailBeanArrayList.size() == 0) {
            return 0;
        }
        int size = thumbnailBeanArrayList.size();
        long result = 0;
        for (int i = 0; i < size; i++) {
            ThumbnailBean bean = thumbnailBeanArrayList.get(i);
            File f = new File(bean.getPath());
            result += f.length();
        }
        return result;
    }

    /**
     * @param thumbnailBeanArrayList
     * @param isMoveIn               移入还是移出私密相册
     * @return
     */
    public static boolean isMemoryEnough(Context context, ArrayList<ThumbnailBean> thumbnailBeanArrayList, boolean isMoveIn, String curPath) {
        if (isMoveIn) {//如果是移入
            if (curPath.startsWith(FileUtil.DEFAULT_ROOT_PATH)) {//同一个存储卡
                return true;
            } else {
                long size = getSdCardSize(FileUtil.DEFAULT_ROOT_PATH);
                long needSize = getFilesSize(thumbnailBeanArrayList);
                if (size >= needSize) {
                    return true;
                } else {
                    return false;
                }
            }
        } else {//如果是移出
            if (curPath.startsWith(FileUtil.DEFAULT_ROOT_PATH)) {//同一个存储卡
                return true;
            } else {
                List<String> path = getVolumePaths(context);
                if (path != null && path.size() >= 2) {
                    long size = getSdCardSize(path.get(1));
                    long needSize = getFilesSize(thumbnailBeanArrayList);
                    if (size >= needSize) {
                        return true;
                    }
                }
                return false;
            }
        }
    }

    public static final String NOMEDIA_FILENAME = ".nomedia";


    /**
     * 获取目录，不存在则创建并在该目录下生成.nomedia文件
     *
     * @param path 目录路径
     * @return 成功则返回{@link File}对象，不成功则返回{@link null}
     */
    public static File getOrMakeNomediaDir(String path) {
        File dir = null;

        try {
            dir = new File(path);

            if (dir.isFile()) {
                if (!dir.delete()) {
                    return null;
                }
            }

            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    return null;
                }
                File file = new File(path, NOMEDIA_FILENAME);
                try {
                    file.createNewFile();
                } catch (IOException e) {
                }
            }
        } catch (Throwable tr) {
            return null;
        }

        return dir;
    }

    /**
     * 获取目录，不存在则创建
     *
     * @param path 目录路径
     * @return 成功则返回{@link File}对象，不成功则返回{@link null}
     */
    public static File getOrCreateDir(String path) {
        File dir = null;

        try {
            dir = new File(path);

            if (dir.isFile()) {
                if (!dir.delete()) {
                    return null;
                }
            }

            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    return null;
                }
            }
        } catch (Throwable tr) {
            return null;
        }

        return dir;
    }

    /**
     * Deletes all files and subdirectories under "dir".
     *
     * @param dir Directory to be deleted
     * @return boolean Returns "true" if all deletions were successful.
     * If a deletion fails, the method stops attempting to
     * delete and returns "false".
     */
    public static boolean deleteDir(File dir) {

        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        // The directory is now empty so now it can be smoked
        return dir.delete();
    }
}
