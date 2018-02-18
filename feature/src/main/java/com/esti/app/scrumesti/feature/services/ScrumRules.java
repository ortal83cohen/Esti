package com.esti.app.scrumesti.feature.services;

/**
 * Created by Ortal Cohen on 17/2/2018.
 */

public class ScrumRules {


	public static Integer numberToFibonacci(int i) {
		if (i < 10) {
			return 0;
		} else if (i < 30) {
			return 1;
		} else if (i < 50) {
			return 2;
		} else if (i < 80) {
			return 3;
		} else if (i < 100) {
			return 5;
		} else if (i < 130) {
			return 8;
		} else if (i < 160) {
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
