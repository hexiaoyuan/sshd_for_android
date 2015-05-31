package me.xyhe.sshd4android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

public class HelpActivity extends Activity implements OnClickListener {

    private TextView mTitle;
    private ImageButton mHelp;
    private ImageButton mOption;
    private ImageButton btn_back;
    private ImageButton mMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(me.xyhe.sshd4android.R.layout.helpview);
        findView();
        init();
    }

    public void findView() {
        mHelp = (ImageButton) findViewById(me.xyhe.sshd4android.R.id.btn_help);
        mOption = (ImageButton) findViewById(me.xyhe.sshd4android.R.id.btn_option);
        btn_back = (ImageButton) findViewById(me.xyhe.sshd4android.R.id.ibtn_back);
        mMenu = (ImageButton) findViewById(me.xyhe.sshd4android.R.id.ibtn_menu);
        mTitle = (TextView) findViewById(me.xyhe.sshd4android.R.id.tv_tite);

        mHelp.setOnClickListener(this);
        mOption.setOnClickListener(this);
        btn_back.setOnClickListener(this);
    }

    public void init() {
        mTitle.setText("Help");
        btn_back.setVisibility(View.VISIBLE);
        mMenu.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case me.xyhe.sshd4android.R.id.ibtn_back:
                Intent intent_main = new Intent();
                intent_main.setClass(HelpActivity.this, MainActivity.class);
                startActivity(intent_main);
                break;

            case me.xyhe.sshd4android.R.id.btn_option:
                Intent intent_option = new Intent();
                intent_option.setClass(HelpActivity.this, SettingsActivity.class);
                startActivity(intent_option);
                break;

            default:
                break;
        }
    }

}
