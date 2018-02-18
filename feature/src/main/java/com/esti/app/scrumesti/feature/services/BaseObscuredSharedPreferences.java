package com.esti.app.scrumesti.feature.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import java.util.Set;

public abstract class BaseObscuredSharedPreferences implements SharedPreferences {

	protected static final String UTF8 = "utf-8";
	protected static char[] SEKRIT;
	protected SharedPreferences delegate;
	protected Context context;

	protected BaseObscuredSharedPreferences(Context context, String key, String fileName) {
		if (context != null) {
			this.delegate = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
			this.context = context;
		}
		if (context != null) {
			this.delegate = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
			this.context = context;
		}
	}

	@Override public abstract String getString(String key, String defValue);

	@Nullable @Override public abstract Set<String> getStringSet(String key, Set<String> defValues);
}
