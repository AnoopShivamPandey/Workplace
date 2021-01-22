package inspection.management.workplace;

//Author is Lazlo Software Solution Private Limited
//        *Created Date-
//        * Contact Details-
//        *Website:www.lazlosoftwaresolutoion.com
//        * email:info@lazlosoftwaresolution.com
//    * OverView-


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;

import android.os.Build;
import android.util.Log;


import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import inspection.management.workplace.activities.WorkplaceLogin;
import inspection.management.workplace.fragments.Notification;


public class FirebaseMessgingService extends com.google.firebase.messaging.FirebaseMessagingService {


    private ArrayList<String> notifications;
    private int count=0;
    public static int notification_count=0,alerts=0,comments=0,ticket_comments=0,update_alert=0;
    private String imageUri;
    private static final String TAG = "FirebaseMessageService";
    Bitmap bitmap;
    private String title="New Notification";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage){
        Intent intent = new Intent();
        intent.setAction("com.my.app.onMessageReceived");
        sendBroadcast(intent);

        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
//            Log.e("token","Payload:"+remoteMessage.getData());
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
//            Log.e("token","body:"+ remoteMessage.getNotification().getBody());
        }

        //imageUri = remoteMessage.getData().get("image");
        String TrueOrFlase = remoteMessage.getData().get("AnotherActivity");
        //To get a Bitmap image from the URL received
        //bitmap = getBitmapfromUrl(imageUri);
        //if (remoteMessage.getNotification().getTitle() != null)
        sendNotification(remoteMessage.getData().get("message"),TrueOrFlase,bitmap);
        showNotification(remoteMessage.getNotification().getTitle(),TrueOrFlase,bitmap);
    }
    private Bitmap getBitmapfromUrl(String imageUri) {
        try {
            URL url = new URL(imageUri);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            return bitmap;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }


    private void showNotification(String message, String trueorfalse, Bitmap bitmap) {
//        dop = new DatabaseOperations(FirebaseMessgingService.this);
//        dop.putNotification(dop,message);
//        notifications = dop.getNotification(dop);
        Intent intent = new Intent(this,Notification.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("AnotherActivity", trueorfalse);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        PendingIntent pendingIntent = PendingIntent.getActivities(this,0,
                new Intent[]{intent},PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                //.setLargeIcon(bitmap)
                .setSmallIcon(R.drawable.workplacelogo)
                .setAutoCancel(true)
                .setContentTitle(notification_count+" New Notification")
//                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
//                .setStyle(new NotificationCompat.BigPictureStyle())
//                        .bigPicture(bitmap))
                .setContentText(message)
                .setContentInfo(message)
                .setSound(defaultSoundUri)
                .setWhen(System.currentTimeMillis())
                .setTicker("ALERT")
                .setColor(Color.RED)
                .setContentIntent(pendingIntent);
//                .setStyle(prepareBigNotificationDetails());

/*        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.addLine(Html.fromHtml("<i>italic</i> <b>bold</b> text works"));
        for (String str: notifications)
        {
            inboxStyle.addLine(str);
        }

        inboxStyle.addLine("Second message:" + message);
        builder.setStyle(inboxStyle);
        builder.setNumber(count++);
*/
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(0,builder.build());
    }

    private void sendNotification(String message, String trueOrFlase, Bitmap bitmap) {
        Intent intent = new Intent(this, Notification.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("AnotherActivity", trueOrFlase);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        PendingIntent pendingIntent = PendingIntent.getActivities(this,0,
                new Intent[]{intent},PendingIntent.FLAG_ONE_SHOT);
        String channel_id = "Channel_ID";
        String channel_name = "ChannelName";
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,channel_id)
                .setContentTitle("New Notification")
                .setContentText(message)
                .setSmallIcon(R.drawable.workplacelogo)
                .setSound(defaultSoundUri)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setColor(Color.RED)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O)
        {
            NotificationChannel channel = new NotificationChannel(channel_id,channel_name,
                    NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);
        }
        manager.notify(0,builder.build());
    }
    private NotificationCompat.InboxStyle prepareBigNotificationDetails() {
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        for (String str: notifications)
        {
            inboxStyle.addLine(str);
        }
        return inboxStyle;
    }

}
