package com.fmtech.fmhotfixjava.module;

import android.content.Context;
import android.widget.Toast;

/**
 * ==================================================================
 * Copyright (C) 2018 FMTech All Rights Reserved.
 *
 * @author Drew.Chiang
 * @email chiangchuna@gmail.com
 * @create_date 2018/9/7 14:25
 * <p>
 * ==================================================================
 */

public class Module {

    public  void testFix(Context context){
        String str = null;
//        String str = "Self-Driving Car Engineer!";
        Toast.makeText(context, "AI:"+str.substring(0), Toast.LENGTH_LONG).show();
    }

}
