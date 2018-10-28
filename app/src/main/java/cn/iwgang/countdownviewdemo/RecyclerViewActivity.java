package cn.iwgang.countdownviewdemo;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.iwgang.countdownview.CountdownView;
import cn.iwgang.familiarrecyclerview.FamiliarRecyclerView;


/**
 * 此类模拟在RecyclerView中使用倒计时,
 * 复用 本地的计时器 —— System.currentTimeMillis(), 不必自行计时
 */
public class RecyclerViewActivity extends AppCompatActivity {
    FamiliarRecyclerView cvFamiliarRecyclerView;
    private MyAdapter mMyAdapter;
    private List<ItemInfo> mDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recyclerview);

        initData();

        cvFamiliarRecyclerView = (FamiliarRecyclerView) findViewById(R.id.cv_familiarRecyclerView);
        cvFamiliarRecyclerView.setAdapter(mMyAdapter = new MyAdapter(this, mDataList));
        cvFamiliarRecyclerView.setOnItemClickListener(new FamiliarRecyclerView.OnItemClickListener() {
            @Override
            public void onItemClick(FamiliarRecyclerView familiarRecyclerView, View view, int position) {
                Toast.makeText(RecyclerViewActivity.this, " " + position, Toast.LENGTH_SHORT).show();

            }
        });


    }


    private void initData() {
        mDataList = new ArrayList<>();

        mDataList.add(new ItemInfo(1000 + 10, "RecyclerView_2323Test_" + 11, "ahihi", 345600000));
        mDataList.add(new ItemInfo(1000 + 10, "RecyclerView_Te23st_" + 11, "ahih2 i", 3456000));
        mDataList.add(new ItemInfo(1000 + 10, "23 2" + 11, "ahi 3 hi", 345600000));
        mDataList.add(new ItemInfo(1000 + 10, "RecyclerView1212_Te23 st_" + 11, "ahi23 hi", 345600000));
        mDataList.add(new ItemInfo(1000 + 10, "RecyclerView4545_Te23 st_" + 11, "ahi23gbdfgfgfgsd sdfsdf hi", 345600000));
        mDataList.add(new ItemInfo(1000 + 10, "RecyclerVi121ew_Te23 st_" + 11, "ahi23 hsdfsdfdsfsdfsdfvsdfsdvsrfasfdfdsgsdfdasfsdfsdfdsfsdfsdfsdfsdfsdfsdfsdfsdfsdfsdfsdfi", 345600000));


        // 校对倒计时
        long curTime = System.currentTimeMillis();
        for (ItemInfo itemInfo : mDataList) {
            itemInfo.setEndTime(curTime + itemInfo.getCountdown());
        }
    }


    static class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
        private Context mContext;
        private List<ItemInfo> mDatas;

        public MyAdapter(Context context, List<ItemInfo> datas) {
            this.mContext = context;
            this.mDatas = datas;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.list_item, parent, false));
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            ItemInfo curItemInfo = mDatas.get(position);
            holder.bindData(curItemInfo);
        }

        @Override
        public int getItemCount() {
            return mDatas.size();
        }

        /**
         * 以下两个接口代替 activity.onStart() 和 activity.onStop(), 控制 timer 的开关
         */
        @Override
        public void onViewAttachedToWindow(MyViewHolder holder) {
            int pos = holder.getAdapterPosition();
//            Log.d("MyViewHolder", String.format("mCvCountdownView %s is attachedToWindow", pos));

            ItemInfo itemInfo = mDatas.get(pos);

            holder.refreshTime(itemInfo.getEndTime() - System.currentTimeMillis());
        }

        @Override
        public void onViewDetachedFromWindow(MyViewHolder holder) {
//            int pos = holder.getAdapterPosition();
//            Log.d("MyViewHolder", String.format("mCvCountdownView %s is detachedFromWindow", pos));

            holder.getCvCountdownView().stop();
        }
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView mTvTitle, tv_desc;
        private CountdownView mCvCountdownView;
        private ItemInfo mItemInfo;

        public MyViewHolder(View itemView) {
            super(itemView);
            mTvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            tv_desc = (TextView) itemView.findViewById(R.id.tv_desc);
            mCvCountdownView = (CountdownView) itemView.findViewById(R.id.cv_countdownView);
        }

        public void bindData(ItemInfo itemInfo) {
            mItemInfo = itemInfo;
            mTvTitle.setText(itemInfo.getTitle());
            tv_desc.setText(itemInfo.getDes());
            refreshTime(mItemInfo.getEndTime() - System.currentTimeMillis());
        }

        public void refreshTime(long leftTime) {
            if (leftTime > 0) {
                mCvCountdownView.start(leftTime);
            } else {
                mCvCountdownView.stop();
                mCvCountdownView.allShowZero();
            }
        }

        public ItemInfo getBean() {
            return mItemInfo;
        }

        public CountdownView getCvCountdownView() {
            return mCvCountdownView;
        }
    }

    public static class ItemInfo {
        private int id;
        private String title;
        private String des;
        private long countdown;
        /*
                   根据服务器返回的countdown换算成手机对应的开奖时间 (毫秒)
                   [正常情况最好由服务器返回countdown字段，然后客户端再校对成该手机对应的时间，不然误差很大]
                 */
        private long endTime;

        public ItemInfo(int id, String title, String des, long countdown, long endTime) {
            this.id = id;
            this.title = title;
            this.des = des;
            this.countdown = countdown;
            this.endTime = endTime;
        }

        public ItemInfo(int id, String title, String des, long countdown) {
            this.id = id;
            this.title = title;
            this.des = des;
            this.countdown = countdown;

        }

        public String getDes() {
            return des;
        }

        public void setDes(String des) {
            this.des = des;
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

}


