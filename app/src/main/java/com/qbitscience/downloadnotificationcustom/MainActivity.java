package com.qbitscience.downloadnotificationcustom;

import android.app.DownloadManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

public class MainActivity extends AppCompatActivity {
    int col;
    int cindex;
    int suceess;
    TextView percentage;

    int a=0;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        percentage=findViewById(R.id.per);

        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.this,101,
                intent,PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder notification = new NotificationCompat.Builder(MainActivity.this,"100")
                .setSmallIcon(android.R.drawable.ic_menu_help)
                .setContentTitle("QbitScience")
                . setContentText("Download Progress")
                .setContentIntent(pendingIntent)
                .setNotificationSilent()
                .setTicker("New Notification")
                .setAutoCancel(true)
            ;

        NotificationChannel channel = new
                NotificationChannel("100","QbitScience",NotificationManager.IMPORTANCE_HIGH);
        NotificationManager manager2 = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);


        String url_Download = "enter your url to be downloaded.....";
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url_Download));
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "myFile.mp4");
        final DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

        final long downloadId = manager.enqueue(request);

        final ProgressBar mProgressBar = (ProgressBar) findViewById(R.id.progressBar1);

        new Thread(new Runnable() {

            @Override
            public void run() {

                boolean downloading = true;

                while (downloading) {

                    DownloadManager.Query q = new DownloadManager.Query();
                    q.setFilterById(downloadId);

                    Cursor cursor = manager.query(q);
                    cursor.moveToFirst();
              col=  cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR);
                    int bytes_downloaded = cursor.getInt(col);
              cindex=cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES);
                    int bytes_total = cursor.getInt(cindex);
                  suceess=cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
                    if (cursor.getInt(suceess) == DownloadManager.STATUS_SUCCESSFUL) {
                        downloading = false;
                    }

                    final int dl_progress = (int) ((bytes_downloaded * 100l) / bytes_total);

                    runOnUiThread(new Runnable() {

                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void run() {

                            mProgressBar.setProgress((int) dl_progress);
                          percentage.setText( dl_progress+"%");
                          a=dl_progress;

                          notification.setProgress(100,dl_progress,false);
                          notification.setContentText(dl_progress+"%");
                          manager2.createNotificationChannel(channel);
                          manager2.notify(11,notification.build());



                        }
                    });
                    cursor.close();
                }



            }
        }).start();

    }

}