package com.esti.app.scrumesti.feature.services;

import com.esti.app.scrumesti.feature.models.Settings;

/**
 * Created by Ortal Cohen on 17/2/2018.
 */

public class ScrumRules {

	static Settings settings = new Settings();

	static public void updateSettings(Settings newSettings) {
		settings = newSettings;
	}

	public static Integer numberToFibonacci(int i) {
		int onePart = 13;//settings.getTotalWeight() / 9;
		if (i < onePart) {
			return 0;
		} else if (i < onePart*2) {
			return 1;
		} else if (i < onePart*3) {
			return 2;
		} else if (i < onePart*4) {
			return 3;
		} else if (i < onePart*5) {
			return 5;
		} else if (i < onePart*6) {
			return 8;
		} else if (i < onePart*7) {
			return 13;
		} else {
			return 21;
		}
	}

	public static Integer totalTeamSum(int sum, int votes) {
		if(votes ==0){
			return 0;
		}
		return numberToFibonacci(sum / votes);
	}

	public static Integer scrumerSum(int complexity, int clarity, int dependency) {
		return numberToFibonacci((Math.max(0, complexity) + Math.max(0, clarity)/2 + Math.max(0,dependency)/2));
	}
}
