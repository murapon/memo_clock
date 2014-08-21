package jp.murapon.memoclock;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import mediba.ad.sdk.android.openx.MasAdView;

import android.app.AlarmManager;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.KeyguardManager.KeyguardLock;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.Vibrator;
import android.os.PowerManager.WakeLock;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AlarmActivity extends MediaActivity {
    private WakeLock wakelock;
    private KeyguardLock keylock;

    private boolean sound_flg = false;
    private MasAdView mad = null;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setScreenContent(R.layout.alarm);
    }

    @Override
    public void onStart() {
        super.onStart();
        setScreenContent(R.layout.alarm);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    private void setScreenContent(int screenId) {

        // 各画面へ振り分け
        setContentView(screenId);
        switch (screenId) {
            // Alarm画面
            case R.layout.alarm: {
                 setAlramScreenContent();
                 break;
            }
            // 広告画面
            case R.layout.alarm_advertise: {
                // 広告表示
                mad = (MasAdView)findViewById(R.id.adview);
                mad.setSid("59b64cccb98938b5793ede49efcf2bfadd707d26a8d5f56d");
                mad.start();
                setAdvertiseScreenContent();
                break;
            }
        }
    }

    // アラーム画面の出力
    private void setAlramScreenContent() {

        // スリープ状態から復帰する
        wakelock = ((PowerManager) getSystemService(Context.POWER_SERVICE))
            .newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK
                | PowerManager.ACQUIRE_CAUSES_WAKEUP
                | PowerManager.ON_AFTER_RELEASE, "disableLock");
        wakelock.acquire();
        wakelock.release();
        // スクリーンロックを解除する
        KeyguardManager keyguard = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        keylock = keyguard.newKeyguardLock("disableLock");
        keylock.disableKeyguard();

        Intent intent = getIntent();
        final String id = intent.getStringExtra("id");
        final String memo = intent.getStringExtra("memo");
        final int vibrator_flg = intent.getIntExtra("vibrator_flg", 0);
        final int volume = intent.getIntExtra("volume", 0);
        final int invalid_silent_mode_flg = intent.getIntExtra("invalid_silent_mode_flg", 0);
        final String media_file = intent.getStringExtra("media_file");

        // notification通知
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification(
                R.drawable.icon,
                memo,
                System.currentTimeMillis());
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);
        // 詳細情報の設定とPendingIntentの設定
        notification.setLatestEventInfo(getApplicationContext(), "Time Manager", memo, pi);
        notificationManager.notify(0, notification);

        // バイブ作動
        if(vibrator_flg == 1){
            long[] pattern = {0, 1500, 500};
            if(vibrator == null){
                vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
            }
            vibrator.vibrate(pattern, 0);
        }

        // 着信音を鳴らすかどうか判断
        // マナーモード設定取得
        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        switch (am.getRingerMode()) {
            // ノーマルモードの場合
            case AudioManager.RINGER_MODE_NORMAL:
                // 着信音量が0以上なら鳴らす
                if(volume > 0){
                    sound_flg = true;
                }
                break;
            // マナーモードの場合
            case AudioManager.RINGER_MODE_SILENT:
            case AudioManager.RINGER_MODE_VIBRATE:
                if(volume > 0 && invalid_silent_mode_flg == 1){
                    sound_flg = true;
                }
                break;
        }

        // 着信音作動
        if(sound_flg){
            try {
                if(media_player == null){
                    media_player = new MediaPlayer();
                }
                media_player.setAudioStreamType(AudioManager.STREAM_ALARM);
                if(media_file != null){
                    media_player.setDataSource(this, Uri.parse(media_file));
                } else {
                    media_player.setDataSource(this, Settings.System.DEFAULT_ALARM_ALERT_URI);
                }
                media_player.setLooping(true);
                double vol = volume / 10.0;
                media_player.setVolume((float)vol, (float)vol);
                media_player.seekTo(0);
                media_player.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // メモ表示
        TextView memoView = (TextView)findViewById(R.id.memo);
        memoView.setText(memo);
        // 現在時刻表示
        TextView timeView = (TextView)findViewById(R.id.current_time);
        Date date = new Date();
        DateFormat df = new SimpleDateFormat("HH:mm");
        timeView.setText(df.format(date));

        // 時間設定画面
        final LinearLayout layout = (LinearLayout)findViewById(R.id.alarm_button_layout);
        final int snooze_interval = InputDataUtil.getSnoozeInterval(intent.getIntExtra("snooze", 0));

        if(snooze_interval > 0){
            getLayoutInflater().inflate(R.layout.alarm_snooze, layout);
            // スヌーズボタン
            Button snoozeButton = (Button)findViewById(R.id.alarm_snooze);
            snoozeButton.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    // 通知を消す
                    NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.cancelAll();
                    // バイブレーターを止める
                    if(vibrator_flg == 1){
                        vibrator.cancel();
                        vibrator = null;
                    }
                    // 着信音を止める
                    if(media_player != null){
                        if (media_player.isPlaying()) {
                            media_player.stop();
                        }
                        media_player.release();
                        media_player = null;
                    }
                    // アプリ終了
                    moveTaskToBack(true);
                }
            });
        } else {
            getLayoutInflater().inflate(R.layout.alarm_no_snooze, layout);
        }

        // ストップボタン
        Button stopButton = (Button)findViewById(R.id.alarm_stop);
        stopButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // 通知を消す
                NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancelAll();
                // バイブレーターを止める
                if(vibrator_flg == 1){
                    vibrator.cancel();
                    vibrator = null;
                }
                // 着信音を止める
                if(media_player != null){
                    if (media_player.isPlaying()) {
                        media_player.stop();
                    }
                    media_player.release();
                    media_player = null;
                }
                // スヌーズ設定ありの場合は、スヌーズ停止
                if(snooze_interval > 0){
                    Intent intent = new Intent(AlarmActivity.this, AlarmReceiver.class);
                    intent.setData(Uri.parse(id));
                    PendingIntent sender = PendingIntent.getBroadcast(AlarmActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    AlarmManager alarmManager = (AlarmManager)AlarmActivity.this.getSystemService(Context.ALARM_SERVICE);
                    alarmManager.cancel(sender);
                }
                // 次の時間を設定
                TimeManagerDao time_manager_dao = new TimeManagerDao(AlarmActivity.this);
                TimeManager item = time_manager_dao.getTime(id);
                if(item.getTimeType() == TimeManager.TIME_TYPE_ALARM){
                    // アラームを設定
                    item = TimeDataUtil.setAlarm(AlarmActivity.this, item, false);
                } else {
                    // タイマーを設定
                    item = TimeDataUtil.setTimer(AlarmActivity.this, item, false);
                }
                // 広告ページへ
                setScreenContent(R.layout.alarm_advertise);
            }
        });
    }

    // 広告画面の出力
    private void setAdvertiseScreenContent() {

        // メインボタン
        Button mainButton = (Button)findViewById(R.id.back_main);
        mainButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // 本体を起動
                Intent main_intent = new Intent();
                main_intent.setClassName("jp.murapon.memoclock", "jp.murapon.memoclock.MainActivity");
                main_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(main_intent);
            }
        });
        // 終了ボタン
//        Button exitButton = (Button)findViewById(R.id.exit);
//        exitButton.setOnClickListener(new OnClickListener() {
//            public void onClick(View v) {
//                // アプリ終了
//                moveTaskToBack(true);
//                finish();
//            }
//        });
    }
}
