package cn.iwgang.countdownviewdemo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cn.iwgang.countdownview.CountdownView;


public class RecyclerViewActivity extends AppCompatActivity implements OnScheduleActionListener {
    private RecyclerView mRecyclerView;
    private MyAdapter mMyAdapter;
    private List<Schedule> mSchedules;
    private IDatabase<Schedule> mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recyclerview);

        mDatabase = new JsonDatabase(this);
        mSchedules = new ArrayList<>();

        mRecyclerView = findViewById(R.id.cv_familiarRecyclerView);
        mMyAdapter = new MyAdapter(this, mSchedules);
        mRecyclerView.setAdapter(mMyAdapter);

        mMyAdapter.setListener(this);

        findViewById(R.id.btn_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogAddSchedule();
            }
        });

        reloadData();
    }

    @SuppressLint("NewApi")
    private void showDialogAddSchedule() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_schedule, null);
        builder.setView(view);

        final EditText editTitle = view.findViewById(R.id.edit_title);
        final EditText editDesc = view.findViewById(R.id.edit_desc);
        final EditText editHour = view.findViewById(R.id.edit_hour);
        final EditText editMinute = view.findViewById(R.id.edit_minute);


        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        view.findViewById(R.id.btn_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = editTitle.getText().toString();
                String desc = editDesc.getText().toString();

                //algorithm
                // lưu thời gian

//                Calendar calendar = GregorianCalendar.getInstance();
//                calendar.setTimeInMillis(System.currentTimeMillis());
//                calendar.set(Calendar.HOUR,
//                        calendar.get(Calendar.HOUR) + Integer.parseInt(editHour.getText().toString()));
//                calendar.set(Calendar.MINUTE,
//                        calendar.get(Calendar.MINUTE) + Integer.parseInt(editMinute.getText().toString()));
//
//                long timeInMillis = calendar.getTimeInMillis();
                long timeInMillis = 0; //lay thoi gian ket thuc
                Schedule schedule = new Schedule(title, desc, timeInMillis);
                mDatabase.add(schedule);
                reloadData();
                alertDialog.dismiss();
            }
        });

    }

    private void reloadData() {
        mSchedules.clear();
        mSchedules.addAll(mDatabase.readAll());

        Collections.sort(mSchedules, new Comparator<Schedule>() {
            @Override
            public int compare(Schedule left, Schedule right) {
                return Long.valueOf(left.getEndTime()).compareTo(right.getEndTime());
            }
        });

        mMyAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClickDelete(final Schedule schedule) {
        new AlertDialog.Builder(this).setTitle("Delete?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        mDatabase.remove(schedule);
                        reloadData();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .create()
                .show();
    }


    static class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
        @NonNull
        private Context mContext;
        @NonNull
        private List<Schedule> mData;
        private OnScheduleActionListener mListener;

        MyAdapter(@NonNull Context context, @NonNull List<Schedule> datas) {
            this.mContext = context;
            this.mData = datas;
        }

        public void setListener(OnScheduleActionListener mListener) {
            this.mListener = mListener;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            return new MyViewHolder(inflater.inflate(R.layout.list_item, parent, false));
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            final Schedule curSchedule = mData.get(position);
            holder.bindData(curSchedule);

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (mListener != null){
                        mListener.onClickDelete(curSchedule);
                    }
                    return true;
                }
            });
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

        @Override
        public void onViewAttachedToWindow(MyViewHolder holder) {
            int pos = holder.getAdapterPosition();
            Schedule schedule = mData.get(pos);
            holder.refreshTime(schedule.getEndTime() - System.currentTimeMillis());
        }

        @Override
        public void onViewDetachedFromWindow(MyViewHolder holder) {
            holder.getCountdownView().stop();
        }
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView mTvTitle, mTxtDesc;
        private CountdownView mCountdownView;
        private Schedule mSchedule;

        MyViewHolder(View itemView) {
            super(itemView);
            mTvTitle = itemView.findViewById(R.id.tv_title);
            mTxtDesc = itemView.findViewById(R.id.tv_desc);
            mCountdownView = itemView.findViewById(R.id.cv_countdownView);

        }

        public void bindData(Schedule schedule) {
            mSchedule = schedule;
            mTvTitle.setText(schedule.getTitle());
            mTxtDesc.setText(schedule.getDescription());
            refreshTime(mSchedule.getEndTime() - System.currentTimeMillis());
        }

        public void refreshTime(long leftTime) {
            if (leftTime > 0) {
                mCountdownView.start(leftTime);
            } else {
                mCountdownView.stop();
                mCountdownView.allShowZero();
            }
        }


        public CountdownView getCountdownView() {
            return mCountdownView;
        }
    }

    public static class Schedule {
        private long id;
        private String title;
        private String des;
        private long endTime;

        public Schedule(long id, String title, String des, long endTime) {
            this.id = id;
            this.title = title;
            this.des = des;
            this.endTime = endTime;
        }

        public Schedule(String title, String desc, long endTime) {
            this(System.nanoTime(), title, desc, endTime);
        }

        public String getDescription() {
            return des;
        }

        public long getId() {
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

        public long getEndTime() {
            return endTime;
        }

        public void setEndTime(long endTime) {
            this.endTime = endTime;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Schedule){
                return id == ((Schedule) obj).id;
            }
            return false;
        }
    }

}


