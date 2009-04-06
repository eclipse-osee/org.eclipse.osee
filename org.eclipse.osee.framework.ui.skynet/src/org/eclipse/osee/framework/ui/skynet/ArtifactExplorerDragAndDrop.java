/**
 * 
 */
package org.eclipse.osee.framework.ui.skynet;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactData;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTransfer;
import org.eclipse.osee.framework.skynet.core.relation.CoreRelationEnumeration;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.plugin.util.Wizards;
import org.eclipse.osee.framework.ui.skynet.Import.ArtifactImportWizard;
import org.eclipse.osee.framework.ui.skynet.update.ArtifactTransferObject;
import org.eclipse.osee.framework.ui.skynet.update.InterArtifactExplorerHandler;
import org.eclipse.osee.framework.ui.skynet.update.TransferMessage;
import org.eclipse.osee.framework.ui.skynet.update.TransferMessage.Type;
import org.eclipse.osee.framework.ui.skynet.util.SkynetDragAndDrop;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IViewPart;

/**
 * @author Jeff C. Phillips
 *
 */
public class ArtifactExplorerDragAndDrop extends SkynetDragAndDrop {
   private TreeViewer treeViewer;
   private String viewId;
   private IViewPart viewPart;
   private InterArtifactExplorerHandler interArtifactExplorerHandler;
   
   public ArtifactExplorerDragAndDrop(TreeViewer treeViewer, String viewId, IViewPart viewPart) {
      super(treeViewer.getTree(), treeViewer.getTree(), viewId);
      
      this.treeViewer = treeViewer;
      this.viewId = viewId;
      this.viewPart = viewPart;
      this.interArtifactExplorerHandler = new InterArtifactExplorerHandler();
   }

   @Override
   public Artifact[] getArtifacts() {
      IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
      Object[] objects = selection.toArray();
      Artifact[] artifacts = new Artifact[objects.length];

      for (int index = 0; index < objects.length; index++)
         artifacts[index] = (Artifact) objects[index];

      return artifacts;
   }

   @Override
   public void performDragOver(DropTargetEvent event) {
      event.feedback = DND.FEEDBACK_SELECT | DND.FEEDBACK_SCROLL | DND.FEEDBACK_EXPAND;

      if (FileTransfer.getInstance().isSupportedType(event.currentDataType)) {
         event.detail = DND.DROP_COPY;
      } else if (isValidForArtifactDrop(event)) {
         event.detail = DND.DROP_MOVE;
      } else {
         event.detail = DND.DROP_NONE;
      }
   }

   private boolean isValidForArtifactDrop(DropTargetEvent event) {
      if (ArtifactTransfer.getInstance().isSupportedType(event.currentDataType)) {
         ArtifactData artData = ArtifactTransfer.getInstance().nativeToJava(event.currentDataType);

         if (artData != null) {

            Artifact parentArtifact = getSelectedArtifact(event);
            if (parentArtifact != null && artData.getSource().equals(viewId)) {
               Artifact[] artifactsToBeRelated = artData.getArtifacts();

               for (Artifact artifact : artifactsToBeRelated) {
                  if (parentArtifact.equals(artifact)) {
                     return false;
                  }
               }
               return true;
            }
         } else {
            // only occurs during the drag on some platforms
            return true;
         }
      }
      return false;
   }

   private Artifact getSelectedArtifact(DropTargetEvent event) {
      TreeItem selected = treeViewer.getTree().getItem(treeViewer.getTree().toControl(event.x, event.y));

      if (selected != null && selected.getData() instanceof Artifact) {
         return (Artifact) selected.getData();
      }
      return null;
   }

   @Override
   public void performDrop(final DropTargetEvent event) {
      final Artifact parentArtifact = getSelectedArtifact(event);

      if (parentArtifact != null) {
         ArtifactData artData = ArtifactTransfer.getInstance().nativeToJava(event.currentDataType);
         final Artifact[] artifactsToBeRelated = artData.getArtifacts();
         if (artifactsToBeRelated.length > 0 && !artifactsToBeRelated[0].getBranch().equals(
               parentArtifact.getBranch())) {
            try {
               interArtifactExplorerHandler.dropArtifactIntoDifferentBranch(parentArtifact, artifactsToBeRelated);
            } catch (OseeCoreException ex) {
               OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE, ex);
            }
         } else {
            if (ArtifactTransfer.getInstance().isSupportedType(event.currentDataType) && isValidForArtifactDrop(event) && MessageDialog.openQuestion(
                  viewPart.getViewSite().getShell(),
                  "Confirm Move",
                  "Are you sure you want to make each of the selected artifacts a child of " + parentArtifact.getDescriptiveName() + "?")) {
               try {
                  SkynetTransaction transaction = new SkynetTransaction(parentArtifact.getBranch());
                  // Replace all of the parent relations
                  for (Artifact artifact : artifactsToBeRelated) {
                     artifact.setSoleRelation(CoreRelationEnumeration.DEFAULT_HIERARCHICAL__PARENT, parentArtifact);
                     artifact.persistAttributesAndRelations(transaction);
                  }
                  transaction.execute();
               } catch (Exception ex) {
                  OseeLog.log(getClass(), OseeLevel.SEVERE_POPUP, ex);
               }
            }

            else if (FileTransfer.getInstance().isSupportedType(event.currentDataType)) {
               Object object = FileTransfer.getInstance().nativeToJava(event.currentDataType);
               if (object instanceof String[]) {
                  String filename = ((String[]) object)[0];

                  ArtifactImportWizard wizard = new ArtifactImportWizard();
                  wizard.setImportResourceAndArtifactDestination(new File(filename), parentArtifact);

                  Wizards.initAndOpen(wizard, viewPart);
               }
            }
         }
      }
   }

   /**
    * @param parentArtifact
    * @param sourceArtifacts
    * @throws OseeCoreException
    */
   private void dropArtifactIntoDifferentBranch(Artifact parentArtifact, Artifact[] sourceArtifacts) throws OseeCoreException {
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
      
      for (Artifact source : sourceArtifacts) {
         if (artifactIds.contains(source.getArtId())) {
            updateArtifacts.add(new ArtifactTransferObject(source, new TransferMessage(Type.INFO, "")));
         } else if (artifactOnBranch()) {
            updateArtifacts.add(new ArtifactTransferObject(source, new TransferMessage(Type.INFO, "")));
         } else if (artifactOnParentBranch()) {
            newBaselineArtifacts.add(new ArtifactTransferObject(source, new TransferMessage(Type.INFO, "")));
         } else {
            newNonBaselineArtifacts.add(new ArtifactTransferObject(source, new TransferMessage(Type.INFO, "")));
         }
      }
      
      
      
      //               ArtifactBaselineUpdate.updateArtifacts(parentArtifact.getBranch(), Arrays.asList(newArtifact), newArtifact.getBranch());
   }

   /**
    * @return
    */
   private boolean artifactOnParentBranch() {
      return false;
   }

   /**
    * @return
    */
   private boolean artifactOnBranch() {
      return false;
   }
}
