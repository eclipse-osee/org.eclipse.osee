/*
 * Created on Jan 29, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.commandHandlers;

import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.framework.db.connection.DbTransaction;
import org.eclipse.osee.framework.db.connection.OseeConnection;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;

public class RevertJob extends Job {
   private List<Artifact> artifacts;

   public RevertJob(List<Artifact> artifacts) {
      super("Reverting " + artifacts.size() + " artifacts.");
      this.artifacts = artifacts;
   }

   @Override
   protected IStatus run(final IProgressMonitor monitor) {
      IStatus toReturn;
      try {
         monitor.beginTask("Reverting ...", artifacts.size());

         DbTransaction dbTransaction = new DbTransaction() {
            @Override
            protected void handleTxWork(OseeConnection connection) throws OseeCoreException {
               for (Artifact artifact : artifacts) {
                  monitor.setTaskName(artifact.getInternalDescriptiveName());
                  ArtifactPersistenceManager.revertArtifact(connection, artifact);
                  monitor.worked(1);
               }
            }
         };
         dbTransaction.execute();

         toReturn = Status.OK_STATUS;
      } catch (Exception ex) {
         toReturn = new Status(Status.ERROR, SkynetGuiPlugin.PLUGIN_ID, -1, ex.getMessage(), ex);
      } finally {
         monitor.done();
      }
      return toReturn;
   }
}
