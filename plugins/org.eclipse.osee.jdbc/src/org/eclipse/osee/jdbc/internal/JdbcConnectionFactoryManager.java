/*********************************************************************
 * Copyright (c) 2013 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.jdbc.internal;

import static org.eclipse.osee.jdbc.JdbcException.newJdbcException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import org.eclipse.osee.jdbc.JdbcDbType;
import org.eclipse.osee.jdbc.JdbcException;

/**
 * @author Roberto E. Escobar
 */
public class JdbcConnectionFactoryManager {

   private final Map<String, JdbcConnectionFactory> factories;

   public JdbcConnectionFactoryManager(Map<String, JdbcConnectionFactory> factories) {
      this.factories = factories;
   }

   public MetaData getMetaData(JdbcConnectionInfo dbInfo) {
      JdbcConnectionFactory proxiedFactory = getFactory(dbInfo.getDriver());
      return getMetaData(proxiedFactory, dbInfo);
   }

   public JdbcConnectionFactory getFactory(String driver) {
      JdbcConnectionFactory factory = factories.get(driver);
      if (factory == null) {
         factory = new DefaultConnectionFactory(driver);
         factories.put(driver, factory);
      }
      return factory;
   }

   private MetaData getMetaData(JdbcConnectionFactory proxiedFactory, JdbcConnectionInfo dbInfo) {
      MetaData metaData = new MetaData();
      Connection connection = null;
      try {
         connection = proxiedFactory.getConnection(dbInfo);
         DatabaseMetaData metadata = connection.getMetaData();
         metaData.setTxIsolationLevelSupported(
            metadata.supportsTransactionIsolationLevel(Connection.TRANSACTION_READ_COMMITTED));
         metaData.setValidationQuery(JdbcDbType.getDbType(metadata).getValidationSql());
      } catch (SQLException ex) {
         throw JdbcException.newJdbcException(ex);
      } finally {
         if (connection != null) {
            try {
               connection.close();
            } catch (SQLException ex) {
               throw JdbcException.newJdbcException(ex);
            }
         }
      }
      return metaData;
   }

   public static final class MetaData {
      private boolean isTxIsolationLevelSupported;
      private String validationQuery;

      public boolean isTxIsolationLevelSupported() {
         return isTxIsolationLevelSupported;
      }

      public void setTxIsolationLevelSupported(boolean isTxIsolationLevelSupported) {
         this.isTxIsolationLevelSupported = isTxIsolationLevelSupported;
      }

      public String getValidationQuery() {
         return validationQuery;
      }

      public void setValidationQuery(String validationQuery) {
         this.validationQuery = validationQuery;
      }

   }

   private static final class DefaultConnectionFactory implements JdbcConnectionFactory {

      private final String driver;

      public DefaultConnectionFactory(String driver) {
         this.driver = driver;
      }

      @Override
      public Connection getConnection(JdbcConnectionInfo dbInfo) {
         try {
            Class.forName(driver);
         } catch (Exception ex) {
            throw newJdbcException(ex, "Unable to find connection factory with driver [%s]", driver);
         }
         try {
            return DriverManager.getConnection(dbInfo.getUri(), dbInfo.getProperties());
         } catch (Exception ex) {
            throw newJdbcException(ex, "Unable to get connection for db - [%s]", dbInfo);
         }
      }

      @Override
      public String getDriver() {
         return driver;
      }
   }

}