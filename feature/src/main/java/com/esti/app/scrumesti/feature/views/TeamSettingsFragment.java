package com.esti.app.scrumesti.feature.views;


import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.esti.app.R;
import com.esti.app.scrumesti.feature.view_models.DataViewModel;

public class TeamSettingsFragment extends Fragment {

	private DataViewModel viewModel;
//	private EditText name;
//	private EditText teamName;
//	private Button doneButton;
//	private String oldName;
//	private String oldTeam;

	public static TeamSettingsFragment newInstance() {
		TeamSettingsFragment fragment = new TeamSettingsFragment();
		Bundle args = new Bundle();
		fragment.setArguments(args);
		return fragment;
	}

	@Override public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_settings, container, false);
	}


	@Override public void onAttach(Context context) {
		super.onAttach(context);

	}

	@Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		viewModel = ViewModelProviders.of(getActivity()).get(DataViewModel.class);


	}

	@Override public void onDetach() {
		super.onDetach();
	}

}
