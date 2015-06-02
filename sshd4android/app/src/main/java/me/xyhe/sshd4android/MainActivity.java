package me.xyhe.sshd4android;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener {

    private static final String TAG = "sshd4android";

    private ImageButton mMenu;
    private ImageButton mOn_off;
    private ImageButton mHelp;
    private ImageButton mOption;
    private ImageButton btn_back;

    private TextView mTitle;
    private TextView mWifi;
    private TextView mAddr;
    private TextView mState;
    private AnimationDrawable AniDraw;
    private PopupWindow mPopupwindow;
    private ListView mLogListView;

    // 资源文件
    private String fn_dropbear;
    private String fn_dropbearconvert;
    private String fn_dropbearkey;
    private String fn_scp;
    private String fn_ssh;
    private String fn_sftp_server;
    private String fn_busybox;
    private String fn_auth_keys;

    // WIFI 信息
    private WifiInfo mWifiInfo;
    private String mDeviceIP;

    private String mLogLine;
    MyAdapter mAdapter;
    private List<String> mLogList;

//    Handler mHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            SharedPreferences sp_logs = getSharedPreferences("logs", 0);
//            SharedPreferences sp_Config = getSharedPreferences("config", 0);
//            switch (msg.what) {
//                case 0x01:
//                    Log.i(TAG, "Message :: MAIN get !");
//                    mLogLine = sp_logs.getString("log", null);
//                    Log.i(TAG, "Message ::" + mLogLine);
//                    mLogList.add(mLogLine);
//                    mAdapter.notifyDataSetChanged();
//                    break;
//
//                default:
//                    break;
//            }
//        }
//    };

    PowerManager mPowerManager = null;
    WakeLock mWakeLock = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        findView();

        make_dirs_for_dropbear();
        extract_asset();

        init();

    }

    public String get_dropbear_home_dir()
    {
        return MainActivity.this.getFilesDir().getParent() + File.separator + "home";
    }

    public String get_dropbear_conf_dir()
    {
        return MainActivity.this.getFilesDir().getParent() + File.separator + "home/.ssh";
    }

    public String get_dropbear_bin_dir()
    {
        return MainActivity.this.getFilesDir().getParent() + File.separator + "dropbear";
    }

    public void make_dirs_for_dropbear()
    {
        File f = new File(this.getApplicationContext().getFilesDir() + "/");
        f.mkdirs();

        Util.mkdirs(get_dropbear_home_dir());
        Util.mkdirs(get_dropbear_bin_dir());
        Util.mkdirs(get_dropbear_conf_dir());
    }


    public void extract_asset()
    {
        String dir_conf = get_dropbear_conf_dir();
        String dir_bin = get_dropbear_bin_dir();

        fn_dropbear = Util.extractAssetToDir(this, "dropbear", dir_bin, "dropbear", false);
        Util.exec("chmod 777 " + fn_dropbear);

        fn_dropbearconvert = Util.extractAssetToDir(this, "dropbearconvert", dir_bin, "dropbearconvert", false);
        Util.exec("chmod 777 " + fn_dropbearconvert);

        fn_dropbearkey = Util.extractAssetToDir(this, "dropbearkey", dir_bin, "dropbearkey", false);
        Util.exec("chmod 777 " + fn_dropbearkey);

        fn_scp = Util.extractAssetToDir(this, "scp", dir_bin, "scp", false);
        Util.exec("chmod 777 " + fn_scp);

        fn_ssh = Util.extractAssetToDir(this, "ssh", dir_bin, "ssh", false);
        Util.exec("chmod 777 " + fn_ssh);

        fn_sftp_server = Util.extractAssetToDir(this, "sftp-server", dir_bin, "sftp-server", false);
        Util.exec("chmod 777 " + fn_sftp_server);

        // curl -O http://www.busybox.net/downloads/binaries/latest/busybox-armv6l <-- OLD
        // curl -O http://www.busybox.net/downloads/binaries/latest/busybox-armv7l
        fn_busybox = Util.extractAssetToDir(this, "busybox-armv7l", dir_bin, "busybox", false);
        Util.exec("chmod 777 " + fn_busybox);

        fn_auth_keys = Util.extractAssetToDir(this, "authorized_keys", dir_conf, "authorized_keys", false);
        Util.exec("chmod 600 " + fn_auth_keys);
    }


    public void findView()
    {
        mMenu = (ImageButton) findViewById(R.id.ibtn_menu);
        mHelp = (ImageButton) findViewById(R.id.btn_help);
        mOn_off = (ImageButton) findViewById(R.id.btn_on_off);
        mOption = (ImageButton) findViewById(R.id.btn_option);
        btn_back = (ImageButton) findViewById(R.id.ibtn_back);

        mTitle = (TextView) findViewById(R.id.tv_tite);
        mWifi = (TextView) findViewById(R.id.tv_wifi);
        mAddr = (TextView) findViewById(R.id.tv_addr);
        mState = (TextView) findViewById(R.id.tv_state);

        mLogListView = (ListView) findViewById(R.id.log_list);

        mMenu.setOnClickListener(this);
        mHelp.setOnClickListener(this);
        mOn_off.setOnClickListener(this);
        mOption.setOnClickListener(this);
    }

    private boolean is_dropbear_running()
    {
        String txt = Util.exec_out(fn_busybox + " ps");
        if (txt.contains(fn_dropbear)) {
            return true;
        }
        return false;
    }

    public void init() {
        //mTitle.setText("SSHD For Android");
        btn_back.setVisibility(View.INVISIBLE);
        mMenu.setVisibility(View.VISIBLE);

        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        mWifiInfo = wifiManager.getConnectionInfo();
        mWifi.setText(mWifiInfo.getSSID() + "");

        mLogList = new ArrayList<String>();
        mAdapter = new MyAdapter(MainActivity.this, mLogList);
        mLogListView.setAdapter(mAdapter);

        boolean running = is_dropbear_running();

        if (running) {
            mDeviceIP = intToIp(mWifiInfo.getIpAddress());
            mAddr.setText(mDeviceIP + ":" + get_config_sshd_port());
            mAddr.setTextColor(mAddr.getResources().getColor(R.color.snow));
            mState.setText("Running...");
            mState.setTextColor(mState.getResources().getColor(R.color.snow));
            mOn_off.setBackgroundResource(R.drawable.btn_touch_on);
            new SshdDeamonTask().start();
            mOn_off.setBackgroundResource(R.anim.pc_loading);
            AniDraw = (AnimationDrawable) mOn_off.getBackground();
            AniDraw.start();
        }

        SharedPreferences sp = getSharedPreferences("config", 0);
        boolean keep_screen_on = sp.getBoolean("keep_screen_on", false);
        if (keep_screen_on) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    public String get_config_sshd_port() {
        SharedPreferences sp = getSharedPreferences("config", 0);
        return sp.getString("sshd_port", "22022");
    }

    public String get_config_sshd_passwd() {
        SharedPreferences sp = getSharedPreferences("config", 0);
        return sp.getString("sshd_passwd", "123123");
    }

    // 转换整形IP地址为字符串类型
    private static String intToIp(int i) {
        return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF)
                + "." + (i >> 24 & 0xFF);
    }

    private class SshdDeamonTask extends Thread {

        @Override
        public void run() {
            String dir_conf = get_dropbear_conf_dir();
            String dir_home = get_dropbear_home_dir();
            String dir_bin = get_dropbear_bin_dir();

            File id_dss = new File(dir_conf, "id_dss"); // dropbear_dss_host_key
            if (!id_dss.exists()) {
                Log.i(TAG, "to make dss key ...");
                Util.exec(fn_dropbearkey + " -t dss -f " + id_dss.getAbsolutePath());
            }

            File id_rsa = new File(dir_conf, "id_rsa"); // dropbear_rsa_host_key
            if (!id_rsa.exists()) {
                Log.i(TAG, "to make rsa key ...");
                Util.exec(fn_dropbearkey + " -t rsa -f " + id_rsa.getAbsolutePath());
            }

            SharedPreferences sp = getSharedPreferences("config", 0);
            String sshd_passwd = sp.getString("sshd_passwd", "123123");
            String sshd_port = sp.getString("sshd_port", "22022");

            Log.i(TAG, "to start dropbear in background ...");

            int uid = getApplicationInfo().uid;
            int gid = uid;
            String uname = "shell"; //  UidNames.getUidName(uid);

            String cli = fn_dropbear + " -A -N " + uname
                    + " -U " + uid
                    + " -G " + gid
                    + " -r " + id_dss.getAbsolutePath()
                    + " -r " + id_rsa.getAbsolutePath()
                    + " -f " + fn_auth_keys
                    + " -C " + get_config_sshd_passwd()
                    + " -p " + get_config_sshd_port()
                    //+ " -F "
                    //+ " -v -v -v -v -v -v"
                    ;

            Log.i(TAG, cli);
            Util.exec(cli);
        }
    }

    //Boolean mStyle = true;

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case me.xyhe.sshd4android.R.id.ibtn_menu:
                if (mPopupwindow != null && mPopupwindow.isShowing()) {
                    mPopupwindow.dismiss();
                    return;
                } else {
                    initmPopupWindowView();
                    mPopupwindow.showAsDropDown(v, 0, 5);
                }
                break;

            case me.xyhe.sshd4android.R.id.btn_help:
                Intent intent_help = new Intent();
                intent_help.setClass(MainActivity.this, HelpActivity.class);
                startActivity(intent_help);
                break;

            case me.xyhe.sshd4android.R.id.btn_on_off:
                boolean running = is_dropbear_running();
                if (!running) { // 开启
                    mWifi.setText(mWifiInfo.getSSID() + "");
                    mDeviceIP = intToIp(mWifiInfo.getIpAddress());
                    mAddr.setText(mDeviceIP + ":" + get_config_sshd_port());
                    mAddr.setTextColor(mAddr.getResources().getColor(R.color.snow));
                    mState.setText("Running...");
                    mState.setTextColor(mState.getResources()
                            .getColor(me.xyhe.sshd4android.R.color.snow));
                    mOn_off.setBackgroundResource(R.drawable.btn_touch_on);
                    new SshdDeamonTask().start();
                    mOn_off.setBackgroundResource(R.anim.pc_loading);
                    AniDraw = (AnimationDrawable) mOn_off.getBackground();
                    AniDraw.start();
                } else { // 关闭
                    AniDraw.stop();
                    mOn_off.setBackgroundResource(R.drawable.btn_touch_off);
                    Util.exec(fn_busybox + " killall dropbear");
                    mWifi.setText(mWifiInfo.getSSID() + "");
                    mDeviceIP = intToIp(mWifiInfo.getIpAddress());
                    mAddr.setText("-");
                    mAddr.setTextColor(mAddr.getResources().getColor(R.color.darkred));
                    mState.setText("-");
                    mState.setTextColor(mState.getResources().getColor(R.color.darkred));
                }
                break;

            case me.xyhe.sshd4android.R.id.btn_option:
                Intent intent_option = new Intent();
                intent_option.setClass(MainActivity.this, SettingsActivity.class);
                startActivity(intent_option);
                break;

            default:
                break;
        }
    }

    public void initmPopupWindowView() {
        DisplayMetrics dm;
        dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        View customView = getLayoutInflater().inflate(R.layout.show, null, false);
        mPopupwindow = new PopupWindow(customView, dm.widthPixels, dm.heightPixels);

        customView.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                if (mPopupwindow != null && mPopupwindow.isShowing()) {
                    mPopupwindow.dismiss();
                    mPopupwindow = null;
                }
                return false;
            }
        });

        Button clear = (Button) customView.findViewById(R.id.btn_clear);
        Button login_out = (Button) customView.findViewById(R.id.btn_login_out);

        clear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        login_out.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Util.exec(fn_busybox + " killall -9 dropbear");
                android.os.Process.killProcess(android.os.Process.myPid()); // clean myself all
                System.exit(0);
            }
        });
    }
}
