package jp.murapon.memoclock;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;  
import java.util.List;
import java.util.TimeZone;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TimePicker;
import mediba.ad.sdk.android.openx.MasAdView;
import com.mediba.jp.*;

public class MainActivity extends MediaActivity{

    static private String prefName = "display_curr";
    private int mScreenId = R.layout.main;
    Calendar calendar = Calendar.getInstance();
    private MasAdView mad = null;

    // 初期入力かどうか設定
    boolean new_input_flg = true;
    // 登録データ
    // ID
    int time_manager_id = 0;
    // メモ
    String memo = "";
    // 時間(アラーム)
    int hour = calendar.get(Calendar.HOUR_OF_DAY);
    int minute = calendar.get(Calendar.MINUTE);
    // 時間(タイマー)
    int timer_hour = 0;
    int timer_minute = 0;
    // 繰り返し
    boolean[] alarm_repeat_list = {false, false, false, false, false, false, false}; // 繰り返し
    int timer_repeat = 0; // (初期値：なし);
    // スヌーズ間隔
    int snooze = 0; // (初期値：なし);
    // メディアファイル
    Uri media_uri = null;
    // アラームかタイマーか
    int time_type = TimeManager.TIME_TYPE_ALARM; //初期値：アラーム
    // ボリューム
    int volume = 0;
    // バイブを利用するかどうか
    boolean vibrator_flg;
    // マナーモードでも鳴らすかどうか
    boolean invalid_silent_mode_flg = false;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setScreenContent(mScreenId);
    }

    @Override
    public void onStart() {
        super.onStart();
        setScreenContent(mScreenId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SharedPreferences prefs = getSharedPreferences(prefName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("screenId", mScreenId);
        editor.commit();
    }

    private void setScreenContent(int screenId) {

        // 各画面へ振り分け
        mScreenId = screenId;
        setContentView(screenId);
        switch (screenId) {
            // メイン画面
            case R.layout.main: {
                // 広告表示
                mad = (MasAdView)findViewById(R.id.adview);
                mad.setSid("59b64cccb98938b5793ede49efcf2bfadd707d26a8d5f56d");
                mad.start();
                setMainScreenContent();
                break;
            }
            // 登録・更新画面
            case R.layout.regist: {
                setRegistScreenContent();
                break;
            }
        }
    }


    // メイン画面の出力
    private void setMainScreenContent() {

        // データの初期化
        final ListAdapter listAdapater;
        // 着信音が再生中だったら止める
        if(media_player != null){
            if (media_player.isPlaying()) {
                media_player.stop();
                try {
                    media_player.prepare();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                media_player.release();
            }
        }
        // バイブ停止
        if(vibrator != null){
            vibrator.cancel();
            vibrator = null;
        }

        // 現在時刻をアラーム設定の初期化基準にする
        Calendar current_cal = Calendar.getInstance(TimeZone.getDefault());

        // DB接続
        DatabaseOpenHelper helper = new DatabaseOpenHelper(this);
        SQLiteDatabase db = helper.getReadableDatabase();
        final List<TimeManager> list_objects = new ArrayList<TimeManager>();
        Cursor c = db.query(TimeManager.TABLE_NAME, new String[] { TimeManager.COLUMN_ID, TimeManager.COLUMN_START_TIME, TimeManager.COLUMN_MEMO,
                                                                   TimeManager.COLUMN_TIME_TYPE, TimeManager.COLUMN_TIME, TimeManager.COLUMN_REPEAT_WEEKLY,
                                                                   TimeManager.COLUMN_REPEAT_COUNT, TimeManager.COLUMN_SNOOZE, TimeManager.COLUMN_VOLUME,
                                                                   TimeManager.COLUMN_VIBRATOR_FLG, TimeManager.COLUMN_AUTO_SILENCE_TIME, TimeManager.COLUMN_USE_FLG, 
                                                                   TimeManager.COLUMN_REMAIN_REPEAT_COUNT, TimeManager.COLUMN_INVALID_SILENT_MODE_FLG, TimeManager.COLUMN_MEDIA_FILE},
                                                                   null, null, null, null, null);
        boolean isEof = c.moveToFirst();
        while (isEof) {
            TimeManager time_manager = new TimeManager();
            time_manager.setID(c.getInt(0));
            if(c.getString(1) == null){
                // アラームが未設定の場合は時間を表示
                time_manager.setStartTime(c.getString(4));
            } else {
                time_manager.setStartTime(c.getString(1));
            }
            time_manager.setMemo(c.getString(2));
            time_manager.setTimeType(c.getInt(3));
            time_manager.setTime(c.getString(4));
            time_manager.setRepeatWeekly(c.getString(5));
            time_manager.setRepeatCount(c.getInt(6));
            time_manager.setSnooze(c.getInt(7));
            time_manager.setVolume(c.getInt(8));
            time_manager.setVibratorFlg(c.getInt(9));
            time_manager.setAutoSilenceTime(c.getInt(10));
            time_manager.setUseFlg(c.getInt(11));
            time_manager.setRemainRepeatCount(c.getInt(12));
            time_manager.setInvalidSilentModeFlg(c.getInt(13));
            if(c.getString(14) != null){
                time_manager.setMediaFile(Uri.parse(c.getString(14)));
            }

            // start_timeが既に過ぎていたら再設定
            if(c.getString(1) != null && c.getString(1).equals("") == false){
                String start_time = c.getString(1);
                int year = Integer.parseInt(start_time.substring(0,4));
                int month = Integer.parseInt(start_time.substring(5,7));
                int day = Integer.parseInt(start_time.substring(8,10));
                int hour = Integer.parseInt(start_time.substring(11,13));
                int min = Integer.parseInt(start_time.substring(14,16));
                Calendar set_cal = Calendar.getInstance(TimeZone.getDefault());
                set_cal.set(year, month-1, day, hour, min);
                if(current_cal.compareTo(set_cal)>0){
                    if(time_manager.getTimeType() == TimeManager.TIME_TYPE_ALARM){
                        // アラームを設定
                        time_manager = TimeDataUtil.setAlarm(this, time_manager, false);
                    } else {
                        // タイマーを設定
                        time_manager = TimeDataUtil.setTimer(this, time_manager, false);
                    }
                }
            }
            list_objects.add(time_manager);
            isEof = c.moveToNext();
        }
        c.close();
        db.close();

        // 一覧データ作成
        listAdapater = new ListAdapter(this, MainActivity.this, 0, list_objects);
        final ListView listViewInput = (ListView)findViewById(R.id.mainListView);
        listViewInput.setAdapter(listAdapater);
        //アイテムがクリックされたときの処理
        listViewInput.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            // 修正画面へ
            @Override
            public void onItemClick(AdapterView<?>parent, View view, int click_position, long id){
                TimeManager item = list_objects.get(click_position);
                setUpdateData(item);
                setScreenContent(R.layout.regist);
            }
        });
        //アイテムがクリックされたときの処理
        listViewInput.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            Resources res = MainActivity.this.getResources();
            // 長押しで複数選択へ
            public boolean onItemLongClick(AdapterView<?>parent, View view, int click_position, long id){
                final TimeManager item = list_objects.get(click_position);
                String data_name = "";
                if(item.getTimeType() == TimeManager.TIME_TYPE_ALARM){
                    data_name = res.getString(R.string.alarm);
                } else {
                    data_name = res.getString(R.string.timer);
                }
                String data_use_name = "";
                if(item.getUseFlg() == 1){
                    data_use_name = res.getString(R.string.dialog_message_off);
                } else {
                    data_use_name = res.getString(R.string.dialog_message_on);
                }
                final String[] dialog_items = {data_name + res.getString(R.string.dialog_message_update),
                                               data_name + res.getString(R.string.dialog_message_delete),
                                               data_name + data_use_name};
                AlertDialog.Builder menu_builder = new AlertDialog.Builder(MainActivity.this);
                menu_builder.setTitle(item.getTime() + " " + item.getMemo());
                menu_builder.setIcon(android.R.drawable.ic_dialog_info);
                menu_builder.setItems(dialog_items, new DialogInterface.OnClickListener(){  
                    @Override  
                    public void onClick(DialogInterface dialog, int idx) {  
                        if(idx == 0){
                            // 変更
                            setUpdateData(item);
                            setScreenContent(R.layout.regist);
                        } else if(idx == 1){
                            // 削除
                            AlertDialog.Builder delete_conf_builder = new AlertDialog.Builder(MainActivity.this);
                            delete_conf_builder.setTitle(res.getString(R.string.dialog_message_delete_title));
                            delete_conf_builder.setMessage(res.getString(R.string.dialog_message_delete_conf));
                            delete_conf_builder.setIcon(android.R.drawable.ic_dialog_alert);
                            delete_conf_builder.setPositiveButton(res.getString(R.string.dialog_message_no),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // いいえが選択された場合何もしない。
                                    }
                                });
                            delete_conf_builder.setNegativeButton(res.getString(R.string.dialog_message_yes),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // 削除処理。物理削除を実行し、メイン画面を再表示。
                                        TimeManagerDao time_manager_dao = new TimeManagerDao(MainActivity.this);
                                        time_manager_dao.delete(item);
                                        setScreenContent(R.layout.main);
                                    }
                                });
                            // アラートダイアログのキャンセルが可能かどうかを設定します
                            delete_conf_builder.setCancelable(true);
                            delete_conf_builder.create().show();
                        } else if(idx == 2){
                            // アラームのON、OFF
                            if(item.getUseFlg() == 1){
                                // データ更新
                                TimeManagerDao time_manager_dao = new TimeManagerDao(MainActivity.this);
                                time_manager_dao.updateAlarmOff(item.getID());
                                // アラーム設定解除
                                Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
                                intent.setData(Uri.parse(Integer.toString(item.getID())));
                                PendingIntent sender = PendingIntent.getBroadcast(MainActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                                AlarmManager alarmManager = (AlarmManager)MainActivity.this.getSystemService(Context.ALARM_SERVICE);
                                alarmManager.cancel(sender);
                            } else {
                                // start_timeを更新
                                if(item.getTimeType() == TimeManager.TIME_TYPE_ALARM){
                                    // アラームを設定
                                    TimeDataUtil.setAlarm(MainActivity.this, item, true);
                                } else {
                                    // タイマーを設定
                                    TimeDataUtil.setTimer(MainActivity.this, item, true);
                                }
                            }
                            setScreenContent(R.layout.main);
                        }
                    }
                });
                menu_builder.setCancelable(true);
                menu_builder.create().show();
                return false;
            }
        });
        // 登録ボタン
        Button registButton = (Button) findViewById(R.id.regist);
        registButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                new_input_flg = true;
                setScreenContent(R.layout.regist);
            }
        });
    }

    // 登録画面
    private void setRegistScreenContent() {

        if(new_input_flg){
            // タイトル設定
            setTitle(R.string.title_regist);
            // データの初期化
            memo = "";
            hour = calendar.get(Calendar.HOUR_OF_DAY);
            minute = calendar.get(Calendar.MINUTE);
            timer_hour = 0;
            timer_minute = 0;
            alarm_repeat_list = new boolean[]{false, false, false, false, false, false, false};
            snooze = 0;
            time_type = 1;
            vibrator_flg = true;
            AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            volume = audio.getStreamVolume(AudioManager.STREAM_ALARM);
            media_uri = null;
            // アラーム音の取得
            media_uri = InputDataUtil.getDefaultAlarm(this);
        } else {
            // タイトル設定
            setTitle(R.string.title_regist);
        }

        // 時間設定画面
        final LinearLayout layout = (LinearLayout)findViewById(R.id.input_time_layout);
        if(new_input_flg){
            getLayoutInflater().inflate(R.layout.regist_alarm, layout);
            // アラート用時間設定FORMの設定
            setInputAlarmTab();
        } else {
            if(time_type == TimeManager.TIME_TYPE_ALARM){
                getLayoutInflater().inflate(R.layout.regist_alarm, layout);
                // アラーム用時間設定FORMの設定
                setInputAlarmTab();
            } else {
                getLayoutInflater().inflate(R.layout.regist_timer, layout);
                // タイマー用時間設定FORMの設定
                setInputTimerTab();
            }
        }

        // アラーム設定用ボタン
        Button inputAlarmButton = (Button)findViewById(R.id.tab_input_alarm_button);
        if(time_type == TimeManager.TIME_TYPE_ALARM){
            inputAlarmButton.setEnabled(false);
        }
        inputAlarmButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                time_type = 1;
                layout.removeAllViews();
                Button backInputAlarmButton = (Button) findViewById(R.id.tab_input_alarm_button);
                backInputAlarmButton.setEnabled(false);
                Button backInputTimerButton = (Button)findViewById(R.id.tab_input_timer_button);
                backInputTimerButton.setEnabled(true);
                getLayoutInflater().inflate(R.layout.regist_alarm, layout);
                // アラート用時間設定FORMの設定
                setInputAlarmTab();
            }
        });

        // タイマー設定用ボタン
        Button inputTimerButton = (Button)findViewById(R.id.tab_input_timer_button);
        if(time_type == TimeManager.TIME_TYPE_TIMER){
            inputTimerButton.setEnabled(false);
        }
        inputTimerButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                time_type = 2;
                layout.removeAllViews();
                Button backInputTimerButton = (Button)findViewById(R.id.tab_input_timer_button);
                backInputTimerButton.setEnabled(false);
                Button backInputAlarmButton = (Button) findViewById(R.id.tab_input_alarm_button);
                backInputAlarmButton.setEnabled(true);
                getLayoutInflater().inflate(R.layout.regist_timer, layout);
                // タイマー用時間設定FORMの設定
                setInputTimerTab();
            }
        });

        // メモ、スヌーズ、着信音、音量、マナーモード時、バイブレーターをリスト形式で入力
        final ListView listViewCommon = (ListView)findViewById(R.id.inputListCommon);
        final List<InputData> common_objects = new ArrayList<InputData>();

        // メモ
        InputData input_memo = InputDataUtil.getDisplayMemo(MainActivity.this, memo);
        common_objects.add(input_memo);
        // スヌーズ
        InputData input_snooze = InputDataUtil.getDisplaySnooze(MainActivity.this, snooze);
        common_objects.add(input_snooze);
        // 着信音
        InputData input_media = InputDataUtil.getDisplayMediaFile(MainActivity.this, media_uri);
        common_objects.add(input_media);
        // ボリューム
        InputData input_volume = InputDataUtil.getDisplayVolume(MainActivity.this, volume);
        common_objects.add(input_volume);
        // マナーモードで鳴らす
        InputData input_invalid_silent_mode_flg = InputDataUtil.getDisplayInvalidSilentMode(MainActivity.this, invalid_silent_mode_flg);
        common_objects.add(input_invalid_silent_mode_flg);
        // バイブレーション
        InputData input_vibrator_flg = InputDataUtil.getDisplayVibrator(MainActivity.this, vibrator_flg);
        common_objects.add(input_vibrator_flg);

        final InputCommonAdapter inputCommonAdapater = new InputCommonAdapter(MainActivity.this, 0, common_objects);
        // 初期ボリューム
        inputCommonAdapater.setVolume(volume);
        listViewCommon.setAdapter(inputCommonAdapater);

        //クリックされたときの処理
        listViewCommon.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?>parent, View view, int click_position, long id){
                final int position = click_position;
                if(position == 0){
                    // メモ選択時
                    final EditText editView = new EditText(MainActivity.this);
                    editView.setText(memo);
                    AlertDialog.Builder input_memo_builder = new AlertDialog.Builder(MainActivity.this);
                    input_memo_builder.setTitle(getString(R.string.memo));
                    input_memo_builder.setView(editView);
                    input_memo_builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            InputData old_item = (InputData)listViewCommon.getItemAtPosition(position);
                            inputCommonAdapater.remove(old_item);
                            memo = editView.getText().toString();
                            InputData memo_data = InputDataUtil.getDisplayMemo(MainActivity.this, memo);
                            inputCommonAdapater.insert(memo_data, position);
                            inputCommonAdapater.notifyDataSetChanged();
                            dialog.cancel();
                        }
                    });
                    input_memo_builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.cancel();
                        }
                    });
                    final AlertDialog alertDialog=input_memo_builder.create();
                    editView.setOnFocusChangeListener(new OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            if (hasFocus) {
                                alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                            }
                        }
                    });
                    alertDialog.show();
                };
                if(position == 1){
                    // スヌーズ選択時
                    AlertDialog.Builder input_snooze_builder = new AlertDialog.Builder(MainActivity.this);
                    input_snooze_builder.setTitle(getString(R.string.snooze));
                    final CharSequence[] snoozeItems = InputDataUtil.getDefaultDataSnooze(MainActivity.this);
                    input_snooze_builder.setSingleChoiceItems(
                        snoozeItems,
                        InputDataUtil.convSnoozeID(snooze),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int input_snooze) {
                                snooze = InputDataUtil.convSnooze(input_snooze);
                                InputData old_item = (InputData)listViewCommon.getItemAtPosition(position);
                                inputCommonAdapater.remove(old_item);
                                InputData snooze_data = InputDataUtil.getDisplaySnooze(MainActivity.this, snooze);
                                inputCommonAdapater.insert(snooze_data, position);
                                inputCommonAdapater.notifyDataSetChanged();
                                dialog.cancel();
                            }
                    });
                    input_snooze_builder.setPositiveButton(
                        getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }
                    );
                    input_snooze_builder.create().show();
                };
                if(position == 2){
                    // メディアファイル選択時
                    AlertDialog.Builder input_media_builder = new AlertDialog.Builder(MainActivity.this);
                    input_media_builder.setTitle(getString(R.string.media_file));
                    CharSequence[] mediaItems = InputDataUtil.getMediaFileList(MainActivity.this);
                    input_media_builder.setSingleChoiceItems(
                        mediaItems,
                        InputDataUtil.convMediaID(MainActivity.this, media_uri),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int input_media) {
                                media_uri = InputDataUtil.convMediaUri(MainActivity.this, input_media);
                                InputData old_item = (InputData)listViewCommon.getItemAtPosition(position);
                                inputCommonAdapater.remove(old_item);
                                InputData media_data = InputDataUtil.getDisplayMediaFile(MainActivity.this, media_uri);
                                inputCommonAdapater.insert(media_data, position);
                                inputCommonAdapater.notifyDataSetChanged();
                                dialog.cancel();
                            }
                        }
                    );
                    input_media_builder.setPositiveButton(
                        getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }
                    );
                    input_media_builder.create().show();
                };
            }
        });

        // 登録ボタン
        Button registButton = (Button)findViewById(R.id.regist);
        registButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // 入力データの取得
                volume = inputCommonAdapater.getVolume();
                invalid_silent_mode_flg = inputCommonAdapater.getInvalidSilentModeFlg();
                vibrator_flg = inputCommonAdapater.getVibratorFlg();
                
                TimeManager time_manager = new TimeManager();
                time_manager.setID(time_manager_id);
                time_manager.setMemo(memo);
                time_manager.setTimeType(time_type);

                if(time_type == TimeManager.TIME_TYPE_ALARM){
                    time_manager.setRepeatWeekly(InputDataUtil.conversionAlarmRepeatForDB(alarm_repeat_list));
                    time_manager.setTime(String.format("%1$02d", hour) + ':' + String.format("%1$02d", minute));
                } else {
                    time_manager.setRepeatCount(timer_repeat);
                    time_manager.setTime(String.format("%1$02d", timer_hour) + ':' + String.format("%1$02d", timer_minute));
                }
                time_manager.setSnooze(snooze);
                time_manager.setMediaFile(media_uri);
                time_manager.setVolume(volume);
                if(vibrator_flg){
                    time_manager.setVibratorFlg(1);
                } else {
                    time_manager.setVibratorFlg(0);
                }
                if(invalid_silent_mode_flg){
                    time_manager.setInvalidSilentModeFlg(1);
                } else {
                    time_manager.setInvalidSilentModeFlg(0);
                }

