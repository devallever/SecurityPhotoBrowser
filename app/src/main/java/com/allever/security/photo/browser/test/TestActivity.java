package com.allever.security.photo.browser.test;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import com.allever.security.photo.browser.R;

import java.io.*;

public class TestActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "TestActivity";

    private static final int KEY = 2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        findViewById(R.id.btn_decode).setOnClickListener(this);
        findViewById(R.id.btn_encode).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_decode:
                decode();
                break;
            case R.id.btn_encode:
                encode();
                break;
            default:
                break;

        }
    }

    private void encode() {
        //加密
        String originPath = Environment.getExternalStorageDirectory().getPath() + "/test.jpg";
        String newPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/private-test";
        Log.d(TAG, "encode: originPath = " + originPath);
        Log.d(TAG, "encode: newPath = " + newPath);

        BufferedInputStream bufferedInputStream = null;
        ByteArrayOutputStream byteArrayOutputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            byte[] b = new byte[1024];
            bufferedInputStream = new BufferedInputStream(new FileInputStream(new File(originPath)));
            int len = 0;
            byteArrayOutputStream = new ByteArrayOutputStream();
            while ((len = bufferedInputStream.read(b)) > 0) {
                for (int i = 0; i < len; i++) {
                    b[i] = (byte) (b[i] ^ (byte) KEY);
                    byteArrayOutputStream.write(b[i]);
                }
            }
            fileOutputStream = new FileOutputStream(new File(newPath));
            fileOutputStream.write(byteArrayOutputStream.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (bufferedInputStream != null) {
                    bufferedInputStream.close();
                }

                if (byteArrayOutputStream != null) {
                    byteArrayOutputStream.close();
                }

                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void decode() {
        //解密
        String privatePath = Environment.getExternalStorageDirectory().getPath() + "/private-test";
        String newPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/decode-test.jpg";
        Log.d(TAG, "decode: privatePath = " + privatePath);
        Log.d(TAG, "decode: newPath = " + newPath);

        BufferedInputStream bufferedInputStream = null;
        ByteArrayOutputStream byteArrayOutputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            byte[] b = new byte[1024];
            bufferedInputStream = new BufferedInputStream(new FileInputStream(new File(privatePath)));
            int len = 0;
            byteArrayOutputStream = new ByteArrayOutputStream();
            while ((len = bufferedInputStream.read(b)) > 0) {
                for (int i = 0; i < len; i++) {
                    b[i] = (byte) (b[i] ^ (byte) KEY);
                    byteArrayOutputStream.write(b[i]);
                }
            }
            fileOutputStream = new FileOutputStream(new File(newPath));
            fileOutputStream.write(byteArrayOutputStream.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (bufferedInputStream != null) {
                    bufferedInputStream.close();
                }

                if (byteArrayOutputStream != null) {
                    byteArrayOutputStream.close();
                }

                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
