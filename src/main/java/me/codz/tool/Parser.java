package me.codz.tool;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface Parser {
	String parse(String content) throws IOException;

	String parse(InputStream in) throws IOException;

	String parse(File file) throws IOException;

	void parse(InputStream in, OutputStream out) throws IOException;
}
