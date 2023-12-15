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
import java.util.List;
import java.util.function.Consumer;
import org.eclipse.osee.jdbc.JdbcDbType;
import org.eclipse.osee.jdbc.JdbcStatement;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Jaden W. Puckett
 */
public class HealthSqlTableSize {
   private final OrcsApi orcsApi;
   private String errorMsg = "";
   private Integer size;

   public HealthSqlTableSize(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   public void querySqlHealthTableSize() {
      if (orcsApi.getJdbcService().getClient().getDbType().equals(JdbcDbType.oracle)) {
         List<String> password = getPassword();
         if (password.size() == 1) {
            String setRoleQuery = "SET ROLE osee_health IDENTIFIED BY " + password.get(0);
            orcsApi.getJdbcService().getClient().runCall(setRoleQuery);

            String selectCountFromMonitoringQuery = "SELECT COUNT(*) AS ROW_COUNT FROM OSEE_SQL_MONITORING";
            Consumer<JdbcStatement> consumer = stmt -> {
               size = stmt.getInt("ROW_COUNT");
            };
            orcsApi.getJdbcService().getClient().runQuery(consumer, selectCountFromMonitoringQuery);

            String unsetRoleQuery = "SET ROLE ALL EXCEPT osee_health";
            orcsApi.getJdbcService().getClient().runCall(unsetRoleQuery);
         }
      } else if (orcsApi.getJdbcService().getClient().getDbType().equals(JdbcDbType.postgresql)) {
         String selectCountFromStatsQuery = "select count(*) as row_count from pg_stat_statements";
         Consumer<JdbcStatement> consumer = stmt -> {
            size = stmt.getInt("row_count");
         };
         orcsApi.getJdbcService().getClient().runQuery(consumer, selectCountFromStatsQuery);
      } else {
         this.errorMsg = "Using unsupported database";
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

   public String getErrorMsg() {
      return errorMsg;
   }

   public Integer getSize() {
      return size;
   }
}
