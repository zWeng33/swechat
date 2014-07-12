package com.wenzchao.core.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

/**
 * DAO基类
 * 
 * @author Venz
 * 
 */
@Repository
public class BaseDao extends JdbcDaoSupport {

	private static Logger log = Logger.getLogger(BaseDao.class);

	@Autowired
	private DataSourceTransactionManager transactionManager;

	@Autowired
	private DataSource dataSource;
	
	@Resource(name = "dataSource")
	public void setSuperDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	public int updateReturnKey(final String sql, List<Object> params) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		Object tempArgs[] = null;
		int result = 0;
		if (null != params) {
			tempArgs = params.toArray();
		}
		final Object ARGS[] = tempArgs;
		try {
	        this.getJdbcTemplate().update(new PreparedStatementCreator() {
	        	public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
	                PreparedStatement ps = conn.prepareStatement(sql, new String[] { "id" });
	                for (int i = 0; i < ARGS.length; i++) {
						ps.setObject((i + 1), ARGS[i]);
					}
	                return ps;
	            }
			}, keyHolder);
	        result = keyHolder.getKey().intValue();
		} catch (Exception e) {
			log.error("执行更新SQL时发生错误：" + sql, e);
			log.error("参数：" + objectArr2Str(ARGS));
		}
		return result;
	}
	
	public int update(String sql, List<Object> params) {
		Object args[] = null;
		int result = 0;
		try {
			if (null == params) {
				result = this.getJdbcTemplate().update(sql);
			} else {
				args = params.toArray();
				result = this.getJdbcTemplate().update(sql, args);
			}
			log.info("执行更新SQL：" + sql);
			log.info("参数：" + objectArr2Str(args));
			log.info("执行结果：" + result + "条");
		} catch (Exception e) {
			log.error("执行更新SQL时发生错误：" + sql, e);
			log.error("参数：" + objectArr2Str(args));
		}
		return result;
	}

	public List<Map<String, Object>> query4List(String sql, List<Object> params) {
		Object args[] = null;
		List<Map<String, Object>> mapList = null;
		try {
			if (null == params) {
				mapList = this.getJdbcTemplate().queryForList(sql);
			} else {
				args = params.toArray();
				mapList = this.getJdbcTemplate().queryForList(sql, args);
			}
			log.info("执行查询SQL：" + sql);
			log.info("参数：" + objectArr2Str(args));
			log.info("执行结果：" + mapList.size() + "条");
		} catch (Exception e) {
			log.error("执行查询SQL时发生错误：" + sql, e);
			log.error("参数：" + objectArr2Str(args));
		}
		return mapList;
	}

	public int query4Integer(String sql, List<Object> params) {
		Object args[] = null;
		List<Integer> intList = null;
		int result = -1;
		try {
			if (null == params) {
				intList = this.getJdbcTemplate().queryForList(sql, Integer.class);
			} else {
				args = params.toArray();
				intList = this.getJdbcTemplate().queryForList(sql, args, Integer.class);
			}
			if (!intList.isEmpty() && intList.size() > 0) {
				result = intList.get(0);
			}
			log.info("执行查询SQL：" + sql);
			log.info("参数：" + objectArr2Str(args));
			log.info("执行结果：" + (intList == null ? 0 : intList.size()) + "条");
		} catch (Exception e) {
			log.error("执行查询SQL时发生错误：" + sql, e);
			log.error("参数：" + objectArr2Str(args));
		}
		return result;
	}
	
	public String query4String(String sql, List<Object> params) {
		Object args[] = null;
		List<String> strList = null;
		String result = null;
		try {
			if (null == params) {
				strList = this.getJdbcTemplate().queryForList(sql, String.class);
			} else {
				args = params.toArray();
				strList = this.getJdbcTemplate().queryForList(sql, args, String.class);
			}
			if (!strList.isEmpty() && strList.size() > 0) {
				result = strList.get(0);
			}
			log.info("执行查询SQL：" + sql);
			log.info("参数：" + objectArr2Str(args));
			log.info("执行结果：" + (strList == null ? 0 : strList.size()) + "条");
		} catch (Exception e) {
			log.error("执行查询SQL时发生错误：" + sql, e);
			log.error("参数：" + objectArr2Str(args));
		}
		return result;
	}
	
	public Map<String, Object> query4Map(String sql, List<Object> params) {
		Object args[] = null;
		List<Map<String, Object>> mapList = null;
		Map<String, Object> map = null;
		try {
			if (null == params) {
				mapList = this.getJdbcTemplate().queryForList(sql);
			} else {
				args = params.toArray();
				mapList = this.getJdbcTemplate().queryForList(sql, args);
			}
			if (!mapList.isEmpty() && mapList.size() > 0) {
				map = mapList.get(0);
			}
			log.info("执行查询SQL：" + sql);
			log.info("参数：" + objectArr2Str(args));
			log.info("执行结果：" + mapList.size() + "条");
		} catch (Exception e) {
			log.error("执行查询SQL时发生错误：" + sql, e);
			log.error("参数：" + objectArr2Str(args));
		}
		return map;
	}

	public void call(String storedProcedure, List<Object> params) {
		Object tempArgs[] = null;
		if (null != params) {
			tempArgs = params.toArray();
		}
		final String STORED_PROCEDURE_NAME = storedProcedure;
		final Object ARGS[] = tempArgs;
		try {
			this.getJdbcTemplate().execute(new CallableStatementCreator() {
				public CallableStatement createCallableStatement(Connection conn) throws SQLException {
					StringBuffer sqlBuffer = new StringBuffer("{").append(STORED_PROCEDURE_NAME).append("(");
					int index = 1;
					if(null != ARGS) {
						index = ARGS.length;
						for (int i = 0; i < index; i++) {
							if(i == ARGS.length - 1) {
								sqlBuffer.append("?");
							} else {
								sqlBuffer.append("?,");
							}
						}
					}
					
					sqlBuffer.append(")}");
					CallableStatement statement = conn.prepareCall(sqlBuffer.toString());
					if(null != ARGS) {
						for (int i = 0; i < index; i++) {
							statement.setObject((i + 1), ARGS[i]);
						}
					}
					log.info("执行存储过程：" + STORED_PROCEDURE_NAME);
					log.info("参数：" + objectArr2Str(ARGS));
					return statement;
				}
			}, new CallableStatementCallback<Object>() {
				public Object doInCallableStatement(CallableStatement statement) throws SQLException, DataAccessException {
					
					log.info("执行存储过程成功：" + STORED_PROCEDURE_NAME);
					return null;
				}
	
			});
		} catch (Exception e) {
			log.error("执行存储过程时发生错误：" + STORED_PROCEDURE_NAME, e);
			log.error("参数：" + objectArr2Str(ARGS));
		}
	}

	private static String objectArr2Str(Object objArr[]) {
		StringBuffer strBuffer = new StringBuffer("[");
		if (null != objArr) {
			for (int i = 0; i < objArr.length; i++) {
				strBuffer.append(null == objArr[i] ? null : objArr[i].toString());
				if (i != objArr.length - 1) {
					strBuffer.append(", ");
				}
			}
		}
		strBuffer.append("]");
		return strBuffer.toString();
	}
	
}
