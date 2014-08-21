package jp.murapon.memoclock;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
 
public class DatabaseOpenHelper extends SQLiteOpenHelper {
    final static private int DB_VERSION = 1;
    final static private String DB_NAME = "timemanager.db";

    public DatabaseOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }
 
    @Override
    public void onCreate(SQLiteDatabase db) {
        // table create
        db.execSQL(
            "CREATE TABLE time_manager("+
            "   id INTEGER PRIMARY KEY,"+
            "   start_time TEXT,"+
            "   memo TEXT,"+
            "   time_type INTEGER,"+
            "   time TEXT,"+
            "   repeat_weekly TEXT,"+
            "   repeat_count INTEGER,"+
            "   snooze INTEGER,"+
            "   volume INTEGER,"+
            "   media_file TEXT,"+
            "   vibrator_flg INTEGER,"+
            "   auto_silence_time INTEGER,"+
            "   use_flg INTEGER,"+
            "   remain_repeat_count INTEGER,"+
            "   invalid_silent_mode_flg INTEGER, "+
            "   auto_del_flg INTEGER, "+
            "   holiday_flg INTEGER"+
            ");"
        );
//        // table row insert
//        db.execSQL("insert into time_manager(start_time,memo) values ('2012-12-12 00:00:00', 'memomemo');");
    }
 
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // データベースの変更が生じた場合は、ここに処理を記述する。
    }
}