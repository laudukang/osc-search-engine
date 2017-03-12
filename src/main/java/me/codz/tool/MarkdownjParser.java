package me.codz.tool;

import com.petebevin.markdown.MarkdownProcessor;
import org.apache.commons.io.IOUtils;

import java.io.*;

/**
 * markdownj解释器
 *
 * @author lk
 * @date 2013-11-6 下午4:27:43
 */
public class MarkdownjParser implements Parser {
	public final static transient String MARKDOWN_CODEBLOCK_TPL = "\n\n<pre class=\"brush: %s\">\n%s\n</pre>\n\n"; // 支持SyntaxHighlighter语法高亮
	private MarkdownProcessor processor;

	public MarkdownjParser() {
		this.processor = new MarkdownProcessor();
		processor.setCodeBlockTemplate(MARKDOWN_CODEBLOCK_TPL);
	}

	@Override
	public String parse(String content) throws IOException {
		return processor.markdown(content);
	}

	@Override
	public String parse(InputStream in) throws IOException {
		return parse(IOUtils.toString(in));
	}

	@Override
	public String parse(File file) throws IOException {
		InputStream in = null;
		try {
			in = new FileInputStream(file);
			return parse(in);
		} finally {
			IOUtils.closeQuietly(in);
		}
	}

	@Override
	public void parse(InputStream in, OutputStream out) throws IOException {
		InputStream sin = null;
		try {
			sin = IOUtils.toInputStream(parse(in));
			IOUtils.copy(sin, out);
		} finally {
			IOUtils.closeQuietly(sin);
		}
	}
}
