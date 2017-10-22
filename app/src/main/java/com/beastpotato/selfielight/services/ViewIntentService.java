package com.beastpotato.selfielight.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.os.Handler;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.beastpotato.selfielight.R;

import uk.co.senab.photoview.PhotoViewAttacher;


public class ViewIntentService extends IntentService {

    private static final String ACTION_TOGGLE_UI = "com.beastpotato.selfielight.services.action.ACTION_TOGGLE_UI";

    private static final String EXTRA_SHOW_UI = "com.beastpotato.selfielight.services.extra.EXTRA_SHOW_UI";
    private static View onTopView;

    public ViewIntentService() {
        super("ViewIntentService");
    }


    public static void startToggleUI(Context context, Boolean showUI) {
        Intent intent = new Intent(context, ViewIntentService.class);
        intent.setAction(ACTION_TOGGLE_UI);
        intent.putExtra(EXTRA_SHOW_UI, showUI);
        context.startService(intent);
    }



    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_TOGGLE_UI.equals(action)) {
                Boolean param = intent.getBooleanExtra(EXTRA_SHOW_UI,false);
                handleActionFoo(param);
            }
        }
    }


    private void handleActionFoo(Boolean shouldShow) {
        Handler mHandler = new Handler(getMainLooper());
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
                if(shouldShow) {
                    showUI(wm);
                }else{
                    hideUI(wm);
                }
                makeToast("Selfie Light: "+(shouldShow?"ON":"OFF"));
            }
        });

    }
    private void showUI( WindowManager wm){
        if(onTopView==null) {
            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            onTopView = inflater.inflate(R.layout.layout_selfie_light,null);
            onTopView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
        }
        try {
            Display display = wm.getDefaultDisplay();
            Point point = new Point();
            display.getSize(point);
            wm.addView(onTopView, getParams(point.x>point.y?point.y:point.x));
            PhotoViewAttacher photoViewAttacher =  new PhotoViewAttacher(onTopView.findViewById(R.id.selfie_light_image_view));
            photoViewAttacher.setMinimumScale(0.5f);
            photoViewAttacher.update();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void makeToast(String text){
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }
    private void hideUI(WindowManager wm){
        try{
            wm.removeView(onTopView);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public ViewGroup.LayoutParams getParams(int width) {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.format = PixelFormat.TRANSLUCENT;
        params.width = width;//WindowManager.LayoutParams.MATCH_PARENT;
        params.height = width;//WindowManager.LayoutParams.MATCH_PARENT;
        params.x =0;
        params.y =0;
        params.gravity = Gravity.TOP | Gravity.CENTER;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O)
            params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        else
            params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        return params;
    }
}
