/**
 * 
 */
package org.eclipse.osee.framework.ui.skynet.update;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactBaselineUpdate;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.ui.skynet.update.TransferMessage.Type;
import org.eclipse.ui.PlatformUI;

/**
 * @author Jeff C. Phillips
 */
public class InterArtifactExplorerHandler {

   public void dropArtifactIntoDifferentBranch(Artifact parentArtifact, Artifact[] sourceArtifacts) throws OseeCoreException {
      if(parentArtifact == null || sourceArtifacts == null || sourceArtifacts.length < 1){
         throw new OseeCoreException("");
      }
      
      //TODO need to lock this down if user doesn't have access to the parent Artifacts branch..
      List<Artifact> descendents = parentArtifact.getDescendants();
      List<Integer> artifactIds = new ArrayList<Integer>();
      artifactIds.add(parentArtifact.getArtId());
      
      for (Artifact artifact : descendents) {
         artifactIds.add(artifact.getArtId());
      }
      
      List<ArtifactTransferObject> updateArtifacts = new LinkedList<ArtifactTransferObject>();
      List<ArtifactTransferObject> newBaselineArtifacts = new LinkedList<ArtifactTransferObject>();
      List<ArtifactTransferObject> newNonBaselineArtifacts = new LinkedList<ArtifactTransferObject>();
      Branch sourceBranch = null;
      
      for (Artifact sourceArtifact : sourceArtifacts) {
         if(sourceBranch == null){
            sourceBranch = sourceArtifact.getBranch();
         }
         
         if (artifactIds.contains(sourceArtifact.getArtId())) {
            updateArtifacts.add(new ArtifactTransferObject(sourceArtifact, new TransferMessage(Type.INFO, "")));
         } else if (artifactOnBranch()) {
            updateArtifacts.add(new ArtifactTransferObject(sourceArtifact, new TransferMessage(Type.INFO, "")));
         } else if (artifactOnParentBranch(sourceBranch, sourceArtifact)) {
            newBaselineArtifacts.add(new ArtifactTransferObject(sourceArtifact, new TransferMessage(Type.INFO, "")));
         } else {
            newNonBaselineArtifacts.add(new ArtifactTransferObject(sourceArtifact, new TransferMessage(Type.INFO, "")));
         }
      }
      
      String message = "";
      
      if(!updateArtifacts.isEmpty()){
         message += "Are you sure you want to update " + updateArtifacts.size() + " artifacts from thier parent branch? \n";
      }
      if(!newBaselineArtifacts.isEmpty()){
         message += "Are you sure you want to add " + newBaselineArtifacts.size() + " artifacts from thier parent branch? \n";
      }
      if(!newNonBaselineArtifacts.isEmpty()){
         message += "Are you sure you want to add " + newNonBaselineArtifacts.size() + " new artifacts from this branch? \n";
      }
      
      if (MessageDialog.openQuestion(
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
            "Confirm Action", message)) {
         
         updateArtifacts(updateArtifacts, parentArtifact.getBranch(), sourceBranch);
         addNewArtifactToBaseline(newBaselineArtifacts, parentArtifact.getBranch(), sourceBranch);
         addNewArtifact(newNonBaselineArtifacts, parentArtifact.getBranch(), sourceBranch);
      }
   }

   /**
    * @param updateArtifacts
    * @param branch
    * @param sourceBranch
    */
   private void addNewArtifact(List<ArtifactTransferObject> updateArtifacts, Branch branch, Branch sourceBranch) {
      // TODO Auto-generated method stub

   }

   /**
    * @param updateArtifacts
    * @param branch
    * @param sourceBranch
    */
   private void addNewArtifactToBaseline(List<ArtifactTransferObject> updateArtifacts, Branch branch, Branch sourceBranch) {
      // TODO Auto-generated method stub

   }

   private void updateArtifacts(List<ArtifactTransferObject> updateArtifacts, Branch destinationBranch, Branch sourceBranch) throws OseeCoreException {
      if (updateArtifacts.isEmpty()) {
         return;
      }

      List<Artifact> artifacts = new LinkedList<Artifact>();
      for (ArtifactTransferObject artifactTransferObject : updateArtifacts) {
         artifacts.add(artifactTransferObject.getArtifact());
      }

      ArtifactBaselineUpdate.updateArtifacts(destinationBranch, artifacts, sourceBranch);
   }

   /**
    * @return
    * @throws OseeCoreException
    */
   private boolean artifactOnParentBranch(Branch branch, Artifact artifact) throws OseeCoreException {
      return ArtifactPersistenceManager.getDefaultHierarchyRootArtifact(branch).getChildren().contains(artifact);
   }

   /**
    * @return
    */
   private boolean artifactOnBranch() {
      return false;
   }

}
