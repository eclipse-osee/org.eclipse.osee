/*
 * Created on Jun 16, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.config;

import java.util.Arrays;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.util.AtsRelation;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.relation.CoreRelationEnumeration;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkItemDefinitionFactory;

/**
 * @author Donald G. Dunne
 */
public class BulkLoadAtsCache extends org.eclipse.core.runtime.jobs.Job {

   private BulkLoadAtsCache() {
      super("Bulk Loading ATS Config Artifacts");
   }

   private static boolean atsTypeDataLoaded = false;

   public static void run(boolean forcePend) {
      if (atsTypeDataLoaded) return;
      atsTypeDataLoaded = true;
      BulkLoadAtsCache job = new BulkLoadAtsCache();
      job.setPriority(Job.SHORT);
      job.setSystem(true);
      job.schedule();
      try {
         if (forcePend) job.join();
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   protected IStatus run(IProgressMonitor monitor) {
      OseeLog.log(AtsPlugin.class, Level.INFO,  getName());
      try {
         SkynetTransaction transaction = new SkynetTransaction(AtsPlugin.getAtsBranch());
         for (Artifact artifact : RelationManager.getRelatedArtifacts(
               Arrays.asList(AtsConfig.getInstance().getOrCreateAtsHeadingArtifact(transaction)), 8,
               CoreRelationEnumeration.DEFAULT_HIERARCHICAL__CHILD, AtsRelation.TeamDefinitionToVersion_Version)) {
            AtsCache.cache(artifact);
         }
         transaction.execute();
         WorkItemDefinitionFactory.loadDefinitions();
      } catch (Exception ex) {
         return new Status(Status.ERROR, AtsPlugin.PLUGIN_ID, -1, ex.getMessage(), ex);
      } finally {
         monitor.done();
      }

      return Status.OK_STATUS;
   }
}
