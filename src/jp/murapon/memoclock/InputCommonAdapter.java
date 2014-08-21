package jp.murapon.memoclock;

import java.util.List;
//import java.util.ArrayList;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class InputCommonAdapter extends ArrayAdapter<InputData> {

    private LayoutInflater layoutInflater_;
    private boolean invalid_silent_mode_flg = false;
    private boolean vibrator_flg = false;
    private int volume = 0;
    private Context context_var;

    public InputCommonAdapter(Context context, int textViewResourceId, List<InputData> objects) {
        super(context, textViewResourceId, objects);
        layoutInflater_ = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        context_var = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 特定の行(position)のデータを得る
        final InputData item = (InputData)getItem(position);

        // positionによってconvertViewを使い分ける
        if (position <= 2){
            convertView = layoutInflater_.inflate(R.layout.regist_input_data, null);
        } else if (position == 3){
            convertView = layoutInflater_.inflate(R.layout.regist_input_data_volume, null);
        } else if (position == 4){
            convertView = layoutInflater_.inflate(R.layout.regist_input_data_silent_mode, null);
        } else if (position == 5){
            convertView = layoutInflater_.inflate(R.layout.regist_input_data_vibrator, null);
        }

        TextView textView1 = (TextView)convertView.findViewById(R.id.colum_name);
        textView1.setText(item.getColumName());
        TextView textView2 = null;
        if (position <= 2){
            textView2 = (TextView)convertView.findViewById(R.id.input_data);
            textView2.setText(item.getInputData());
        } else if (position == 3){
            final SeekBar edit_volume = (SeekBar)convertView.findViewById(R.id.volume);
            edit_volume.setProgress(Integer.parseInt(item.getInputData()));
            edit_volume.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
                // トラッキング開始時に呼び出されます
                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }
                // トラッキング中に呼び出されます
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
                }
                // トラッキング終了時に呼び出されます
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    volume = edit_volume.getProgress();
                    edit_volume.setProgress(volume);
                    item.setInputData(String.valueOf(volume));
                }
            });
        } else if (position == 4){
            final CheckBox check_invalid_silent_mode_flg = (CheckBox)convertView.findViewById(R.id.invalid_silent_mode_flg);
            if(item.getInputData().equals("1")){
                invalid_silent_mode_flg = true;
            }
            check_invalid_silent_mode_flg.setChecked(invalid_silent_mode_flg);
            check_invalid_silent_mode_flg.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    invalid_silent_mode_flg = check_invalid_silent_mode_flg.isChecked();
                    check_invalid_silent_mode_flg.setChecked(invalid_silent_mode_flg);
                    if(invalid_silent_mode_flg){
                        item.setInputData("1");
                    } else{
                        item.setInputData("0");
                    }
                }
            });
        } else if (position == 5){
            final CheckBox check_vibrator_flg = (CheckBox)convertView.findViewById(R.id.vibrator_flg);
            if(item.getInputData().equals("1")){
                vibrator_flg = true;
            }
            check_vibrator_flg.setChecked(vibrator_flg);
            check_vibrator_flg.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    vibrator_flg = check_vibrator_flg.isChecked();
                    check_vibrator_flg.setChecked(vibrator_flg);
                    if(vibrator_flg){
                        item.setInputData("1");
                    } else{
                        item.setInputData("0");
                    }
                }
            });
        }
        return convertView;
    }

    public void setVolume(int temp_volume) {
        volume = temp_volume;
    }
    
    public boolean getInvalidSilentModeFlg() {
        return invalid_silent_mode_flg;
    }
    public boolean getVibratorFlg() {
        return vibrator_flg;
    }
    public int getVolume() {
        return volume;
    }

}