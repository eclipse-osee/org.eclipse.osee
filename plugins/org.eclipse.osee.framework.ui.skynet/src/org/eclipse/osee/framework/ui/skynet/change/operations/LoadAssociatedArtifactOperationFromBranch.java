package org.eclipse.osee.framework.ui.skynet.change.operations;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.IBranchProvider;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;

public class LoadAssociatedArtifactOperationFromBranch extends AbstractOperation {

   private final IBranchProvider branchProvider;

   public LoadAssociatedArtifactOperationFromBranch(IBranchProvider branchProvider) {
      super("Load Associated Artifact", SkynetGuiPlugin.PLUGIN_ID);
      this.branchProvider = branchProvider;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      branchProvider.getBranch(monitor);
      monitor.worked(calculateWork(0.80));
      BranchManager.getAssociatedArtifact(branchProvider.getBranch(null));
      monitor.worked(calculateWork(0.20));
   }
}