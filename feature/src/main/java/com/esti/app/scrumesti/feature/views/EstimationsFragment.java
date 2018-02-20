package com.esti.app.scrumesti.feature.views;


import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatSeekBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import com.esti.app.R;
import com.esti.app.scrumesti.feature.view_models.DataViewModel;
import timber.log.Timber;

public class EstimationsFragment extends Fragment {

	Vibrator vibrator;
	private static final String TAG = "EstimationsFragment";
	private DataViewModel viewModel;
	OnSeekBarChangeListener onSeekBarChangeListener = new OnSeekBarChangeListener() {
		@Override public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
			vibrator.vibrate(1);
		}

		@Override public void onStartTrackingTouch(SeekBar seekBar) {

		}

		@Override public void onStopTrackingTouch(SeekBar seekBar) {
			viewModel.setChecked(seekBar.getTag().toString(), Integer.valueOf(seekBar.getProgress())*10);
		}
	};

	private AppCompatSeekBar complexitySB;
	private AppCompatSeekBar claritySB;
	private AppCompatSeekBar dependencySB;
	private Button nextEstiButton;
	private boolean isCounting = false;
	private boolean iClicked = false;
	private CountDownTimer nextEstiButtonTimer = null;
	private Sensor sensor;

	public static EstimationsFragment newInstance() {
		EstimationsFragment fragment = new EstimationsFragment();
		Bundle args = new Bundle();
		fragment.setArguments(args);
		return fragment;
	}

	@Override public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_estimations, container, false);
	}

	@Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		viewModel = ViewModelProviders.of(getActivity()).get(DataViewModel.class);
		complexitySB = view.findViewById(R.id.complexitySB);
		claritySB = view.findViewById(R.id.claritySB);
		dependencySB = view.findViewById(R.id.dependencySB);
		nextEstiButton = view.findViewById(R.id.nextEstiButton);
		vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
		nextEstiButton.setOnClickListener(view12 -> {
			if (isCounting) {
				viewModel.setNextEsti();
			} else {
				viewModel.askForNextEsti();
				iClicked = true;
			}
		});

		viewModel.getSettings().observe(this, settings -> {

		});

		viewModel.getNextEstiStatus().observe(this, s -> {
			switch (s) {
				case DataViewModel.NEXT_ESTI_STATUS_COUNTING:
					if (!isCounting) {
						Timber.tag(TAG).i("NEXT_ESTI_STATUS_COUNTING");
						isCounting = true;
						if (iClicked) {
							nextEstiButton.setEnabled(false);
						} else {
							nextEstiButton.setEnabled(true);
						}
						nextEstiButtonTimer = new CountDownTimer(7000, 1000) {

							public void onTick(long millisUntilFinished) {
								Timber.tag(TAG).i("nextEstiButtonTimer onTick");
								if (iClicked) {
									nextEstiButton.setText("One more scrummer needs to click or shake in " + String.valueOf(millisUntilFinished / 1000));
								} else {
									nextEstiButton.setText(String.valueOf(millisUntilFinished / 1000));
								}
							}

							public void onFinish() {
								Timber.tag(TAG).i("nextEstiButtonTimer onFinish");
								isCounting = false;
								iClicked = false;
								nextEstiButton.setText("Next Esti");
								nextEstiButton.setEnabled(viewModel.getAllVoteDone().getValue());
							}
						};
						nextEstiButtonTimer.start();
					}
					break;
				case DataViewModel.NEXT_ESTI_STATUS_SET:
					Timber.tag(TAG).i("NEXT_ESTI_STATUS_SET");
					setUnchecked();
					break;
			}
		});
		complexitySB.setOnSeekBarChangeListener(onSeekBarChangeListener);
		claritySB.setOnSeekBarChangeListener(onSeekBarChangeListener);
		dependencySB.setOnSeekBarChangeListener(onSeekBarChangeListener);
		viewModel = ViewModelProviders.of(getActivity()).get(DataViewModel.class);

		viewModel.getFullGroupData().observe(getActivity(), stringHashMapHashMap -> {
			if (stringHashMapHashMap.isEmpty()) {
				setUnchecked();
			}
		});

		viewModel.getAllVoteDone().observe(getActivity(), aBoolean -> {
			if (!isCounting) {
				Timber.tag(TAG).i("getAllVoteDone "+aBoolean);
				nextEstiButton.setEnabled(aBoolean);
			}
		});
		SensorManager sManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
		sensor = sManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		sManager.registerListener(new ShakeEventListener(), sensor, SensorManager.SENSOR_DELAY_NORMAL);

	}

	private void setUnchecked() {
		if (nextEstiButtonTimer != null) {
			nextEstiButtonTimer.cancel();
			nextEstiButtonTimer = null;
		}
		complexitySB.setProgress(0);
		claritySB.setProgress(0);
		dependencySB.setProgress(0);
		viewModel.setChecked(DataViewModel.COMPLEXITY, -1);
		viewModel.setChecked(DataViewModel.CLARITY, -1);
		viewModel.setChecked(DataViewModel.DEPENDENCY, -1);
		nextEstiButton.setText("Next Esti");
		nextEstiButton.setEnabled(false);
		isCounting = false;
		iClicked = false;

	}

	@Override public void onAttach(Context context) {
		super.onAttach(context);

	}

	@Override public void onDetach() {
		super.onDetach();
	}


	public class ShakeEventListener implements SensorEventListener {

		private static final int MIN_FORCE = 6;

		private static final int MIN_DIRECTION_CHANGE = 3;

		private static final int MAX_PAUSE_BETHWEEN_DIRECTION_CHANGE = 300;

		private static final int MIN_TOTAL_DURATION_OF_SHAKE = 400;

		private long mFirstDirectionChangeTime = 0;

		private long mLastDirectionChangeTime;

		private int mDirectionChangeCount = 0;

		private float lastX = 0;

		private float lastY = 0;

		private float lastZ = 0;

		@Override public void onSensorChanged(SensorEvent se) {
			// get sensor data
			float x = se.values[SensorManager.DATA_X];
			float y = se.values[SensorManager.DATA_Y];
			float z = se.values[SensorManager.DATA_Z];

			// calculate movement
			float totalMovement = Math.abs(x + y + z - lastX - lastY - lastZ);

			if (totalMovement > MIN_FORCE) {

				// get time
				long now = System.currentTimeMillis();

				// store first movement time
				if (mFirstDirectionChangeTime == 0) {
					mFirstDirectionChangeTime = now;
					mLastDirectionChangeTime = now;
				}

				// check if the last movement was not long ago
				long lastChangeWasAgo = now - mLastDirectionChangeTime;
				if (lastChangeWasAgo < MAX_PAUSE_BETHWEEN_DIRECTION_CHANGE) {

					// store movement data
					mLastDirectionChangeTime = now;
					mDirectionChangeCount++;

					// store last sensor data
					lastX = x;
					lastY = y;
					lastZ = z;

					// check how many movements are so far
					if (mDirectionChangeCount >= MIN_DIRECTION_CHANGE) {

						// check total duration
						long totalDuration = now - mFirstDirectionChangeTime;
						if (totalDuration >= MIN_TOTAL_DURATION_OF_SHAKE) {
							onShake();
							resetShakeParameters();
						}
					}

				} else {
					resetShakeParameters();
				}
			}
		}

		private void resetShakeParameters() {
			mFirstDirectionChangeTime = 0;
			mDirectionChangeCount = 0;
			mLastDirectionChangeTime = 0;
			lastX = 0;
			lastY = 0;
			lastZ = 0;
		}

		@Override public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}

	}

	private void onShake() {
		if (nextEstiButton.isEnabled()) {
			nextEstiButton.callOnClick();
		}
	}

}
