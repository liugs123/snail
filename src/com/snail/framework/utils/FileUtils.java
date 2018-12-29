package com.snail.framework.utils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {
	public static void findClassFile(File file,List<String> list){
		File[] listFiles = file.listFiles();
		for (File file2 : listFiles) {
			if(file2.getAbsolutePath().contains(".class")){
				list.add(file2.getAbsolutePath());
			}else{
				if(file2.isDirectory()){
					FileUtils.findClassFile(file2, list);
				}
			}
		}
	}

	public static void findSqlFile(File file,List<String> list){
		File[] listFiles = file.listFiles();
		for (File file2 : listFiles) {
			if(file2.getAbsolutePath().contains(".sql")){
				list.add(file2.getAbsolutePath());
			}else{
				if(file2.isDirectory()){
					FileUtils.findSqlFile(file2, list);
				}
			}
		}
	}

	public static List<String> readLines(InputStream input, String encoding) throws IOException {
		InputStreamReader reader = new InputStreamReader(input, encoding);
		return readLines(reader);
	}

	public static List<String> readLines(Reader input) throws IOException {
		BufferedReader reader = toBufferedReader(input);
		List<String> list = new ArrayList<String>();
		String line = reader.readLine();
		while (line != null) {
			list.add(line);
			line = reader.readLine();
		}
		return list;
	}

	public static BufferedReader toBufferedReader(Reader reader) {
		return reader instanceof BufferedReader ? (BufferedReader) reader : new BufferedReader(reader);
	}

}
