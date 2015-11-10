package vn.iadd.autobean;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import vn.iadd.autobean.Column;

public class DbProvider {
	private DatabaseType dbType;
	private String host;
	private String username;
	private String password;

	private String driver;

	private void setDriver() {
		if (getDbType() == DbProvider.DatabaseType.ORACLE) {
			driver = "oracle.jdbc.driver.OracleDriver";
		} else if (getDbType() == DbProvider.DatabaseType.MYSQL) {
			driver = "com.mysql.jdbc.Driver";
		} else {
			driver = "Not implemented";
		}
	}

	public DbProvider() {
		setDbType(DbProvider.DatabaseType.ORACLE);
		setDriver();
	}

	public DatabaseType getDbType() {
		return dbType;
	}

	public void setDbType(DatabaseType dbType) {
		this.dbType = dbType;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDriver() {
		return driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public Column[] getColumns(String tableName) {

		String query = "SELECT * FROM " + tableName;

		java.sql.Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		ResultSetMetaData rsmd = null;

		String[] columns = null;
		Column[] lstColumns = null;

		try {
			Class.forName(getDriver()).newInstance();
			conn = DriverManager.getConnection(getHost(), getUsername(), getPassword());
			stmt = conn.prepareStatement(query);
			rs = stmt.executeQuery();
			rsmd = rs.getMetaData();
			int columnCount = rsmd.getColumnCount();
			columns = new String[columnCount];
			lstColumns = new Column[columnCount];
			for (int i = 0; i < columnCount; i++) {
				Column c = new Column(rsmd.getColumnName(i + 1), rsmd.getColumnTypeName(i + 1),
						rsmd.getColumnDisplaySize(i + 1));
				columns[i] = rsmd.getColumnName(i + 1);
				lstColumns[i] = c;
			}

		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
			System.out.println("Error: unable to load driver class!");
			System.exit(1);
		} catch (IllegalAccessException ex) {
			System.out.println("Error: access problem while loading!");
			System.exit(2);
		} catch (InstantiationException ex) {
			System.out.println("Error: unable to instantiate driver!");
			System.exit(3);
		} catch (SQLException ex) {
			System.out.println(ex);
			System.exit(4);
		} finally {
			if (conn != null) {
				try {
					rs.close();
					stmt.close();
					conn.close();
				} catch (Exception e) {
					System.out.println("ERROR===" + e.getMessage());
				}
			}
		}

		// Result
		return lstColumns;
	}

	public static enum DatabaseType {
		ORACLE, MYSQL
	}

	private static Map<String, String> lstType = new HashMap<String, String>();

	static {
		lstType.put("NUMBER", "int");
		lstType.put("VARCHAR2", "String");
		lstType.put("VARCHAR", "String");
		lstType.put("DATE", "java.util.Date");
		lstType.put("CHAR", "String");
	}

	public static Map<String, String> getListType() {
		return lstType;
	}
}
