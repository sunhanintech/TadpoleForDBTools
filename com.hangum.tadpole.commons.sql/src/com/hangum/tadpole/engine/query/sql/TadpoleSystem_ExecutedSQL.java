/*******************************************************************************
 * Copyright (c) 2013 hangum.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     hangum - initial API and implementation
 ******************************************************************************/
package com.hangum.tadpole.engine.query.sql;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.hangum.tadpole.commons.dialogs.message.dao.RequestResultDAO;
import com.hangum.tadpole.commons.exception.TadpoleSQLManagerException;
import com.hangum.tadpole.commons.libs.core.dao.LicenseDAO;
import com.hangum.tadpole.commons.libs.core.define.PublicTadpoleDefine;
import com.hangum.tadpole.commons.libs.core.message.CommonMessages;
import com.hangum.tadpole.commons.libs.core.utils.LicenseValidator;
import com.hangum.tadpole.commons.util.ApplicationArgumentUtils;
import com.hangum.tadpole.engine.initialize.TadpoleSystemInitializer;
import com.hangum.tadpole.engine.manager.TadpoleSQLManager;
import com.hangum.tadpole.engine.query.dao.system.ExecutedSQLResultDataDAO;
import com.hangum.tadpole.engine.query.dao.system.ExecutedSqlResourceDAO;
import com.hangum.tadpole.engine.query.dao.system.ExecutedSqlResourceDataDAO;
import com.hangum.tadpole.engine.query.dao.system.UserDBDAO;
import com.hangum.tadpole.engine.utils.TimeZoneUtil;
import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * executed_sql_resource관련 테이블 쿼리.
 * 해당 테이블은 사용자 sql 실행쿼리에 관한 정보를 가지고 있습니다.
 * 
 * @author hangum
 *
 */
public class TadpoleSystem_ExecutedSQL {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(TadpoleSystem_ExecutedSQL.class);
	
	/**
	 * 모든 sql 히스토리 조회
	 * 
	 * @param strEmail
	 * @param strType
	 * @param startTime
	 * @param endTime
	 * @param duringExecute
	 * @param strSearch
	 * @return
	 * @throws TadpoleSQLManagerException
	 * @throws SQLException
	 */
	public static List<RequestResultDAO> getAllExecuteQueryHistoryDetail(String strEmail, String strType, long startTime, long endTime, int duringExecute, String strSearch, int _indexStart, int _indexEnd) throws TadpoleSQLManagerException, SQLException {
		return getExecuteQueryHistoryDetail(strEmail, strType, "", startTime, endTime, duringExecute, strSearch, "", _indexStart, _indexEnd);
	}

