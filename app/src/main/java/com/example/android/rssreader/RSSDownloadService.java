package com.example.android.rssreader;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
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
        sendBroadcast(new Intent(NOTIFICATION));
        Notification noti = new Notification.Builder(this)
                .setContentTitle("New Rss items available. Total of " + parse.size())
                .setAutoCancel(true).setSmallIcon(R.drawable.notification_icon_background)
                .setContentText("New data").build();
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.notify(0, noti);

    }

}
