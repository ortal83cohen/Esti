package com.esti.app.scrumesti.feature.services;

import android.app.Activity;
import android.content.Context;
import com.google.android.instantapps.InstantApps;

public class InstantAppsUtil {

	public static void showInstallPrompt(Activity context, int i, String referrer) {
		InstantApps.showInstallPrompt(context, i, referrer);
	}

	public static boolean isInstantApp(Context context) {
		return InstantApps.isInstantApp(context);
	}
}
