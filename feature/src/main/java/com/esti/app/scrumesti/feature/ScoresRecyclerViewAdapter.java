package com.esti.app.scrumesti.feature;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.esti.app.R;
import com.esti.app.scrumesti.feature.services.ScrumRules;
import java.util.List;

/**
 * Created by Ortal Cohen on 13/2/2018.
 */
public class ScoresRecyclerViewAdapter extends RecyclerView.Adapter<ScoresRecyclerViewAdapter.ViewHolder> {

	private static final String TAG = ScoresRecyclerViewAdapter.class.getSimpleName();

	Context context;
	private List<User> list;
	private OnItemClickListener onItemClickListener;
	private Boolean allVoteDone = false;
	private AnimatorSet mSetRightOut;
	private AnimatorSet mSetLeftIn;

	public ScoresRecyclerViewAdapter(Context context, List<User> list, OnItemClickListener onItemClickListener) {
		this.context = context;
		this.list = list;
		this.onItemClickListener = onItemClickListener;
		loadAnimations();
	}

	public void setAllVoteDone(Boolean allVoteDone) {
		this.allVoteDone = allVoteDone;
		notifyDataSetChanged();
	}

	public void setAll(boolean b) {

	}

	public class ViewHolder extends RecyclerView.ViewHolder {

		private final View mCardBackLayout;
		private final View mCardFrontLayout;
		TextView complexityText;
		TextView clarityText;
		TextView dependencyText;
		TextView name;
		TextView nameBack;
		TextView total;
		private boolean mIsBackVisible;

		public ViewHolder(View itemView) {
			super(itemView);
			name = itemView.findViewById(R.id.name);
			nameBack = itemView.findViewById(R.id.name_back);
			total = itemView.findViewById(R.id.total);
			mCardBackLayout = itemView.findViewById(R.id.card_back);
			mCardFrontLayout = itemView.findViewById(R.id.card_front);
			complexityText = itemView.findViewById(R.id.complexityText);
			clarityText = itemView.findViewById(R.id.clarityText);
			dependencyText = itemView.findViewById(R.id.dependencyText);
			changeCameraDistance(itemView);
		}

		public void bind(final User model, final OnItemClickListener listener) {

			itemView.setOnClickListener(view -> {
				flipCard(null);
			});
			if (model.isMe) {
				name.setTypeface(Typeface.DEFAULT_BOLD);
				name.setText("Me");
				nameBack.setTypeface(Typeface.DEFAULT_BOLD);
				nameBack.setText("Me");
			} else {
				name.setTypeface(Typeface.DEFAULT);
				name.setText(model.name);
				nameBack.setTypeface(Typeface.DEFAULT);
				nameBack.setText(model.name);
			}
			if (!allVoteDone && !model.isMe) {
				total.setText((model.dependency >= 0 && model.clarity >= 0 && model.complexity >= 0) ? "✔" : "?");
				complexityText.setText(model.complexity >= 0 ? "✔" : "?");
				clarityText.setText(model.clarity >= 0 ? "✔" : "?");
				dependencyText.setText(model.dependency >= 0 ? "✔" : "?");

			} else {
				total.setText(ScrumRules.scrumerSum(model.complexity, model.clarity, model.dependency).toString());
				complexityText.setText(model.complexity < 0 ? "-" : model.complexity  + "%");
				clarityText.setText(model.clarity < 0 ? "-" : model.clarity  + "%");
				dependencyText.setText(model.dependency < 0 ? "-" : model.dependency + "%");

			}
		}

		private void changeCameraDistance(View view) {
			int distance = 8000;
			float scale = context.getResources().getDisplayMetrics().density * distance;
			mCardFrontLayout.setCameraDistance(scale);
			mCardBackLayout.setCameraDistance(scale);
		}

		public void flipCard(Boolean b) {
			if(b!=null){
				mIsBackVisible=b;
			}
			if (!mIsBackVisible) {
				mSetRightOut.setTarget(mCardFrontLayout);
				mSetLeftIn.setTarget(mCardBackLayout);
				mSetRightOut.start();
				mSetLeftIn.start();
				mIsBackVisible = true;
			} else {
				mSetRightOut.setTarget(mCardBackLayout);
				mSetLeftIn.setTarget(mCardFrontLayout);
				mSetRightOut.start();
				mSetLeftIn.start();
				mIsBackVisible = false;
			}
		}
	}

	@Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		Context context = parent.getContext();
		LayoutInflater inflater = LayoutInflater.from(context);

		View view = inflater.inflate(R.layout.card_item, parent, false);

		ViewHolder viewHolder = new ViewHolder(view);

		return viewHolder;
	}


	@Override public void onBindViewHolder(ViewHolder holder, int position) {
		User item = list.get(position);
		holder.bind(item, onItemClickListener);
	}

	@Override public int getItemCount() {
		return list.size();
	}

	public interface OnItemClickListener {

		void onItemClick(int position);
	}


	private void loadAnimations() {
		mSetRightOut = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.out_animation);
		mSetLeftIn = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.in_animation);
	}

}