/*
 * Created on Oct 23, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.workflow.vue;

/**
 * @author Roberto E. Escobar
 */
public class AtsDbConfig {

   protected void executeLoadAIsAndTeamsAction(String bundleId) {
      LoadAIsAndTeamsAction.executeForDbConfig(bundleId);
   }
}
