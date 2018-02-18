package com.esti.app.scrumesti.feature.utils;

/**
 * Created by Ortal Cohen on 11/2/2018.
 */

public class Strings {

	public static boolean isNullOrEmpty(String string) {
		if (string == null) {
			return true;
		}
		if (string.equals("")) {
			return true;
		}
		return false;
	}

	public static boolean isNullOrNegative(Integer integer) {
		if (integer == null || integer < 0) {
			return true;
		}
		return false;
	}
}
