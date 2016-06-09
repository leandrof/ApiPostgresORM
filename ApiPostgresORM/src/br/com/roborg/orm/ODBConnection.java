package br.com.roborg.orm;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ODBConnection {
	public static class Config {
		private final String url;
		private final int porta;
		private final String user;
		private final String pass;
		private final String dbName;
		public Config(String url, int porta, String dbName, String user, String pass) {
			this.url = url;
			this.porta = porta;
			this.user = user;
			this.pass = pass;
			this.dbName = dbName;
		}
		public final String getConnectionString(){
			return "jdbc:postgresql://" + url + ":" + porta + "/" + dbName;
		}
	}
	
	
	/***/
	private static Config cfg;
	private Connection db;
	
	private ODBConnection() throws ClassNotFoundException, SQLException {
		if (cfg == null) {
			throw new RuntimeException(
					"Use ODBConnection.setCfg(new Config(url, porta, dbName, user, pass));");
		}
		Class.forName("org.postgresql.Driver");
		db = open();
	}
	
	private static ODBConnection instance;
	
	public static ODBConnection getInstance() throws ClassNotFoundException, SQLException {
		if (instance == null) {
			instance = new ODBConnection();
		}
		return instance;
	}
	
	public static void setCfg(Config cfg) {
		ODBConnection.cfg = cfg;
		if (instance != null) instance.close();
	}
	
	public final boolean isOpen(){
		try {
			return db != null && !db.isClosed();
		} catch (SQLException e) {
			return false;
		}
	}
	
	public Connection getDb() throws SQLException {
		if (!isOpen()) db = open();
		return db;
	}
	
	private final Connection open() throws SQLException{
		return DriverManager
				.getConnection(cfg.getConnectionString(), cfg.user, cfg.pass);
	}
	
	public final void close(){
		if (isOpen()) {
			try {
				db.close();
				instance = null;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
