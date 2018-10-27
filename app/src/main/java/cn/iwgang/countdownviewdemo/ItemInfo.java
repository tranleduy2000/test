package cn.iwgang.countdownviewdemo;

public class ItemInfo {
    private int id;
    private String title;
    private long countdown;
    /*
       根据服务器返回的countdown换算成手机对应的开奖时间 (毫秒)
       [正常情况最好由服务器返回countdown字段，然后客户端再校对成该手机对应的时间，不然误差很大]
     */
    private long endTime;

    public ItemInfo(int id, String title, long countdown) {
        this.id = id;
        this.title = title;
        this.countdown = countdown;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getCountdown() {
        return countdown;
    }

    public void setCountdown(long countdown) {
        this.countdown = countdown;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }
}