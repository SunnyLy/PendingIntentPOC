package com.sunny.hello.poc;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    private EditText mEtCMD;
    private TextView mTvInfo;
    private TextView mTvError;
    private Button mBtnExc;

    private StringBuffer mSBConsole = new StringBuffer();
    private StringBuffer mSBError = new StringBuffer();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEtCMD = findViewById(R.id.et_cmd);
        mBtnExc = findViewById(R.id.btn_exc);
        mTvInfo = findViewById(R.id.tv_result);
        mTvError = findViewById(R.id.tv_errorInfo);
        mSBConsole.append("Result:\n");
        mSBError.append("---------------\nError:\n");
    }

    public void jumpShareTrans(View view) {
        Intent shareTrans = new Intent("com.sina.weibo.sdk.action.ACTION_SDK_REQ_ACTIVITY");
        shareTrans.addCategory("android.intent.category.DEFAULT");
        shareTrans.setClassName("com.lexin.android.maiya", "com.sina.weibo.sdk.share.ShareTransActivity");
        shareTrans.putExtra("start_flag", 1001);
        shareTrans.putExtra("progress_id", android.R.layout.test_list_item);
        startActivity(shareTrans);

    }

    /**
     * 开始执行命令
     *
     * @param view
     */
    public void startExc(View view) {
        String cmd = mEtCMD.getText().toString().trim();
        if (TextUtils.isEmpty(cmd)) {
            Toast.makeText(this, "请输入命令", Toast.LENGTH_SHORT).show();
            return;
        }

//        cmd = "ps -T -p " + Process.myPid();

        startCMD(cmd);
    }

    private void freshUI(String result) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                mSBConsole.append(result);
                mSBConsole.append("\n");
                mTvInfo.setText(mSBConsole.toString());
                mTvError.setText("");
                Log.e("sunny", mSBConsole.toString());
            }
        });
    }


    private void startCMD(String cmd) {
        Runtime runtime = Runtime.getRuntime();
        java.lang.Process process = null;
        InputStream cmdResultIS = null;
        BufferedReader bis = null;
        try {
            process = runtime.exec(cmd);
            if (process != null) {
                cmdResultIS = process.getInputStream();
                bis = new BufferedReader(new InputStreamReader(cmdResultIS));
                BufferedReader finalBis = bis;

                process.waitFor();
                if (finalBis != null) {
                    try {
                        while (finalBis.readLine() != null) {
                            freshUI(finalBis.readLine());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        printError(e.getMessage());
                    } finally {
                        if (finalBis != null) {
                            try {
                                finalBis.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            printError(e.getMessage());
        } finally {
            try {
                if (cmdResultIS != null) {
                    cmdResultIS.close();
                }
                if (bis != null) {
                    bis.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }

    private void printError(String message) {
        mSBError.append(message);
        mTvError.setText(mSBError.toString());
    }

    public void clearConsole(View view) {
        if (mSBConsole != null) {
            mSBConsole.setLength(0);
            mSBError.setLength(0);
            mSBConsole.append("Result:\n");
            mTvInfo.setText(mSBConsole.toString());
            mTvError.setText("");
        }
    }
}