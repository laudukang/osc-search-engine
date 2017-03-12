package me.codz.tool;

import com.github.rjeschke.txtmark.BlockEmitter;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * markdown4j代码高亮
 *
 * @author lk
 * @date 2013-11-6 下午4:27:43
 */
public class OscCodeBlockEmitter implements BlockEmitter {
	public static final Pattern lang_pattern = Pattern
			.compile("((<!--){0,1}[ ]*(lang){1}[ ]*[:]?[ ]*([^ |^\\n|^\\t]*[a-zA-Z|\\+|#])?[ ]*(-->){0,1})[\\n\\t]*");
	public static final Pattern is_lang = Pattern
			.compile("^(?i)(Apache|Bash|C#|C\\+\\+|CSS|CoffeeScript|Diff|HTML|XML|HTTP|Ini|JSON|Java|JavaScript|Makefile|Markdown|Nginx|Objective|C|PHP|Perl|Python|Ruby|SQL|ActionScript|AppleScript|Clojure|D|Dart|Delphi|Django|Erlang|F#|Go|Groovy|Haml|Haskell|Less|Lisp|Lua|OCaml|PowerShell|R|SCSS|SML|Scala|Scheme|Stylus|Swift|TeX|TypeScript|VBScript|Vim Script)$");
	        /*.compile("^(c|actionscript3|as3|bash|shell|haskell|coldfusion|cf|cpp|c|c#|c-sharp|csharp|css|delphi|pascal|pas|erl|erlang|groovy|java|jfx|javafx|js|jscript|javascript|perl|Perl|pl|php|text|plain|py|python|ruby|rails|ror|rb|scala|sql|vb|vbnet|lua|xml|xhtml|xslt|html)");*/

	@Override
	public void emitBlock(StringBuilder out, List<String> lines, String meta) {
		// TODO Auto-generated method stub
		String lang = null;
		for (int i = 0; i < lines.size(); i++) {
			String s = lines.get(i);
			if (StringUtils.isBlank(s))
				continue;
			Matcher matcher = lang_pattern.matcher(s);
			String hole = "";
			while (matcher.find()) {
				lang = matcher.group(4);
				hole = matcher.group(1);
			}
			if (StringUtils.isBlank(lang))
				continue;
			hole = hole.replaceAll("\\+", "\\\\+");
			if (is_lang.matcher(lang).matches()) {
				s = s.replaceFirst(hole, "");
			}
			lines.set(0, s);
			break;
		}
		if (StringUtils.isBlank(lang)) {
			lang = StringUtils.isBlank(meta) ? "default" : meta;
		}
		if (!is_lang.matcher(lang).matches()) {
			lang = "default";
		}
		out.append("<pre><code  class='brush: " + lang + "; auto-links: false;'>");
		for (final String s : lines) {
			for (int i = 0; i < s.length(); i++) {
				final char c = s.charAt(i);
				switch (c) {
					case '&':
						out.append("&amp;");
						break;
					case '<':
						out.append("&lt;");
						break;
					case '>':
						out.append("&gt;");
						break;
					default:
						out.append(c);
						break;
				}
			}
			out.append('\n');
		}
		out.append("</code></pre>\n");
	}

}
