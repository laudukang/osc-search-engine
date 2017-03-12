package me.codz.tool;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;

/**
 * <p>Created with IDEA
 * <p>Author: laudukang
 * <p>Date: 2016/6/5
 * <p>Time: 17:38
 * <p>Version: 1.0
 */
public class FormatUtil {
	public static String getPlainText(String html) {
		if (StringUtils.isBlank(html)) return "";
		return Jsoup.parse(html).text();
	}
}