	/**
	 * 쿼리 실행 히스토리 디테일 창을 얻습니다.
	 * 
	 * @param strEmail
	 * @param strType
	 * @param dbSeq
	 * @param startTime
	 * @param endTime
	 * @param duringExecute
	 * @param strSearch
	 * @param _indexStart
	 * @param _indexEnd
	 * @return
	 * @throws TadpoleSQLManagerException
	 * @throws SQLException
	 */
	public static List<RequestResultDAO> getExecuteQueryHistoryDetail(String strEmail, 
																	String strType, 
																	String dbSeq, 
																	long startTime, 
																	long endTime, 
																	int duringExecute, 
																	String strSearch, 
																	String aResult, 
																	int _indexStart, 
																	int _indexEnd
																	) throws TadpoleSQLManagerException, SQLException {
		LicenseDAO licenseDAO = LicenseValidator.getLicense();
		if(!licenseDAO.isValidate()) {
			return new ArrayList<RequestResultDAO>();
		}
		
		List<RequestResultDAO> returnSQLHistory = new ArrayList<RequestResultDAO>();
		
		Map<String, Object> queryMap = new HashMap<String, Object>();
		queryMap.put("email", 	strEmail);
		if(!"".equals(dbSeq)) queryMap.put("db_seq", 	dbSeq);
		if(!CommonMessages.get().All.equals(strType)) queryMap.put("type", strType);
		
		if(ApplicationArgumentUtils.isDBServer()) {
			Date date = new Date(TimeZoneUtil.chageTimeZone(startTime));
			DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			queryMap.put("startTime",  formatter.format(date));
			
			Date dateendTime = new Date(TimeZoneUtil.chageTimeZone(endTime));
			queryMap.put("endTime", formatter.format(dateendTime));			
		} else {
			queryMap.put("startTime",  startTime);
			queryMap.put("endTime", endTime);
		}
		
		queryMap.put("duration", duringExecute);
		queryMap.put("strSearch", strSearch);
		if(aResult.equalsIgnoreCase(CommonMessages.get().Success) == true) {
			queryMap.put("result", "S");
		} else if(aResult.equalsIgnoreCase(CommonMessages.get().Failure) == true){
			queryMap.put("result", "F");
		} else {
			queryMap.put("result", "%");  // All
		}
		queryMap.put("_indexStart", _indexStart);
		queryMap.put("_indexEnd", _indexEnd);
		
		SqlMapClient sqlClient = TadpoleSQLManager.getInstance(TadpoleSystemInitializer.getUserDB());
		List<java.util.Map> listResourceData =  new ArrayList<Map>();
		if(PublicTadpoleDefine.EXECUTE_SQL_TYPE.API.name().endsWith(strType)) {
			listResourceData = sqlClient.queryForList("getExecuteQueryHistoryAPIDetail", queryMap);			
		} else {
			listResourceData = sqlClient.queryForList("getExecuteQueryHistoryDetail", queryMap);
		}
		
		for (Map resultMap : listResourceData) {
			long seq = (Long)resultMap.get("executed_sql_resource_seq");

			Long startdateexecute = 0l;
			String strSQLText = (String)resultMap.get("sql_data");
			String queryDetails = (String)resultMap.get("queryDetails");
			Long enddateexecute = 0l;
			
			if(ApplicationArgumentUtils.isDBServer()) {
				startdateexecute = ((Timestamp)resultMap.get("startdateexecute")).getTime();
				enddateexecute 	= ((Timestamp)resultMap.get("enddateexecute")).getTime();
			} else {
				startdateexecute = (Long)resultMap.get("startdateexecute");
				enddateexecute = (Long)resultMap.get("enddateexecute");
			}
			
			int row = (Integer)resultMap.get("row");
			String result = (String)resultMap.get("result");
			
			String userName =  resultMap.get("name") == null?"":(String)resultMap.get("name");
			String userEmail = resultMap.get("email") == null?"":(String)resultMap.get("email");
			String strFullName = "".equals(userEmail)?"":userName+"("+ userEmail+")";
			
			String dbName = (String) resultMap.get("display_name");
			
			String ipAddress = (String) resultMap.get("ipaddress");
			int dbSeq2 = (Integer) resultMap.get("dbseq");
			
			String strMessage = (String)resultMap.get("message");
			int duration = (Integer) resultMap.get("duration");
			String strDescription = (String)resultMap.get("description");
			
			String strResultSaveYn = (String)resultMap.get("result_save_yn");

			RequestResultDAO dao = new RequestResultDAO(duration,strFullName, dbName, new Timestamp(startdateexecute), strSQLText, queryDetails, new Timestamp(enddateexecute), row, result, strMessage,
					ipAddress, dbSeq2, strDescription);
			dao.setSeq(seq);
			if(PublicTadpoleDefine.EXECUTE_SQL_TYPE.API.name().endsWith(strType)) {
				dao.setEXECUSTE_SQL_TYPE(PublicTadpoleDefine.EXECUTE_SQL_TYPE.API);
			} else {
				dao.setEXECUSTE_SQL_TYPE(PublicTadpoleDefine.EXECUTE_SQL_TYPE.EDITOR);
			}
			dao.setResult_save_yn(strResultSaveYn);
			
			returnSQLHistory.add(dao);
		}
		
		return returnSQLHistory;
	}
	
