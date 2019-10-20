package com.axel.datatracking;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

    private LayoutInflater mInflater;
    private List<AppInfo> mApps;
    // keep track of which boxes have been checked
    private SparseBooleanArray itemStateArray = new SparseBooleanArray();

    // data is passed to constructor
    MyRecyclerViewAdapter(Context context, List<AppInfo> apps) {
        this.mInflater = LayoutInflater.from(context);
        this.mApps = apps;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_row, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the textview in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.myImageView.setImageDrawable(mApps.get(position).getAppLogo());
        holder.myTextView.setText(mApps.get(position).getAppName());
        holder.bind(position);
    }

    // row count
    @Override
    public int getItemCount() {
        if (mApps == null) {
            return 0;
        }
        return mApps.size();
    }

    void loadItems(List<AppInfo> apps) {
        this.mApps = apps;
        notifyDataSetChanged();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView myTextView;
        ImageView myImageView;
        CheckBox myCheckBox;

        ViewHolder (View itemView) {
            super(itemView);
            myImageView = itemView.findViewById(R.id.app_logo);
            myTextView = itemView.findViewById(R.id.app_name);
            myCheckBox = itemView.findViewById(R.id.app_check);
            itemView.setOnClickListener(this);
        }

        void bind(int position){
            if(!itemStateArray.get(position, false)) {
                mApps.get(position).setFlag(false);
                myCheckBox.setChecked(false);
            } else {
                mApps.get(position).setFlag(true);
                myCheckBox.setChecked(true);
            }
        }

        @Override
        public void onClick(View view) {
            int adapterPos = getAdapterPosition();
            if(!itemStateArray.get(adapterPos, false)){
                mApps.get(adapterPos).setFlag(true);
                myCheckBox.setChecked(true);
                itemStateArray.put(adapterPos, true);
            } else {
                mApps.get(adapterPos).setFlag(false);
                myCheckBox.setChecked(false);
                itemStateArray.put(adapterPos, false);
            }
        }
    }
}
