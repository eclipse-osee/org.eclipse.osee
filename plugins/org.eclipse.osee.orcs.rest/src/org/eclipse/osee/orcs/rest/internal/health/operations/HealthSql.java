/*******************************************************************************
 * Copyright (c) 2023 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.rest.internal.health.operations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import org.eclipse.osee.jdbc.JdbcDbType;
import org.eclipse.osee.jdbc.JdbcStatement;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Jaden W. Puckett
 */
public class HealthSql {
   private OrcsApi orcsApi;
   private String errorMsg = "";
   private long pageNum;
   private long pageSize;
   private String orderByName;
   private String orderByDirection;
   private final List<SqlMonitoringMetric> sqls = new ArrayList<>();

   public HealthSql(OrcsApi orcsApi, long pageNum, long pageSize, String orderByName, String orderByDirection) {
      this.orcsApi = orcsApi;
      this.pageNum = pageNum;
      this.pageSize = pageSize;
      this.orderByName = orderByName;
      this.orderByDirection = orderByDirection;
   }

   public HealthSql(String errorMsg) {
      this.errorMsg = errorMsg;
   }

   public void querySqlHealth() {
      if (orcsApi.getJdbcService().getClient().getDbType().equals(JdbcDbType.oracle)) {
         List<String> password = getPassword();
         if (password.size() == 1) {
            String setRoleQuery = "SET ROLE osee_health IDENTIFIED BY " + password.get(0);
            orcsApi.getJdbcService().getClient().runCall(setRoleQuery);

            String selectFromMonitoringQuery =
               "SELECT * FROM (SELECT 100 * ELAPSED_TIME / SUM(ELAPSED_TIME) OVER () PERCENT, ELAPSED_TIME, SQL_FULLTEXT, SQL_ID, CHILD_NUMBER, EXECUTIONS, ELAPSED_TIME_AVG, row_number() OVER (" + handleOrderBy() + ") rn FROM OSEE_SQL_MONITORING) monitor " + handlePagination();
            Consumer<JdbcStatement> consumer = stmt -> {
               sqls.add(new SqlMonitoringMetric(stmt.getString("SQL_FULLTEXT"), stmt.getString("ELAPSED_TIME"),
                  stmt.getString("EXECUTIONS"), stmt.getString("ELAPSED_TIME_AVG"),
                  Double.toString(stmt.getDouble("PERCENT"))));
            };
            orcsApi.getJdbcService().getClient().runQuery(consumer, selectFromMonitoringQuery);

            String unsetRoleQuery = "SET ROLE ALL EXCEPT osee_health";
            orcsApi.getJdbcService().getClient().runCall(unsetRoleQuery);
         }
      } else if (orcsApi.getJdbcService().getClient().getDbType().equals(JdbcDbType.postgresql)) {
         String pgStatsQuery =
            "select round(( 100 * total_exec_time / sum(total_exec_time) over ())::numeric, 2) as PERCENT, total_exec_time as ELAPSED_TIME, calls as EXECUTIONS, (total_exec_time / calls) as ELAPSED_TIME_AVG, query as SQL_FULLTEXT, mean_exec_time, stddev_exec_time from pg_stat_statements " + handleOrderBy() + " " + handlePagination();
         Consumer<JdbcStatement> consumer = stmt -> {
            sqls.add(new SqlMonitoringMetric(stmt.getString("SQL_FULLTEXT"), stmt.getString("ELAPSED_TIME"),
               stmt.getString("EXECUTIONS"), stmt.getString("ELAPSED_TIME_AVG"), stmt.getString("PERCENT")));
         };
         orcsApi.getJdbcService().getClient().runQuery(consumer, pgStatsQuery);
      } else {
         this.errorMsg = "Using unsupported database";
      }
      // Sort calculated by calculated PERCENT column (if specified by orderByName)
      if (orderByName.equals("percent")) {
         if (orderByDirection.equals("asc")) {
            Collections.sort(sqls, Comparator.comparingDouble(obj -> Double.parseDouble(obj.getPercent())));
         } else {
            Collections.sort(sqls,
               Comparator.comparing(obj -> Double.parseDouble(obj.getPercent()), Comparator.reverseOrder()));
         }
      }

   }

   private List<String> getPassword() {
      List<String> password = new ArrayList<>();
      Consumer<JdbcStatement> consumer = stmt -> {
         password.add(stmt.getString("OSEE_VALUE"));
      };
      String selectPasswordQuery = "SELECT OSEE_VALUE FROM OSEE_INFO WHERE OSEE_KEY = 'osee.health.view.password'";
      orcsApi.getJdbcService().getClient().runQuery(consumer, selectPasswordQuery);
      return password;
   }

   public class SqlMonitoringMetric {
      private String sqlText = "";
      private String elapsedTime = "";
      private String executions = "";
      private String elapsedTimeAverage = "";
      private String percent = "";

      public SqlMonitoringMetric(String sqlText, String elapsedTime, String executions, String elapsedTimeAverage, String percent) {
         this.sqlText = sqlText;
         this.elapsedTime = elapsedTime;
         this.executions = executions;
         this.elapsedTimeAverage = elapsedTimeAverage;
         this.percent = percent;
      }

      public String getSqlText() {
         return sqlText;
      }

      public String getElapsedTime() {
         return elapsedTime;
      }

      public String getExecutions() {
         return executions;
      }

      public String getElapsedTimeAverage() {
         return elapsedTimeAverage;
      }

      public String getPercent() {
         return percent;
      }
   }

   public String handlePagination() {
      Long tempLowerBound = (pageNum - 1) * pageSize;
      Long lowerBound = tempLowerBound == 0 ? tempLowerBound : tempLowerBound + 1L;
      Long upperBound = tempLowerBound == 0 ? lowerBound + pageSize : lowerBound + pageSize - 1L;
      if (orcsApi.getJdbcService().getClient().getDbType().equals(JdbcDbType.oracle)) {
         return "WHERE rn BETWEEN " + lowerBound + " AND " + upperBound;
      } else if (orcsApi.getJdbcService().getClient().getDbType().equals(JdbcDbType.postgresql)) {
         return "LIMIT " + pageSize + " OFFSET " + lowerBound;
      } else {
         return "";
      }
   }

   private String handleOrderBy() {
      String orderByString = "ORDER BY ";
      switch (orderByName) {
         case "elapsedTime":
            orderByString += "ELAPSED_TIME ";
            break;
         case "sqlText":
            orderByString += "SQL_FULLTEXT ";
            break;
         case "executions":
            orderByString += "EXECUTIONS ";
            break;
         case "elapsedTimeAverage":
            orderByString += "ELAPSED_TIME_AVG ";
            break;
         default:
            orderByString += "ELAPSED_TIME ";
      }
      switch (orderByDirection) {
         case "asc":
            orderByString += "ASC";
            break;
         case "desc":
            orderByString += "DESC";
            break;
         default:
            orderByString += "DESC";
      }
      return orderByString;
   }

   public String getErrorMsg() {
      return errorMsg;
   }

   public List<SqlMonitoringMetric> getSqls() {
      return sqls;
   }
}
