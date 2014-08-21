package jp.murapon.memoclock;

import android.content.res.Resources;
import android.media.RingtoneManager;
import android.media.Ringtone;
import android.net.Uri;
import android.database.Cursor;

public class InputDataUtil {

    static public CharSequence[] getDefaultDataRepeat(MainActivity content){
        Resources res = content.getResources();
        CharSequence[] chkItems = {res.getString(R.string.monday),
                                   res.getString(R.string.tuesday),
                                   res.getString(R.string.wednesday),
                                   res.getString(R.string.thursday),
                                   res.getString(R.string.friday),
                                   res.getString(R.string.saturday),
                                   res.getString(R.string.sunday)};
        return chkItems;
    }

    static public CharSequence[] getDefaultDataSnooze(MainActivity content){
        Resources res = content.getResources();
        CharSequence[] chkItems = {res.getString(R.string.snooze_unset),
                                   res.getString(R.string.snooze_5minute),
                                   res.getString(R.string.snooze_10minute),
                                   res.getString(R.string.snooze_15minute),
                                   res.getString(R.string.snooze_20minute),
                                   res.getString(R.string.snooze_25minute),
                                   res.getString(R.string.snooze_30minute)};
        return chkItems;
    }

    static public CharSequence[] getDefaultDataTimerRepeat(MainActivity content){
        Resources res = content.getResources();
        CharSequence[] chkItems = {res.getString(R.string.timer_repeat_unset),
                                   res.getString(R.string.timer_repeat_2),
                                   res.getString(R.string.timer_repeat_3),
                                   res.getString(R.string.timer_repeat_4),
                                   res.getString(R.string.timer_repeat_5),
                                   res.getString(R.string.timer_repeat_10),
                                   res.getString(R.string.timer_repeat_unlimit)};
        return chkItems;
    }

