package me.codz.tool;

import org.apache.commons.io.IOUtils;
import org.markdown4j.Markdown4jProcessor;

import java.io.*;

/**
 * markdown4j解释器
 *
 * @author lk
 * @date 2013-11-6 下午4:27:43
 */
public class Markdown4jParser implements Parser {
	private Markdown4jProcessor processor;

	public Markdown4jParser() {
		this.processor = new Markdown4jProcessor();
		processor.setCodeBlockEmitter(new OscCodeBlockEmitter());
	}

	@Override
	public String parse(String content) throws IOException {
		return processor.process(content);
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
