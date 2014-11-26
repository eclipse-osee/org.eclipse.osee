/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.intergration;

import static org.eclipse.osee.orcs.db.intergration.IntegrationUtil.integrationRule;
import java.sql.DatabaseMetaData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.jdbc.JdbcConnection;
import org.eclipse.osee.jdbc.JdbcDbType;
import org.eclipse.osee.jdbc.JdbcService;
import org.eclipse.osee.orcs.core.ds.DataStoreConstants;
import org.eclipse.osee.orcs.db.internal.SqlProvider;
import org.eclipse.osee.orcs.db.internal.accessor.OseeInfoDataAccessor;
import org.eclipse.osee.orcs.db.internal.resource.ResourceConstants;
import org.eclipse.osee.orcs.db.mock.OsgiService;
import org.eclipse.osee.orcs.db.mocks.MockLog;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.rules.TestRule;

/**
 * Test Case for {@link OseeInfoDataAccessor}
 * 
 * @author Roberto E. Escobar
 */
public class OseeInfoDataAccessorTest {

   @Rule
   public TestRule db = integrationRule(this);

   //@formatter:off
   @OsgiService private JdbcService jdbcService;
   //@formatter:on

   @org.junit.Test
   public void testGetSetValue() throws OseeCoreException {
      OseeInfoDataAccessor accessor = new OseeInfoDataAccessor();
      accessor.setLogger(new MockLog());
      accessor.setJdbcService(jdbcService);

      String value = accessor.getValue("test.data");
      Assert.assertEquals("", value);

      boolean wasSuccessful = accessor.putValue("test.data", "testing 1,2,3");
      Assert.assertTrue(wasSuccessful);

      String value1 = accessor.getValue("test.data");
      Assert.assertEquals("testing 1,2,3", value1);
   }

   @org.junit.Test(expected = OseeStateException.class)
   public void testSetBinaryDataPath() throws OseeCoreException {
      OseeInfoDataAccessor accessor = new OseeInfoDataAccessor();
      accessor.setLogger(new MockLog());
      accessor.setJdbcService(jdbcService);

      accessor.putValue(ResourceConstants.BINARY_DATA_PATH, "dummy");
   }

   @org.junit.Test
   public void testGetBinaryDataPath() throws OseeCoreException {
      OseeInfoDataAccessor accessor = new OseeInfoDataAccessor();
      accessor.setLogger(new MockLog());
      accessor.setJdbcService(jdbcService);

      String original = accessor.getValue(ResourceConstants.BINARY_DATA_PATH);
      Assert.assertEquals(System.getProperty(ResourceConstants.BINARY_DATA_PATH), original);

      System.setProperty(ResourceConstants.BINARY_DATA_PATH, "");
      try {
         String actual = accessor.getValue(ResourceConstants.BINARY_DATA_PATH);
         Assert.assertEquals(System.getProperty("user.home"), actual);
      } finally {
         System.setProperty(ResourceConstants.BINARY_DATA_PATH, original);
      }
   }

   @org.junit.Test(expected = OseeStateException.class)
   public void testSetDatabaseHintsSupported() throws OseeCoreException {
      OseeInfoDataAccessor accessor = new OseeInfoDataAccessor();
      accessor.setLogger(new MockLog());
      accessor.setJdbcService(jdbcService);

      accessor.putValue(SqlProvider.SQL_DATABASE_HINTS_SUPPORTED_KEY, "dummy");
   }

   @org.junit.Test
   public void testGetDatabaseHintsSupported() throws OseeCoreException {
      OseeInfoDataAccessor accessor = new OseeInfoDataAccessor();
      accessor.setLogger(new MockLog());
      accessor.setJdbcService(jdbcService);

      String original = accessor.getValue(SqlProvider.SQL_DATABASE_HINTS_SUPPORTED_KEY);

      boolean expected = false;
      JdbcConnection connection = jdbcService.getClient().getConnection();
      try {
         DatabaseMetaData metaData = connection.getMetaData();
         expected = JdbcDbType.isDatabaseType(metaData, JdbcDbType.oracle);
      } finally {
         connection.close();
      }
      Assert.assertEquals(expected, Boolean.parseBoolean(original));
   }

