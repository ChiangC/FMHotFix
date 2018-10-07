package com.fmtech.fmhotfixjava.utils;

import android.content.Context;

import com.fmtech.fmhotfixjava.FMApplication;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.HashSet;

import dalvik.system.DexClassLoader;
import dalvik.system.PathClassLoader;

/**
 * ==================================================================
 * Copyright (C) 2018 FMTech All Rights Reserved.
 *
 * @author Drew.Chiang
 * @email chiangchuna@gmail.com
 * @create_date 2018/9/7 14:22
 * <p>
 * ==================================================================
 */

public class FixDexUtils {
    private static HashSet<File> sLoadedDexFiles = new HashSet();

    static {
        sLoadedDexFiles.clear();
    }

    public static void loadFixedDex(Context context){
        File fileDir = context.getDir(Constants.DEX_DIR, Context.MODE_PRIVATE);
        File[] dexFiles = fileDir.listFiles();
        for(File file:dexFiles){
            if(file.getName().startsWith("classes") && file.getName().endsWith(".dex")){
                sLoadedDexFiles.add(file);
            }
        }

        doDexInject(context, fileDir, sLoadedDexFiles);
    }

    private static void doDexInject(final Context context, File filesDir, HashSet<File> loadedDexs){
        String optimizedDir = filesDir.getAbsolutePath() + File.separator + Constants.OPT_DEX;
        File fopt = new File(optimizedDir);
        if(!fopt.exists()){
            fopt.mkdirs();
        }
        try {
            PathClassLoader pathClassLoader = (PathClassLoader) context.getClassLoader();
            for(File dex:loadedDexs){
                DexClassLoader dexClassLoader = new DexClassLoader(dex.getAbsolutePath(), fopt.getAbsolutePath(), null, pathClassLoader);

                Object dexObj = getPathList(dexClassLoader);
                Object pathObj = getPathList(pathClassLoader);

                Object dexElementsList = getDexElements(dexObj);
                Object pathDexElementsList = getDexElements(pathObj);

                //合并dexElements
                Object dexElements = combineArray(dexElementsList, pathDexElementsList);

                //重新给PathList里面的Element[] dexElements赋值
                Object pathList = getPathList(pathClassLoader);
                setFiled(pathList, pathList.getClass(), "dexElements", dexElements);
                Object finalElementsList = getDexElements(pathList);
                System.out.println("--------"+finalElementsList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void setFiled(Object obj, Class<?> clazz, String fieldName, Object value) throws Exception {
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(obj, value);
    }

    private static Object getField(Object obj, Class<?> clazz, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(obj);
    }

    private static Object getPathList(Object baseDexClassLoader) throws Exception {
        return getField(baseDexClassLoader, Class.forName("dalvik.system.BaseDexClassLoader"), "pathList");
    }

    private static Object getDexElements(Object obj) throws Exception {
        return getField(obj, obj.getClass(), "dexElements");
    }

    private static Object combineArray(Object arrLhs, Object arrRhs){
        Class<?> clazz = arrLhs.getClass().getComponentType();
        int i = Array.getLength(arrLhs);
        int len = i + Array.getLength(arrRhs);
        Object arrResult = Array.newInstance(clazz, len);
        for(int k = 0; k < len; k++){
            if(k < i){
                Array.set(arrResult, k, Array.get(arrLhs, k));
            }else{
                Array.set(arrResult, k, Array.get(arrRhs, k - i));
            }
        }
        return arrResult;
    }

}
