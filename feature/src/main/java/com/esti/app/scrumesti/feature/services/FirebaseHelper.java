package com.esti.app.scrumesti.feature.services;

import android.app.Activity;
import android.app.Application;
import android.arch.lifecycle.LifecycleOwner;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import com.esti.app.BuildConfig;
import com.esti.app.scrumesti.feature.DataViewModel;
import com.esti.app.scrumesti.feature.utils.Strings;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import java.util.UUID;
import timber.log.Timber;

/**
 * Created by AhmedsMacbook on 14/02/2017.
 */

public class FirebaseHelper {

	public static final String SAVED_UUID = "SAVED_UUID";
	public static final String IS_A_DEVELOPER = "is_a_developer";
	private static final String USER = "USER";
	private static final String TEAM = "TEAM";
	private static final String TEAM_ACTIVE = "TEAM_ACTIVE";
	private static final String USER_ACTIVE = "USER_ACTIVE";

	public static String userUUID = UUID.randomUUID().toString();
	private static FirebaseRemoteConfig remoteConfigInstance;
	public static FirebaseAnalytics firebaseAnalyticsInstance;

	public static void init(final Application application) {

		FirebaseApp firebaseApp = FirebaseApp.initializeApp(application);
		Timber.d("FirebaseApp " + firebaseApp);

		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(application);

		String savedUUID = sharedPreferences.getString(SAVED_UUID, null);
		if (savedUUID == null) {
			sharedPreferences.edit().putString(SAVED_UUID, userUUID).apply();
		} else {
			userUUID = savedUUID;
		}
		Timber.d(" uuid " + userUUID);

		remoteConfigInstance = FirebaseRemoteConfig.getInstance();
		firebaseAnalyticsInstance = FirebaseAnalytics.getInstance(application);

		firebaseAnalyticsInstance.setUserId(userUUID);
		FirebaseRemoteConfigSettings config = new FirebaseRemoteConfigSettings.Builder().setDeveloperModeEnabled(BuildConfig.DEBUG).build();
		remoteConfigInstance.setConfigSettings(config);

//		remoteConfigInstance.setDefaults(R.xml.remote_config_defaults);

		remoteConfigInstance.fetch(1).
				addOnCompleteListener(task -> {
					if (task.isSuccessful()) {
						Timber.d("Fetch Succeeded");
						remoteConfigInstance.activateFetched();
					} else {
						Timber.d("Fetch Failed");
					}
				});

		firebaseAnalyticsInstance.logEvent("app_init", new Bundle());

		firebaseAnalyticsInstance.setUserProperty("VERSION_CODE", String.valueOf(BuildConfig.VERSION_CODE));
		if (InstantAppsUtil.isInstantApp(application)) {
			firebaseAnalyticsInstance.setUserProperty("app_type", "instant");
		} else {
			firebaseAnalyticsInstance.setUserProperty("app_type", "installed");
		}
		String developersAndroidIds = getStringRemoteConfig("developers_android_ids");
		if (!Strings.isNullOrEmpty(developersAndroidIds)) {
			if (developersAndroidIds.contains(Settings.Secure.getString(application.getContentResolver(), Settings.Secure.ANDROID_ID))) {
				firebaseAnalyticsInstance.setUserProperty("developer_device", "true");
				Timber.d("Firebase developer_device is true");
			} else {
				firebaseAnalyticsInstance.setUserProperty("developer_device", "false");
				Timber.d("Firebase developer_device is false");
			}
		}

	}

	public static boolean getBooleanRemoteConfig(String key) {
		boolean results = remoteConfigInstance.getBoolean(key);
		Timber.d("Firebase remote config for " + key + " was " + results);
		return results;

	}

	public static long getLongRemoteConfig(String key) {
		long results = remoteConfigInstance.getLong(key);
		Timber.d("Firebase remote config for " + key + " was " + results);
		return results;

	}

	public static String getStringRemoteConfig(String key) {
		String results = remoteConfigInstance.getString(key);
		Timber.d("Firebase remote config for " + key + " was " + results);
		return results;
	}

	public static void setScreenView(Activity activity, String screenName, String screenClass) {
		if (firebaseAnalyticsInstance != null) {
			firebaseAnalyticsInstance.setCurrentScreen(activity, screenName, screenClass);
		}
	}

	public static boolean isADeveloper() {
		return getBooleanRemoteConfig(FirebaseHelper.IS_A_DEVELOPER);
	}

	public static void addListeners(LifecycleOwner activity, DataViewModel viewModel) {
		viewModel.getUser().observe(activity, s -> {
			firebaseAnalyticsInstance.setUserProperty(USER, s);
						Bundle bundle = new Bundle();
			bundle.putString("UserID", userUUID);
			bundle.putString(USER, s);
			firebaseAnalyticsInstance.logEvent(USER_ACTIVE, bundle);
		});
		viewModel.getTeam().observe(activity, s -> {
			firebaseAnalyticsInstance.setUserProperty(TEAM, s);
			Bundle bundle = new Bundle();
			bundle.putString("UserID", userUUID);
			bundle.putString(TEAM, s);
			firebaseAnalyticsInstance.logEvent(TEAM_ACTIVE, bundle);
		});
	}
}