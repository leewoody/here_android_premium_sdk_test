package com.fancynavi.android.app;

import android.view.View;

import com.here.android.mpa.guidance.NavigationManager;
import com.here.android.mpa.guidance.SafetySpotNotification;

import java.lang.ref.WeakReference;
import java.util.EnumSet;

import static com.fancynavi.android.app.MapFragmentView.emptyMapOnTouchListener;
import static com.fancynavi.android.app.MapFragmentView.isNavigating;
import static com.fancynavi.android.app.MapFragmentView.laneInformationMapOverlay;
import static com.fancynavi.android.app.MapFragmentView.mapOnTouchListenerForNavigation;
import static com.fancynavi.android.app.MapFragmentView.navigationListeners;

class MapModeChanger {

    private static NavigationManager.SafetySpotListener simpleSafetySpotListener = new NavigationManager.SafetySpotListener() {
        @Override
        public void onSafetySpot(SafetySpotNotification safetySpotNotification) {
            super.onSafetySpot(safetySpotNotification);
        }
    };

    static void setMapUpdateMode(NavigationManager.MapUpdateMode mapUpdateMode) {
        DataHolder.getNavigationManager().setMapUpdateMode(mapUpdateMode);
    }

    static void setMapTilt(float tilt) {
        DataHolder.getMap().setTilt(tilt);
    }

    static void setMapZoomLevel(double zoomLevel) {
        DataHolder.getMap().setZoomLevel(zoomLevel);
    }

    static void addNavigationListeners() {
        if (DataHolder.getNavigationManager() != null) {
            EnumSet<NavigationManager.AudioEvent> audioEventEnumSet = EnumSet.of(
                    NavigationManager.AudioEvent.MANEUVER,
                    NavigationManager.AudioEvent.ROUTE,
                    NavigationManager.AudioEvent.SPEED_LIMIT,
                    NavigationManager.AudioEvent.GPS
            );
            DataHolder.getNavigationManager().setEnabledAudioEvents(audioEventEnumSet);
            DataHolder.getNavigationManager().removeSafetySpotListener(simpleSafetySpotListener);
            DataHolder.getNavigationManager().addLaneInformationListener(new WeakReference<>(navigationListeners.getLaneinformationListener()));
            DataHolder.getNavigationManager().addRealisticViewListener(new WeakReference<>(navigationListeners.getRealisticViewListener()));
            DataHolder.getNavigationManager().addSafetySpotListener(new WeakReference<>(navigationListeners.getSafetySpotListener()));
        }
    }

    static void removeNavigationListeners() {
        if (DataHolder.getNavigationManager() != null) {
            DataHolder.getNavigationManager().removeLaneInformationListener(navigationListeners.getLaneinformationListener());
            DataHolder.getNavigationManager().removeRealisticViewListener(navigationListeners.getRealisticViewListener());
            DataHolder.getNavigationManager().removeSafetySpotListener(navigationListeners.getSafetySpotListener());
            DataHolder.getNavigationManager().addSafetySpotListener(new WeakReference<>(simpleSafetySpotListener));
            EnumSet<NavigationManager.AudioEvent> audioEventEnumSet = EnumSet.of(
                    NavigationManager.AudioEvent.MANEUVER,
                    NavigationManager.AudioEvent.ROUTE,
                    NavigationManager.AudioEvent.SPEED_LIMIT,
                    NavigationManager.AudioEvent.GPS,
                    NavigationManager.AudioEvent.SAFETY_SPOT
            );
            DataHolder.getNavigationManager().setEnabledAudioEvents(audioEventEnumSet);

        }
    }

    static void setSimpleMode() {
        DataHolder.getActivity().findViewById(R.id.spd_text_view).setAlpha(0);
        DataHolder.getActivity().findViewById(R.id.guidance_speed_view).setAlpha(0);
        DataHolder.getActivity().findViewById(R.id.sat_map_button).setVisibility(View.GONE);
        DataHolder.getActivity().findViewById(R.id.traffic_button).setVisibility(View.GONE);
        DataHolder.getActivity().findViewById(R.id.junctionImageView).setAlpha(0);
        DataHolder.getActivity().findViewById(R.id.signpostImageView).setAlpha(0);
        DataHolder.getActivity().findViewById(R.id.sign_imageView_1).setAlpha(0);
        DataHolder.getActivity().findViewById(R.id.sign_imageView_2).setAlpha(0);
        DataHolder.getActivity().findViewById(R.id.sign_imageView_3).setAlpha(0);
        DataHolder.getActivity().findViewById(R.id.zoom_in).setVisibility(View.GONE);
        DataHolder.getActivity().findViewById(R.id.zoom_out).setVisibility(View.GONE);
        DataHolder.getActivity().findViewById(R.id.log_button).setVisibility(View.GONE);
        DataHolder.getActivity().findViewById(R.id.traffic_warning_text_view).setAlpha(0);
        new ShiftMapCenter(DataHolder.getMap(), 0.5f, 0.5f);
        DataHolder.getSupportMapFragment().setOnTouchListener(emptyMapOnTouchListener);
        if (laneInformationMapOverlay != null) {
            DataHolder.getMap().removeMapOverlay(laneInformationMapOverlay);
        }
    }

    static void setFullMode() {
        DataHolder.getActivity().findViewById(R.id.spd_text_view).setAlpha(1);
        DataHolder.getActivity().findViewById(R.id.guidance_speed_view).setAlpha(1);
        DataHolder.getActivity().findViewById(R.id.sat_map_button).setVisibility(View.VISIBLE);
        DataHolder.getActivity().findViewById(R.id.traffic_button).setVisibility(View.VISIBLE);
        DataHolder.getActivity().findViewById(R.id.junctionImageView).setAlpha(1);
        DataHolder.getActivity().findViewById(R.id.signpostImageView).setAlpha(1);
        DataHolder.getActivity().findViewById(R.id.sign_imageView_1).setAlpha(0.7f);
        DataHolder.getActivity().findViewById(R.id.sign_imageView_2).setAlpha(0.7f);
        DataHolder.getActivity().findViewById(R.id.sign_imageView_3).setAlpha(0.7f);
        if (!isNavigating) {
            DataHolder.getActivity().findViewById(R.id.zoom_in).setVisibility(View.VISIBLE);
            DataHolder.getActivity().findViewById(R.id.zoom_out).setVisibility(View.VISIBLE);
        }
        DataHolder.getActivity().findViewById(R.id.log_button).setVisibility(View.VISIBLE);
        DataHolder.getActivity().findViewById(R.id.traffic_warning_text_view).setAlpha(1);
        if (isNavigating) {
            new ShiftMapCenter(DataHolder.getMap(), 0.5f, 0.8f);
            DataHolder.getNavigationManager().setMapUpdateMode(NavigationManager.MapUpdateMode.ROADVIEW);
        } else {
            new ShiftMapCenter(DataHolder.getMap(), 0.5f, 0.6f);
        }
        DataHolder.getSupportMapFragment().setOnTouchListener(mapOnTouchListenerForNavigation);
    }
}
