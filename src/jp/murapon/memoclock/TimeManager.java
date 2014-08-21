package jp.murapon.memoclock;

import java.io.Serializable;
import android.net.Uri;

/**
 * 1レコードのデータを保持するオブジェクト
 * Intentに詰めてやり取りするのでSerializableをimplementsする
 */
public class TimeManager implements Serializable{
    // テーブル名
    public static final String TABLE_NAME = "time_manager";
    
    // カラム名
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_START_TIME = "start_time";
    public static final String COLUMN_MEMO = "memo";
    public static final String COLUMN_TIME_TYPE = "time_type";
    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_REPEAT_WEEKLY = "repeat_weekly";
    public static final String COLUMN_REPEAT_COUNT = "repeat_count";
    public static final String COLUMN_SNOOZE = "snooze";
    public static final String COLUMN_VOLUME = "volume";
    public static final String COLUMN_MEDIA_FILE = "media_file";
    public static final String COLUMN_VIBRATOR_FLG = "vibrator_flg";
    public static final String COLUMN_AUTO_SILENCE_TIME = "auto_silence_time";
    public static final String COLUMN_USE_FLG = "use_flg";
    public static final String COLUMN_REMAIN_REPEAT_COUNT = "remain_repeat_count";
    public static final String COLUMN_INVALID_SILENT_MODE_FLG = "invalid_silent_mode_flg";
    public static final String COLUMN_HOLIDAY_FLG = "holiday_flg";
    public static final String COLUMN_AUTO_DEL_FLG = "auto_del_flg";

    // プロパティ
    private int id = 0;
    private String start_time = null;
    private String memo = null;
    private int time_type = 0;
    private String time = null;
    private String repeat_weekly = null;
    private int repeat_count = 0;
    private int snooze = 0;
    private int volume = 0;
    private Uri media_file = null;
    private int vibrator_flg = 0;
    private int auto_silence_time = 0;
    private int use_flg = 0;
    private int remain_repeat_count = 0;
    private int invalid_silent_mode_flg = 0;
    private int holiday_flg = 0;
    private int auto_del_flg = 0;

    // 設定値
    public static final int TIME_TYPE_ALARM = 1;
    public static final int TIME_TYPE_TIMER = 2;

    /**
     * IDの設定
     * @return void
     */
    public void setID(int temp_id) {
        id = temp_id;
    }
    /**
     * IDの取得
     * @return Long
     */
    public int getID() {
        return id;
    }

    /**
     * 開始時刻の設定
     * @return void
     */
    public void setStartTime(String temp_start_time) {
        start_time = temp_start_time;
    }
    /**
     * 開始時刻の取得
     * @return String
     */
    public String getStartTime() {
        return start_time;
    }

    /**
     * お知らせの設定
     * @return void
     */
    public void setMemo(String temp_memo) {
        memo = temp_memo;
    }
    /**
     * お知らせの取得
     * @return String
     */
    public String getMemo() {
        return memo;
    }

    /**
     * 時間タイプの設定
     * @return void
     */
    public void setTimeType(int temp_time_type) {
        time_type = temp_time_type;
    }
    /**
     * 時間タイプの取得
     * @return int
     */
    public int getTimeType() {
        return time_type;
    }

    /**
     * 設定時間の設定
     * @return void
     */
    public void setTime(String temp_time) {
        time = temp_time;
    }
    /**
     * 設定時間の取得
     * @return String
     */
    public String getTime() {
        return time;
    }

    /**
     * 毎週繰り返す際の設定
     * @return void
     */
    public void setRepeatWeekly(String temp_repeat_weekly) {
        repeat_weekly = temp_repeat_weekly;
    }
    /**
     * 繰り返しタイプの取得
     * @return String
     */
    public String getRepeatWeekly() {
        return repeat_weekly;
    }

    /**
     * 繰り返し回数の設定
     * @return void
     */
    public void setRepeatCount(int temp_repeat_count) {
        repeat_count = temp_repeat_count;
    }
    /**
     * 繰り返し回数の取得
     * @return int
     */
    public int getRepeatCount() {
        return repeat_count;
    }

    /**
     * スヌーズの設定
     * @return void
     */
    public void setSnooze(int temp_snooze) {
        snooze = temp_snooze;
    }
    /**
     * スヌーズの取得
     * @return String
     */
    public int getSnooze() {
        return snooze;
    }

    /**
     * 音量の設定
     * @return void
     */
    public void setVolume(int temp_volume) {
        volume = temp_volume;
    }
    /**
     * 音量の取得
     * @return int
     */
    public int getVolume() {
        return volume;
    }

    /**
     * 音源ファイルの設定
     * @return void
     */
    public void setMediaFile(Uri temp_media_file) {
        media_file = temp_media_file;
    }
    /**
     * 音源ファイルの取得
     * @return String
     */
    public Uri getMediaFile() {
        return media_file;
    }

    /**
     * バイブOnOffの設定
     * @return void
     */
    public void setVibratorFlg(int temp_vibrator_flg) {
        vibrator_flg = temp_vibrator_flg;
    }
    /**
     * バイブOnOffの取得
     * @return String
     */
    public int getVibratorFlg() {
        return vibrator_flg;
    }

    /**
     * 自動消音時間の設定
     * @return void
     */
    public void setAutoSilenceTime(int temp_auto_silence_time) {
        auto_silence_time = temp_auto_silence_time;
    }
    /**
     * 自動消音時間の取得
     * @return int
     */
    public int getAutoSilenceTime() {
        return auto_silence_time;
    }

    /**
     * 利用しているかどうかの設定
     * @return void
     */
    public void setUseFlg(int temp_use_flg) {
        use_flg = temp_use_flg;
    }
    /**
     * 利用しているかどうかの取得
     * @return int
     */
    public int getUseFlg() {
        return use_flg;
    }

    /**
     * 残りの繰り返し回数の設定
     * @return void
     */
    public void setRemainRepeatCount(int temp_remain_repeat_count) {
        remain_repeat_count = temp_remain_repeat_count;
    }
    /**
     * 残りの繰り返し回数の取得
     * @return int
     */
    public int getRemainRepeatCount() {
        return remain_repeat_count;
    }

    /**
     * マナーモードを無視するかどうかの設定
     * @return void
     */
    public void setInvalidSilentModeFlg(int temp_invalid_silent_mode_flg) {
        invalid_silent_mode_flg = temp_invalid_silent_mode_flg;
    }
    /**
     * マナーモードを無視するかどうかの取得
     * @return int
     */
    public int getInvalidSilentModeFlg() {
        return invalid_silent_mode_flg;
    }

    /**
     * 休日利用の設定
     * @return void
     */
    public void setHolidayFlg(int temp_holiday_flg) {
        holiday_flg = temp_holiday_flg;
    }
    /**
     * 休日利用の取得
     * @return int
     */
    public int getHolidayFlg() {
        return holiday_flg;
    }
    
    /**
     * 設定したタスクの自動削除
     * @return void
     */
    public void setAutoDelFlg(int temp_auto_del_flg) {
        auto_del_flg = temp_auto_del_flg;
    }
    /**
     * 設定したタスクの自動削除の取得
     * @return int
     */
    public int getAutoDelFlg() {
        return auto_del_flg;
    }
}
