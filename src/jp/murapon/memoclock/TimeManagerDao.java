package jp.murapon.memoclock;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * TimeManager用データアクセスクラス
 */
public class TimeManagerDao {
    
    private DatabaseOpenHelper helper = null;
    
    public TimeManagerDao(Context context) {
        helper = new DatabaseOpenHelper(context);
    }

    /**
     * TimeManagerの登録
     * @param timeManager 保存対象のオブジェクト
     * @return void
     */
    public TimeManager getTime(String id){
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.rawQuery("select " + TimeManager.COLUMN_TIME_TYPE + "," + TimeManager.COLUMN_TIME + "," +
                                           TimeManager.COLUMN_REPEAT_WEEKLY + "," + TimeManager.COLUMN_REPEAT_COUNT + "," +
                                           TimeManager.COLUMN_SNOOZE+ "," + TimeManager.COLUMN_VOLUME + "," +
                                           TimeManager.COLUMN_MEDIA_FILE + "," + TimeManager.COLUMN_VIBRATOR_FLG + "," +
                                           TimeManager.COLUMN_AUTO_SILENCE_TIME + "," + TimeManager.COLUMN_USE_FLG + "," +
                                           TimeManager.COLUMN_REMAIN_REPEAT_COUNT + "," + TimeManager.COLUMN_INVALID_SILENT_MODE_FLG + "," +
                                           TimeManager.COLUMN_HOLIDAY_FLG +
                               " from " + TimeManager.TABLE_NAME + " where id = ?", new String[] { id });

        TimeManager time_manager = new TimeManager();
        if (c.moveToFirst()){
            time_manager.setID(Integer.parseInt(id));
            time_manager.setTimeType(c.getInt(0));
            time_manager.setTime(c.getString(1));
            time_manager.setRepeatWeekly(c.getString(2));
            time_manager.setRepeatCount(c.getInt(3));
            time_manager.setSnooze(c.getInt(4));
            time_manager.setVolume(c.getInt(5));
            time_manager.setMediaFile(Uri.parse(c.getString(6)));
            time_manager.setVibratorFlg(c.getInt(7));
            time_manager.setAutoSilenceTime(c.getInt(8));
            time_manager.setUseFlg(c.getInt(9));
            time_manager.setRemainRepeatCount(c.getInt(10));
            time_manager.setInvalidSilentModeFlg(c.getInt(11));
            time_manager.setHolidayFlg(c.getInt(12));
        }
        return time_manager;
    }

    /**
     * TimeManagerの登録
     * @param timeManager 保存対象のオブジェクト
     * @return void
     */
    public TimeManager insert(TimeManager timeManager){
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put( TimeManager.COLUMN_START_TIME, timeManager.getStartTime());
            values.put( TimeManager.COLUMN_MEMO, timeManager.getMemo());
            values.put( TimeManager.COLUMN_TIME_TYPE, timeManager.getTimeType());
            values.put( TimeManager.COLUMN_TIME, timeManager.getTime());
            values.put( TimeManager.COLUMN_REPEAT_WEEKLY, timeManager.getRepeatWeekly());
            values.put( TimeManager.COLUMN_REPEAT_COUNT, timeManager.getRepeatCount());
            values.put( TimeManager.COLUMN_SNOOZE, timeManager.getSnooze());
            values.put( TimeManager.COLUMN_VOLUME, timeManager.getVolume());
            values.put( TimeManager.COLUMN_VIBRATOR_FLG, timeManager.getVibratorFlg());
            values.put( TimeManager.COLUMN_INVALID_SILENT_MODE_FLG, timeManager.getInvalidSilentModeFlg());
            values.put( TimeManager.COLUMN_MEDIA_FILE, timeManager.getMediaFile().toString());
            db.insert("time_manager", "", values);

            Cursor c = db.rawQuery("select id from " + TimeManager.TABLE_NAME + " where ROWID = last_insert_rowid()", new String[] {});
            if (c.moveToFirst()){
                timeManager.setID(c.getInt(0));
            }
        } finally {
            db.close();
        }
        return timeManager;
    }

    /**
     * TimeManagerの更新
     * @param timeManager 保存対象のオブジェクト
     * @return void
     */
    public void update(TimeManager timeManager){
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put( TimeManager.COLUMN_MEMO, timeManager.getMemo());
            values.put( TimeManager.COLUMN_TIME_TYPE, timeManager.getTimeType());
            values.put( TimeManager.COLUMN_TIME, timeManager.getTime());
            values.put( TimeManager.COLUMN_REPEAT_WEEKLY, timeManager.getRepeatWeekly());
            values.put( TimeManager.COLUMN_REPEAT_COUNT, timeManager.getRepeatCount());
            values.put( TimeManager.COLUMN_SNOOZE, timeManager.getSnooze());
            values.put( TimeManager.COLUMN_VOLUME, timeManager.getVolume());
            values.put( TimeManager.COLUMN_VIBRATOR_FLG, timeManager.getVibratorFlg());
            values.put( TimeManager.COLUMN_INVALID_SILENT_MODE_FLG, timeManager.getInvalidSilentModeFlg());
            values.put( TimeManager.COLUMN_MEDIA_FILE, timeManager.getMediaFile().toString());
            db.update("time_manager", values, "id = '" + timeManager.getID() + "'", null);
        } finally {
            db.close();
        }
    }

    /**
     * TimeManagerの更新
     * @param timeManager 保存対象のオブジェクト
     * @return void
     */
    public void updateAlarmOn(int id, String start_time){
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put( TimeManager.COLUMN_START_TIME, start_time);
            values.put( TimeManager.COLUMN_USE_FLG, "1");
            db.update("time_manager", values, "id = '" + id + "'", null);
        } finally {
            db.close();
        }
    }

    /**
     * TimeManagerの更新
     * @param timeManager 保存対象のオブジェクト
     * @return void
     */
    public void updateAlarmOff(int id){
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put( TimeManager.COLUMN_START_TIME, "");
            values.put( TimeManager.COLUMN_USE_FLG, "0");
            values.put( TimeManager.COLUMN_REMAIN_REPEAT_COUNT, "0");
            db.update("time_manager", values, "id = '" + id + "'", null);
        } finally {
            db.close();
        }
    }

    /**
     * TimeManagerの更新
     * @param timeManager 保存対象のオブジェクト
     * @return void
     */
    public void updateTimer(int id, String start_time, int use_flg, int remain_repeat_count){
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put( TimeManager.COLUMN_START_TIME, start_time);
            values.put( TimeManager.COLUMN_USE_FLG, use_flg);
            values.put( TimeManager.COLUMN_REMAIN_REPEAT_COUNT, remain_repeat_count);
            db.update("time_manager", values, "id = '" + id + "'", null);
        } finally {
            db.close();
        }
    }

    /**
     * TimeManagerの削除
     * @param timeManager 保存対象のオブジェクト
     * @return void
     */
    public void delete(TimeManager timeManager){
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            db.delete("time_manager", "id = '" + timeManager.getID() + "'", null);
        } finally {
            db.close();
        }
    }
}