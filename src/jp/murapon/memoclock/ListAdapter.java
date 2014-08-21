package jp.murapon.memoclock;

import java.util.List;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

public class ListAdapter extends ArrayAdapter<TimeManager> {

    private LayoutInflater layoutInflater_;
    private MainActivity content_;
    private Context context_;
    private Button button;

    public ListAdapter(MainActivity content, Context context, int textViewResourceId, List<TimeManager> objects){
        super(context, textViewResourceId, objects);
        layoutInflater_ = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        content_ = content;
        context_ = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 特定の行(position)のデータを得る
        TimeManager item = (TimeManager)getItem(position);

        // convertViewは使い回しされている可能性があるのでnullの時だけ新しく作る
        if (null == convertView) {
            convertView = layoutInflater_.inflate(R.layout.list_data, null);
        }

        TextView textView1 = (TextView)convertView.findViewById(R.id.time);
        if(item.getUseFlg() == 0){
            textView1.setText(item.getTime());
        } else {
            textView1.setText(item.getStartTime());
        }
        TextView textView2 = (TextView)convertView.findViewById(R.id.memo);
        textView2.setText(item.getMemo());

        TextView textView3 = (TextView)convertView.findViewById(R.id.repeat);
        String repeat = "";
        if(item.getTimeType() == TimeManager.TIME_TYPE_ALARM){
            repeat = InputDataUtil.getDisplayRepeatWeeklyForList(content_, item.getRepeatWeekly());
        } else {
            repeat = InputDataUtil.getDisplayRepeatCount(content_, item.getRepeatCount(), item.getRemainRepeatCount());
        }
        textView3.setText(repeat);
        
        button = (Button)convertView.findViewById(R.id.switch_button);
        // 初期表示
        if(item.getUseFlg() == 0){
            button.setBackgroundResource(R.drawable.switch_button_off);
            if(item.getTimeType() == TimeManager.TIME_TYPE_ALARM){
                button.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.alarm_dark);
            } else {
                button.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.timer_dark);
            }
        } else {
            button.setBackgroundResource(R.drawable.switch_button_on);
            if(item.getTimeType() == TimeManager.TIME_TYPE_ALARM){
                button.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.alarm_light);
            } else {
                button.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.timer_light);
            }
        }
        button.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {

                Integer update_position = (Integer)v.getTag();
                TimeManager item = (TimeManager)(getItem((update_position)));
                remove(item);
                Button b = (Button)v;

                if(item.getUseFlg() == 0){
                    // start_timeを更新
                    if(item.getTimeType() == TimeManager.TIME_TYPE_ALARM){
                        // アラームを設定
                        item = TimeDataUtil.setAlarm(context_, item, true);
                    } else {
                        // タイマーを設定
                        item = TimeDataUtil.setTimer(context_, item, true);
                        item.setRemainRepeatCount(item.getRepeatCount());
                    }
                    // データ表示の切り替え
                    item.setUseFlg(1);
                    b.setBackgroundResource(R.drawable.switch_button_on);
                } else {
                    // データ更新
                    TimeManagerDao time_manager_dao = new TimeManagerDao(context_);
                    time_manager_dao.updateAlarmOff(item.getID());
                    // アラーム設定解除
                    Intent intent = new Intent(context_, AlarmReceiver.class);
                    intent.setData(Uri.parse(Integer.toString(item.getID())));
                    PendingIntent sender = PendingIntent.getBroadcast(context_, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    AlarmManager alarmManager = (AlarmManager)context_.getSystemService(Context.ALARM_SERVICE);
                    alarmManager.cancel(sender);
                    
                    // データ表示の切り替え
                    if(item.getTimeType() == TimeManager.TIME_TYPE_TIMER){
                        item.setRemainRepeatCount(0);
                    }
                    item.setUseFlg(0);
                    item.setStartTime("");
                    b.setBackgroundResource(R.drawable.switch_button_off);
                }
                // データ表示の再挿入
                insert(item, update_position);
            }
        });
        button.setTag((Integer)position);
        return convertView;
    }
}