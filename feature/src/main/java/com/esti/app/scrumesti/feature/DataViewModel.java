package com.esti.app.scrumesti.feature;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.os.Handler;
import com.esti.app.scrumesti.feature.services.ObscuredSharedPreferences;
import com.esti.app.scrumesti.feature.utils.Strings;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import timber.log.Timber;

/**
 * Created by Ortal Cohen on 11/2/2018.
 */

public class DataViewModel extends ViewModel {

	static final String DATA_VIEW_MODEL = "DATA_VIEW_MODEL";
	public static final String USERS = "users";
	public static final String USER_NAME = "USER_NAME";
	public static final String GROUPS = "groups";
	public static final String GROUP_NAME = "GROUP_NAME";
	public static final String COMPLEXITY = "complexity";
	public static final String CLARITY = "clarity";
	public static final String DEPENDENCY = "dependency";
	public static final String NEXT_ESTI = "NEXT_ESTI";
	public static final String APP_VERSION = "APP_VERSION";
	public static final String DATE = "DATE";
	public static final String NEXT_ESTI_STATUS_COUNTING = "NEXT_ESTI_STATUS_COUNTING";
	public static final String NEXT_ESTI_STATUS_SET = "NEXT_ESTI_STATUS_SET";
	public static final String NEXT_ESTI_STATUS_ENABLE = "NEXT_ESTI_STATUS_ENABLE";

	private final MutableLiveData<String> user = new MutableLiveData<>();
	private final MutableLiveData<String> group = new MutableLiveData<>();
	private final MutableLiveData<HashMap<String, HashMap<String, Integer>>> fullGroupData = new MutableLiveData<>();
	private final MutableLiveData<String> nextEstiStatus = new MutableLiveData<>();
	private final MutableLiveData<Boolean> allVoteDone = new MutableLiveData<>();
	private DatabaseReference myRef;
	private final FirebaseDatabase database;
	private ObscuredSharedPreferences obscuredSharedPreferences;
	private boolean isActiveUser = true;
	HashMap<String, Integer> selected = new HashMap<>();
	private final LiveData<String> massage = Transformations.map(fullGroupData, new Function<HashMap<String, HashMap<String, Integer>>, String>() {
		private HashMap<String, HashMap<String, Integer>> lastInput = new HashMap<>();

		@Override public String apply(HashMap<String, HashMap<String, Integer>> input) {
			String massage = "";
			if (input != null) {
				if (input.size() != lastInput.size()) {
					for (String userName : input.keySet()) {
						if (!lastInput.containsKey(userName) && !userName.equals(user.getValue())) {
							massage += userName + " Joined";
						}
					}
					for (String userName : lastInput.keySet()) {
						if (!input.containsKey(userName) && !userName.equals(user.getValue())) {
							massage += userName + " Left";
						}
					}
				}
			}
			lastInput = input;
			return massage;
		}
	});

	private final LiveData<List<User>> usersList = Transformations.map(fullGroupData, input -> {
		List<User> usersList = new ArrayList<>();
		boolean votesDone = true;
		for (Map.Entry<String, HashMap<String, Integer>> user : input.entrySet()) {
			if (Strings.isNullOrNegative(user.getValue().get(COMPLEXITY)) || Strings.isNullOrNegative(user.getValue().get(CLARITY)) || Strings.isNullOrNegative(user.getValue().get(DEPENDENCY))) {
				votesDone = false;
			}
			usersList.add(user.getKey().equals(this.user.getValue()) ? 0: usersList.size(), new User(user.getKey(),
					user.getValue().get(COMPLEXITY) == null ? -1 : user.getValue().get(COMPLEXITY).intValue(),
					user.getValue().get(CLARITY) == null ? -1 : user.getValue().get(CLARITY).intValue(),
					user.getValue().get(DEPENDENCY) == null ? -1 : user.getValue().get(DEPENDENCY).intValue(), user.getKey().equals(this.user.getValue())));
			if (!user.getKey().equals(this.user.getValue())) {
			}
			allVoteDone.postValue(votesDone);
		}
		return usersList;
	});

	ValueEventListener valueEventListener = new ValueEventListener() {

		@Override public void onDataChange(DataSnapshot dataSnapshot) {
			Timber.tag(DATA_VIEW_MODEL).e(getMethodName());
			Handler handler = new Handler();
			if (dataSnapshot.getValue() == null) {
				fullGroupData.postValue(null);
				if (isActiveUser) {
					handler.postDelayed(() -> myRef.child(group.getValue()).child(USERS).child(user.getValue()).setValue(user.getValue()), 500);
				}
			} else {
				String newNextEsti = dataSnapshot.child(NEXT_ESTI).getValue(String.class);
				if (!Strings.isNullOrEmpty(newNextEsti)) {
					nextEstiStatus.setValue(newNextEsti);
					if (newNextEsti.equals(NEXT_ESTI_STATUS_COUNTING)) {
						final Handler h = new Handler();
						h.postDelayed(() -> {
							if (nextEstiStatus.getValue().equals(NEXT_ESTI_STATUS_COUNTING)) {
								myRef.child(group.getValue()).child(NEXT_ESTI).removeValue();
								nextEstiStatus.postValue(NEXT_ESTI_STATUS_ENABLE);
							}
						}, 6000);
					} else if (newNextEsti.equals(NEXT_ESTI_STATUS_SET)) {
						if (isActiveUser) {
							handler.postDelayed(() -> {
								myRef.child(group.getValue()).child(USERS).child(user.getValue()).setValue(user.getValue());
								myRef.child(group.getValue()).child(NEXT_ESTI).removeValue();
							}, 500);
						}
					}
				}
			}
			HashMap<String, HashMap<String, Integer>> fullGroup = new HashMap<>();
			//Get map of users in datasnapshot
			for (DataSnapshot user : dataSnapshot.child(USERS).getChildren()) {
				HashMap<String, Integer> votes = new HashMap<>();
				for (DataSnapshot vote : user.getChildren()) {
					votes.put(vote.getKey(), vote.getValue(Integer.class));
				}
				fullGroup.put(user.getKey(), votes);
			}
			fullGroupData.postValue(fullGroup);
		}

		@Override public void onCancelled(DatabaseError databaseError) {
			//handle databaseError
		}
	};

