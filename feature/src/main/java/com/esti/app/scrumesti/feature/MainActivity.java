package com.esti.app.scrumesti.feature;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.esti.app.R;
import com.esti.app.scrumesti.feature.services.FirebaseHelper;
import com.esti.app.scrumesti.feature.utils.Strings;
import com.esti.app.scrumesti.feature.view_models.DataViewModel;
import com.esti.app.scrumesti.feature.views.TeamSettingsFragment;
import com.esti.app.scrumesti.feature.views.TeamDetailsFragment;
import com.google.android.gms.tasks.Task;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import java.util.Random;

/**
 * Created by Ortal Cohen on 11/2/2018.
 */

public class MainActivity extends BaseActivity {

	android.support.v7.widget.Toolbar toolbar;
	private DataViewModel viewModel;
	private TeamDetailsFragment teamDetailsFragment = null;
	private TeamSettingsFragment teamSettingsFragment = null;

	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		viewModel = ViewModelProviders.of(this).get(DataViewModel.class);
		viewModel.init(this);
		viewModel.getMassage().observe(this, string -> {
			if (!Strings.isNullOrEmpty(string)) {
				Toast.makeText(getBaseContext(), string, Toast.LENGTH_SHORT).show();
			}
		});
		FirebaseHelper.addListeners(this,viewModel);
		Uri uriData = getIntent().getData();
		if (uriData != null) {
			deepLink(uriData);
		}
	}

	private void deepLink(Uri uriData) {
		if (!Strings.isNullOrEmpty(uriData.getPath())) {
			viewModel.setTeam(uriData.getPath().replace("/", ""));
			addTeamDetailsFragment();
		}
	}

	@Override protected void onPostCreate(@Nullable Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		if (!viewModel.isUserAndGroupExist()) {
			addTeamDetailsFragment();
		}
	}

	@Override public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override protected void onStart() {
		super.onStart();
		viewModel.onActive();
	}

	@Override protected void onStop() {
		super.onStop();
		viewModel.onInActive();
	}

	@Override public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getTitle().equals(getString(R.string.action_settings))) {
			addSettingsFragment();
		} else if (item.getTitle().equals(getString(R.string.feedback))) {
			Intent send = new Intent(Intent.ACTION_SENDTO);
			String uriText = "mailto:" + Uri.encode("estiscrum@gmail.com") + "?subject=" + Uri.encode("Esti feedback");
			Uri uri = Uri.parse(uriText);
			send.setData(uri);
			startActivity(Intent.createChooser(send, "Send mail"));
		} else if (item.getTitle().equals(getString(R.string.action_how_to))) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(Html.fromHtml(getString(R.string.how_to_text)));
			builder.setPositiveButton("OK", null);
			AlertDialog dialog = builder.create();
			dialog.show();
		} else if (item.getTitle().equals(getString(R.string.action_edit))) {
			addTeamDetailsFragment();
		} else if (item.getTitle().equals(getString(R.string.action_reset))) {
			viewModel.resetGroup();
		} else if (item.getTitle().equals(getString(R.string.action_share))) {
			String newGroupName;
			if (Strings.isNullOrEmpty(viewModel.getTeam().getValue())) {
				Random r = new Random();
				viewModel.setTeam(newGroupName = String.valueOf(r.nextInt()));
			} else {
				newGroupName = viewModel.getTeam().getValue();
			}
			createLink(newGroupName);

		}

		return super.onOptionsItemSelected(item);
	}

	private void createLink(String s) {
		Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink().setLink(Uri.parse("http://app.esti.com/" + s)).setDynamicLinkDomain("mxg55.app.goo.gl")
				// Open links with this app on Android
				.setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
				// Open links with com.example.ios on iOS
				.setIosParameters(new DynamicLink.IosParameters.Builder("get an android").build()).buildShortDynamicLink().addOnCompleteListener(this, task -> {
					if (task.isSuccessful()) {
						Intent sharingIntent = new Intent(Intent.ACTION_SEND);
						sharingIntent.setType("text/plain");
						sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here");
						sharingIntent.putExtra(Intent.EXTRA_TEXT, task.getResult().getShortLink().toString());
						startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.share_using)));
					} else {
						Toast.makeText(getApplicationContext(), "internet error", Toast.LENGTH_LONG).show();
					}
				});
	}

	private void addTeamDetailsFragment() {
		if (teamDetailsFragment == null) {
			teamDetailsFragment = TeamDetailsFragment.newInstance();
		}
		addFragment(teamDetailsFragment);
	}

	private void addSettingsFragment() {
		if (teamSettingsFragment == null) {
			teamSettingsFragment = TeamSettingsFragment.newInstance();
		}
		if(viewModel.isUserAndGroupExist()) {
			addFragment(teamSettingsFragment);
		}else {
			Toast.makeText(this,R.string.must_have_team,Toast.LENGTH_LONG).show();
		}
	}

	private void addFragment(Fragment fragment) {
		toolbar.getMenu().setGroupEnabled(R.id.menuOnMain, false);

		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.setCustomAnimations(R.animator.slow_fade_in, 0);
		fragmentTransaction.replace(R.id.container_fragment, fragment, "TeamDetailsFragment");
		fragmentTransaction.commit();
	}

	public void removeGroupDetailsFragment() {
		removeFragment(teamDetailsFragment);
	}
	public void removeSettingsFragment() {
		removeFragment(teamSettingsFragment);
	}

	private void removeFragment(Fragment Fragment) {
		toolbar.getMenu().setGroupEnabled(R.id.menuOnMain, true);

		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.setCustomAnimations(R.animator.slow_fade_in, 0);
		fragmentTransaction.remove(Fragment);
		fragmentTransaction.commit();
	}

	@Override public void onBackPressed() {
		if (teamDetailsFragment != null && teamDetailsFragment.isAdded() && viewModel.isUserAndGroupExist()) {
			removeGroupDetailsFragment();
			return;
		}		if (teamSettingsFragment != null && teamSettingsFragment.isAdded() ) {
			removeSettingsFragment();
			return;
		}
		super.onBackPressed();
	}

}
