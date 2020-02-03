package com.wei.handler02;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;

import java.lang.ref.WeakReference;

public class Handler02Activity extends AppCompatActivity {

    // 啟動與停止 Handler 的 Switch 元件
    private Switch handler01Switch, handler02Switch, handler03Switch;
    // 顯示 Handler 運算進度的元件
    private SeekBar handler01seekBar, handler02seekBar, handler03seekBar;
    // 處理多個不同工作的 seekBar Handler 物件
    private SeekBarHandler seekBarHandler = new SeekBarHandler(this);

    // <summary> 用來處理多個不同工作的 Handler 類別
    private static class SeekBarHandler extends Handler {
        private final WeakReference<Handler02Activity> mActivity;

        SeekBarHandler(Handler02Activity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            Handler02Activity activity = mActivity.get();

            if (activity != null) {
                super.handleMessage(msg);

                long t = 0;
                SeekBar target = null;

                switch (msg.what) {
                    case R.id.handler01Switch:
                        target = activity.handler01seekBar;
                        t = 100;
                        break;
                    case R.id.handler02Switch:
                        target = activity.handler02seekBar;
                        t = 200;
                        break;
                    case R.id.handler03Switch:
                        target = activity.handler03seekBar;
                        t = 300;
                        break;
                }

                assert target != null;
                target.setProgress(getNextProgress(target));
                activity.seekBarHandler.sendEmptyMessageDelayed(msg.what, t);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        processViews();
        processControllers();
    }

    private void processViews() {
        handler01Switch = findViewById(R.id.handler01Switch);
        handler02Switch = findViewById(R.id.handler02Switch);
        handler03Switch = findViewById(R.id.handler03Switch);

        handler01seekBar = findViewById(R.id.handler01_seekBar);
        handler02seekBar = findViewById(R.id.handler02_seekBar);
        handler03seekBar = findViewById(R.id.handler03_seekBar);
    }

    private void processControllers() {
        CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(
                    CompoundButton buttonView,
                    boolean isChecked) {
                int id = buttonView.getId();

                if (isChecked)
                    seekBarHandler.sendEmptyMessage(id);
                else
                    seekBarHandler.removeMessages(id);
            }
        };

        handler01Switch.setOnCheckedChangeListener(listener);
        handler02Switch.setOnCheckedChangeListener(listener);
        handler03Switch.setOnCheckedChangeListener(listener);
    }

    private static int getNextProgress(SeekBar seekBar) {
        int now = seekBar.getProgress();
        int max = seekBar.getMax();

        if (++now >= max)
            now = 0;

        return now;
    }
}
