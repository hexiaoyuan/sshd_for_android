package me.xyhe.sshd4android;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

public class SettingsActivity extends Activity implements OnClickListener {

    TextView mTitle;

    TextView tv_port;
    TextView tv_passwd;

    private ImageButton mOn_off;
    private ImageButton mHelp;
    private ImageButton mOption;

    private ImageButton mMenu;
    private ImageButton btn_back;
    private ImageButton btn_auto_start;
    private ImageButton btn_start_on_boot;
    private ImageButton btn_keep_screen_on;

    //PowerManager powerManager = null;
    //WakeLock wakeLock = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(me.xyhe.sshd4android.R.layout.option);

        findView();
        init();
    }

    public void findView() {
        tv_port = (TextView) findViewById(me.xyhe.sshd4android.R.id.tv_sshd_port);
        tv_passwd = (TextView) findViewById(me.xyhe.sshd4android.R.id.tv_sshd_passwd);
        mTitle = (TextView) findViewById(me.xyhe.sshd4android.R.id.tv_tite);

        mHelp = (ImageButton) findViewById(me.xyhe.sshd4android.R.id.btn_help);
        mOn_off = (ImageButton) findViewById(me.xyhe.sshd4android.R.id.btn_on_off);
        mOption = (ImageButton) findViewById(me.xyhe.sshd4android.R.id.btn_option);

        mMenu = (ImageButton) findViewById(me.xyhe.sshd4android.R.id.ibtn_menu);
        btn_back = (ImageButton) findViewById(me.xyhe.sshd4android.R.id.ibtn_back);
        btn_auto_start = (ImageButton) findViewById(me.xyhe.sshd4android.R.id.btn_auto_start);
        btn_start_on_boot = (ImageButton) findViewById(me.xyhe.sshd4android.R.id.btn_start_on_boot);
        btn_keep_screen_on = (ImageButton) findViewById(me.xyhe.sshd4android.R.id.btn_keep_screen_on);

        mHelp.setOnClickListener(this);
        btn_back.setOnClickListener(this);
        btn_auto_start.setOnClickListener(this);
        btn_start_on_boot.setOnClickListener(this);
        btn_keep_screen_on.setOnClickListener(this);
    }

    public void init() {
        mTitle.setText("Settings");
        btn_back.setVisibility(View.VISIBLE);
        mMenu.setVisibility(View.INVISIBLE);
        mOn_off.setEnabled(false);
        mOn_off.setBackgroundResource(me.xyhe.sshd4android.R.drawable.dont_open);

        // 初始化CheckBox选择状态
        SharedPreferences sp = getSharedPreferences("config", 0);
        if (sp.getBoolean("auto_start", false)) {
            btn_auto_start.setBackgroundResource(me.xyhe.sshd4android.R.drawable.ok);
        } else {
            btn_auto_start.setBackgroundResource(me.xyhe.sshd4android.R.drawable.no);
        }
        if (sp.getBoolean("start_on_boot", false)) {
            btn_start_on_boot.setBackgroundResource(me.xyhe.sshd4android.R.drawable.ok);
        } else {
            btn_start_on_boot.setBackgroundResource(me.xyhe.sshd4android.R.drawable.no);
        }

        // 应用运行时，保持屏幕高亮，不锁屏
        if (sp.getBoolean("keep_screen_on", false)) {
            btn_keep_screen_on.setBackgroundResource(me.xyhe.sshd4android.R.drawable.ok);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            btn_keep_screen_on.setBackgroundResource(me.xyhe.sshd4android.R.drawable.no);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        tv_passwd.setText(sp.getString("sshd_passwd", "123123"));
        tv_port.setText(sp.getString("sshd_port", "22022"));

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case me.xyhe.sshd4android.R.id.ibtn_back: {
                Intent intent_main = new Intent();
                intent_main.setClass(SettingsActivity.this, MainActivity.class);
                startActivity(intent_main);
                break;
            }
            case me.xyhe.sshd4android.R.id.btn_auto_start: {
                SharedPreferences sp = getSharedPreferences("config", 0);
                boolean val = (!sp.getBoolean("auto_start", false));
                v.setBackgroundResource(val ? me.xyhe.sshd4android.R.drawable.ok : me.xyhe.sshd4android.R.drawable.no);
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean("auto_start", val);
                editor.apply();
                break;
            }
            case me.xyhe.sshd4android.R.id.btn_start_on_boot: {
                SharedPreferences sp = getSharedPreferences("config", 0);
                boolean val = (!sp.getBoolean("start_on_boot", false));
                v.setBackgroundResource(val ? me.xyhe.sshd4android.R.drawable.ok : me.xyhe.sshd4android.R.drawable.no);
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean("start_on_boot", val);
                editor.apply();
                break;
            }
            case me.xyhe.sshd4android.R.id.btn_keep_screen_on: {
                SharedPreferences sp = getSharedPreferences("config", 0);
                boolean val = (!sp.getBoolean("keep_screen_on", false));
                v.setBackgroundResource(val ? me.xyhe.sshd4android.R.drawable.ok : me.xyhe.sshd4android.R.drawable.no);
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean("keep_screen_on", val);
                editor.apply();
                break;
            }
            case me.xyhe.sshd4android.R.id.btn_help: {
                Intent intent_help = new Intent();
                intent_help.setClass(SettingsActivity.this, HelpActivity.class);
                startActivity(intent_help);
                break;
            }
            default:
                break;
        }
    }
}
