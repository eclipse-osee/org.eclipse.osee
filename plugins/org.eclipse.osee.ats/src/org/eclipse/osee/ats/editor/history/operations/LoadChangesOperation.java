package org.eclipse.osee.ats.editor.history.operations;

import java.util.Collection;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.ats.editor.history.column.EventColumn;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.revision.ChangeManager;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;

public final class LoadChangesOperation extends AbstractOperation {

   private final Collection<Change> changes;
   private final Artifact workflowArtifact;

   public LoadChangesOperation(Artifact workflowArtifact, Collection<Change> changes) {
      super("Load History Viewer - Changes", SkynetGuiPlugin.PLUGIN_ID);
      this.workflowArtifact = workflowArtifact;
      this.changes = changes;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      try {
         changes.clear();
         for (Change change : ChangeManager.getChangesPerArtifact(workflowArtifact, null)) {
            // Only show changes with event text
            if (Strings.isValid(EventColumn.getInstance().getColumnText(change, null, 0))) {
               changes.add(change);
            }
         }

      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, "Error loading History View - See Error Log", ex);
      }
   }
}