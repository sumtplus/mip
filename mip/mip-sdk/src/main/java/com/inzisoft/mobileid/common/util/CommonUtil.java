package com.inzisoft.mobileid.common.util;

import java.util.Collection;

public class CommonUtil {
	public static boolean isNullOrEmpty(String target) {
		return target == null || target.isEmpty();
	}

	public static boolean isNotNullAndEmpty(String target) {
		return !isNullOrEmpty(target);
	}

	public static <T> boolean isNullOrEmpty(Collection<T> collection) {
		return collection == null || collection.isEmpty();
	}

	public static <T> boolean isNotNullAndEmpty(Collection<T> collection) {
		return !isNullOrEmpty(collection);
	}

	public static boolean isNullOrEmpty(String... targets) {
		for (String target : targets) {
			if (!isNullOrEmpty(target)) {
				return false;
			}
		}
		return true;
	}
}
