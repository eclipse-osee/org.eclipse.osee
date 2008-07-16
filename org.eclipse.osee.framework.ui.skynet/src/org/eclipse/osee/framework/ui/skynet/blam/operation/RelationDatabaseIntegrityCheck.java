/*
 * Created on Jun 24, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.blam.operation;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.xresults.XResultData;

/**
 * @author Jeff C. Phillips
 */
public class RelationDatabaseIntegrityCheck extends AbstractBlam {

   private static final String NO_ADDRESSING_ARTIFACTS =
         "select * from osee_Define_rel_link t1 where (a_art_id not in (select art_id from osee_Define_artifact_version t2, osee_Define_txs t3 where t2.gamma_id = t3.gamma_id) OR b_art_id not in (select art_id from osee_Define_artifact_version t4, osee_Define_txs t5 where t4.gamma_id = t5.gamma_id))";
   private static final String DELETED_ARTIFACTS = "";

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#runOperation(org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap, org.eclipse.osee.framework.skynet.core.artifact.Branch, org.eclipse.core.runtime.IProgressMonitor)
    */
   public void runOperation(BlamVariableMap variableMap, IProgressMonitor monitor) throws Exception {
      try {
         final XResultData rd = new XResultData(SkynetActivator.getLogger());
         runIt(monitor, rd);
         rd.report(getName());
      } catch (Exception ex) {
         OSEELog.logException(SkynetActivator.class, ex, false);
      }
      monitor.done();
   }

   /**
    * @param monitor
    * @param rd
    */
   private void runIt(IProgressMonitor monitor, XResultData rd) {
   }

   public String getXWidgetsXml() {
      return "<xWidgets></xWidgets>";
   }
}
