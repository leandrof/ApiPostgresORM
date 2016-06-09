package br.com.roborg.orm.model;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.com.roborg.orm.model.entity.Entity;

public class SmartResultSet {

	private final ResultSet rs;
	private final PreparedStatement pstmt;
	public final int count;

	public SmartResultSet(PreparedStatement pstmt) throws SQLException {
		this.pstmt = pstmt;
		this.rs = pstmt.executeQuery();
		if (rs != null) {
			rs.last();
			count = rs.getRow();
		} else {
			count = 0;
		}
	}

	public <T extends Entity> T getObject(Class<T> clazz) {
		Field[] fields = clazz.getDeclaredFields();
		T obj = null;
		try {
			obj = clazz.newInstance();
			for (Field field : fields) {
				field.setAccessible(true);
				switch (field.getType().getSimpleName().toLowerCase()) {
				case "int":
				case "integer":
					field.set(obj, rs.getInt(field.getName()));
					break;
				case "long":
					field.set(obj, rs.getLong(field.getName()));
					break;
				case "float":
					field.set(obj, rs.getFloat(field.getName()));
					break;
				case "double":
					field.set(obj, rs.getDouble(field.getName()));
					break;
				case "bigdecimal":
					field.set(obj, rs.getBigDecimal(field.getName()));
					break;
				case "string":
					field.set(obj, rs.getString(field.getName()));
					break;
				case "character":
				case "char":
					field.set(obj, rs.getString(field.getName()).charAt(0));
					break;
				case "boolean":
					field.set(obj, rs.getBoolean(field.getName()));
					break;
				case "calendar":
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(rs.getDate(field.getName()));
					field.set(obj, calendar);
					break;
				case "date":
					Date date = rs.getDate(field.getName());
					field.set(obj, date);
					break;
				default:
					break;
				}
			}
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return obj;
	}

	public <T extends Entity> List<T> getList(Class<T> clazz)
			throws SQLException {
		List<T> list = new ArrayList<T>(count);
		do {
			list.add(getObject(clazz));
		} while (rs.previous());
		close();
		return list;
	}

	public final void close() {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if (pstmt != null) {
			try {
				pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
