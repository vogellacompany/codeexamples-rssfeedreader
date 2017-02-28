package com.example.android.rssreader;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;

import com.example.android.rssfeedlibrary.RssFeedProvider;
import com.example.android.rssfeedlibrary.RssItem;

import java.util.ArrayList;
import java.util.List;

public class MyListFragment extends Fragment {

    private OnItemSelectedListener listener;
    RssItemAdapter adapter;
    List<RssItem> rssItems;
    RecyclerView mRecyclerView;

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            setListContent(new ArrayList<RssItem>(RssApplication.list));
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(receiver, new IntentFilter(RSSDownloadService.NOTIFICATION));
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().unregisterReceiver(receiver);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rsslist_overview, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        rssItems = RssApplication.list;
        adapter = new RssItemAdapter(rssItems, this);
        mRecyclerView.setAdapter(adapter);
        if (rssItems.isEmpty()) { // #1
            updateListContent();
        }

        // We need to detect scrolling changes in the RecyclerView
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    final View tb = getActivity().findViewById(R.id.toolbar);
                    tb.animate()
                            .translationY(0)
                            .setInterpolator(new LinearInterpolator())
                            .setDuration(180)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    tb.setVisibility(View.VISIBLE);
                                }
                            });
                } else if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    final View tb = getActivity().findViewById(R.id.toolbar);
                    tb.animate()
                            .translationY(tb.getHeight())
                            .setInterpolator(new LinearInterpolator())
                            .setDuration(180)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    tb.setVisibility(View.GONE);
                                }
                            });

                }
            }
        });


        return view;
    }

    private void updateListContent() {
        Intent intent = new Intent(getActivity(), RSSDownloadService.class);
        intent.putExtra("uri", "http://www.vogella.com/article.rss");
        getActivity().startService(intent);
    }


    public interface OnItemSelectedListener {
        void onRssItemSelected(String link);

        void toolbarAnimateShow();

        void toolbarAnimateHide();
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



    public void setListContent(List<RssItem> result) {
        rssItems.clear();
        rssItems.addAll(result);
        adapter.notifyDataSetChanged();
    }

    // triggers update of the details fragment
    public void updateDetail(String uri) {  // #<1>
        listener.onRssItemSelected(uri);
    }

    private static class ParseTask extends AsyncTask<String, Void, List<RssItem>> {

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
        protected void onPostExecute(List<RssItem> result)
        {
            fragment.setListContent(result);
        }
    }
}