package com.beastpotato.selfielight.utils;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

/**
 * MIT License
 * <p>
 * Copyright (c) 2017 Oleksiy
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

public class PreffsHelper {
    private static final String KEY_IMAGE_CIRCLE = "KEY_IMAGE_CIRCLE";

    private static PreffsHelper instance;

    private PreffsHelper(){
    }

    public static PreffsHelper getInstance(){
        if(instance ==null)
            instance = new PreffsHelper();
        return instance;
    }
    public boolean isCurrentViewCircle(Context c){
        return getBoolean(c,KEY_IMAGE_CIRCLE);
    }
    public void setCurrentViewCircle(Context c, boolean b){
        setBool(c,KEY_IMAGE_CIRCLE,b);
    }
    private void setBool(Context c, String key, boolean b){
        SharedPreferences.Editor editor = c.getSharedPreferences("app_preffs", MODE_PRIVATE).edit();
        editor.putBoolean(key, b);
        editor.apply();
    }

    private boolean getBoolean(Context c, String key){
        SharedPreferences prefs = c.getSharedPreferences("app_preffs", MODE_PRIVATE);
        return prefs.getBoolean(key,false);
    }
}
