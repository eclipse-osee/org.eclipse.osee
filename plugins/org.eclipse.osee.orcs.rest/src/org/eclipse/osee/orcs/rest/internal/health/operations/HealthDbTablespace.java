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
public class HealthDbTablespace {
   private final OrcsApi orcsApi;
   private String errorMsg = "";
   private final String orderByName;
   private final String orderByDirection;

   private final List<TablespaceMonitoringMetric> tablespaces = new ArrayList<>();

   public HealthDbTablespace(OrcsApi orcsApi, String orderByName, String orderByDirection) {
      this.orcsApi = orcsApi;
      this.orderByName = orderByName;
      this.orderByDirection = orderByDirection;
   }

   public void queryDbTablespace() {
      if (orcsApi.getJdbcService().getClient().getDbType().equals(JdbcDbType.oracle)) {
         List<String> password = getPassword();
         if (password.size() == 1) {
            String setRoleQuery = "SET ROLE osee_health IDENTIFIED BY " + password.get(0);
            orcsApi.getJdbcService().getClient().runCall(setRoleQuery);

            String selectFromTblSummaryQuery = "SELECT * FROM OSEE_DB_TABLESPACE_SUMMARY " + handleOrderByOracle();
            Consumer<JdbcStatement> consumer = stmt -> {
               tablespaces.add(new TablespaceMonitoringMetric(stmt.getString("TABLESPACE_NAME"),
                  stmt.getString("MAX_TS_PCT_USED"), stmt.getString("AUTO_EXT"), stmt.getString("TS_PCT_USED"),
                  stmt.getString("TS_PCT_FREE"), stmt.getString("USED_TS_SIZE"), stmt.getString("FREE_TS_SIZE"),
                  stmt.getString("CURR_TS_SIZE"), stmt.getString("MAX_TX_SIZE")));
            };
            orcsApi.getJdbcService().getClient().runQuery(consumer, selectFromTblSummaryQuery);

            String unsetRoleQuery = "SET ROLE ALL EXCEPT osee_health";
            orcsApi.getJdbcService().getClient().runCall(unsetRoleQuery);
         }
      } else if (orcsApi.getJdbcService().getClient().getDbType().equals(JdbcDbType.postgresql)) {
         String selectFromTablespaceQuery =
            "SELECT ts.spcname AS tablespace_name, pg_size_pretty(pg_tablespace_size(ts.oid)) AS tablespace_size FROM pg_tablespace ts " + handleOrderByPostgres();
         Consumer<JdbcStatement> consumer = stmt -> {
            tablespaces.add(
               new TablespaceMonitoringMetric(stmt.getString("tablespace_name"), stmt.getString("tablespace_size")));
         };
         orcsApi.getJdbcService().getClient().runQuery(consumer, selectFromTablespaceQuery);
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

   public class TablespaceMonitoringMetric {
      private String tablespaceName = "";
      private String maxTsPctUsed = "";
      private String autoExtend = "";
      private String tsPctUsed = "";
      private String tsPctFree = "";
      private String usedTsSize = "";
      private String freeTsSize = "";
      private String currTsSize = "";
      private String maxTxSize = "";

      public TablespaceMonitoringMetric(String tablespaceName, String maxTsPctUsed, String autoExtend, String tsPctUsed, String tsPctFree, String usedTsSize, String freeTsSize, String currTsSize, String maxTxSize) {
         this.tablespaceName = tablespaceName;
         if (maxTsPctUsed != null) {
            this.maxTsPctUsed = maxTsPctUsed;
         } else {
            this.maxTsPctUsed = "";
         }
         this.autoExtend = autoExtend;
         if (tsPctUsed != null) {
            this.tsPctUsed = tsPctUsed;
         } else {
            this.tsPctUsed = "";
         }
         this.tsPctFree = tsPctFree;
         if (usedTsSize != null) {
            this.usedTsSize = usedTsSize;
         } else {
            this.usedTsSize = "";
         }
         if (freeTsSize != null) {
            this.freeTsSize = freeTsSize;
         } else {
            this.freeTsSize = "";
         }
         this.currTsSize = currTsSize;
         this.maxTxSize = maxTxSize;
      }

      public TablespaceMonitoringMetric(String tablespaceName, String usedTsSize) {
         this.tablespaceName = tablespaceName;
         this.usedTsSize = usedTsSize;
      }

      public String getTablespaceName() {
         return tablespaceName;
      }

      public String getMaxTsPctUsed() {
         return maxTsPctUsed;
      }

      public String getAutoExtend() {
         return autoExtend;
      }

      public String getTsPctUsed() {
         return tsPctUsed;
      }

      public String getTsPctFree() {
         return tsPctFree;
      }

      public String getUsedTsSize() {
         return usedTsSize;
      }

      public String getFreeTsSize() {
         return freeTsSize;
      }

      public String getCurrTsSize() {
         return currTsSize;
      }

      public String getMaxTxSize() {
         return maxTxSize;
      }
   }

   public String handleOrderByPostgres() {
      String orderByString = " ORDER BY ";
      switch (orderByName) {
         case "tablespaceName":
            orderByString += "tablespace_name ";
            break;
         case "usedTsSize":
            orderByString += "tablespace_size ";
            break;
         default:
            orderByString += "tablespace_size ";
      }
      switch (orderByDirection) {
         case "asc":
            orderByString += "asc ";
            break;
         case "desc":
            orderByString += "desc ";
            break;
         default:
            orderByString += "desc ";
      }
      return orderByString;
   }

   public String handleOrderByOracle() {
      String orderByString = " ORDER BY ";
      switch (orderByName) {
         case "tablespaceName":
            orderByString += "TABLESPACE_NAME ";
            break;
         case "maxTsPctUsed":
            orderByString += "MAX_TS_PCT_USED ";
            break;
         case "autoExtend":
            orderByString += "AUTO_EXT ";
            break;
         case "tsPctUsed":
            orderByString += "TS_PCT_USED ";
            break;
         case "tsPctFree":
            orderByString += "TS_PCT_FREE ";
            break;
         case "usedTsSize":
            orderByString += "USED_TS_SIZE ";
            break;
         case "freeTsSize":
            orderByString += "FREE_TS_SIZE ";
            break;
         case "currTsSize":
            orderByString += "CURR_TS_SIZE ";
            break;
         case "maxTxSize":
            orderByString += "MAX_TS_SIZE ";
            break;
         default:
            orderByString += "MAX_TS_PCT_USED ";
      }
      switch (orderByDirection) {
         case "asc":
            orderByString += "asc ";
            break;
         case "desc":
            orderByString += "desc ";
            break;
         default:
            orderByString += "desc ";
      }
      return orderByString;
   }

   public String getErrorMsg() {
      return errorMsg;
   }

   public List<TablespaceMonitoringMetric> getTablespaces() {
      return tablespaces;
   }
}
