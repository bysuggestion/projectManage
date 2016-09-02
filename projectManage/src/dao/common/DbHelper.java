package dao.common;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
 
public class DbHelper {
    private Connection connection;
    private PreparedStatement ps;
    private CallableStatement cs;
    private ResultSet rs;
    private String sourceName="bookshop";
    private IConnectionProvider connectionProvider;

    /**
     * ����ʱ�¼�
     */
    private int queryTime;
    public DbHelper() throws SQLException{
    	try {
			this.connectionProvider = new JdbcProvider("com.mysql.jdbc.Driver","jdbc:mysql://localhost:3306/","root","root");
		} catch (ClassNotFoundException e) {
			throw new SQLException("δ�ҵ����ݿ���������");
		}
    }
    public DbHelper(IConnectionProvider connectionProvider,String sourceName,int queryTime) {
        this.connectionProvider = connectionProvider;
     
		this.sourceName = sourceName;
        this.queryTime = queryTime;
    }
     
    /**
     * ͳ������ѯ ֱ�ӷ���Ψһֵ
     * @param sql
     * @param objs
     * @return
     * @throws SQLException
     */
    public Object findReturn(String sql,Object... objs) throws SQLException {
        try {
            getConnection();
             
            ps = connection.prepareStatement(sql);
            ps.setQueryTimeout(queryTime);
             
            if(objs != null) {
                for(int i = 0 ; i < objs.length; i ++)
                    ps.setObject(i + 1, objs[i]);
            }
            rs = ps.executeQuery();
            if(rs.next())
                return rs.getObject(1);
            else
                return null;
        } finally {
            close();
        }
    }
     
    /**
     * ִ�д洢���� ���ֶ����ڴ�����ֵ ���Դ洢����д�������� {?=call PRODUCENAME(?,?...,?)}
     * @param call �洢����
     * @param returnType ���ز������� [Types.XXXX]
     * @param objs �����б�
     * @return
     * @throws SQLException
     */
    public Object callWithReturn(String call,int returnType,Object... objs) throws SQLException {        
        try {
            getConnection();
            cs = connection.prepareCall(call);
            cs.setQueryTimeout(queryTime);
            cs.registerOutParameter(1, returnType);
            for(int i = 0; i < objs.length; i ++)
                cs.setObject(i+2,objs[i]);
             
            cs.execute();
            return cs.getObject(1);
        } finally {
            close();
        }
    }
     
    /**
     * ����ִ�з����б�Ĵ洢����  ��ӳ�䵽�����б���
     * @param <X>
     * @param clazz ӳ�����
     * @param sql ��ѯ���
     * @param smap ӳ�����ñ�<�ֶ���,����>
     * @param objs �����б�
     * @return
     * @throws Exception
     */
    public <X> List<X> call(Class<X> clazz,String sql,Map<String,Class<?>> smap,Object... objs) throws Exception {
        try {
            rs = call(sql, objs);
             
            List<X> list = new ArrayList<X>();
             
            while(rs != null && rs.next()) {
                X obj = returnObject(clazz,smap);
                 
                list.add(obj);
            }
             
            return list;
        } catch (Exception e) {
            close();
            throw e;
        } finally {
            close();
        }
    }
     
    private ResultSet call(String sql,Object... params) throws SQLException {
        getConnection();
         
        cs = connection.prepareCall(sql,ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY);
        cs.setQueryTimeout(queryTime);
         
        cs.registerOutParameter(1, Types.REAL);
        for(int i = 0; i < params.length; i ++)
            cs.setObject(i + 2, params[i]);
         
        return cs.executeQuery();
    }
     
    /**
     * ��ѯ�������� ��ӳ�䵽������
     * @param <X>
     * @param clazz ӳ�����
     * @param sql ��ѯ���
     * @param smap ӳ�����ñ�<�ֶ���,����>
     * @param objs �����б�
     * @return
     * @throws Exception
     */
    public <X> X get(Class<X> clazz,String sql,Map<String,Class<?>> smap,Object... objs) throws SQLException {
        try {
            rs = query(sql, objs);
             
            if(rs != null && rs.next()) {
                X obj = returnObject(clazz,smap);
                 
                return obj;
            } else {
                return null;
            }
             
        } catch (Exception e) {
            throw new SQLException(e);
        } finally {
            close();
        }
    }
     
    private ResultSet query(String sql,Object... objs) throws Exception {
        getConnection();
 
        ps = connection.prepareStatement(sql,ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY);
        ps.setQueryTimeout(queryTime);
         
        if(objs != null) {
            for(int i = 0; i < objs.length; i ++)
                ps.setObject(i + 1, objs[i]);
        }
        return ps.executeQuery();
    }
    /**
     * ���ڽ�����ѯ��� ��ӳ�䵽����
     * @param <X>
     * @param clazz ӳ�����
     * @param smap ӳ���
     * @return
     * @throws InstantiationException
     * @throws SecurityException
     * @throws NoSuchMethodException
     * @throws SQLException
     * @throws IllegalAccessException
     */
    private <X> X returnObject(Class<X> clazz,Map<String,Class<?>> smap) throws InstantiationException,  SecurityException, NoSuchMethodException, SQLException, IllegalAccessException {
        X obj = clazz.newInstance();
         
        for(Entry<String,Class<?>> en : smap.entrySet()) {
            try {
                Object value = rs.getObject(en.getKey());
                 
                setField(obj,en.getKey(),en.getValue(),(en.getValue().equals(String.class) ? (value != null ? value : null) : value));
            } catch (IllegalArgumentException e1) {
            	e1.printStackTrace();
            } catch (InvocationTargetException e1) {
                e1.printStackTrace();
            }
        }
         
        return obj;
    }
     
