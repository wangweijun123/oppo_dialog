package com.example.wangweijun.windowmanger2;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";
    private Handler mHandler = null;

    FloatWindow floatWindow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mHandler = new Handler();

        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 23) {
                    boolean canDrawOverlays = Settings.canDrawOverlays(getApplicationContext());
                    if (!canDrawOverlays) {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                        intent.setData(Uri.parse("package:" + getPackageName()));
                        startActivityForResult(intent, 100);
                    } else {
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (floatWindow == null) {
                                    floatWindow = new FloatWindow(getApplicationContext());
                                }
                                floatWindow.showFloatWindow();
                            }
                        }, 1000 * 3);
                    }
                }
            }
        });
    }


    public void showToast(View v) {
        Toast.makeText(getApplicationContext(), "xxxxx", Toast.LENGTH_SHORT).show();
    }

    public void hideFloatwindow(View v) {
        if (floatWindow != null) {
            floatWindow.hideFloatWindow();
        }
    }

    public void moveBtn(View v) {
        startActivity(new Intent(getApplicationContext(), FloatActivity.class));
    }

    FloatView floatWindow2;
    public void addFloatView2(View v) {
        if (Build.VERSION.SDK_INT >= 23) {
            boolean canDrawOverlays = Settings.canDrawOverlays(getApplicationContext());
            if (!canDrawOverlays) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 100);
            } else {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        floatWindow2 = new FloatView(getApplicationContext());
                        floatWindow2.showFloatWindow();
                    }
                }, 1000 * 1);
            }
        }
    }

    public void deleteFloatView2(View v) {
        floatWindow2.hideFloatWindow();
    }

    public void movefloat(View v) {
        floatWindow2.moveFloatWindow();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100) {
            if (Build.VERSION.SDK_INT >= 23) {
                if (!Settings.canDrawOverlays(this)) {
                    // SYSTEM_ALERT_WINDOW permission not granted...
                    Toast.makeText(getApplicationContext(),"not granted",Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(),"granted",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public static void requestAlertWindowPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(context)) {
                Intent openIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                String path = "package:" + context.getPackageName();
                openIntent.setData(Uri.parse(path));
                openIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(openIntent);
            }
        }
    }


    public void addFloatView(View v) {
        if (Build.VERSION.SDK_INT >= 23) {
            boolean canDrawOverlays = Settings.canDrawOverlays(getApplicationContext());
            if (!canDrawOverlays) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 100);
            } else {
                add();
            }
        }
    }


    private void add() {
        WindowManager windowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.width   = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height  = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.type= WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        layoutParams.flags   =  WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        layoutParams.format  = PixelFormat.TRANSLUCENT;
        layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        layoutParams.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        FloatView view = new FloatView(getApplicationContext()) /*{
            @Override
            public boolean dispatchKeyEvent(KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                    // do you code
                    Log.i("wangweijun", "dispatchKeyEvent back key");
                    return true;
                }
                return super.dispatchKeyEvent(event);
            }
        }*/;
        windowManager.addView(view, layoutParams);
    }
}