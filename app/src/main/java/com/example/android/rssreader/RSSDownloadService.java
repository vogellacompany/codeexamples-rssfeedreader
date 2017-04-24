package com.example.android.rssreader;

import android.app.IntentService;
import android.content.Intent;

import com.example.android.rssfeedlibrary.RssFeedProvider;
import com.example.android.rssfeedlibrary.RssItem;

import java.util.List;


public class RSSDownloadService extends IntentService {
    public static String NOTIFICATION = "rssfeedupdated";

    public RSSDownloadService() {
        super("Rssdownload service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String uri = intent.getStringExtra("uri");
        List<RssItem> parse = RssFeedProvider.parse(uri);
        RssApplication.list.clear();
        RssApplication.list.addAll(parse);

        Intent i = new Intent(NOTIFICATION);
        sendBroadcast(i);

    }

}
