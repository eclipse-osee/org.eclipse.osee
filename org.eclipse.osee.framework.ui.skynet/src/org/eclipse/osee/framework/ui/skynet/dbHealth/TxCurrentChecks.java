/*
 * Created on Oct 21, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.dbHealth;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * @author Ryan D. Brooks
 */
public class TxCurrentChecks extends DatabaseHealthOperation {

   public TxCurrentChecks() {
      super("All tx currents and mod types");
   }

   @Override
   protected void doHealthCheck(IProgressMonitor monitor) throws Exception {
      getResultsProvider().clearTabs();

      doSubWork(new InvalidTxCurrentsAndModTypes("osee_artifact_version", "art_id", getResultsProvider(),
            isFixOperationEnabled()), monitor, 0.3);
      doSubWork(new InvalidTxCurrentsAndModTypes("osee_attribute", "attr_id", getResultsProvider(),
            isFixOperationEnabled()), monitor, 0.3);
      doSubWork(new InvalidTxCurrentsAndModTypes("osee_relation_link", "rel_link_id", getResultsProvider(),
            isFixOperationEnabled()), monitor, 0.3);
   }

   @Override
   public String getCheckDescription() {
      return "Find versions of artifact, attributes, and relations that currents that ";
   }

   @Override
   public String getFixDescription() {
      return null;
   }
}