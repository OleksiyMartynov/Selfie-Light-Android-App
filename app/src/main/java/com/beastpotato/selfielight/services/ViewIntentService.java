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
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.beastpotato.selfielight.R;
import com.beastpotato.selfielight.utils.PreffsHelper;


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
            boolean isCircle = PreffsHelper.getInstance().isCurrentViewCircle(getApplicationContext());
            setImage(isCircle);
        }
        try {

            wm.addView(onTopView, getParams(getDisplayWidth(wm)));
            onTopView.setOnTouchListener(new ViewTouchListener(wm));
            onTopView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isCircle = !PreffsHelper.getInstance().isCurrentViewCircle(getApplicationContext());
                    setImage(isCircle);
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private int getDisplayWidth(WindowManager wm){
        Display display = wm.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        return point.x>point.y?point.y:point.x;
    }

    private void setImage(boolean isCircle) {
        ImageView iv = onTopView.findViewById(R.id.selfie_light_image_view);
        PreffsHelper.getInstance().setCurrentViewCircle(getApplicationContext(),isCircle);
        iv.setImageResource(isCircle?R.drawable.ic_selfie_light:R.drawable.ic_selfie_light_rect);
    }


    private void makeToast(String text){
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }
    private void hideUI(WindowManager wm){
        try{
            wm.removeView(onTopView);
            onTopView=null;
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

    public static boolean isActive() {
        return onTopView!=null;
    }

    private class ViewTouchListener implements View.OnTouchListener {
        private int lastAction;
        private int initialX;
        private int initialY;
        private float initialTouchX;
        private float initialTouchY;
        private WindowManager windowManager;
        private ScaleGestureDetector scaleGestureDetector;
        private OnPinchListener pinchListener;
        public ViewTouchListener(WindowManager windowManager) {
            this.windowManager = windowManager;
            pinchListener = new OnPinchListener();
            scaleGestureDetector = new ScaleGestureDetector(getApplicationContext(), pinchListener);
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            WindowManager.LayoutParams params = (WindowManager.LayoutParams)v.getLayoutParams();
            pinchListener.setView(v);
            pinchListener.setWindowManager(windowManager);
            scaleGestureDetector.onTouchEvent(event);
            if(!scaleGestureDetector.isInProgress()) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        initialX = params.x;
                        initialY = params.y;

                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();

                        lastAction = event.getAction();
                        return false;
                    case MotionEvent.ACTION_UP:

                        lastAction = event.getAction();
                        if(initialX!=params.x || initialY!=params.y)
                            return true;
                        else
                            return false;
                    case MotionEvent.ACTION_MOVE:

                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);

                        windowManager.updateViewLayout(v, params);
                        lastAction = event.getAction();
                        if(initialX!=params.x || initialY!=params.y)
                            return true;
                        else
                            return false;
                }
            }
            return false;
        }
    }
    private class OnPinchListener extends ScaleGestureDetector.SimpleOnScaleGestureListener{
        float startingSpan;
        float endSpan;
        float startFocusX;
        float startFocusY;
        private View view;
        private WindowManager windowManager;

        public OnPinchListener() {
        }

        public void setView(View view) {
            this.view = view;
        }

        public void setWindowManager(WindowManager windowManager) {
            this.windowManager = windowManager;
        }

        public boolean onScaleBegin(ScaleGestureDetector detector) {
            startingSpan = detector.getCurrentSpan();
            startFocusX = detector.getFocusX();
            startFocusY = detector.getFocusY();
            return true;
        }

        public boolean onScale(ScaleGestureDetector detector) {
            float delta = detector.getCurrentSpan()/startingSpan;
            int max = getDisplayWidth(windowManager);
            int min = (int)((float)max *0.33f);
            WindowManager.LayoutParams params = (WindowManager.LayoutParams)view.getLayoutParams();
            int newWidth = (int) (params.width*delta);
            int newHeight =(int) (params.height*delta);
            if(newWidth<=max && newWidth>=min) {
                params.width =newWidth;
                params.height =newHeight;
                windowManager.updateViewLayout(view, params);
                return true;
            }else {
                return false;
            }
        }

        public void onScaleEnd(ScaleGestureDetector detector) {

        }
    }
}
