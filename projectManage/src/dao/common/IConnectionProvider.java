package dao.common;

import java.sql.Connection;
import java.sql.SQLException;
 
/**
	用于获取连接的接口
**/
public interface IConnectionProvider {
    public Connection getConnection(String sourceName) throws SQLException;
}
