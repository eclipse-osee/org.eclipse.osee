/*
 * Created on Mar 9, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.artifact;

import java.util.Collection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.xnavigate.IOperationFactory;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.artifact.massEditor.MassArtifactEditor;

/**
 * @author Donald G. Dunne
 */
public class MassEditDirtyArtifactOperation extends AbstractOperation implements IOperationFactory {

   public static String NAME = "Mass Edit Dirty Artifacts";

   public MassEditDirtyArtifactOperation() {
      super(NAME, SkynetGuiPlugin.PLUGIN_ID);
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      Collection<Artifact> dirtyArts = ArtifactCache.getDirtyArtifacts();
      if (dirtyArts.size() == 0) {
         AWorkbench.popup("No Dirty Artifacts Found");
         return;
      }
      HashCollection<Branch, Artifact> branchMap = Artifacts.getBranchArtifactMap(ArtifactCache.getDirtyArtifacts());
      for (Branch branch : branchMap.keySet()) {
         MassArtifactEditor.editArtifacts(String.format("Dirty Artifacts for Branch [%s]", branch),
               branchMap.getValues(branch));
      }
   }

   @Override
   public IOperation createOperation() {
      return this;
   }

}