	/**
	 * execute query
	 * 
	 * @param seq
	 * @return
	 * @throws TadpoleSQLManagerException
	 * @throws SQLException
	 */
	public static List<ExecutedSQLResultDataDAO> getExecuteResultData(long seq) throws TadpoleSQLManagerException, SQLException {
		SqlMapClient sqlClient = TadpoleSQLManager.getInstance(TadpoleSystemInitializer.getUserDB());
		
		return sqlClient.queryForList("getExecuteResultData", seq);
	}
	
	/**
	 * 마지막 실행했떤 쿼리 20개를 리턴합니다.
	 * 
	 * @param user_seq
	 * @param dbSeq
	 * @return
	 * @throws TadpoleSQLManagerException, SQLException
	 */
	public static List<RequestResultDAO> getExecuteQueryHistory(int user_seq, int dbSeq, String filter) throws TadpoleSQLManagerException, SQLException {
		List<RequestResultDAO> returnSQLHistory = new ArrayList<RequestResultDAO>();
		
		Map<String, Object> queryMap = new HashMap<String, Object>();
		queryMap.put("user_seq",user_seq);
		queryMap.put("db_seq", 	dbSeq);
		queryMap.put("filter", "%" + filter + "%");
		queryMap.put("count", 	20);
		
		SqlMapClient sqlClient = TadpoleSQLManager.getInstance(TadpoleSystemInitializer.getUserDB());
		List<java.util.Map> listResourceData =  sqlClient.queryForList("getExecuteQueryHistory", queryMap);
		
		for (Map resultMap : listResourceData) {
			long seq 				= (Long)resultMap.get("executed_sql_resource_seq");
			
			Long startdateexecute 	= 0l;
			// This case sqlite
			if(resultMap.get("startdateexecute") instanceof Long) {
				startdateexecute = (Long)resultMap.get("startdateexecute");
			// This case mysql
			} else {
				startdateexecute = ((Timestamp)resultMap.get("startdateexecute")).getTime();
			}
			String strSQLText 		= (String)resultMap.get("sql_data");
			Long enddateexecute 	= 0l;
			// This case sqlite
			if(resultMap.get("enddateexecute") instanceof Long) {
				enddateexecute = (Long)resultMap.get("enddateexecute");
			// This case mysql
			} else {
				enddateexecute = ((Timestamp)resultMap.get("enddateexecute")).getTime();
			}
			
			String strMessage 		= (String)resultMap.get("message");
			
			int row 			= (Integer)resultMap.get("row");
			String result 		= (String)resultMap.get("result");
			
			int duration 		= (Integer)resultMap.get("duration");
			
			RequestResultDAO dao = new RequestResultDAO(duration, new Timestamp(startdateexecute), strSQLText, new Timestamp(enddateexecute), row, result, strMessage);
			dao.setSeq(seq);
			returnSQLHistory.add(dao);
		}
		
		return returnSQLHistory;
	}

