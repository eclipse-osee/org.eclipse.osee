package org.eclipse.osee.framework.ui.skynet.change.operations;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.change.ChangeUiData;

public class LoadAssociatedArtifactOperation extends AbstractOperation {
   private final ChangeUiData changeData;

   public LoadAssociatedArtifactOperation(ChangeUiData changeData) {
      super("Load Associated Artifact", SkynetGuiPlugin.PLUGIN_ID);
      this.changeData = changeData;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      Artifact associatedArtifact = null;
      if (changeData.isBranchValid()) {
         associatedArtifact = (Artifact) changeData.getBranch().getAssociatedArtifact().getFullArtifact();
      } else if (changeData.isTransactionValid()) {
         int commitId = changeData.getTransaction().getCommit();
         if (commitId != 0) {
            associatedArtifact = ArtifactQuery.getArtifactFromId(commitId, BranchManager.getCommonBranch());
         }
      }
      monitor.worked(calculateWork(0.80));
      changeData.setAssociatedArtifact(associatedArtifact);
      monitor.worked(calculateWork(0.20));
   }
}