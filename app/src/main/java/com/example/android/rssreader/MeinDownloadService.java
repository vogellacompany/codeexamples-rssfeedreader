package com.example.android.rssreader;

import android.app.IntentService;
import android.content.Intent;

import com.example.android.rssfeedlibrary.RssFeedProvider;
import com.example.android.rssfeedlibrary.RssItem;

import java.util.List;

/**
 * Created by vogella on 02.03.17.
 */

public class MeinDownloadService extends IntentService {

    public MeinDownloadService() {
        super("Rssfeed download");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        List<RssItem> parse = RssFeedProvider.parse("http://www.vogella.com/article.rss");
        RssApplication.list.clear();
        RssApplication.list.addAll(parse);
        Intent i = new Intent(RSSDownloadService.NOTIFICATION);
        sendBroadcast(i);
    }

}
