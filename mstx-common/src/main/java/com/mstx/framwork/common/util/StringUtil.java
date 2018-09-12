package com.mstx.framwork.common.util;


public class StringUtil {

	public static boolean areNotEmpty(String[] values) {
		boolean result = true;
		if ((values == null) || (values.length == 0))
			result = false;
		else {
			for (String value : values) {
				result &= !isEmpty(value);
			}
		}
		return result;
	}

	public static boolean isEmpty(String value) {
		int strLen;
		if ((value == null) || ((strLen = value.length()) == 0))
			return true;
		for (int i = 0; i < strLen; i++) {
			if (!Character.isWhitespace(value.charAt(i))) {
				return false;
			}
		}
		return true;
	}
}