    static public CharSequence[] getMediaFileList(MainActivity content){
        RingtoneManager ringtoneManager = new RingtoneManager(content);
        Cursor cursor = ringtoneManager.getCursor();
        CharSequence[] chkItems = new CharSequence[cursor.getCount()+1];
        chkItems[0] = "Default";
        int i=1;
        while (cursor.moveToNext()) {
            chkItems[i] = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX);
            i++;
        }
        return chkItems;
    }

    static public InputData getDisplayMemo(MainActivity content, String memo){
        Resources res = content.getResources();
        InputData input_data = new InputData();
        input_data.setColumName(res.getString(R.string.memo));
        input_data.setInputData(memo);
        return input_data;
    }

    static public InputData getDisplayTime(MainActivity content, int hour, int minute){
        Resources res = content.getResources();
        InputData input_data = new InputData();
        input_data.setColumName(res.getString(R.string.time));
        input_data.setInputData(hour + ":" + String.format("%1$02d", minute));
        return input_data;
    }
    static public InputData getDisplayRepeatWeekly(MainActivity content, boolean[] chkSts, CharSequence[] chkItems){
        Resources res = content.getResources();
        InputData input_data = new InputData();
        input_data.setColumName(res.getString(R.string.repeat));
        String display_repeat_week = "";
        boolean repeat_flg = false;
        for (int i = 0; i < chkSts.length; i++){
            if(chkSts[i] == true){
                display_repeat_week = display_repeat_week + chkItems[i];
                repeat_flg = true;
            }
        }
        if(repeat_flg == false){
            display_repeat_week = res.getString(R.string.repeat_unset);
        }
        input_data.setInputData(display_repeat_week);
        return input_data;
    }
    static public String getDisplayRepeatWeeklyForList(MainActivity content, String repeat_weekly){

        String[] repeat_weekly_list = repeat_weekly.split(",", 0);
        CharSequence[] weekly = getDefaultDataRepeat(content);
        String display_repeat = "";
        for (int i = 0; i < repeat_weekly_list.length; i++){
            if(repeat_weekly_list[i].equals("1")){
                display_repeat += weekly[i];
            }
        }
        if(display_repeat.equals("") == false){
            return "[" + display_repeat + "]";
        } else {
            return "";
        }
    }
    static public String getDisplayRepeatCount(MainActivity content, int repeat_count, int remain_repeat_count){
        if(repeat_count > 0){
            return "[" + remain_repeat_count + "/" + repeat_count + "]";
        } else {
            return "";
        }
    }

    static public InputData getDisplaySnooze(MainActivity content, int snooze){
        Resources res = content.getResources();
        InputData input_data = new InputData();
        input_data.setColumName(res.getString(R.string.snooze));
        String display_snooze = "";
        switch(snooze){
            case 0: display_snooze = res.getString(R.string.snooze_unset);
                    break;
            case 5: display_snooze = res.getString(R.string.snooze_5minute);
                    break;
            case 10: display_snooze = res.getString(R.string.snooze_10minute);
                    break;
            case 15: display_snooze = res.getString(R.string.snooze_15minute);
                    break;
            case 20: display_snooze = res.getString(R.string.snooze_20minute);
                    break;
            case 25: display_snooze = res.getString(R.string.snooze_25minute);
                    break;
            case 30: display_snooze = res.getString(R.string.snooze_30minute);
                    break;
        }
        input_data.setInputData(display_snooze);
        return input_data;
    }

    // selectboxの選択値から、実数へ変換
    static public int convSnooze(int snooze_id){
        int snooze = 0;
        switch(snooze_id){
            case 0: snooze = 0;
                    break;
            case 1: snooze = 5;
                    break;
            case 2: snooze = 10;
                    break;
            case 3: snooze = 15;
                    break;
            case 4: snooze = 20;
                    break;
            case 5: snooze = 25;
                    break;
            case 6: snooze = 30;
                    break;
        }
        return snooze;
    }
    // 実数からselectboxのIDへ変換
    static public int convSnoozeID(int snooze){
        int snooze_id = 0;
        switch(snooze){
            case 0: snooze_id = 0;
                    break;
            case 5: snooze_id = 1;
                    break;
            case 10: snooze_id = 2;
                    break;
            case 15: snooze_id = 3;
                    break;
            case 20: snooze_id = 4;
                    break;
            case 25: snooze_id = 5;
                    break;
            case 30: snooze_id = 6;
                    break;
        }
        return snooze_id;
    }

    static public Uri getDefaultAlarm(MainActivity content){
        // アラーム音の取得
        Uri media_uri = RingtoneManager.getActualDefaultRingtoneUri(content, RingtoneManager.TYPE_ALARM);
        if (media_uri == null) {
            // アラーム音が無ければ通知音を使う
            media_uri = RingtoneManager.getActualDefaultRingtoneUri(content, RingtoneManager.TYPE_NOTIFICATION);
            if (media_uri == null) {
                // 通知音も無ければ着信音を使う
                media_uri = RingtoneManager.getActualDefaultRingtoneUri(content, RingtoneManager.TYPE_RINGTONE);
            }
        }
        return media_uri;
    }

    static public InputData getDisplayMediaFile(MainActivity content, Uri media_uri){
        Resources res = content.getResources();
        InputData input_data = new InputData();
        input_data.setColumName(res.getString(R.string.media_file));
        RingtoneManager ringtoneManager = new RingtoneManager(content);
        Ringtone ringtone = ringtoneManager.getRingtone(content, media_uri);
        String media_file = ringtone.getTitle(content);
        input_data.setInputData(media_file);
        return input_data;
    }

    static public Uri convMediaUri(MainActivity content, int media_id){
        media_id = media_id - 1;
        Uri uri = null;
        if(media_id > -1){
            RingtoneManager ringtoneManager = new RingtoneManager(content);
            uri = ringtoneManager.getRingtoneUri(media_id);
        } else {
            uri = getDefaultAlarm(content);
        }
        return uri;
    }

    static public int convMediaID(MainActivity content, Uri uri){
        RingtoneManager ringtoneManager = new RingtoneManager(content);
        int media_id = ringtoneManager.getRingtonePosition(uri);
        if(media_id == -1){
            media_id = 0;
        } else {
            media_id = media_id + 1;
        }
        return media_id;
    }

    static public InputData getDisplayTimerRepeat(MainActivity content, int timer_repeat){
        Resources res = content.getResources();
        InputData input_data = new InputData();
        input_data.setColumName(res.getString(R.string.repeat));
        String display_timer_repeat = "";
        switch(timer_repeat){
            case 0: display_timer_repeat = res.getString(R.string.timer_repeat_unset);
                    break;
            case 2: display_timer_repeat = res.getString(R.string.timer_repeat_2);
                    break;
            case 3: display_timer_repeat = res.getString(R.string.timer_repeat_3);
                    break;
            case 4: display_timer_repeat = res.getString(R.string.timer_repeat_4);
                    break;
            case 5: display_timer_repeat = res.getString(R.string.timer_repeat_5);
                    break;
            case 10: display_timer_repeat = res.getString(R.string.timer_repeat_10);
                    break;
            case -1: display_timer_repeat = res.getString(R.string.timer_repeat_unlimit);
                    break;
        }
        input_data.setInputData(display_timer_repeat);
        return input_data;
    }

    // selectboxの選択値から、実数へ変換
    static public int convRepeatCount(int repeat_count_id){
        int repeat_count = 0;
        switch(repeat_count_id){
            case 0: repeat_count = 0;
                    break;
            case 1: repeat_count = 2;
                    break;
            case 2: repeat_count = 3;
                    break;
            case 3: repeat_count = 4;
                    break;
            case 4: repeat_count = 5;
                    break;
            case 5: repeat_count = 10;
                    break;
            case 6: repeat_count = -1;
                    break;
        }
        return repeat_count;
    }
    // 実数からselectboxのIDへ変換
    static public int convRepeatCountID(int repeat_count){
        int repeat_count_id = 0;
        switch(repeat_count){
            case 0: repeat_count_id = 0;
                    break;
            case 1: repeat_count_id = 1;
                    break;
            case 2: repeat_count_id = 2;
                    break;
            case 3: repeat_count_id = 3;
                    break;
            case 4: repeat_count_id = 4;
                    break;
            case 5: repeat_count_id = 5;
                    break;
            case 10: repeat_count_id = 6;
                    break;
            case -1: repeat_count_id = 7;
                    break;
        }
        return repeat_count_id;
    }


    static public boolean[] conversionAlarmRepeat(String db_alarm_repeat){
        String[] db_alarm_repeat_list = db_alarm_repeat.split(",", 0);
        boolean[] alarm_repeat_list = new boolean[db_alarm_repeat_list.length];
        for (int i = 0; i < db_alarm_repeat_list.length; i++) {
            if(db_alarm_repeat_list[i].equals("1")){
                alarm_repeat_list[i] = true;
            } else {
                alarm_repeat_list[i] = false;
            }
        }
        return alarm_repeat_list;
    }
    static public String conversionAlarmRepeatForDB(boolean[] alarm_repeat_list){
        String repeat_weekly = "";
        for (int i = 0; i < alarm_repeat_list.length; i++) {
            if(alarm_repeat_list[i]){
                repeat_weekly = repeat_weekly + "1,";
            } else {
                repeat_weekly = repeat_weekly + "0,";
            }
        }
        return repeat_weekly;
    }


    static public int getSnoozeInterval(int snooze_id){
        int snooze_interval = 0;
        switch(snooze_id){
            case 0: snooze_interval = 0;
                    break;
            case 1: snooze_interval = 5;
                    break;
            case 2: snooze_interval = 10;
                    break;
            case 3: snooze_interval = 15;
                    break;
            case 4: snooze_interval = 20;
                    break;
            case 5: snooze_interval = 25;
                    break;
            case 6: snooze_interval = 30;
                    break;
        }
        return snooze_interval;
    }

    static public int getRepeatCount(int repeat_count_id){
        int repeat_count = 0;
        switch(repeat_count_id){
            case 0: repeat_count = 0;
                    break;
            case 1: repeat_count = 1;
                    break;
            case 2: repeat_count = 2;
                    break;
            case 3: repeat_count = 3;
                    break;
            case 4: repeat_count = 4;
                    break;
            case 5: repeat_count = 5;
                    break;
            case 6: repeat_count = 10;
                    break;
            case 7: repeat_count = -1;
                    break;
        }
        return repeat_count;
    }

    static public InputData getDisplayVolume(MainActivity content, int volume){
        Resources res = content.getResources();
        InputData input_data = new InputData();
        input_data.setColumName(res.getString(R.string.volume_name));
        input_data.setInputData(Integer.toString(volume));
        return input_data;
    }

    static public InputData getDisplayInvalidSilentMode(MainActivity content, boolean invalid_silent_mode_flg){
        Resources res = content.getResources();
        InputData input_data = new InputData();
        input_data.setColumName(res.getString(R.string.invalid_silent_mode));
        String flg = "0";
        if(invalid_silent_mode_flg){
            flg = "1";
        }
        input_data.setInputData(flg);
        return input_data;
    }

    static public InputData getDisplayVibrator(MainActivity content, boolean vibrator_flg){
        Resources res = content.getResources();
        InputData input_data = new InputData();
        input_data.setColumName(res.getString(R.string.vibrator));
        String flg = "0";
        if(vibrator_flg){
            flg = "1";
        }
        input_data.setInputData(flg);
        return input_data;
    }
}