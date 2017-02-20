package com.example.android.rssreader;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.rssfeedlibrary.RssItem;

import java.util.List;

public class RssItemAdapter
        extends RecyclerView.Adapter<RssItemAdapter.ViewHolder> {

    private List<RssItem> rssItems;
    private MyListFragment myListFragment;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= null;
        v = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.rowlayout, parent, false);
        return new ViewHolder(v);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View mainLayout;
        public TextView txtHeader;
        public TextView txtFooter;
        public ImageView imageView;

        public ViewHolder(View v) {
            super(v);
            mainLayout = v;
            txtHeader = (TextView) v.findViewById(R.id.rsstitle);
            txtFooter = (TextView) v.findViewById(R.id.rssurl);
            imageView = (ImageView) v.findViewById(R.id.icon);
        }
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final RssItem rssItem = rssItems.get(position);
        holder.txtHeader.setText(rssItem.getTitle());
        holder.txtFooter.setText(rssItem.getLink());
        holder.mainLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                myListFragment.updateDetail(rssItem.getLink());
            }
        });
    }

    @Override
    public int getItemCount() {
        return rssItems.size();
    }

    public RssItemAdapter(List<RssItem> rssItems, MyListFragment myListFragment) {
        this.rssItems = rssItems;
        this.myListFragment = myListFragment;
    }
}
