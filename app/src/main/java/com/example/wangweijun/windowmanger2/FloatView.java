package com.example.wangweijun.windowmanger2;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.reflect.Field;

public class FloatView extends LinearLayout implements View.OnTouchListener {
    private WindowManager.LayoutParams mWindowParams;
    private WindowManager mWindowManager;
    private Context mContext;
    private float mInViewX;
    private float mInViewY;
    private float mDownInScreenX;
    private float mDownInScreenY;
    private float mInScreenX;
    private float mInScreenY;

    public FloatView(Context context) {
        super(context);
        mContext = context;
        init(LayoutInflater.from(context));
    }

    public FloatView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init(LayoutInflater.from(context));
    }

    public FloatView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init(LayoutInflater.from(context));
    }

    public FloatView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = context;
        init(LayoutInflater.from(context));
    }

    int minY = 0;
    private LinearLayout bg_container;
    public void init(LayoutInflater inflater) {
        final View mFloatLayout = (View) inflater.inflate(R.layout.layout_float, null);
        mFloatLayout.findViewById(R.id.clickMe).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.i("wangweijun", " onClick ...height:"+mFloatLayout.getHeight() + ", minY:"+minY);
            }
        });

        mFloatLayout.post(new Runnable() {
            @Override
            public void run() {
                int width = mFloatLayout.getMeasuredWidth();
                int height = mFloatLayout.getMeasuredHeight();
                minY = screenHeight - height - 20;
                Log.i("wangweijun", "post height :"+height + ", minY:"+minY);
            }
        });

        bg_container = mFloatLayout.findViewById(R.id.bg_container);
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        getScreenSize();
        mWindowParams = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT >= 26) {//8.0新特性
            mWindowParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            mWindowParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
        mWindowParams.format = PixelFormat.TRANSLUCENT;
        mWindowParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
        mWindowParams.gravity = Gravity.START | Gravity.TOP;
        mWindowParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        mWindowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        bg_container.setOnTouchListener(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        addView(mFloatLayout, params);
    }

    private int screenHeight;
    private void getScreenSize() {
        DisplayMetrics metrics =new DisplayMetrics();
        mWindowManager.getDefaultDisplay().getRealMetrics(metrics);
        int width = metrics.widthPixels;
        screenHeight = metrics.heightPixels;// width:1080, height:1920  view height:1044
        Log.i("wangweijun", " getScreenSize  width:"+width+", screenHeight:"+screenHeight);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return floatLayoutTouch(motionEvent);
    }


    int currY = 0;
    boolean added = false;
    private boolean floatLayoutTouch(MotionEvent motionEvent) {

        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 获取相对View的坐标，即以此View左上角为原点
//                mInViewX = motionEvent.getX();
//                mInViewY = motionEvent.getY();
                // 获取相对屏幕的坐标，即以屏幕左上角为原点
                mDownInScreenX = motionEvent.getRawX();
                mDownInScreenY = motionEvent.getRawY();
                currY = mWindowParams.y;
//                mInScreenX = motionEvent.getRawX();
//                mInScreenY = motionEvent.getRawY();
                Log.i("wangweijun", "action_down currY:"+currY+", mDownInScreenY:"+mDownInScreenY);
                break;
            case MotionEvent.ACTION_MOVE:
                // 更新浮动窗口位置参数
                mInScreenX = motionEvent.getRawX();
                mInScreenY = motionEvent.getRawY();
                int moveY = (int)(mInScreenY - mDownInScreenY);
                int resultY = currY + moveY;
                if (resultY <= minY) {
                    resultY = minY;
                }
                mWindowParams.y = resultY;
                // 手指移动的时候更新小悬浮窗的位置
                Log.i("wangweijun", "action_move position x:" + mWindowParams.x + ", y:" + mWindowParams.y+
                        ", moveY:"+moveY+", mInScreenY:"+mInScreenY+", mDownInScreenY:"+mDownInScreenY);

                mWindowManager.updateViewLayout(this, mWindowParams);
//                bg_container.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
//                if (!added) {
//                    added = true;
//                    addBgView();
//                }
                break;
            case MotionEvent.ACTION_UP:
                // 如果手指离开屏幕时，xDownInScreen和xInScreen相等，
                // 且yDownInScreen和yInScreen相等，则视为触发了单击事件。
                float upX = motionEvent.getRawX();
                float upY = motionEvent.getRawY();
                Log.i("wangweijun", "action_up mDownInScreenX:"+mDownInScreenX
                        +", upX:"+upX+"， mDownInScreenY:"+mDownInScreenY+", upY:"+upY);
                if (mDownInScreenX == upX && mDownInScreenY == upY) {

                }
                break;
        }
        return true;
    }
    boolean isShowed = true;
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (isShowed) {
                // do you code
                Log.i("wangweijun", "dispatchKeyEvent back key");
                isShowed = false;
                hideFloatWindow();
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    public void showFloatWindow() {
        isShowed = true;
        DisplayMetrics metrics = new DisplayMetrics();
        //默认固定位置，靠屏幕右边缘的中间
        mWindowManager.getDefaultDisplay().getMetrics(metrics);
//        mWindowParams.x = metrics.widthPixels;
//        mWindowParams.y = metrics.heightPixels / 2 - getSysBarHeight(mContext);
        mWindowParams.x = 0;
        mWindowParams.y = 1724;// test 1724
        mWindowManager.addView(this, mWindowParams);
    }

    public void hideFloatWindow(){
        mWindowManager.removeView(this);
    }

    int ty = 0;
    public void moveFloatWindow(){
        // 手指移动的时候更新小悬浮窗的位置
        mWindowParams.y = ty;
        Log.i("wangweijun", "y:" + mWindowParams.y);
        mWindowManager.updateViewLayout(this, mWindowParams);
        ty = ty +10;
    }


    // 获取系统状态栏高度
    public static int getSysBarHeight(Context contex) {
        Class<?> c;
        Object obj;
        Field field;
        int x;
        int sbar = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            sbar = contex.getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return sbar;
    }
}