   @org.junit.Test(expected = OseeStateException.class)
   public void testSetSQLRecursiveKeyword() throws OseeCoreException {
      OseeInfoDataAccessor accessor = new OseeInfoDataAccessor();
      accessor.setLogger(new MockLog());
      accessor.setJdbcService(jdbcService);

      accessor.putValue(SqlProvider.SQL_RECURSIVE_WITH_KEY, "dummy");
   }

   @org.junit.Test
   public void testGetSQLRecursiveKeyword() throws OseeCoreException {
      OseeInfoDataAccessor accessor = new OseeInfoDataAccessor();
      accessor.setLogger(new MockLog());
      accessor.setJdbcService(jdbcService);

      String original = accessor.getValue(SqlProvider.SQL_RECURSIVE_WITH_KEY);

      String expected = "";
      JdbcConnection connection = jdbcService.getClient().getConnection();
      try {
         DatabaseMetaData metaData = connection.getMetaData();
         if (!JdbcDbType.isDatabaseType(metaData, JdbcDbType.oracle)) {
            expected = "RECURSIVE";
         }
      } finally {
         connection.close();
      }
      Assert.assertEquals(expected, original);
   }

   @org.junit.Test(expected = OseeStateException.class)
   public void testSetSQLRegExpPattern() throws OseeCoreException {
      OseeInfoDataAccessor accessor = new OseeInfoDataAccessor();
      accessor.setLogger(new MockLog());
      accessor.setJdbcService(jdbcService);

      accessor.putValue(SqlProvider.SQL_REG_EXP_PATTERN_KEY, "dummy");
   }

   @org.junit.Test
   public void testGetSQLRegExpPattern() throws OseeCoreException {
      OseeInfoDataAccessor accessor = new OseeInfoDataAccessor();
      accessor.setLogger(new MockLog());
      accessor.setJdbcService(jdbcService);

      String original = accessor.getValue(SqlProvider.SQL_REG_EXP_PATTERN_KEY);

      String expected = "";
      JdbcConnection connection = jdbcService.getClient().getConnection();
      try {
         DatabaseMetaData metaData = connection.getMetaData();
         JdbcDbType db = JdbcDbType.getDatabaseType(metaData);
         if (JdbcDbType.oracle == db) {
            expected = "REGEXP_LIKE (%s, %s)";
         } else if (JdbcDbType.hsql == db || JdbcDbType.postgresql == db) {
            expected = "REGEXP_MATCHES (%s, %s)";
         } else if (JdbcDbType.mysql == db) {
            expected = "(%s REGEXP %s)";
         }
      } finally {
         connection.close();
      }
      Assert.assertEquals(expected, original);
   }

   @org.junit.Test(expected = OseeStateException.class)
   public void testSetCheckTagQueueOnStartupAllowed() throws OseeCoreException {
      OseeInfoDataAccessor accessor = new OseeInfoDataAccessor();
      accessor.setLogger(new MockLog());
      accessor.setJdbcService(jdbcService);

      accessor.putValue(DataStoreConstants.DATASTORE_INDEX_ON_START_UP, "dummy");
   }

   @org.junit.Test
   public void testGetCheckTagQueueOnStartupAllowed() throws OseeCoreException {
      OseeInfoDataAccessor accessor = new OseeInfoDataAccessor();
      accessor.setLogger(new MockLog());
      accessor.setJdbcService(jdbcService);

      String original = accessor.getValue(DataStoreConstants.DATASTORE_INDEX_ON_START_UP);
      Assert.assertEquals(System.getProperty(DataStoreConstants.DATASTORE_INDEX_ON_START_UP, "false"), original);

      System.setProperty(DataStoreConstants.DATASTORE_INDEX_ON_START_UP, "true");
      try {
         String actual = accessor.getValue(DataStoreConstants.DATASTORE_INDEX_ON_START_UP);
         Assert.assertEquals("true", actual);
      } finally {
         System.setProperty(DataStoreConstants.DATASTORE_INDEX_ON_START_UP, original);
      }
   }

   @org.junit.Test
   public void testGetKeys() throws OseeCoreException {
      OseeInfoDataAccessor accessor = new OseeInfoDataAccessor();
      accessor.setLogger(new MockLog());
      accessor.setJdbcService(jdbcService);
      Assert.assertTrue(!accessor.getKeys().isEmpty());
   }

}