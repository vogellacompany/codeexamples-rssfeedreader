package com.example.android.rssreader;

import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.android.rssfeedlibrary.RssFeedProvider;
import com.example.android.rssfeedlibrary.RssItem;

import java.util.ArrayList;
import java.util.List;

public class MyListFragment extends Fragment {

    ParseTask parseTask;
    private OnItemSelectedListener listener;
    RssItemAdapter adapter;
    List<RssItem> rssItems;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rsslist_overview, container, false);
        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        rssItems = RssApplication.list;
        adapter = new RssItemAdapter(rssItems, this);
        mRecyclerView.setAdapter(adapter);
        if(rssItems.isEmpty()) { // #1
            updateListContent();
        }

        return view;
    }

    public interface OnItemSelectedListener {
        void onRssItemSelected(String link);
    }
    

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnItemSelectedListener) {
            listener = (OnItemSelectedListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement MyListFragment.OnItemSelectedListener");
        }
    }

    public void updateListContent() {
        if (parseTask == null) {

            parseTask = new ParseTask();

            parseTask.setFragment(this);

            parseTask.execute("http://www.vogella.com/article.rss");

        }
    }

    public void setListContent(List<RssItem> result) {
        rssItems.clear();
        rssItems.addAll(result);
        adapter.notifyDataSetChanged();
        parseTask.setFragment(null);
        parseTask = null;
    }

    // triggers update of the details fragment
    public void updateDetail(String uri) {  // #<1>
        listener.onRssItemSelected(uri);
    }

    private static class ParseTask extends  AsyncTask<String, Void, List<RssItem>> {

        private MyListFragment fragment;


        public void setFragment(MyListFragment fragment) {
            this.fragment = fragment;
        }

        @Override
        protected List<RssItem> doInBackground(String... params) {
            List<RssItem> list = RssFeedProvider.parse(params[0]);
            return list;
        }




        @Override
        protected void onPostExecute(List<RssItem> result) {
            fragment.setListContent(result);
        }
    }
}