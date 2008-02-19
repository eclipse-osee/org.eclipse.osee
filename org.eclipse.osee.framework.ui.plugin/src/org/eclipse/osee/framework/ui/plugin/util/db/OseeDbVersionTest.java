/*
 * Created on Jan 6, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.plugin.util.db;

import java.sql.SQLException;
import junit.framework.TestCase;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.plugin.core.db.OseeCodeVersion;
import org.eclipse.osee.framework.plugin.core.db.OseeDbVersion;

/**
 * @author Donald G. Dunne
 */
public class OseeDbVersionTest extends TestCase {

   private static String PRE_TIME_STAMP = "0.1.3 M2 2007-12-04 17:00";
   private static String TIME_STAMP = "0.1.3 M2 2007-12-04 17:13";
   private static String POST_TIME_STAMP = "0.1.3 M2 2007-12-04 17:33";

   /**
    * Test method for
    * {@link org.eclipse.osee.framework.plugin.core.db.OseeDbVersion#setOseeDbVersion(java.sql.Connection)}.
    */
   public void testSetOseeDbVersion() throws Exception {
      OseeDbVersion.setOseeDbVersion(ConnectionHandler.getConnection(), TIME_STAMP);
      String str = OseeDbVersion.getOseeDbVersion(ConnectionHandler.getConnection());
      assertTrue(str.equals(TIME_STAMP));
      OseeDbVersion.setOseeDbVersion(ConnectionHandler.getConnection(), OseeCodeVersion.DEFAULT_DEVELOPMENT_VERSION);
      str = OseeDbVersion.getOseeDbVersion(ConnectionHandler.getConnection());
      assertTrue(str.equals(OseeCodeVersion.DEFAULT_DEVELOPMENT_VERSION));
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.framework.plugin.core.db.OseeDbVersion#getOseeDbVersion(java.sql.Connection)}.
    */
   public void testGetOseeDbVersion() throws SQLException {
      String str = OseeDbVersion.getOseeDbVersion(ConnectionHandler.getConnection());
      assertTrue(str.equals(OseeCodeVersion.DEFAULT_DEVELOPMENT_VERSION));
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.framework.plugin.core.db.OseeDbVersion#setOseeDbCheckVersion(java.sql.Connection)}.
    */
   public void testSetOseeDbCheckVersion() throws SQLException {
      OseeDbVersion.setOseeDbCheckVersion(ConnectionHandler.getConnection(), false);
      boolean set = OseeDbVersion.getOseeDbCheckVersion(ConnectionHandler.getConnection());
      assertFalse(set);
      OseeDbVersion.setOseeDbCheckVersion(ConnectionHandler.getConnection(), true);
      set = OseeDbVersion.getOseeDbCheckVersion(ConnectionHandler.getConnection());
      assertTrue(set);
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.framework.plugin.core.db.OseeDbVersion#getOseeDbCheckVersion(java.sql.Connection)}.
    */
   public void testGetOseeDbCheckVersion() throws SQLException {
      boolean check = OseeDbVersion.getOseeDbCheckVersion(ConnectionHandler.getConnection());
      assertTrue(check);
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.framework.plugin.core.db.OseeDbVersion#ensureDatabaseCompatability(java.sql.Connection)}.
    */
   public void testEnsureDatabaseCompatability_DevDevCheck() throws Exception {
      // codeVersion = "Development";  dbVersion = "Development"; checkVersion = true; == PASS 
      OseeCodeVersion.getInstance().set(OseeCodeVersion.DEFAULT_DEVELOPMENT_VERSION);
      OseeDbVersion.setOseeDbVersion(ConnectionHandler.getConnection(), OseeCodeVersion.DEFAULT_DEVELOPMENT_VERSION);
      OseeDbVersion.setOseeDbCheckVersion(ConnectionHandler.getConnection(), true);
      OseeDbVersion.ensureDatabaseCompatability(ConnectionHandler.getConnection());
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.framework.plugin.core.db.OseeDbVersion#ensureDatabaseCompatability(java.sql.Connection)}.
    */
   public void testEnsureDatabaseCompatability_DevTimeCheck() throws Exception {
      // codeVersion = "Development";  dbVersion = TIME_STAMP; checkVersion = true; == PASS 
      OseeCodeVersion.getInstance().set(OseeCodeVersion.DEFAULT_DEVELOPMENT_VERSION);
      OseeDbVersion.setOseeDbVersion(ConnectionHandler.getConnection(), TIME_STAMP);
      OseeDbVersion.setOseeDbCheckVersion(ConnectionHandler.getConnection(), true);
      OseeDbVersion.ensureDatabaseCompatability(ConnectionHandler.getConnection());
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.framework.plugin.core.db.OseeDbVersion#ensureDatabaseCompatability(java.sql.Connection)}.
    */
   public void testEnsureDatabaseCompatability_TimeTimeCheck() throws Exception {
      // codeVersion = TIME_STAMP;  dbVersion = TIME_STAMP; checkVersion = true; == PASS 
      OseeCodeVersion.getInstance().set(TIME_STAMP);
      OseeDbVersion.setOseeDbVersion(ConnectionHandler.getConnection(), TIME_STAMP);
      OseeDbVersion.setOseeDbCheckVersion(ConnectionHandler.getConnection(), true);
      OseeDbVersion.ensureDatabaseCompatability(ConnectionHandler.getConnection());
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.framework.plugin.core.db.OseeDbVersion#ensureDatabaseCompatability(java.sql.Connection)}.
    */
   public void testEnsureDatabaseCompatability_PosttimeTimeCheck() throws Exception {
      // codeVersion = POST_TIME_STAMP;  dbVersion = TIME_STAMP; checkVersion = true; == PASS 
      OseeCodeVersion.getInstance().set(POST_TIME_STAMP);
      OseeDbVersion.setOseeDbVersion(ConnectionHandler.getConnection(), TIME_STAMP);
      OseeDbVersion.setOseeDbCheckVersion(ConnectionHandler.getConnection(), true);
      OseeDbVersion.ensureDatabaseCompatability(ConnectionHandler.getConnection());
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.framework.plugin.core.db.OseeDbVersion#ensureDatabaseCompatability(java.sql.Connection)}.
    */
   public void testEnsureDatabaseCompatability_PretimeTimeCheck() throws Exception {
      // codeVersion = PRE_TIME_STAMP;  dbVersion = TIME_STAMP; checkVersion = true; == Exception
      OseeCodeVersion.getInstance().set(PRE_TIME_STAMP);
      OseeDbVersion.setOseeDbVersion(ConnectionHandler.getConnection(), TIME_STAMP);
      OseeDbVersion.setOseeDbCheckVersion(ConnectionHandler.getConnection(), true);
      boolean exception = false;
      try {
         OseeDbVersion.ensureDatabaseCompatability(ConnectionHandler.getConnection());
      } catch (IllegalArgumentException ex) {
         if (ex.getLocalizedMessage().contains("is out of date")) exception = true;
      }
      assertTrue(exception);
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.framework.plugin.core.db.OseeDbVersion#ensureDatabaseCompatability(java.sql.Connection)}.
    */
   public void testEnsureDatabaseCompatability_PretimeTimeDontcheck() throws Exception {
      // codeVersion = PRE_TIME_STAMP;  dbVersion = TIME_STAMP; checkVersion = false; == PASS
      OseeCodeVersion.getInstance().set(PRE_TIME_STAMP);
      OseeDbVersion.setOseeDbVersion(ConnectionHandler.getConnection(), TIME_STAMP);
      OseeDbVersion.setOseeDbCheckVersion(ConnectionHandler.getConnection(), false);
      OseeDbVersion.ensureDatabaseCompatability(ConnectionHandler.getConnection());
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.framework.plugin.core.db.OseeDbVersion#ensureDatabaseCompatability(java.sql.Connection)}.
    */
   public void testEnsureDatabaseCompatability_PretimeTimeCheckParamoverride() throws Exception {
      // codeVersion = PRE_TIME_STAMP;  dbVersion = TIME_STAMP; checkVersion = true; == Exception
      // AND set runtime parameter to override check == Ok
      OseeCodeVersion.getInstance().set(PRE_TIME_STAMP);
      OseeDbVersion.setOseeDbVersion(ConnectionHandler.getConnection(), TIME_STAMP);
      OseeDbVersion.setOseeDbCheckVersion(ConnectionHandler.getConnection(), true);
      System.setProperty(OseeProperties.OSEE_OVERRIDE_VERSION_CHECK, "");
      OseeDbVersion.ensureDatabaseCompatability(ConnectionHandler.getConnection());
   }

}
