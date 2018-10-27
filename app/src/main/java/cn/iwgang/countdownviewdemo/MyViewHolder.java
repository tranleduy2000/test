package cn.iwgang.countdownviewdemo;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import cn.iwgang.countdownview.CountdownView;

public class MyViewHolder extends RecyclerView.ViewHolder {
    private TextView mTvTitle;
    private CountdownView mCvCountdownView;
    private ItemInfo mItemInfo;

    public MyViewHolder(View itemView) {
        super(itemView);
        mTvTitle = (TextView) itemView.findViewById(R.id.tv_title);
        mCvCountdownView = (CountdownView) itemView.findViewById(R.id.cv_countdownView);
    }

    public void bindData(ItemInfo itemInfo) {
        mItemInfo = itemInfo;

        if (itemInfo.getCountdown() > 0) {
            refreshTime(System.currentTimeMillis());
        } else {
            mCvCountdownView.allShowZero();
        }

        mTvTitle.setText(itemInfo.getTitle());
    }

    public void refreshTime(long curTimeMillis) {
        if (null == mItemInfo || mItemInfo.getCountdown() <= 0) return;

        mCvCountdownView.updateShow(mItemInfo.getEndTime() - curTimeMillis);
    }

    public ItemInfo getBean() {
        return mItemInfo;
    }
}


