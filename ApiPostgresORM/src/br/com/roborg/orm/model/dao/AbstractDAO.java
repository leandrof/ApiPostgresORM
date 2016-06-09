package br.com.roborg.orm.model.dao;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import br.com.roborg.orm.ODBConnection;
import br.com.roborg.orm.annotation.Table;
import br.com.roborg.orm.model.SmartResultSet;
import br.com.roborg.orm.model.entity.Entity;

public class AbstractDAO<T extends Entity> {
	protected final Class<T> clazz;
	
	@SuppressWarnings("unchecked")
	protected AbstractDAO() {
		Type mySuperclass = getClass().getGenericSuperclass();
		Type tType = ((ParameterizedType)mySuperclass).getActualTypeArguments()[0];
		this.clazz = (Class<T>) tType.getClass();
	}
	
	protected Connection getDb() throws ClassNotFoundException, SQLException{
		return ODBConnection.getInstance().getDb();
	}
	
	public List<T> getAll() throws ClassNotFoundException, SQLException{
		String sql = "SELECT * FROM " + getTableName();
		SmartResultSet rs = executeQuery(sql);
		return getList(rs);
	}
	
	protected String getTableName(){
		if (clazz.isAnnotationPresent(Table.class)){
			Table table = clazz.getAnnotation(Table.class);
			return table.value();
		}
		return "";
	}
	
	protected List<T> getList(SmartResultSet rs) throws SQLException{
		return rs.getList(clazz);
	}
	
	protected SmartResultSet executeQuery(String sql, String... params) throws ClassNotFoundException, SQLException{
		PreparedStatement pstmt = getDb().prepareStatement(sql);
		if (params != null) {
			
		}
		return new SmartResultSet(pstmt);
	}
}
