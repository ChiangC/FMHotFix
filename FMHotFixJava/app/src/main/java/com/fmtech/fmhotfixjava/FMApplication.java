package com.fmtech.fmhotfixjava;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.fmtech.fmhotfixjava.utils.FixDexUtils;

/**
 * ==================================================================
 * Copyright (C) 2018 FMTech All Rights Reserved.
 *
 * @author Drew.Chiang
 * @email chiangchuna@gmail.com
 * @create_date 2018/9/7 14:15
 * <p>
 * ==================================================================
 */

public class FMApplication extends Application {

    @Override
    public void onCreate() {
        System.out.println("--------onCreate");
        super.onCreate();
    }

    @Override
    protected void attachBaseContext(Context base) {
        System.out.println("--------attachBaseContext");
        MultiDex.install(base);
        FixDexUtils.loadFixedDex(base);
        super.attachBaseContext(base);

    }


}
