package util;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;


/** 
* @author: 张子开
* @date：2017年4月15日 下午12:40:12
* @version 1.0 
*/

public class JDBCUtil {
	private static class LazyHolder {
		private static final JDBCUtil INSTANCE = new JDBCUtil();
	}
	private Connection conn = null;
	
	public JDBCUtil() {
		InputStream in = JDBCUtil.class.getClassLoader().getResourceAsStream("db.properties");
		Properties properties = new Properties();
		try {
			properties.load(in);
			String driver = properties.getProperty("driver");
			String url = properties.getProperty("url");
			String username = properties.getProperty("username");
			String password = properties.getProperty("password");
			
			// 加载数据库驱动
			Class.forName(driver);
			conn = DriverManager.getConnection(url, username, password);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static Connection getConnection() {
		return LazyHolder.INSTANCE.conn;
	}
	
	public static boolean isFieldExist(String fieldName){
		try {
			final ResultSetMetaData metaData = getConnection().createStatement().executeQuery("SELECT * FROM usdaplants").getMetaData();
			for (int i = 1; i <= metaData.getColumnCount(); i++) {
				final String columnName = metaData.getColumnName(i);
				if(columnName.equals(fieldName))
					return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	public static void main(String[] args) throws SQLException {
		System.out.println(isFieldExist("Symbol"));
		createField("sdfass");
	}

	public static void createField(String field) {
		String sql= "ALTER TABLE usdaplants ADD "+field+" varchar(400)";
		try {
			getConnection().createStatement().execute(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
