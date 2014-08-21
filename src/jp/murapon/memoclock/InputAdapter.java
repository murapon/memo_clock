package jp.murapon.memoclock;

import java.util.List;
//import java.util.ArrayList;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class InputAdapter extends ArrayAdapter<InputData> {

    private LayoutInflater layoutInflater_;
 
    public InputAdapter(Context context, int textViewResourceId, List<InputData> objects) {
        super(context, textViewResourceId, objects);
        layoutInflater_ = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 特定の行(position)のデータを得る
        InputData item = (InputData)getItem(position);

        // convertViewは使い回しされている可能性があるのでnullの時だけ新しく作る
        if (null == convertView) {
            convertView = layoutInflater_.inflate(R.layout.regist_input_data, null);
        }

        TextView textView1 = (TextView)convertView.findViewById(R.id.colum_name);
        textView1.setText(item.getColumName());
        TextView textView2 = (TextView)convertView.findViewById(R.id.input_data);
        textView2.setText(item.getInputData());
        return convertView;
    }
}