	/**
	 * save sqlhistory 
	 * 
	 * @param user_seq
	 * @param userDB
	 * @param sqlType
	 * @param strExecuteResultData
	 * @param requestResultDAO
	 */
	public static long saveExecuteSQUeryResource(final int user_seq, final UserDBDAO userDB, final PublicTadpoleDefine.EXECUTE_SQL_TYPE sqlType, final String strExecuteResultData, final RequestResultDAO requestResultDAO) throws TadpoleSQLManagerException, SQLException {
		if(PublicTadpoleDefine.YES_NO.YES.name().equals(userDB.getIs_profile())) {
			ExecutedSqlResourceDAO executeSQLResourceDao = new ExecutedSqlResourceDAO();
			executeSQLResourceDao.setDb_seq(userDB.getSeq());
			executeSQLResourceDao.setUser_seq(user_seq);
			executeSQLResourceDao.setTypes(sqlType.toString());
			
			executeSQLResourceDao.setStartDateExecute(requestResultDAO.getStartDateExecute());
			executeSQLResourceDao.setEndDateExecute(requestResultDAO.getEndDateExecute());
			long duration = 0l;
			try {
				duration = requestResultDAO.getEndDateExecute().getTime() - requestResultDAO.getStartDateExecute().getTime();
			} catch(Exception e){}
			executeSQLResourceDao.setDuration(Integer.parseInt(""+duration));
			
			executeSQLResourceDao.setRow(requestResultDAO.getRows());
			executeSQLResourceDao.setResult(requestResultDAO.getResult());
			executeSQLResourceDao.setMessage(requestResultDAO.getMesssage());
			executeSQLResourceDao.setIpAddress(requestResultDAO.getIpAddress());
			if(!"".equals(strExecuteResultData)) {
				executeSQLResourceDao.setResult_save_yn(PublicTadpoleDefine.YES_NO.YES.name());
			}
			
			// 기존에 등록 되어 있는지 검사한다
			SqlMapClient sqlClient = TadpoleSQLManager.getInstance(TadpoleSystemInitializer.getUserDB());
			ExecutedSqlResourceDAO executeSQL =  (ExecutedSqlResourceDAO)sqlClient.insert("userExecuteSQLResourceInsert", executeSQLResourceDao); //$NON-NLS-1$
			
			insertResourceSQLData(executeSQL.getSeq(), requestResultDAO.getStartDateExecute(), requestResultDAO.getStrSQLText(), requestResultDAO.getQueryDetails());
			
			if(!"".equals(strExecuteResultData)) {
				insertResourceResultData(executeSQL.getSeq(), requestResultDAO.getStartDateExecute(), strExecuteResultData);
			}
			
			return executeSQL.getSeq();
		}
		
		return -1;
	}
	
	/**
	 * insert execute sql data
	 * 
	 * @param seq
	 * @param startDateExecute
	 * @param contents
	 * @throws TadpoleSQLManagerException, SQLException
	 */
	private static void insertResourceSQLData(final long seq, final Timestamp startDateExecute, final String sql, String queryDetails) throws TadpoleSQLManagerException, SQLException {
		SqlMapClient sqlClient = TadpoleSQLManager.getInstance(TadpoleSystemInitializer.getUserDB());
		
		// content data를 저장합니다.
		ExecutedSqlResourceDataDAO dataDao = new ExecutedSqlResourceDataDAO();
		dataDao.setExecuted_sql_resource_seq(seq);
		dataDao.setStartDateExecute(startDateExecute);
		dataDao.setSql_data(sql);
		dataDao.setQueryDetails(queryDetails);
		
		sqlClient.insert("userExecuteSQLResourceDataInsert", dataDao); //$NON-NLS-1$				
	}
	
	/**
	 * insert execute sql result data 
	 * 
	 * @param seq
	 * @param startDateExecute
	 * @param contents
	 * @throws TadpoleSQLManagerException, SQLException
	 */
	public static void insertResourceResultData(final long seq, final Timestamp startDateExecute, final String contents) throws TadpoleSQLManagerException, SQLException {
		SqlMapClient sqlClient = TadpoleSQLManager.getInstance(TadpoleSystemInitializer.getUserDB());
		
		// content data를 저장합니다.
		ExecutedSQLResultDataDAO dataDao = new ExecutedSQLResultDataDAO();
		dataDao.setExecuted_sql_resource_seq(seq);
		dataDao.setStartDateExecute(startDateExecute);
		dataDao.setResult_data(contents);
		
		sqlClient.insert("InsertExecuteSQLResultData", dataDao); //$NON-NLS-1$				
	}

}