    /**
     * ���ö������ֶε�ֵ
     * ����ֻ֧�ּ򵥵Ķ������� String Integer Short Double �ȱ�׼����
     * ������չ�����������֧��һЩ�Ƚϸ��ӵĶ����ʽ
     * @param obj ӳ��Ķ���
     * @param fieldname �ֶ����� ����������set������������
     * @param type �ֶ�����
     * @param value �ֶ�ֵ
     * @throws SecurityException
     * @throws NoSuchMethodException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    protected void setField(Object obj,String fieldname,Class<?> type,Object value) throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException  {
        Method method = obj.getClass().getMethod(setMethod(fieldname),type);
        method.invoke(obj, value);
    }
 
    /**
     * ִ�в������,���������ɵ�����
     * @param sql �������
     * @param objs �����б�
     * @return ������䷵�ص�����ֵ
     * @throws SQLException
     */
    public int insertAndReturnKey(String sql,Object... objs) throws SQLException {
        int countRow = 0;
        int key = 0;
         
        try {
            getConnection();
             
            connection.setAutoCommit(false);
 
            ps = connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
            ps.setQueryTimeout(queryTime);
             
            if(objs != null) {
                for(int i = 0; i < objs.length; i ++)
                    ps.setObject(i+1,objs[i]);
            }
             
            countRow = ps.executeUpdate();
            if(countRow > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if(rs.next())
                    key = rs.getInt(1);
            }
            connection.commit();
        } catch (SQLException e) {
            countRow = 0;
            connection.rollback();
            closeConnection();
            throw e;
        } finally {
            if (connection != null) {
                connection.setAutoCommit(true);
            }
            close();
        }
        return key;
    }
     
    /**
     * ִ��Ԥ����SQL
     * @param sql SQL���
     * @param objs �����б�
     * @return ִ��Ӱ������
     * @throws SQLException
     */
    public int updatePrepareSQL(String sql,Object... objs) throws SQLException {
        int countRow = 0;
        try {
            getConnection();
 
            connection.setAutoCommit(false);
 
            ps = connection.prepareStatement(sql);
            ps.setQueryTimeout(queryTime);
            if(objs != null) {
                for(int i = 0; i < objs.length; i ++)
                    ps.setObject(i+1,objs[i]);
            }
            countRow = ps.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            countRow = 0;
            connection.rollback();
            closeConnection();
            throw e;
        } finally {
            if (connection != null) {
                connection.setAutoCommit(true);
            }
            close();
        }
        return countRow;
    }
 
    public <X> List<X> find(String sql,Class<X> clazz,Map<String,Class<?>> smap,Object... objs) throws SQLException {
        try {             
            rs = query(sql, objs);
             
            List<X> list = new ArrayList<X>();
             
            while(rs != null && rs.next()) {
                X obj = returnObject(clazz,smap);
                 
                list.add(obj);
            }
             
            return list;
        } catch (Exception e) {
            throw new SQLException(e);
        } finally {
            close();
        }
    }
 
    public void closeConnection() {
        try {
            if (connection != null) {
                connection.close();
            }
            connection = null;
        } catch (Exception e) {
        }
    }
     
    public Connection getConnection() throws SQLException {
        if(sourceName == null)
            throw new SQLException("û����������Դ");
         
        if (connection == null || connection.isClosed()) {
            try {
                closeConnection();
                connection = connectionProvider.getConnection(sourceName);
            } catch (Exception sqle) {
            	sqle.printStackTrace();
            }
        }
        return connection;
    }
     
    public void close() {
        try {
            super.finalize();
            if (rs != null) {
                rs.close();
                rs = null;
            }
            if (ps != null) {
                ps.close();
                ps = null;
            }
            if (cs != null) {
                cs.close();
                cs = null;
            }
            if (connection != null) {
                connection.close();
                connection = null;
            }
        } catch (Throwable te) {
        }
    }
 
    public static Map<String,Class<?>> parseSmap(Class<?> clazz,String... paramNames) {
        Map<String,Class<?>> smap = new HashMap<String, Class<?>>(paramNames.length);
         
        for(String name : paramNames) {
            try {
                Field field = clazz.getDeclaredField(name);
                smap.put(name, field.getType());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
         
        return smap;
    }
     
    public static String getMethod(String name) {
        return "get" + name.replaceFirst(name.substring(0,1), name.substring(0,1).toUpperCase());
    }
     
    public static String setMethod(String name) {
        return "set" + name.replaceFirst(name.substring(0,1), name.substring(0,1).toUpperCase());
    }
}
