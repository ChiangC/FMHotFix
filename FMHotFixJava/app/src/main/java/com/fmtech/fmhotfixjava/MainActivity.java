package com.fmtech.fmhotfixjava;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.fmtech.fmhotfixjava.module.Module;
import com.fmtech.fmhotfixjava.utils.Constants;
import com.fmtech.fmhotfixjava.utils.FixDexUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {
    private Button mTestBtn;
    private Button mHotFixBtn;
    private Module mModule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTestBtn= (Button) findViewById(R.id.btn_test);
        mHotFixBtn= (Button) findViewById(R.id.btn_hot_fix);

        mModule = new Module();

        mTestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mModule.testFix(MainActivity.this);
            }
        });

        mHotFixBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hotFix();
            }
        });

        checkPermission();

    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
        }
    }

    /**
     * 1.把从服务器下载下来的classes2.dex复制到当前应用的
     * 目录(/data/data/packagename/odex)下。
     */
    private void hotFix(){
        ///data/data/packagename/odex
        File appDir = getDir(Constants.DEX_DIR, Context.MODE_PRIVATE);
        if(!appDir.exists()){
            appDir.mkdirs();
        }

        File fixedDexFile = new File(appDir, Constants.DEX_NAME);
        if(fixedDexFile.exists()){
            fixedDexFile.delete();
        }

        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {
            inputStream = new FileInputStream(Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+Constants.DEX_NAME);
            outputStream = new FileOutputStream(fixedDexFile);

            byte[] buffer = new byte[1024];
            int len = 0;
            while((len = inputStream.read(buffer)) != -1){
                outputStream.write(buffer, 0, len);
            }

            if(fixedDexFile.exists()){
                Toast.makeText(this	,"dex文件复制成功", Toast.LENGTH_SHORT).show();
            }

            FixDexUtils.loadFixedDex(MainActivity.this);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }

}
