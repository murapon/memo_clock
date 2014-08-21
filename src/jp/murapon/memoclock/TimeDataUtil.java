package jp.murapon.memoclock;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;
public class TimeDataUtil {

    static public String getWeekFlg(String[] repeat_weekly_array, int week){
        if(week >= 2){
            return repeat_weekly_array[week - 2];
        } else {
            return repeat_weekly_array[6];
        }
    }

    // アラームでの時間設定
    static public TimeManager setAlarm(Context context_, TimeManager item, boolean new_flg){

        // データ更新
        // start_timeを計算
        String start_time = "";
        start_time = getStartTimeForAlarm(item, new_flg);
        if(start_time != "") {
            Date date = null;
            SimpleDateFormat format = new SimpleDateFormat("yy/MM/dd HH:mm");
            try {
                date = format.parse(start_time);
            } catch (ParseException e) {
                e.printStackTrace();
                Toast.makeText(context_, "Error!", Toast.LENGTH_SHORT).show();
            }
            if(date != null){
                // アラームを設定
                Intent intent = new Intent(context_, AlarmReceiver.class);
                intent.putExtra("id", String.valueOf(item.getID()));
                intent.setData(Uri.parse(String.valueOf(item.getID())));
                PendingIntent sender = PendingIntent.getBroadcast(context_, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                AlarmManager alarmManager = (AlarmManager)context_.getSystemService(Context.ALARM_SERVICE);  
                format.setTimeZone(TimeZone.getDefault()); 
                Calendar cal = Calendar.getInstance(TimeZone.getDefault());
                cal.setTime(date);
                cal.setTimeZone(TimeZone.getDefault());
                int snooze_interval = item.getSnooze();
                if(snooze_interval == 0){

                    // テスト用
//                    cal.setTimeInMillis(System.currentTimeMillis());  
//                    cal.add(Calendar.SECOND, 3);

                    alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), sender);
                } else {

                    // テスト用
//                    cal.setTimeInMillis(System.currentTimeMillis());  
//                    cal.add(Calendar.SECOND, 10);

                    long interval = snooze_interval * 1000 * 60;
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), interval, sender);
                }
                item.setStartTime(start_time);
                Toast.makeText(context_, "Set " + start_time, Toast.LENGTH_SHORT).show();
            }
            // start_timeを設定
            TimeManagerDao time_manager_dao = new TimeManagerDao(context_);
            time_manager_dao.updateAlarmOn(item.getID(), start_time);
        } else {
            // start_timeの解除、無効化
            TimeManagerDao time_manager_dao = new TimeManagerDao(context_);
            time_manager_dao.updateAlarmOff(item.getID());
        }
        return item;
    }

    // アラーム時間用の開始時間算出
    static public String getStartTimeForAlarm(TimeManager item, boolean new_flg){
        String start_time = "";
        String time = item.getTime();
        String get_repeat_weekly = item.getRepeatWeekly();
        String[] repeat_weekly_array = get_repeat_weekly.split(",", 0);

        Calendar cal_curr = Calendar.getInstance(); 
        int week_curr = cal_curr.get(Calendar.DAY_OF_WEEK);
        int time_h = Integer.parseInt(time.substring(0,2));
        int time_m = Integer.parseInt(time.substring(3,5));
        if(TimeDataUtil.getWeekFlg(repeat_weekly_array, week_curr).equals("1")){
            // 同じ曜日に設定されている。
            int year = cal_curr.get(Calendar.YEAR);
            int month = cal_curr.get(Calendar.MONTH) + 1;
            int day = cal_curr.get(Calendar.DATE);
            Calendar cal_set = Calendar.getInstance();
            cal_set.set(year, month - 1, day, time_h, time_m, 0);
            if(cal_curr.compareTo(cal_set) < 0){
                // start_dateを設定
                start_time = String.valueOf(year) + '/' + String.format("%1$02d", month) +
                             '/' + String.format("%1$02d", day) + ' ' + String.format("%1$02d", time_h) +
                             ':' + String.format("%1$02d", time_m);
            }
        }

        // 別の曜日、もしくは既に過ぎていた場合
        if(start_time.equals("")){
            int week = week_curr;
            for (int i = 0; i < 7; i++){
                week = week + 1;
                if(week <= 7){
                    if(TimeDataUtil.getWeekFlg(repeat_weekly_array, week).equals("1")){
                        int diff = 0;
                        if(week - week_curr > 0){
                            diff = week - week_curr;
                        } else {
                            diff = 7 - week_curr + week;
                        }
                        cal_curr.add(Calendar.DATE,diff);
                        int year = cal_curr.get(Calendar.YEAR);
                        int month = cal_curr.get(Calendar.MONTH) + 1;
                        int day = cal_curr.get(Calendar.DATE);
                        start_time = String.valueOf(year) + '/' + String.format("%1$02d", month) +
                                '/' + String.format("%1$02d", day) + ' ' + String.format("%1$02d", time_h) +
                                ':' + String.format("%1$02d", time_m);
                        break;
                    }
                } else {
                    week = 0;
                }
            }
        }
        // 繰り返しの設定がなかった場合は、新規設定の時のみ次の時間を算出
        // 新規でない場合は算出しない
        if(start_time.equals("") && new_flg == true){
            int year = cal_curr.get(Calendar.YEAR);
            int month = cal_curr.get(Calendar.MONTH) + 1;
            int day = cal_curr.get(Calendar.DATE);
            Calendar cal_set = Calendar.getInstance();
            cal_set.set(year, month - 1, day, time_h, time_m, 0);
            if(cal_curr.compareTo(cal_set) > 0){
                cal_curr.add(Calendar.DATE,1);
                year = cal_curr.get(Calendar.YEAR);
                month = cal_curr.get(Calendar.MONTH) + 1;
                day = cal_curr.get(Calendar.DATE);
            }
            start_time = String.valueOf(year) + '/' + String.format("%1$02d", month) +
                    '/' + String.format("%1$02d", day) + ' ' + String.format("%1$02d", time_h) +
                    ':' + String.format("%1$02d", time_m);
        }
        return start_time;
    }


    // タイマーでの時間設定
    static public TimeManager setTimer(Context context_, TimeManager item, boolean new_flg){

        // データ更新
        // start_timeを計算
        String start_time = "";
        int repeat_count = 0;
        if(new_flg){
            // 残り回数を初期化
            repeat_count = item.getRepeatCount();
        } else {
            // 繰り返しカウントを1減らす
            if(item.getRemainRepeatCount() <= 1){
                // 残り回数０、必要回数繰り返していたらnullを返す
                TimeManagerDao time_manager_dao = new TimeManagerDao(context_);
                time_manager_dao.updateTimer(item.getID(), null, 0, 0);
                item.setStartTime(null);
                item.setStartTime(start_time);
                item.setUseFlg(0);
                return item;
            }
            repeat_count = item.getRemainRepeatCount() - 1;
        }
        String time = item.getTime();
        int time_h = Integer.parseInt(time.substring(0,2));
        int time_m = Integer.parseInt(time.substring(3,5));
        Calendar cal_set = Calendar.getInstance();
        cal_set.add(Calendar.HOUR_OF_DAY, time_h);
        cal_set.add(Calendar.MINUTE, time_m);
        int year = cal_set.get(Calendar.YEAR);
        int month = cal_set.get(Calendar.MONTH) + 1;
        int day = cal_set.get(Calendar.DATE);
        int hour = cal_set.get(Calendar.HOUR_OF_DAY);
        int minute = cal_set.get(Calendar.MINUTE);
        start_time = String.valueOf(year) + '/' + String.format("%1$02d", month) +
                     '/' + String.format("%1$02d", day) + ' ' + String.format("%1$02d", hour) +
                     ':' + String.format("%1$02d", minute);
        if(start_time != "") {
            Date date = null;
            SimpleDateFormat format = new SimpleDateFormat("yy/MM/dd HH:mm");
            try {
                date = format.parse(start_time);
            } catch (ParseException e) {
                e.printStackTrace();
                Toast.makeText(context_, "Error!", Toast.LENGTH_SHORT).show();
            }
            if(date != null){
                // タイマーを設定
                Intent intent = new Intent(context_, AlarmReceiver.class);
                intent.putExtra("id", String.valueOf(item.getID()));
                intent.setData(Uri.parse(String.valueOf(item.getID())));
                PendingIntent sender = PendingIntent.getBroadcast(context_, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                AlarmManager alarmManager = (AlarmManager)context_.getSystemService(Context.ALARM_SERVICE);  
                format.setTimeZone(TimeZone.getDefault()); 
                Calendar cal = Calendar.getInstance(TimeZone.getDefault());
                cal.setTime(date);
                cal.setTimeZone(TimeZone.getDefault());
                int snooze_interval = InputDataUtil.getSnoozeInterval(item.getSnooze());

                if(snooze_interval == 0){

                    // テスト用
//                    cal.setTimeInMillis(System.currentTimeMillis());  
//                    cal.add(Calendar.SECOND, 3);
                    
                    
                    alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), sender);
                } else {

                    // テスト用
//                    cal.setTimeInMillis(System.currentTimeMillis());  
//                    cal.add(Calendar.SECOND, 3);

                    long interval = snooze_interval * 1000 * 60;
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), interval, sender);
                }
                item.setStartTime(start_time);
                Toast.makeText(context_, "Set " + start_time, Toast.LENGTH_SHORT).show();
            }
            // start_timeを設定
            TimeManagerDao time_manager_dao = new TimeManagerDao(context_);
            time_manager_dao.updateTimer(item.getID(), start_time, 1, repeat_count);
        } else {
            Toast.makeText(context_, "Error!", Toast.LENGTH_SHORT).show();
        }
        return item;
    }
}