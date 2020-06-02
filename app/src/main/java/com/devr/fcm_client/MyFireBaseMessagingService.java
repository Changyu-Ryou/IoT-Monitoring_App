package com.devr.fcm_client;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFireBaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onNewToken(String token) {
        Log.d("FCM Log", "Refreshed token: " + token);
    }
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getNotification() != null) {                      //포어그라운드
            sendNotification(remoteMessage.getNotification().getBody(), remoteMessage.getNotification().getTitle());
        }else if (remoteMessage.getData().size() > 0) {                           //백그라운드
            sendNotification(remoteMessage.getData().get("body"), remoteMessage.getData().get("title"));
            /* 백그라운드 작동 내용 */
        }
    }

        private void sendNotification(String messageBody, String messageTitle)  {
            Log.d("FCM Log", "알림 메시지: " + messageBody);
            //String messageBody = remoteMessage.getNotification().getBody();     //메시지 내용
            //String messageTitle = remoteMessage.getNotification().getTitle();   //메시지 제목

            /* 알림의 탭 작업 설정 */
            Intent intent = new Intent(this, AlarmActivity.class);

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            String channelId = "Channel ID";
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            /* 알림 만들기 */
            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(this, channelId)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle(messageTitle)
                            .setContentText(messageBody)
                            .setAutoCancel(true)
                            //.setSound(defaultSoundUri)
                            //.setContentIntent(pendingIntent)
                            .setFullScreenIntent(pendingIntent, true);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            /* 새로운 인텐트로 앱 열기 */
            Intent newintent = new Intent(this, AlarmActivity.class);
            newintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity( newintent );

            /* 채널 만들기*/
            /* Android 8.0 이상에서 알림을 게시하려면 알림을 만들어야 함 */
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                String channelName = "Channel Name";
                NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
                notificationManager.createNotificationChannel(channel);
            }

            notificationManager.notify(0, notificationBuilder.build());
        }

}