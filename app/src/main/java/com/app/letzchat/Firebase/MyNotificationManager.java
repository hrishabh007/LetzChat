package com.app.letzchat.Firebase;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;




/**
 * Created by Rishabh on 2/8/2018.
 */

public class MyNotificationManager {

    public static int ID_SMALL_NOTIFICATION = 115;
    public static final String NOTI_DATA = "NotiData";

    public static final int NOTI_TYPE_HOLIDAY = 1;
    public static final int NOTI_TYPE_EVENT = 2;
    public static final int NOTI_TYPE_HOMEWORK = 3;
    public static final int NOTI_TYPE_MESSAGE = 4;
    public static final int NOTI_TYPE_ATTENDANCE = 5;


    private Context mCtx;
    private NotiModel notiModel;

    public MyNotificationManager(Context mCtx, NotiModel notiModel) {
        this.mCtx = mCtx;
        this.notiModel = notiModel;
       // showNotification();
    }


   /* public void showNotification() {
        try{
            Intent mNotificationIntent = new Intent(mCtx, NavigateActivity.class);
            mNotificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);



                if(notiModel.getType() == NOTI_TYPE_MESSAGE){
                    new MessageThread(mCtx).start();
                }
                else if(notiModel.getType() == NOTI_TYPE_EVENT){
                    new EventThread(mCtx).start();
                }
                else if(notiModel.getType() == NOTI_TYPE_HOMEWORK){
                    new HomeworkThread(mCtx).start();
                }
                else if(notiModel.getType() == NOTI_TYPE_HOLIDAY){
                    new HolidayThread(mCtx).start();
                }
                else if(notiModel.getType() == NOTI_TYPE_ATTENDANCE){
                    new AttendanceThread(mCtx).start();
                    new HolidayThread(mCtx).start();
                }



            Bundle bundle = new Bundle();
            bundle.putSerializable(NOTI_DATA, notiModel);
            mNotificationIntent.putExtras(bundle);
            PendingIntent pendingIntent = PendingIntent.getActivity(mCtx, 0,
                    mNotificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);


            NotificationManager mNotificationManager = (NotificationManager) mCtx
                    .getSystemService(Context.NOTIFICATION_SERVICE);

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mCtx);

            mBuilder.setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(notiModel.getTitle());
            mBuilder.setPriority(Notification.PRIORITY_HIGH);
            mBuilder.setContentIntent(pendingIntent);
            mBuilder.setAutoCancel(true);

            String uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION).toString();
            if(!C.isEmpty(uri)){
                mBuilder.setSound(Uri.parse(uri));
            }

            mBuilder.setContentText(notiModel.getMessage());
            mNotificationManager.notify(ID_SMALL_NOTIFICATION++, mBuilder.build());
        }catch (Exception e){
            e.printStackTrace();
        }
    }*/



}