//                time_manager.setAutoSilenceTime(auto_silence_time);
//                time_manager.setHolidayFlg(holiday_flg);
                TimeManagerDao time_manager_dao = new TimeManagerDao(MainActivity.this);
                if(new_input_flg){
                    time_manager = time_manager_dao.insert(time_manager);
                } else {
                    time_manager_dao.update(time_manager);
                }
                // アラーム設定
                if(time_type == TimeManager.TIME_TYPE_ALARM){
                    TimeDataUtil.setAlarm(MainActivity.this, time_manager, true);
                } else {
                    TimeDataUtil.setTimer(MainActivity.this, time_manager, true);
                }
                setScreenContent(R.layout.main);
            }
        });
        
        // トップへの戻るボタン
        Button backMainButton = (Button) findViewById(R.id.back_main);
        backMainButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                setScreenContent(R.layout.main);
            }
        });
    }

    // アラーム設定
    private void setInputAlarmTab() {
        final List<InputData> input_objects;
        final CharSequence[] repeatItems = InputDataUtil.getDefaultDataRepeat(MainActivity.this); // 繰り返し
        input_objects = new ArrayList<InputData>();
        InputData input_time = InputDataUtil.getDisplayTime(MainActivity.this, hour, minute);
        input_objects.add(input_time);
        InputData input_repeat = InputDataUtil.getDisplayRepeatWeekly(MainActivity.this, alarm_repeat_list, repeatItems);
        input_objects.add(input_repeat);

        final InputAdapter inputAdapater = new InputAdapter(MainActivity.this, 0, input_objects);
        final ListView listViewInput = (ListView)findViewById(R.id.inputListView);
        listViewInput.setAdapter(inputAdapater);
        //アイテムがクリックされたときの処理
        listViewInput.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?>parent, View view, int click_position, long id){
                final int position = click_position;
                if(position == 0){
                    // 時間入力のダイアログを表示
                    TimePickerDialog.OnTimeSetListener timeSetListener = new OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int input_hour, int input_minute) {
                            hour = input_hour;
                            minute = input_minute;
                            InputData time_data = InputDataUtil.getDisplayTime(MainActivity.this, hour, minute);
                            InputData old_item = (InputData)listViewInput.getItemAtPosition(position);
                            inputAdapater.remove(old_item);
                            inputAdapater.insert(time_data, position);
                            inputAdapater.notifyDataSetChanged();
                        }
                    };
                    TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this, timeSetListener, hour, minute, true);
                    timePickerDialog.setTitle(R.string.dialog_title_alarm);
                    timePickerDialog.show(); 
                };
                if(position == 1){
                    // 繰り返し表示用のダイアログ
                    AlertDialog.Builder input_repaet_builder = new AlertDialog.Builder(MainActivity.this);
                    input_repaet_builder.setTitle(getString(R.string.repeat));
                    input_repaet_builder.setMultiChoiceItems(
                        repeatItems,
                        alarm_repeat_list,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            public void onClick(DialogInterface dialog, int which, boolean flag) {
                                alarm_repeat_list[which] = flag;
                            }
                        }
                    );
                    input_repaet_builder.setPositiveButton(
                        "OK", 
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                InputData old_item = (InputData)listViewInput.getItemAtPosition(position);
                                inputAdapater.remove(old_item);
                                InputData repeat_data = InputDataUtil.getDisplayRepeatWeekly(MainActivity.this, alarm_repeat_list, repeatItems);
                                inputAdapater.insert(repeat_data, position);
                                inputAdapater.notifyDataSetChanged();
                            }
                        }
                    );
                    // 表示
                    input_repaet_builder.create().show();
                };
            };
        });
    }

    // タイマー設定
    private void setInputTimerTab() {
        final List<InputData> input_objects;
        final CharSequence[] repeatItems = InputDataUtil.getDefaultDataTimerRepeat(MainActivity.this);
        input_objects = new ArrayList<InputData>();
        InputData input_time = InputDataUtil.getDisplayTime(MainActivity.this, timer_hour, timer_minute);
        input_objects.add(input_time);
        InputData input_repeat = InputDataUtil.getDisplayTimerRepeat(MainActivity.this, timer_repeat);
        input_objects.add(input_repeat);

        final InputAdapter inputAdapater = new InputAdapter(MainActivity.this, 0, input_objects);
        final ListView listViewInput = (ListView)findViewById(R.id.inputListView);
        listViewInput.setAdapter(inputAdapater);
        //アイテムがクリックされたときの処理
        listViewInput.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?>parent, View view, int click_position, long id){
                final int position = click_position;
                if(position == 0){
                    // 時間入力のダイアログを表示
                    TimePickerDialog.OnTimeSetListener timeSetListener = new OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int input_hour, int input_minute) {
                            timer_hour = input_hour;
                            timer_minute = input_minute;
                            InputData time_data = InputDataUtil.getDisplayTime(MainActivity.this, timer_hour, timer_minute);
                            InputData old_item = (InputData)listViewInput.getItemAtPosition(position);
                            inputAdapater.remove(old_item);
                            inputAdapater.insert(time_data, position);
                            inputAdapater.notifyDataSetChanged();
                        }
                    };
                    TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this, timeSetListener, timer_hour, timer_minute, true);
                    timePickerDialog.setTitle(R.string.dialog_title_timer);
                    timePickerDialog.show(); 
                };
                if(position == 1){
                    // 繰り返しのダイアログ
                    AlertDialog.Builder input_repeat_builder = new AlertDialog.Builder(MainActivity.this);
                    input_repeat_builder.setTitle(getString(R.string.repeat));
                    input_repeat_builder.setSingleChoiceItems(
                        repeatItems,
                        InputDataUtil.convRepeatCountID(timer_repeat),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int input_repeat) {
                                timer_repeat = InputDataUtil.convRepeatCount(input_repeat);
                                InputData old_item = (InputData)listViewInput.getItemAtPosition(position);
                                inputAdapater.remove(old_item);
                                InputData repeat_data = InputDataUtil.getDisplayTimerRepeat(MainActivity.this, timer_repeat);
                                inputAdapater.insert(repeat_data, position);
                                inputAdapater.notifyDataSetChanged();
                                dialog.cancel();
                            }
                    });
                    input_repeat_builder.setPositiveButton(
                        getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }
                    );
                    input_repeat_builder.create().show();
                };
            };
        });
    }


    // 更新用にデータを設定
    // 一覧画面でデータをクリック、長押しでの編集選択
    private void setUpdateData(TimeManager item) {
        time_manager_id = item.getID();
        memo = item.getMemo();
        time_type = item.getTimeType();
        if(time_type == TimeManager.TIME_TYPE_ALARM){
            String[] time = item.getTime().split(":", 0);
            hour = Integer.parseInt(time[0]);
            minute = Integer.parseInt(time[1]);
            alarm_repeat_list = InputDataUtil.conversionAlarmRepeat(item.getRepeatWeekly());
        } else {
            String[] time = item.getTime().split(":", 0);
            timer_hour = Integer.parseInt(time[0]);
            timer_minute = Integer.parseInt(time[1]);
            timer_repeat = item.getRepeatCount();
        }
        snooze = item.getSnooze();
        media_uri = item.getMediaFile();
        volume = item.getVolume();
        if(item.getVibratorFlg() == 1){
            vibrator_flg = true;
        } else {
            vibrator_flg = false;
        }
        if(item.getInvalidSilentModeFlg() == 1){
            invalid_silent_mode_flg = true;
        } else {
            invalid_silent_mode_flg = false;
        }
        new_input_flg = false;
    }
}