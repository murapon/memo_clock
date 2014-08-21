package jp.murapon.memoclock;

public class ListData {
    private String time_type;
    private String time;
    private String memo;
 
    public void setTimeType(String temp_time_type) {
        time_type = temp_time_type;
    }
 
    public String getTimeType() {
        return time_type;
    }
 
    public void setTime(String temp_time) {
        time = temp_time;
    }
 
    public String getTime() {
        return time;
    }

    public void setMemo(String temp_memo) {
        memo = temp_memo;
    }
 
    public String getMemo() {
        return memo;
    }
}
