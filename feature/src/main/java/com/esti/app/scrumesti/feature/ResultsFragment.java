package com.esti.app.scrumesti.feature;


import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.esti.app.R;
import com.esti.app.scrumesti.feature.services.ScrumRules;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ResultsFragment extends Fragment {

	private DataViewModel viewModel;
	private RecyclerView recyclerView;
	private TextView total;
	private TextView welcomeText;
	private List<User> usersList = new ArrayList<>();
	private ScoresRecyclerViewAdapter adapter;
	private boolean isInFront = true;


	public static ResultsFragment newInstance(String param1, String param2) {
		ResultsFragment fragment = new ResultsFragment();
		Bundle args = new Bundle();
		fragment.setArguments(args);
		return fragment;
	}

	@Override public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_results, container, false);
	}

	@Override public void onAttach(Context context) {
		super.onAttach(context);

	}

	@Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		recyclerView = view.findViewById(R.id.recyclerView);
		total = view.findViewById(R.id.total);
		welcomeText = view.findViewById(R.id.welcomeText);
		welcomeText.setText(Html.fromHtml(getString(R.string.welcome_text)));
		total.setOnClickListener(view1 -> {
			for(int childCount = recyclerView.getChildCount(), i = 0; i < childCount; ++i) {
//				((ScoresRecyclerViewAdapter.ViewHolder)recyclerView.getChildViewHolder(recyclerView.getChildAt(i))).flipCard(isInFront);
			}
			isInFront = !isInFront;
		});
		viewModel = ViewModelProviders.of(getActivity()).get(DataViewModel.class);
		viewModel.getUsersList().observe(getActivity(), users -> {
			usersList.clear();
			usersList.addAll(users);
			adapter.notifyDataSetChanged();
		});
		viewModel.getAllVoteDone().observe(getActivity(), aBoolean -> adapter.setAllVoteDone(aBoolean));
		viewModel.getFullGroupData().observe(getActivity(), fullGroupData -> {

			int numberOfUsers = 0;
			int sumCOMPLEXITY = 0;
			int sumCLARITY = 0;
			int sumDEPENDENCY = 0;
			int userHwoVoted = 0;
			for (HashMap.Entry<String, HashMap<String, Integer>> user : fullGroupData.entrySet()) {
				numberOfUsers++;
				int userVote = 0;
				for (HashMap.Entry<String, Integer> vote : user.getValue().entrySet()) {
					userVote++;
				}
				sumCOMPLEXITY += user.getValue().containsKey(DataViewModel.COMPLEXITY) ? user.getValue().get(DataViewModel.COMPLEXITY) : 0;
				sumCLARITY += user.getValue().containsKey(DataViewModel.CLARITY) ? user.getValue().get(DataViewModel.CLARITY) : 0;
				sumDEPENDENCY += user.getValue().containsKey(DataViewModel.DEPENDENCY) ? user.getValue().get(DataViewModel.DEPENDENCY) : 0;
				if (userVote == 3) {
					userHwoVoted++;
				}
			}
			if (numberOfUsers > 2) {
				welcomeText.setVisibility(View.GONE);
			} else {
				welcomeText.setVisibility(View.VISIBLE);
			}

			if (userHwoVoted == numberOfUsers) {
				total.setText(ScrumRules.scrumerSum(sumCOMPLEXITY / numberOfUsers, sumCLARITY / numberOfUsers, sumDEPENDENCY / numberOfUsers).toString());
			} else {
				total.setText("?");
			}

		});
		adapter = new ScoresRecyclerViewAdapter(getContext(), usersList, position -> {});
		recyclerView.setAdapter(adapter);
		recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2, GridLayoutManager.HORIZONTAL, false));
	}


	@Override public void onDetach() {
		super.onDetach();
	}

}
