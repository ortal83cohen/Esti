package com.esti.app.scrumesti.feature;


import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import com.esti.app.R;
import com.esti.app.scrumesti.feature.utils.Strings;

public class GroupDetailsFragment extends Fragment {

	private DataViewModel viewModel;
	private EditText name;
	private EditText groupName;
	private Button doneButton;

	public static GroupDetailsFragment newInstance() {
		GroupDetailsFragment fragment = new GroupDetailsFragment();
		Bundle args = new Bundle();
		fragment.setArguments(args);
		return fragment;
	}

	@Override public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_group_details, container, false);
	}


	@Override public void onAttach(Context context) {
		super.onAttach(context);

	}

	@Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		viewModel = ViewModelProviders.of(getActivity()).get(DataViewModel.class);

		name = view.findViewById(R.id.name);
		groupName = view.findViewById(R.id.groupName);
		doneButton = view.findViewById(R.id.doneButton);

		if (viewModel.getUser().getValue() != null) {
			name.setText(viewModel.getUser().getValue());
		}

		if (viewModel.getGroup().getValue() != null) {
			groupName.setText(viewModel.getGroup().getValue());
		}

		doneButton.setOnClickListener(new OnClickListener() {
			@Override public void onClick(View view) {
				if (!Strings.isNullOrEmpty(groupName.getText().toString()) && !Strings.isNullOrEmpty(name.getText().toString())) {
					viewModel.setGroupAndUser(groupName.getText().toString(), name.getText().toString());
					((MainActivity) getActivity()).removeGroupDetailsFragment();
				}
			}
		});

		name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override public void onFocusChange(View view, boolean hasFocus) {
				if (hasFocus) {
					name.setHint("Everybody should recognize you");
				} else {
					name.setHint("");
				}
			}
		});
		groupName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override public void onFocusChange(View view, boolean hasFocus) {
				if (hasFocus) {
					groupName.setHint("Try something sort and easy");
				} else {
					groupName.setHint("");
				}
			}
		});
	}

	@Override public void onDetach() {
		super.onDetach();
	}

}
