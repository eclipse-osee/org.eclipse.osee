/*
 * Created on May 21, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.support.test.util;

import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public class TestUtil {

   public static String DEMO_CODE_TEAM_WORKFLOW_ARTIFACT = "Demo Code Team Workflow";
   public static String DEMO_REQ_TEAM_WORKFLOW_ARTIFACT = "Demo Req Team Workflow";
   public static String DEMO_TEST_TEAM_WORKFLOW_ARTIFACT = "Demo Test Team Workflow";

   public static boolean isProductionDb() throws OseeCoreException {
      return ClientSessionManager.isProductionDataStore();
   }

   public static boolean isDemoDb() throws OseeCoreException {
      return ClientSessionManager.getAuthenticationProtocols().contains("demo");
   }

   public static void sleep(long milliseconds) throws Exception {
      System.out.println("Sleeping " + milliseconds);
      Thread.sleep(milliseconds);
      System.out.println("Awake");
   }

}
