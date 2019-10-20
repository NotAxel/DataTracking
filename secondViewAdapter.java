package com.axel.datatracking;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class secondViewAdapter extends RecyclerView.Adapter<secondViewAdapter.ViewHolder> {

    private LayoutInflater mInflater;
    private List<SimplifiedAppInfo> mSelectedApps;
    private Context source;

    secondViewAdapter(Context context, List<SimplifiedAppInfo> apps) {
        this.mInflater = LayoutInflater.from(context);
        this.source = context;
        this.mSelectedApps = apps;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recycler_ui_2_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.myTextView.setText(mSelectedApps.get(position).getAppName());
        int temp = mSelectedApps.get(position).getUid();
        holder.myTextView2.setText(Integer.toString(temp));
        holder.appDown.setText(Long.toString(mSelectedApps.get(position).getDownbytes()));
        holder.appUp.setText(Long.toString(mSelectedApps.get(position).getUpbyts()));
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if(mSelectedApps == null) {
            return 0;
        }
        return mSelectedApps.size();
    }

    void loadItems(List<SimplifiedAppInfo> apps) {
        this.mSelectedApps = apps;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView myTextView;
        TextView myTextView2;
        TextView appDown;
        TextView appUp;

        ViewHolder (View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.appName);
            myTextView2 = itemView.findViewById(R.id.packageName);
            appDown = itemView.findViewById(R.id.down_text);
            appUp = itemView.findViewById(R.id.up_text);
            itemView.setOnClickListener(this);
        }

        void bind(int position) {
        }

        @Override
        public void onClick(View view) {
            int pos = getAdapterPosition();
            SimplifiedAppInfo temp = mSelectedApps.get(pos);
            Intent mIntent = new Intent(source, viewDataGraph.class);
            mIntent.putExtra("CHOOSEN", temp);
            Log.d("ui layer 2", "clicked on" + mSelectedApps.get(pos).getAppName());
            source.startActivity(mIntent);
        }
    }
}

