package com.esti.app.scrumesti.feature;

import static android.arch.lifecycle.Lifecycle.Event.ON_CREATE;
import static android.arch.lifecycle.Lifecycle.Event.ON_DESTROY;
import static android.arch.lifecycle.Lifecycle.Event.ON_START;
import static android.arch.lifecycle.Lifecycle.Event.ON_STOP;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LifecycleRegistry;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Ortal Cohen on 11/2/2018.
 */

public class BaseActivity extends AppCompatActivity implements LifecycleOwner {


	private LifecycleRegistry lifecycleRegistry = new LifecycleRegistry(this);

	@Override public LifecycleRegistry getLifecycle() {
		return lifecycleRegistry;
	}


	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		lifecycleRegistry.handleLifecycleEvent(ON_CREATE);

	}

	@Override protected void onStart() {
		super.onStart();
		lifecycleRegistry.handleLifecycleEvent(ON_START);
	}

	@Override protected void onStop() {
		lifecycleRegistry.handleLifecycleEvent(ON_STOP);
		super.onStop();
	}

	@Override protected void onDestroy() {
		lifecycleRegistry.handleLifecycleEvent(ON_DESTROY);
		super.onDestroy();
	}

	@Override public void finish() {
		super.finish();
	}

}

