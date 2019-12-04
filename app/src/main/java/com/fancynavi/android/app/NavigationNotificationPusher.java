package com.fancynavi.android.app;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;

import com.here.android.mpa.common.OnScreenCaptureListener;
import com.here.android.mpa.guidance.NavigationManager;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapOffScreenRenderer;
import com.here.android.mpa.routing.Maneuver;

import static com.fancynavi.android.app.DataHolder.CHANNEL;
import static com.fancynavi.android.app.DataHolder.FOREGROUND_SERVICE_ID;
import static com.fancynavi.android.app.DataHolder.TAG;

class NavigationNotificationPusher {
    NavigationNotificationPusher() {

        MapOffScreenRenderer mapOffScreenRenderer = new MapOffScreenRenderer(DataHolder.getActivity());
        mapOffScreenRenderer.setSize(1080, 640);
        mapOffScreenRenderer.setMap(DataHolder.getMap());
//        mapOffScreenRenderer.setBlockingRendering(true);
//        DataHolder.setMapOffScreenRenderer(mapOffScreenRenderer);

        Intent notificationIntent = new Intent(DataHolder.getActivity(), MainActivity.class);
        notificationIntent.setAction(Intent.ACTION_MAIN);
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        PendingIntent pendingIntent = PendingIntent.getActivity(DataHolder.getActivity(), 0, notificationIntent, 0);

        Maneuver nextManeuver = DataHolder.getNavigationManager().getNextManeuver();
        DataHolder.getNavigationManager().setMapUpdateMode(NavigationManager.MapUpdateMode.NONE);
        DataHolder.getMap().setOrientation(nextManeuver.getMapOrientation());
        new ShiftMapCenter(DataHolder.getMap(), 0.5f, 0.5f);
        DataHolder.getMap().setTilt(0);
        DataHolder.getMap().setCenter(nextManeuver.getCoordinate(), Map.Animation.NONE);
        DataHolder.getMap().setZoomLevel(18);
        String nextRoadName = DataHolder.getNavigationManager().getNextManeuver().getNextRoadName();
        Maneuver.Turn turn = DataHolder.getNavigationManager().getNextManeuver().getTurn();
        TurnPresenter turnPresenter = new TurnPresenter(turn);
        String localizedNameOfTurn = turnPresenter.getTurnLocalizedName();
        long distance = DataHolder.getNavigationManager().getNextManeuverDistance();
        String distanceString;
        if (distance >= 1000) {
            distanceString = distance / 1000 + "." + distance % 1000 + "公里";
        } else {
            distanceString = distance + "公尺";
        }
        mapOffScreenRenderer.start();
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mapOffScreenRenderer.getScreenCapture(new OnScreenCaptureListener() {
            @Override
            public void onScreenCaptured(Bitmap bitmap) {
                Log.d(TAG, "onScreenCaptured");
                Notification notification =
                        new Notification.Builder(DataHolder.getActivity().getApplicationContext(), CHANNEL)
                                .setContentTitle("Guidance")
                                .setContentText("Guidance in progress ...")
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setLargeIcon(bitmap)
                                .setStyle(new Notification.BigPictureStyle().bigPicture(bitmap))
                                .setContentTitle(distanceString)
                                .setContentText(localizedNameOfTurn + "進入" + nextRoadName)
                                .setContentIntent(pendingIntent)
                                .setLocalOnly(true)
                                .build();
                DataHolder.getNotificationManager().cancel(FOREGROUND_SERVICE_ID);
                DataHolder.getNotificationManager().notify(FOREGROUND_SERVICE_ID, notification);
            }
        });
        mapOffScreenRenderer.stop();
        mapOffScreenRenderer = null;
    }
}
