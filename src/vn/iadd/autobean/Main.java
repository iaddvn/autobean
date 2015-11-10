package vn.iadd.autobean;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class Main {

	private static DbProvider db = new DbProvider();
	private static final String JAR_FILE = "auto_bean.jar";

	private static java.util.Map<String, String> lstOptions = new java.util.LinkedHashMap<String, String>();
	private static java.util.Map<String, String> lstUsage = new java.util.HashMap<String, String>();

	private static String host = null;
	private static String username = null;
	private static String passwd = null;

	static {
		lstOptions.put(Const.OPTION_TABLE_NAME, "--table-name");
		lstOptions.put(Const.OPTION_FILE_NAME, "--file-name");
		lstOptions.put(Const.OPTION_HOST_NAME, "--host-name");
		lstOptions.put(Const.OPTION_USER_NAME, "--user-name");
		lstOptions.put(Const.OPTION_PASSWD, "--password");
		lstOptions.put(Const.OPTION_CONFIG_FILE, "--config-file");
		lstOptions.put(Const.OPTION_HELP_2, "--help");
		lstOptions.put(Const.OPTION_HELP_1, "--help");

		lstUsage.put(Const.OPTION_TABLE_NAME, "table name in database.");
		lstUsage.put(Const.OPTION_FILE_NAME, "file name.java.");
		lstUsage.put(Const.OPTION_HOST_NAME, "ip of database. Ex: jdbc:oracle:thin:@<IP>:<PORT>:<SID>");
		lstUsage.put(Const.OPTION_USER_NAME, "user name.");
		lstUsage.put(Const.OPTION_PASSWD, "password.");
		lstUsage.put(Const.OPTION_CONFIG_FILE, "config file. Default is " + Const.CONFIG_FILE);
		lstUsage.put(Const.OPTION_HELP_2, "list option.");
		lstUsage.put(Const.OPTION_HELP_1, "list option.");
	}

	public static void main(String[] args) {
		int length = args.length;
		if (length == 0) {
			printOptions();
		} else {
			start(args);
		}

	}

	private static void printOptions() {
		System.out.println("Usage: java -jar " + JAR_FILE + " [OPTION]");
		System.out.println("Auto generate bean from table column in database..\n");
		System.out.println("List of options.");
		Iterator<Entry<String, String>> it = lstOptions.entrySet().iterator();
		while (it.hasNext()) {
			java.util.Map.Entry<String, String> obj = (java.util.Map.Entry<String, String>) it.next();
			System.out.println("  " + obj.getKey() + ", " + obj.getValue() + "\t" + lstUsage.get(obj.getKey()));
		}
	}

	private static void printInvalidParam(String param) {
		System.out.println(JAR_FILE + ": invalid option -- " + param);
		System.out
				.println("Try 'java -jar " + JAR_FILE + " " + Const.OPTION_VERBOSE_HELP_1 + "' for more information.");
	}

	private static void start(String[] args) {
		Column[] columns = null;
		// int length = args.length;
		java.util.Map<String, String> params = readParams(args);
		// System.out.println(params);
		if (!params.containsKey(Const.OPTION_VERBOSE_TABLE_NAME)) {
			System.out.println("Please input table name...");
			System.exit(5);
		}

		String tableName = params.get(Const.OPTION_VERBOSE_TABLE_NAME);
		String configFile = null;
		if (params.containsKey(Const.OPTION_VERBOSE_CONFIG_FILE)) {
			configFile = params.get(Const.OPTION_VERBOSE_CONFIG_FILE);
		} else {
			configFile = Const.CONFIG_FILE;
		}
		readFromFile(configFile);
		readFromEnv(params);
		writeConfigFile(configFile);

		db.setHost(host);
		db.setUsername(username);
		db.setPassword(passwd);

		columns = db.getColumns(tableName);
		if (!params.containsKey(Const.OPTION_VERBOSE_FILE_NAME)) {
			FileHelper.writeBeanFile(tableName, columns);
		} else {
			FileHelper.writeToFile(params.get(Const.OPTION_VERBOSE_FILE_NAME), FileHelper.process(tableName, columns));
		}

	}

	private static void readFromEnv(java.util.Map<String, String> params) {
		if (params.containsKey(Const.OPTION_VERBOSE_HOST_NAME)) {
			host = params.get(Const.OPTION_VERBOSE_HOST_NAME);
		}
		if (params.containsKey(Const.OPTION_VERBOSE_USER_NAME)) {
			username = params.get(Const.OPTION_VERBOSE_USER_NAME);
		}
		if (params.containsKey(Const.OPTION_VERBOSE_PASSWD)) {
			passwd = params.get(Const.OPTION_VERBOSE_PASSWD);
		}
	}

	private static java.util.Map<String, String> readParams(String[] args) {
		java.util.Map<String, String> lstResult = new java.util.HashMap<String, String>();
		int length = args.length;
		if (length == 1) {
			if (args[0].equals(lstOptions.get(Const.OPTION_HELP_1)) || args[0].equals(Const.OPTION_HELP_2)) {
				printOptions();
				System.exit(0);
			} else {
				printInvalidParam(args[0]);
				System.exit(1);
			}
		}
		if (length % 2 == 1) {
			printInvalidParam("");
			System.exit(1);
		}
		try {
			for (int i = 0; i < length; i += 2) {
				String name = args[i];
				String value = args[i + 1];
				if (lstOptions.containsKey(name)) {
					lstResult.put(lstOptions.get(name), value);
				} else if (lstOptions.containsValue(name)) {
					lstResult.put(name, value);
				} else {
					printInvalidParam(args[i]);
					System.exit(2);
				}
			}
		} catch (IndexOutOfBoundsException ex) {
			System.out.println(ex);
		}
		return lstResult;
	}

	private static void readFromFile(String configFile) {
		File file = new File(configFile);
		if (file.exists() && file.isFile()) {
			host = FileHelper.getProperty(configFile, Const.CONFIG_HOST) != null
					? FileHelper.getProperty(configFile, Const.CONFIG_HOST) : Const.DEFAULT_HOST;
			username = FileHelper.getProperty(configFile, Const.CONFIG_USER_NAME) != null
					? FileHelper.getProperty(configFile, Const.CONFIG_USER_NAME) : Const.DEFAULT_USER;
			passwd = FileHelper.getProperty(configFile, Const.CONFIG_PASSWD) != null
					? FileHelper.getProperty(configFile, Const.CONFIG_PASSWD) : Const.DEFAULT_PASS;
		}
	}

	private static void writeConfigFile(String configFile) {//
		Map<String, String> properties = new HashMap<String, String>();

		properties.put(Const.CONFIG_HOST, host);
		properties.put(Const.CONFIG_USER_NAME, username);
		properties.put(Const.CONFIG_PASSWD, passwd);
		FileHelper.addProperty(configFile, properties);

	}

}
