/*
 * Created on May 26, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.client.integration.tests.suite;

import static org.junit.Assert.assertTrue;
import java.util.logging.Level;
import org.eclipse.osee.ats.config.demo.config.DemoDbUtil;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.client.OseeClientProperties;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.database.init.DatabaseInitializationOperation;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.ui.skynet.render.RenderingUtil;
import org.eclipse.osee.support.test.util.TestUtil;
import org.junit.BeforeClass;

/**
 * @author Donald G. Dunne
 */
public class DemoDbInitTest {
   private static boolean wasDbInitSuccessful = false;

   @BeforeClass
   public static void setup() throws Exception {
      OseeProperties.setIsInTest(true);
      assertTrue("Demo Application Server must be running",
         ClientSessionManager.getAuthenticationProtocols().contains("demo"));
      RenderingUtil.setPopupsAllowed(false);
      try {
         DemoDbUtil.setDbInitSuccessful(false);
      } catch (OseeCoreException ex) {
         if (!ex.getMessage().contains("Schema OSEE not found")) {
            throw ex;
         }
      }
   }

   @org.junit.Test
   public void testDemoDbInit() throws Exception {
      System.out.println("\nBegin database initialization...");

      String lastAuthenticationProtocol = OseeClientProperties.getAuthenticationProtocol();
      try {
         SevereLoggingMonitor monitorLog = TestUtil.severeLoggingStart();
         OseeLog.registerLoggerListener(monitorLog);
         OseeClientProperties.setAuthenticationProtocol("trustAll");
         DatabaseInitializationOperation.executeWithoutPrompting("OSEE Demo Database");

         TestUtil.severeLoggingEnd(monitorLog);
         OseeLog.log(DatabaseInitializationOperation.class, Level.INFO, "Completed database initialization");
         wasDbInitSuccessful = true;
      } finally {
         OseeClientProperties.setAuthenticationProtocol(lastAuthenticationProtocol);
      }

      if (wasDbInitSuccessful) {
         DemoDbUtil.setDbInitSuccessful(true);

         ClientSessionManager.releaseSession();
         // Re-authenticate so we can continue
         ClientSessionManager.getSession();
      }

      System.out.println("End database initialization...");

   }

}
