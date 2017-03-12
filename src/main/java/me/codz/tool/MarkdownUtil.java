package me.codz.tool;


import org.apache.commons.lang3.StringEscapeUtils;

public class MarkdownUtil {
	private static Parser parser = new MarkdownjParser();

	public static String markdown(String content) {
		try {
			//update by wpl
			//将所有html转义字符还原 如'&glt;'->'>'
			content = StringEscapeUtils.unescapeHtml4(content);
			content = parser.parse(content);
			//markdown4J解析html时，源码里检查/的时候，多添加了个空格（蛋疼），会把http:// 转成 http://,这里转回来
			content = content.replaceAll("http: //", "http://");
			content = content.replaceAll("<br  />", "<br/>");
			return content;
		} catch (Exception e) {
			return content;
		}
	}

	public static String markdown4Doc(String content) {
		try {
			content = parser.parse(content);
			//markdown4J解析html时，源码里检查/的时候，多添加了个空格（蛋疼），会把http:// 转成 http://,这里转回来
			content = content.replaceAll("http: //", "http://");
			return content;
		} catch (Exception e) {
			return content;
		}
	}
}
