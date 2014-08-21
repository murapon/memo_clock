package jp.murapon.memoclock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class AlarmReceiver extends BroadcastReceiver {
  
    @Override  
    public void onReceive(Context context, Intent intent) {

        String id = intent.getStringExtra("id");
        String memo = "";
        String time = null;
        int snooze = 0;
        int volume = 0;
        String media_file = null;
        int vibrator_flg = 0;
        int auto_silence_time = 0;
        int invalid_silent_mode_flg = 0;

        // データ取得
        DatabaseOpenHelper helper = new DatabaseOpenHelper(context);
        SQLiteDatabase db = helper.getReadableDatabase();
        try {
            Cursor cursor = db.query(TimeManager.TABLE_NAME,
                                     new String[] { TimeManager.COLUMN_MEMO, TimeManager.COLUMN_TIME,
                                                    TimeManager.COLUMN_SNOOZE, TimeManager.COLUMN_VOLUME,
                                                    TimeManager.COLUMN_VIBRATOR_FLG, TimeManager.COLUMN_INVALID_SILENT_MODE_FLG,
                                                    TimeManager.COLUMN_MEDIA_FILE},
                                     TimeManager.COLUMN_ID + "=?", new String[] { id }, null, null, null);
            cursor.moveToFirst();
            memo = cursor.getString(0);
            time = cursor.getString(1);
            snooze = cursor.getInt(2);
            volume = cursor.getInt(3);
            vibrator_flg = cursor.getInt(4);
            invalid_silent_mode_flg = cursor.getInt(5);
            media_file = cursor.getString(6);
        } finally {
            db.close();
        }

        // アラーム画面起動
        Intent alarm_intent = new Intent(context, AlarmActivity.class);
        alarm_intent.putExtra("id", id);
        alarm_intent.putExtra("memo", memo);
        alarm_intent.putExtra("time", time);
        alarm_intent.putExtra("snooze", snooze);
        alarm_intent.putExtra("vibrator_flg", vibrator_flg);
        alarm_intent.putExtra("volume", volume);
        alarm_intent.putExtra("invalid_silent_mode_flg", invalid_silent_mode_flg);
        alarm_intent.putExtra("media_file", media_file);
        alarm_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(alarm_intent);
    }
}