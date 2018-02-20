package com.esti.app.scrumesti.feature.views;


import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import com.esti.app.R;
import com.esti.app.scrumesti.feature.view_models.DataViewModel;
import com.esti.app.scrumesti.feature.MainActivity;
import com.esti.app.scrumesti.feature.utils.Strings;

public class TeamDetailsFragment extends Fragment {

	private DataViewModel viewModel;
	private EditText name;
	private EditText teamName;
	private Button doneButton;
	private String oldName;
	private String oldTeam;

	public static TeamDetailsFragment newInstance() {
		TeamDetailsFragment fragment = new TeamDetailsFragment();
		Bundle args = new Bundle();
		fragment.setArguments(args);
		return fragment;
	}

	@Override public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_team_details, container, false);
	}


	@Override public void onAttach(Context context) {
		super.onAttach(context);

	}

	@Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		viewModel = ViewModelProviders.of(getActivity()).get(DataViewModel.class);

		name = (EditText)view.findViewById(R.id.name);
		teamName = view.findViewById(R.id.groupName);
		doneButton = view.findViewById(R.id.doneButton);

		if (viewModel.getUser().getValue() != null) {
			name.setText(viewModel.getUser().getValue());
			oldName = viewModel.getUser().getValue();
		}

		if (viewModel.getTeam().getValue() != null) {
			teamName.setText(viewModel.getTeam().getValue());
			oldTeam = viewModel.getTeam().getValue();
		}

		doneButton.setOnClickListener(view1 -> {
			if (!Strings.isNullOrEmpty(teamName.getText().toString()) && !Strings.isNullOrEmpty(name.getText().toString())) {
				viewModel.setGroupAndUser(teamName.getText().toString(), name.getText().toString());
				((MainActivity) getActivity()).removeGroupDetailsFragment();

				if (oldName!=null && !oldName.equals(name.getText().toString()) || oldTeam!=null && !oldTeam.equals(teamName.getText().toString())) {
					viewModel.removeUserFromTeam(oldName, oldTeam);
				}
			}
		});

		name.setOnFocusChangeListener((view12, hasFocus) -> {
			if (hasFocus) {
				name.setHint("Everybody should recognize you");
			} else {
				name.setHint("");
			}
		});
		teamName.setOnFocusChangeListener((view13, hasFocus) -> {
			if (hasFocus) {
				teamName.setHint("Try something unique, short and easy");
			} else {
				teamName.setHint("");
			}
		});
	}

	@Override public void onDetach() {
		super.onDetach();
	}

}
