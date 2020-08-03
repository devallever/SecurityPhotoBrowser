//package org.xm.secret.photo.album.test;
//
//import android.os.Bundle;
//import android.os.Environment;
//import android.support.annotation.Nullable;
//import android.support.v7.app.AppCompatActivity;
//import android.util.Log;
//import android.view.View;
//import org.xm.secret.photo.album.R;
//import org.xm.secret.photo.album.function.endecode.EnDecodeListener;
//import org.xm.secret.photo.album.function.endecode.PrivateHelper;
//import org.xm.secret.photo.album.util.BytesUtils;
//import org.jetbrains.annotations.NotNull;
//
//import java.io.*;
//import java.util.Random;
//
//public class TestActivity extends AppCompatActivity implements View.OnClickListener {
//    private static final String TAG = "TestActivity";
//
//    private static final int KEY = 7;
//
//    //头部
////    字段	字节数
////    魔法值	4
////    原始数据偏移量	4
////    原始数据长度	8
////    时间戳	8
////    原始路径	n
////    加密路径	n
////    原始路径的长度	4
////    加密路径的长度	4
////    秘钥	1
//
////    private static final String MAGIC_VALUE = "-PP-";
////    private int mOriginOffset = 0;
////    private long mOriginLength = 0L;
////    private long mTimeMillis = 0L;
////    private String mOriginPath = "";
////    private String mEncodePath = "";
////    private int mKey =  new Random().nextInt(8) + 1;
//////    private int mKey = KEY;
//
//    String originPath = Environment.getExternalStorageDirectory().getPath() + "/test.jpg";
//    String encodePath = "";
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_test);
//
//        findViewById(R.id.btn_decode).setOnClickListener(this);
//        findViewById(R.id.btn_encode).setOnClickListener(this);
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.btn_decode:
//                String encodePath = "/storage/emulated/0/com.allever.security.photo.browser/Image/Cache/Decode/test.jpg";
//                PrivateHelper.INSTANCE.decode(encodePath, new EnDecodeListener() {
//                    @Override
//                    public void onStart() {
//                        Log.d(TAG, "onStart: decode");
//                    }
//
//                    @Override
//                    public void onSuccess(String path) {
//                        Log.d(TAG, "onSuccess: decode originPath = " + path);
//
//                    }
//
//                    @Override
//                    public void onFail() {
//                        Log.d(TAG, "onFail: decode");
//                    }
//                });
//
//
//
//                break;
//            case R.id.btn_encode:
////                encode();
//                //加密
//                PrivateHelper.INSTANCE.encode(originPath, new EnDecodeListener() {
//                    @Override
//                    public void onStart() {
//                        Log.d(TAG, "onStart: encode");
//                    }
//
//                    @Override
//                    public void onSuccess(String path) {
//                        Log.d(TAG, "onSuccess: encodePath = " + path);
//                        TestActivity.this.encodePath = path;
//                    }
//
//                    @Override
//                    public void onFail() {
//                        Log.d(TAG, "onFail: encode");
//                    }
//                });
//                break;
//            default:
//                break;
//
//        }
//    }
//
//    /***
//     * //    魔法值	        4
//     * //    原始数据偏移量	4
//     * //    原始数据长度	    8
//     * //    时间戳	        8
//     * //    原始路径       	n
//     * //    加密路径	        n
//     * //    原始路径的长度	4
//     * //    加密路径的长度	4
//     * //    秘钥	        1
//     */
//
//
////    private void encode() {
////        //加密
////        String originPath = Environment.getExternalStorageDirectory().getPath() + "/test.jpg";
////        String encodePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/test-private";
////        Log.d(TAG, "encode: originPath = " + originPath);
////        Log.d(TAG, "encode: encodePath = " + encodePath);
////
////
////        BufferedInputStream bufferedInputStream = null;
////        ByteArrayOutputStream byteArrayOutputStream = null;
////        FileOutputStream fileOutputStream = null;
////        try {
////            byteArrayOutputStream = new ByteArrayOutputStream();
////
////            //魔法值
////            byte[] magicBytes = MAGIC_VALUE.getBytes();
////            Log.d(TAG, "encode: magicBytes.size = " + magicBytes.length);
////            //原始路径
////            byte[] originPathBytes = originPath.getBytes();
////            Log.d(TAG, "encode: originPathBytes.size = " + originPathBytes.length);
////            //加密路径
////            byte[] encodePathBytes = encodePath.getBytes();
////            Log.d(TAG, "encode: encodePathBytes.size = " + encodePathBytes.length);
////            //原始路径长度
////            int originPathLength = originPath.length();
////            byte[] originPathLengthBytes = BytesUtils.intToByteArray(originPathLength);
////            Log.d(TAG, "encode: originPathLengthBytes.size = " + originPathLengthBytes.length);
////            //加密路径长度
////            int encodePathLength = encodePath.length();
////            byte[] encodePathLengthBytes = BytesUtils.intToByteArray(encodePathLength);
////            Log.d(TAG, "encode: encodePathLengthBytes.size = " + encodePathLengthBytes.length);
////            //时间戳
////            long time = System.currentTimeMillis();
////            byte[] timeBytes = BytesUtils.longToBytes(time);
////            Log.d(TAG, "encode: timeBytes.size = " + timeBytes.length);
////            //原始数据长度
////            File originFile = new File(originPath);
////            long originFileLength = originFile.length();
////            byte[] originFileLengthBytes = BytesUtils.longToBytes(originFileLength);
////            Log.d(TAG, "encode: originFileLengthBytes.size = " + originFileLengthBytes.length);
////            //key
////            byte[] keyBytes = new byte[1];
////            keyBytes[0] =  BytesUtils.intToByte(mKey);
////            Log.d(TAG, "encode: key = " + mKey);
////            Log.d(TAG, "encode: keyBytes.size = " + keyBytes.length);
////            //原始数据偏移量 = 头部长度
////            int originOffset = magicBytes.length +
////                    originPathBytes.length +
////                    encodePathBytes.length +
////                    originPathLengthBytes.length +
////                    encodePathLengthBytes.length +
////                    timeBytes.length +
////                    originFileLengthBytes.length +
////                    keyBytes.length +
////                    4;//本身
////            byte[] originOffsetBytes = BytesUtils.intToByteArray(originOffset);
////            Log.d(TAG, "encode: originOffsetBytes.size = " + originOffsetBytes.length);
////            Log.d(TAG, "encode: head length.size = " + originOffset);
////
////
////            /***
////             *      * //    魔法值	        4
////             *      * //    原始数据偏移量	    4
////             *      * //    原始数据长度	    8
////             *      * //    时间戳	        8
////             *      * //    原始路径的长度	    4
////             *      * //    原始路径       	n
////             *      * //    加密路径的长度	    4
////             *      * //    加密路径	        n
////             *      * //    秘钥	            1
////             */
////            writeBytes(byteArrayOutputStream, magicBytes);
////            writeBytes(byteArrayOutputStream, originOffsetBytes);
////            writeBytes(byteArrayOutputStream, originFileLengthBytes);
////            writeBytes(byteArrayOutputStream, timeBytes);
////            writeBytes(byteArrayOutputStream, originPathLengthBytes);
////            writeBytes(byteArrayOutputStream, originPathBytes);
////            writeBytes(byteArrayOutputStream, encodePathLengthBytes);
////            writeBytes(byteArrayOutputStream, encodePathBytes);
////            writeBytes(byteArrayOutputStream, keyBytes);
////
////            byte[] b = new byte[1024];
////            bufferedInputStream = new BufferedInputStream(new FileInputStream(originFile));
////            int len = 0;
////            while ((len = bufferedInputStream.read(b)) > 0) {
////                for (int i = 0; i < len; i++) {
////                    b[i] = (byte) (b[i] ^ (byte) mKey);
//////                    byteArrayOutputStream.write(b[i]);
////                }
////                byteArrayOutputStream.write(b, 0, len);
////            }
////            fileOutputStream = new FileOutputStream(new File(encodePath));
////            fileOutputStream.write(byteArrayOutputStream.toByteArray());
////        } catch (Exception e) {
////            e.printStackTrace();
////        } finally {
////            try {
////                if (bufferedInputStream != null) {
////                    bufferedInputStream.close();
////                }
////
////                if (byteArrayOutputStream != null) {
////                    byteArrayOutputStream.close();
////                }
////
////                if (fileOutputStream != null) {
////                    fileOutputStream.close();
////                }
////            } catch (Exception e) {
////                e.printStackTrace();
////            }
////        }
////    }
//
//    private void writeBytes(ByteArrayOutputStream baos, byte[] bytes) {
//        baos.write(bytes, 0, bytes.length);
//    }
//
////    private void decode() {
////        //解密
////        String privatePath = Environment.getExternalStorageDirectory().getPath() + "/test-private";
////        String newPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/test-decode.jpg";
////        Log.d(TAG, "decode: privatePath = " + privatePath);
////        Log.d(TAG, "decode: newPath = " + newPath);
////
////        BufferedInputStream bufferedInputStream = null;
////        ByteArrayOutputStream byteArrayOutputStream = null;
////        FileOutputStream fileOutputStream = null;
////        try {
////            byte[] b = new byte[1024];
////            bufferedInputStream = new BufferedInputStream(new FileInputStream(new File(privatePath)));
////
////            byte[] headBytesBuffer = new byte[1024];
////            int headLength = bufferedInputStream.read(headBytesBuffer);
////            ByteArrayOutputStream headBaos = new ByteArrayOutputStream();
////            headBaos.write(headBytesBuffer, 0, headLength);
////            byte[] headBytes = headBaos.toByteArray();
////            headBaos.close();
////            bufferedInputStream.close();
////
////
////            /***
////             *      * //    魔法值	        4
////             *      * //    原始数据偏移量	    4
////             *      * //    原始数据长度	    8
////             *      * //    时间戳	        8
////             *      * //    原始路径的长度	    4
////             *      * //    原始路径       	n
////             *      * //    加密路径的长度	    4
////             *      * //    加密路径	        n
////             *      * //    秘钥	            1
////             */
////
////            int offset = 0;
////
////            //魔法值
////            byte[] magicBytes = new byte[4];
////            offset = cutBytes(headBytes, 0, magicBytes, 0, 4);
////            String magic = new String(magicBytes);
////            Log.d(TAG, "decode: magic = " + magic);
////            //原始数据偏移量
////            byte[] originOffsetBytes = new byte[4];
////            offset = cutBytes(headBytes, offset, originOffsetBytes, 0, 4);
////            int originOffset = BytesUtils.byteArrayToInt(originOffsetBytes);
////            Log.d(TAG, "decode: originOffset = " + originOffset);
////            //原始数据长度
////            byte[] originFileLengthBytes = new byte[8];
////            offset = cutBytes(headBytes, offset, originFileLengthBytes, 0, 8);
////            long originFileLength = BytesUtils.bytesToLong(originFileLengthBytes);
////            Log.d(TAG, "decode: originFileLength = " + originFileLength);
////            //时间戳
////            byte[] timeBytes = new byte[8];
////            offset = cutBytes(headBytes, offset, timeBytes, 0, 8);
////            //原始路径的长度
////            byte[] originPathLengthBytes = new byte[4];
////            offset = cutBytes(headBytes, offset, originPathLengthBytes, 0, 4);
////            int originPathLength = BytesUtils.byteArrayToInt(originPathLengthBytes);
////            Log.d(TAG, "decode: originPathLength = " + originPathLength);
////            //原始路径
////            byte[] originPathBytes = new byte[originPathLength];
////            offset = cutBytes(headBytes, offset, originPathBytes, 0, originPathLength);
////            String originPath = new String(originPathBytes);
////            Log.d(TAG, "decode: originPath = " + originPath);
////            //加密路径的长度
////            byte[] encodePathLengthBytes = new byte[4];
////            offset = cutBytes(headBytes, offset, encodePathLengthBytes, 0, 4);
////            int encodePathLength = BytesUtils.byteArrayToInt(encodePathLengthBytes);
////            Log.d(TAG, "decode: encodePathLength = " + encodePathLength);
////            //加密路径
////            byte[] encodePathBytes = new byte[encodePathLength];
////            offset = cutBytes(headBytes, offset, encodePathBytes, 0, encodePathLength);
////            String encodePath = new String(encodePathBytes);
////            Log.d(TAG, "decode: encodePath = " + encodePath);
////            byte[] keyBytes = new byte[1];
////            offset = cutBytes(headBytes, offset, keyBytes, 0, 1);
////            int key = BytesUtils.byteToInt(keyBytes[0]);
////            Log.d(TAG, "decode: key = " + key);
////
////            //如果要跳过1个字节数，传的是1
////            bufferedInputStream = new BufferedInputStream(new FileInputStream(new File(privatePath)));
////            bufferedInputStream.skip(originOffset);
////            byteArrayOutputStream = new ByteArrayOutputStream();
////            int len = 0;
////            while ((len = bufferedInputStream.read(b)) > 0) {
////                for (int i = 0; i < len; i++) {
////                    b[i] = (byte) (b[i] ^ (byte) key);
//////                    byteArrayOutputStream.write(b[i]);
////                }
////                byteArrayOutputStream.write(b, 0, len);
////            }
////            fileOutputStream = new FileOutputStream(new File(newPath));
////            fileOutputStream.write(byteArrayOutputStream.toByteArray());
////        } catch (Exception e) {
////            e.printStackTrace();
////        } finally {
////            try {
////                if (bufferedInputStream != null) {
////                    bufferedInputStream.close();
////                }
////
////                if (byteArrayOutputStream != null) {
////                    byteArrayOutputStream.close();
////                }
////
////                if (fileOutputStream != null) {
////                    fileOutputStream.close();
////                }
////            } catch (Exception e) {
////                e.printStackTrace();
////            }
////        }
////    }
//
//    /***
//     * @param      src      the source array.
//     * @param      offset   starting position in the source array.
//     * @param      dest     the destination array.
//     * @param      destPos  starting position in the destination data.
//     * @param      length   the number of array elements to be copied.
//     * @return
//     */
//    private int cutBytes(byte[] src, int offset, byte[] dest, int destPos, int length) {
//        System.arraycopy(src, offset, dest, destPos, length);
//        return offset + length;
//    }
//
//}
