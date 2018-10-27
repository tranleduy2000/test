package cn.iwgang.countdownviewdemo;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.iwgang.countdownview.CountdownView;
import cn.iwgang.familiarrecyclerview.FamiliarRecyclerView;


public class RecyclerViewActivity2 extends AppCompatActivity {
    private MyAdapter mMyAdapter;
    private List<ItemInfo> mDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recyclerview);

        initData();

        FamiliarRecyclerView cvFamiliarRecyclerView = (FamiliarRecyclerView) findViewById(R.id.cv_familiarRecyclerView);
        cvFamiliarRecyclerView.setAdapter(mMyAdapter = new MyAdapter(this, mDataList));
    }

    private void initData() {
        mDataList = new ArrayList<>();

        for (int i = 1; i < 20; i++) {
            mDataList.add(new ItemInfo(1000 + i, "RecyclerView_测试标题_" + i, i * 20 * 1000));
        }

        // 校对倒计时
        long curTime = System.currentTimeMillis();
        for (ItemInfo itemInfo : mDataList) {
            itemInfo.setEndTime(curTime + itemInfo.getCountdown());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null != mMyAdapter) {
            mMyAdapter.startRefreshTime();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (null != mMyAdapter) {
            mMyAdapter.cancelRefreshTime();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mMyAdapter) {
            mMyAdapter.cancelRefreshTime();
        }
    }

    static class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
        private Context mContext;
        private List<ItemInfo> mDatas;
        private final SparseArray<MyViewHolder> mCountdownVHList;
        private Handler mHandler = new Handler();
        private Timer mTimer;
        private boolean isCancel = true;

        public MyAdapter(Context context, List<ItemInfo> datas) {
            this.mContext = context;
            this.mDatas = datas;
            mCountdownVHList = new SparseArray<>();
            startRefreshTime();
        }

        public void startRefreshTime() {
            if (!isCancel) return;

            if (null != mTimer) {
                mTimer.cancel();
            }

            isCancel = false;
            mTimer = new Timer();
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    mHandler.post(mRefreshTimeRunnable);
                }
            }, 0, 10);
        }

        public void cancelRefreshTime() {
            isCancel = true;
            if (null != mTimer) {
                mTimer.cancel();
            }
            mHandler.removeCallbacks(mRefreshTimeRunnable);
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.list_item, parent, false));
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            ItemInfo curItemInfo = mDatas.get(position);
            holder.bindData(curItemInfo);

            // 处理倒计时
            if (curItemInfo.getCountdown() > 0) {
                synchronized (mCountdownVHList) {
                    mCountdownVHList.put(curItemInfo.getId(), holder);
                }
            }
        }

        @Override
        public int getItemCount() {
            return mDatas.size();
        }

        @Override
        public void onViewRecycled(MyViewHolder holder) {
            super.onViewRecycled(holder);

            ItemInfo curAnnounceGoodsInfo = holder.getBean();
            if (null != curAnnounceGoodsInfo && curAnnounceGoodsInfo.getCountdown() > 0) {
                mCountdownVHList.remove(curAnnounceGoodsInfo.getId());
            }
        }

        private Runnable mRefreshTimeRunnable = new Runnable() {
            @Override
            public void run() {
                if (mCountdownVHList.size() == 0) return;

                synchronized (mCountdownVHList) {
                    long currentTime = System.currentTimeMillis();
                    int key;
                    for (int i = 0; i < mCountdownVHList.size(); i++) {
                        key = mCountdownVHList.keyAt(i);
                        MyViewHolder curMyViewHolder = mCountdownVHList.get(key);
                        if (currentTime >= curMyViewHolder.getBean().getEndTime()) {
                            // 倒计时结束
                            curMyViewHolder.getBean().setCountdown(0);
                            mCountdownVHList.remove(key);
                            notifyDataSetChanged();
                        } else {
                            curMyViewHolder.refreshTime(currentTime);
                        }

                    }
                }
            }
        };
    }



}
