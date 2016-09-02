package dao.common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
 
/**
 	标准JDBC连接获取
*/
public class JdbcProvider implements IConnectionProvider {
    private String DBDriver;
    private String DBUrl;
    private String username;
    private String password;
     
    public JdbcProvider(String DBDriver,String DBUrl,String username,String password) throws ClassNotFoundException {
        this.DBDriver = DBDriver;
        this.DBUrl = DBUrl;
        this.username = username;
        this.password = password;
         
        Class.forName(DBDriver);
    }
     
    @Override
    public Connection getConnection(String sourceName) throws SQLException {
        return DriverManager.getConnection(DBUrl + sourceName,username,password);
    }
    public String getDBUrl() {
        return DBUrl;
    }
    public void setDBUrl(String dBUrl) {
        DBUrl = dBUrl;
    }
    public String getDBDriver() {
        return DBDriver;
    }
    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }
}