	public LiveData<String> getMassage() {
		return massage;
	}

	public DataViewModel() {
		Timber.tag(DATA_VIEW_MODEL).e(getMethodName());
		database = FirebaseDatabase.getInstance();
		myRef = database.getReference(GROUPS);
	}

	public LiveData<List<User>> getUsersList() {
		return usersList;
	}

	public void init(BaseActivity context) {
		Timber.tag(DATA_VIEW_MODEL).e(getMethodName());
		obscuredSharedPreferences = new ObscuredSharedPreferences(context, "key", "fileName");
		group.observe(context, string -> {
			obscuredSharedPreferences.edit().putString(GROUP_NAME, string).commit();
			myRef.child(string).addValueEventListener(valueEventListener);
		});
		user.observe(context, string -> obscuredSharedPreferences.edit().putString(USER_NAME, string).commit());
		loadFromCache();
	}

	private void loadFromCache() {
		Timber.tag(DATA_VIEW_MODEL).e(getMethodName());
		String userName = obscuredSharedPreferences.getString(USER_NAME, "");
		String groupName = obscuredSharedPreferences.getString(GROUP_NAME, "");
		if (!Strings.isNullOrEmpty(groupName)) {
			setGroupAndUser(groupName, userName);
		}
	}

	public LiveData<HashMap<String, HashMap<String, Integer>>> getFullGroupData() {
		Timber.tag(DATA_VIEW_MODEL).e(getMethodName());
		return fullGroupData;
	}

	public LiveData<String> getUser() {
		Timber.tag(DATA_VIEW_MODEL).e(getMethodName());
		return user;
	}

	public MutableLiveData<String> getTeam() {
		Timber.tag(DATA_VIEW_MODEL).e(getMethodName());
		return group;
	}

	public void setChecked(String s, int i) {
		Timber.tag(DATA_VIEW_MODEL).e(getMethodName());
		if (i == -1) {
			selected.remove(s);
		} else {
			selected.put(s, i);
			myRef.child(getTeam().getValue()).child(USERS).child(getUser().getValue()).setValue(selected);
		}
	}

	@Override protected void onCleared() {
		Timber.tag(DATA_VIEW_MODEL).e(getMethodName());
		super.onCleared();
		onInActive();
	}

	public void onActive() {
		Timber.tag(DATA_VIEW_MODEL).e(getMethodName());
		isActiveUser = true;
		if (isUserAndGroupExist()) {
//			myRef.child(group.getValue()).child(APP_VERSION).setValue(BuildConfig.VERSION_CODE);
//			myRef.child(group.getValue()).child(DATE).setValue(DateFormat.getDateTimeInstance().format(new Date()));
			setGroupAndUser(group.getValue(), user.getValue());
		}
	}

	public void onInActive() {
		Timber.tag(DATA_VIEW_MODEL).e(getMethodName());
		try {
			isActiveUser = false;
			myRef.child(group.getValue()).child(USERS).child(user.getValue()).removeValue();
		}catch (Exception e){Timber.e(e);}
	}

	public void resetGroup() {
		Timber.tag(DATA_VIEW_MODEL).e(getMethodName());
		if (!Strings.isNullOrEmpty(getTeam().getValue())) {
			myRef.child(group.getValue()).child(USERS).removeValue();
		}
	}


	public void setGroupAndUser(String groupName, String userName) {
		Timber.tag(DATA_VIEW_MODEL).e(getMethodName());
		group.setValue(groupName);
		user.setValue(userName);
		myRef.child(groupName).child(USERS).child(userName).setValue(selected.isEmpty() ? userName : selected);
	}

	public LiveData<Boolean> getAllVoteDone() {
		return allVoteDone;
	}

	public void setGroup(String groupName) {
		Timber.tag(DATA_VIEW_MODEL).e(getMethodName());
		group.setValue(groupName);
	}

	public boolean isUserAndGroupExist() {
		Timber.tag(DATA_VIEW_MODEL).e(getMethodName());
		if (Strings.isNullOrEmpty(group.getValue()) || Strings.isNullOrEmpty(user.getValue())) {
			return false;
		}
		return true;
	}

	public LiveData<String> getNextEstiStatus() {
		return nextEstiStatus;
	}

	public static String getMethodName() {
		return Thread.currentThread().getStackTrace()[3].getMethodName();
	}

	public void askForNextEsti() {
		if (nextEstiStatus.getValue() != null && nextEstiStatus.getValue().equals(NEXT_ESTI_STATUS_COUNTING)) {
			setNextEsti();
		} else {
			myRef.child(group.getValue()).child(NEXT_ESTI).setValue(NEXT_ESTI_STATUS_COUNTING);
		}
	}

	public void setNextEsti() {
		myRef.child(group.getValue()).child(NEXT_ESTI).setValue(NEXT_ESTI_STATUS_SET);
	}

	public void removeUserFromTeam(String oldName, String oldTeam) {
		myRef.child(oldTeam).child(USERS).child(oldName).removeValue();
	}
}
