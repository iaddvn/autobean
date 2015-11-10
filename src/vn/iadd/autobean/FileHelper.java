package vn.iadd.autobean;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;;

public class FileHelper {

	private static Properties prop = new Properties();

	public static void writeToFile(String fileName, String content) {
		java.io.FileWriter fw = null;
		java.io.BufferedWriter bw = null;
		try {
			java.io.File file = new java.io.File(fileName);
			if (!file.exists()) {
				file.createNewFile();
			}
			// System.out.println(file.getAbsolutePath());
			fw = new java.io.FileWriter(file.getAbsoluteFile());
			bw = new java.io.BufferedWriter(fw);
			bw.write(content);
			bw.close();
		} catch (java.io.IOException ex) {
			System.out.println(ex);
		} finally {
			if (bw != null) {
				try {
					bw.close();
				} catch (IOException e) {
					System.out.println(e);
				}
			}
		}
		System.out.println("Written to file: " + fileName);
	}

	public static String process(String table, Column[] columns) {
		String fileName = Character.toUpperCase(table.charAt(0)) + removeUnderscore(table.toLowerCase().substring(1))
				+ "Bean";
		StringBuilder sb = new StringBuilder();
		sb.append("public class " + fileName + " {\n");
		for (Column c : columns) {
			String type = null;
			if (!DbProvider.getListType().containsKey(c.getType())) {
				type = "CHECK_HERE_" + c.getType();
			} else {
				type = DbProvider.getListType().get(c.getType());
			}
			sb.append("\tprivate " + type + " " + removeUnderscore(c.getName().toLowerCase()) + ";");
			sb.append("\n");
		}

		sb.append("\n}");
		return sb.toString();
	}

	public static void writeBeanFile(String table, Column[] columns) {
		String fileName = Character.toUpperCase(table.charAt(0)) + removeUnderscore(table.toLowerCase().substring(1));
		writeToFile(fileName + ".java", process(table, columns));
	}

	public static String removeUnderscore(String str) {
		// return str.replaceAll("([A-Za-z])_([A-Za-z])", "$1 $2");
		StringBuilder sb = new StringBuilder();
		char[] arr = str.toCharArray();
		boolean capitalizeNext = false;
		for (char c : arr) {
			if (c == '_') {
				capitalizeNext = true;
			} else {
				if (capitalizeNext) {
					sb.append(Character.toUpperCase(c));
					capitalizeNext = false;
				} else {
					sb.append(c);
				}
			}
		}
		return sb.toString();
	}

	public static String getProperty(String filename, String property) {
		InputStream input = null;
		String result = null;
		try {
			input = new FileInputStream(filename);
			prop.load(input);
			result = prop.getProperty(property);
		} catch (FileNotFoundException ex) {
			System.out.println(ex);
		} catch (IOException ex) {
			System.out.println(ex);
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}

	public static void addProperty(String filename, String property, String content) {
		OutputStream output = null;
		// String result = null;
		try {
			output = new FileOutputStream(filename);
			prop.setProperty(property, content);
			prop.store(output, null);
		} catch (FileNotFoundException ex) {
			System.out.println(ex);
		} catch (IOException ex) {
			System.out.println(ex);
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		System.out.println("Written config to file: " + filename);
	}

	public static void addProperty(String filename, Map<String, String> properties) {
		OutputStream output = null;
		try {
			output = new FileOutputStream(filename);
			// prop.setProperty(property, content);
			Set<Entry<String, String>> entries = properties.entrySet();
			for (Entry<String, String> entry : entries) {
				System.out.println(entry.getKey() + "===" + entry.getValue());
				prop.setProperty(entry.getKey(), entry.getValue());
			}
			prop.store(output, null);
		} catch (FileNotFoundException ex) {
			System.out.println(ex);
		} catch (IOException ex) {
			System.out.println(ex);
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		System.out.println("Write done.");
	}

	public static void main(String[] args) {
		// String str = "iadd_abcd_efgh";
		// System.out.println(removeUnderscore(str));
		final String filename = "test.conf";
		addProperty(filename, "aaaa", "bbbccc");
	}
}
