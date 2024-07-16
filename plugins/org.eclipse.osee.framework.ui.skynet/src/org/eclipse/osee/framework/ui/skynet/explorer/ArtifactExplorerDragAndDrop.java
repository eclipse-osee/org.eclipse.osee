/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.ui.skynet.explorer;

import static org.eclipse.osee.framework.core.enums.RelationSorter.USER_DEFINED;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.io.FilenameUtils;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.osee.framework.core.JaxRsApi;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.JsonArtifact;
import org.eclipse.osee.framework.core.data.JsonAttribute;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.OseeSystemArtifacts;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactData;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.importing.parsers.IArtifactExtractor;
import org.eclipse.osee.framework.skynet.core.importing.parsers.NativeDocumentExtractor;
import org.eclipse.osee.framework.skynet.core.importing.parsers.WholeWordDocumentExtractor;
import org.eclipse.osee.framework.skynet.core.importing.resolvers.IArtifactImportResolver;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.plugin.util.Wizards;
import org.eclipse.osee.framework.ui.skynet.ArtifactStructuredSelection;
import org.eclipse.osee.framework.ui.skynet.Import.ArtifactImportOperationFactory;
import org.eclipse.osee.framework.ui.skynet.Import.ArtifactImportOperationParameter;
import org.eclipse.osee.framework.ui.skynet.Import.ArtifactImportWizard;
import org.eclipse.osee.framework.ui.skynet.Import.ArtifactResolverFactory;
import org.eclipse.osee.framework.ui.skynet.Import.ArtifactResolverFactory.ArtifactCreationStrategy;
import org.eclipse.osee.framework.ui.skynet.artifact.ArtifactTransfer;
import org.eclipse.osee.framework.ui.skynet.internal.ServiceUtil;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.skynet.update.InterArtifactExplorerDropHandlerOperation;
import org.eclipse.osee.framework.ui.skynet.util.SkynetDragAndDrop;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.ui.IViewPart;

/**
 * @author Jeff C. Phillips
 */
public class ArtifactExplorerDragAndDrop extends SkynetDragAndDrop {
   private final TreeViewer treeViewer;
   private final IViewPart viewPart;
   private final JaxRsApi jaxRsApi;
   private BranchId selectedBranch;

   public ArtifactExplorerDragAndDrop(TreeViewer treeViewer, String viewId, IViewPart viewPart, BranchId selectedBranch) {
      super(treeViewer.getTree(), treeViewer.getTree(), viewId);
      this.treeViewer = treeViewer;
      this.viewPart = viewPart;
      this.selectedBranch = selectedBranch;
      jaxRsApi = ServiceUtil.getOseeClient().jaxRsApi();
   }

   public void updateBranch(BranchId branch) {
      selectedBranch = branch;
   }

   @Override
   public Artifact[] getArtifacts() {
      IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
      Object[] objects = selection.toArray();
      Artifact[] artifacts = new Artifact[objects.length];

      for (int index = 0; index < objects.length; index++) {
         artifacts[index] = (Artifact) objects[index];
      }

      return artifacts;
   }

   @Override
   public void performDragOver(DropTargetEvent event) {
      event.feedback = DND.FEEDBACK_SELECT | DND.FEEDBACK_SCROLL | DND.FEEDBACK_EXPAND;

      if (FileTransfer.getInstance().isSupportedType(event.currentDataType)) {
         event.detail = DND.DROP_COPY;
      } else if (isValidForArtifactDrop(event)) {
         event.detail = DND.DROP_MOVE;
      } else if (TextTransfer.getInstance().isSupportedType(event.currentDataType)) {
         event.detail = DND.DROP_COPY;
      } else {
         event.detail = DND.DROP_NONE;
      }
   }

   /**
    * Do the minimum check to see if valid. After drop, perform the full permissions check.
    */
   private boolean isValidForArtifactDrop(DropTargetEvent event) {
      boolean valid = false;
      if (ArtifactTransfer.getInstance().isSupportedType(event.currentDataType)) {
         valid = true;
      }
      return valid;
   }

   private XResultData validateDrop(DropTargetEvent event) {
      XResultData rd = new XResultData();
      Artifact dropTarget = getSelectedArtifact(event);
      if (dropTarget == null) {
         rd.errorf("dropTarget == NULL\\n");
         return rd;
      }
      rd.logf("dropTarget %s\n", dropTarget.toStringWithId());
      ArtifactData toBeDropped = ArtifactTransfer.getInstance().nativeToJava(event.currentDataType);
      if (toBeDropped == null) {
         rd.errorf("toBeDropped == NULL\\n");
         return rd;
      }
      rd.logf("toBeDropped %s\n", toBeDropped.getArtifacts()[0].toStringWithId());
      try {
         Artifact[] artifactsBeingDropped = toBeDropped.getArtifacts();
         List<Artifact> artsOnSameBranchAsDestination = new LinkedList<>();
         BranchId destinationBranch = dropTarget.getBranch();
         for (Artifact art : artifactsBeingDropped) {
            if (art.isOnBranch(destinationBranch)) {
               artsOnSameBranchAsDestination.add(art);
            }
         }

         boolean valid = ServiceUtil.accessControlService().hasRelationTypePermission(dropTarget,
            CoreRelationTypes.DefaultHierarchical_Child, artsOnSameBranchAsDestination, PermissionEnum.WRITE,
            null).isSuccess();
         if (!valid) {
            rd.errorf("Artifacts are not on the same branch");
            return rd;
         }

         // if we are deparenting ourself, make sure our parent's child side can be modified
         for (Artifact art : artsOnSameBranchAsDestination) {
            if (art.hasParent()) {
               valid = ServiceUtil.accessControlService().hasRelationTypePermission(art.getParent(),
                  CoreRelationTypes.DefaultHierarchical_Child, Collections.emptyList(), PermissionEnum.WRITE,
                  null).isSuccess();
            }
            if (!valid) {
               rd.errorf("User does not have access to perform this operation\n");
            }
         }
      } catch (OseeCoreException ex) {
         rd.errorf("Exception performing drop %s\n", Lib.exceptionToString(ex));
      }
      return rd;
   }

   private Artifact getSelectedArtifact(DropTargetEvent event) {
      if (event.item != null && event.item.getData() instanceof Artifact) {
         return (Artifact) event.item.getData();
      }
      return null;
   }

   @Override
   public void performDrop(final DropTargetEvent event) {

      XResultData rd = validateDrop(event);
      if (rd.isErrors()) {
         XResultDataUI.report(rd, "Perform Drop");
         return;
      }
      Artifact parentArtifact = getSelectedArtifact(event);

      if (parentArtifact == null && selectedBranch != null) {
         //try Default Root Hierarchy
         try {
            parentArtifact = OseeSystemArtifacts.getDefaultHierarchyRootArtifact(selectedBranch);
         } catch (Exception ex) {
            OseeLog.log(getClass(), OseeLevel.SEVERE_POPUP, ex);
         }
      }

      if (parentArtifact != null) {
         if (ArtifactTransfer.getInstance().isSupportedType(event.currentDataType)) {
            performArtifactTransfer(event, parentArtifact);
         } else if (FileTransfer.getInstance().isSupportedType(event.currentDataType)) {
            performFileTransfer(event, parentArtifact);
         } else if (TextTransfer.getInstance().isSupportedType(event.currentDataType)) {
            performTextTransfer(event, parentArtifact);
         }
      }
   }

   private void performArtifactTransfer(final DropTargetEvent event, Artifact parentArtifact) {
      ArtifactData artData = ArtifactTransfer.getInstance().nativeToJava(event.currentDataType);
      final Artifact[] artifactsToBeRelated = artData.getArtifacts();
      if (artifactsToBeRelated != null && artifactsToBeRelated.length > 0 && !artifactsToBeRelated[0].isOnSameBranch(
         parentArtifact)) {
         InterArtifactExplorerDropHandlerOperation interDropHandler =
            new InterArtifactExplorerDropHandlerOperation(parentArtifact, artifactsToBeRelated, true);
         Operations.executeAsJob(interDropHandler, true);
      } else if (artifactsToBeRelated != null && isValidForArtifactDrop(event) && MessageDialog.openQuestion(
         viewPart.getViewSite().getShell(), "Confirm Move",
         "Are you sure you want to make each of the selected artifacts a child of\n\n" + parentArtifact.getName() + "?")) {
         try {
            SkynetTransaction transaction =
               TransactionManager.createTransaction(parentArtifact.getBranch(), "Artifact explorer drag & drop");
            // Replace all of the parent relations
            for (Artifact artifact : artifactsToBeRelated) {
               Artifact currentParent = artifact.getParent();
               if (currentParent != null) {
                  currentParent.deleteRelation(CoreRelationTypes.DefaultHierarchical_Child, artifact);
                  currentParent.persist(transaction);
               }
               parentArtifact.addChild(USER_DEFINED, artifact);
               parentArtifact.persist(transaction);
            }
            transaction.execute();
         } catch (OseeCoreException ex) {
            OseeLog.log(getClass(), OseeLevel.SEVERE_POPUP, ex);
         }
      }
   }

   private void performFileTransfer(final DropTargetEvent event, Artifact parentArtifact) {
      Object object = FileTransfer.getInstance().nativeToJava(event.currentDataType);

      if (object instanceof String[]) {
         String[] items = (String[]) object;
         File importFile = new File(items[0]);

         ArtifactImportWizard wizard = new ArtifactImportWizard();
         wizard.setImportFile(importFile);
         wizard.setDestinationArtifact(parentArtifact);

         String fileName = importFile.getName();
         boolean importWizardUserSelection = true;
         if (items.length > 1) {
            importWizardUserSelection = importWithMoreThanOneFile(fileName);
         }

         if (importWizardUserSelection == true) {
            if (isSameName(parentArtifact, fileName)) {
               importSimilarArtifact(parentArtifact, importFile, wizard, fileName);
            } else {
               importOverChild(parentArtifact, importFile, wizard, fileName);
            }
         }
      }
   }

   private void importOverChild(Artifact parentArt, File importFile, ArtifactImportWizard wizard, String fileName) {
      boolean imported = false;
      for (Artifact child : parentArt.getChildren()) {
         if (child.getName().equals(FilenameUtils.getBaseName(fileName))) {
            importSimilarArtifact(child, importFile, wizard, fileName);
            imported = true;
            break;
         }
      }
      if (!imported) {
         Wizards.initAndOpen(wizard, viewPart, new ArtifactStructuredSelection(parentArt));
      }
   }

   private void importSimilarArtifact(Artifact parentArtifact, File importFile, ArtifactImportWizard wizard,
      String fileName) {
      {
         String promptMsg = String.format(
            "Artifact [%s] has same base file name as [%s]. \n\nDo you want to update the exisiting file? \nIf 'NO' selected, you'll be taken to the Artifact Import Wizard",
            parentArtifact.getName(), FilenameUtils.getName(fileName));

         if (MessageDialog.openQuestion(viewPart.getViewSite().getShell(), "Confirm Import", promptMsg)) {

            IArtifactImportResolver resolver = ArtifactResolverFactory.createResolver(
               ArtifactCreationStrategy.CREATE_ON_DIFFERENT_ATTRIBUTES, parentArtifact.getArtifactType(),
               Arrays.asList(CoreAttributeTypes.Name), true, false, parentArtifact);
            try {
               ArtifactImportOperationParameter parameter = new ArtifactImportOperationParameter();
               parameter.setSourceFile(importFile);
               parameter.setDestinationArtifact(parentArtifact.getParent());
               parameter.setExtractor(getArtifactExtractor(parentArtifact.getArtifactType()));
               parameter.setResolver(resolver);
               parameter.setStopOnError(true);

               IOperation operation = ArtifactImportOperationFactory.completeOperation(parameter);
               Operations.executeWorkAndCheckStatus(operation);
            } catch (OseeCoreException ex) {
               OseeLog.log(getClass(), OseeLevel.SEVERE_POPUP, ex);
            }

         } else {
            Wizards.initAndOpen(wizard, viewPart, new ArtifactStructuredSelection(parentArtifact));
         }
      }
   }

   private boolean importWithMoreThanOneFile(String fileName) {
      {
         String promptMsg = String.format(
            "It appears you are trying to import more than 1 file, only the first file, [%s] will be choosen.\n\nIs that okay? \nIf no selected, the process will cease.",
            FilenameUtils.getName(fileName));

         if (MessageDialog.openQuestion(viewPart.getViewSite().getShell(), "Confirm First File Import", promptMsg)) {
            return true;

         } else {
            return false;
         }
      }
   }

   private void performTextTransfer(final DropTargetEvent event, Artifact parentArtifact) {
      Object object = TextTransfer.getInstance().nativeToJava(event.currentDataType);
      try {
         if (object instanceof String) {
            String fromJson = (String) object;
            if (fromJson.trim().charAt(0) == '[') {
               transferJsonArtifacts(parentArtifact, fromJson);
            } else {
               throw new OseeArgumentException("Dropped text formatted incorrectly", fromJson);
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(getClass(), OseeLevel.SEVERE_POPUP, ex);
      }
   }

   private void transferJsonArtifacts(Artifact parentArtifact, String fromJson) {
      SkynetTransaction transaction =
         TransactionManager.createTransaction(parentArtifact.getBranch(), "Artifact explorer drag & drop");
      try {
         List<JsonArtifact> reqts = jaxRsApi.readCollectionValue(fromJson, List.class, JsonArtifact.class);
         for (JsonArtifact item : reqts) {
            Artifact art = ArtifactTypeManager.addArtifact(item.getType(), parentArtifact.getBranch(), item.getName());
            List<JsonAttribute> attrs = item.getAttrs();
            for (JsonAttribute attr : attrs) {
               art.addAttributeFromString(attr.getTypeId(), attr.getValue());
            }
            art.persist(transaction);
            parentArtifact.addChild(art);
            parentArtifact.persist(transaction);
         }
         transaction.execute();
      } catch (Exception ex) {
         OseeLog.log(getClass(), OseeLevel.SEVERE_POPUP, ex);
      }
   }

   private boolean isSameName(Artifact art, String fileName) {
      if (art.isTypeEqual(CoreArtifactTypes.Folder)) {
         return false;
      } else if (fileName.contains("_" + art.getIdString() + "_")) {
         return true;
      } else if (FilenameUtils.getBaseName(fileName).startsWith(art.getSafeName())) {
         return true;
      } else {
         return false;
      }
   }

   public static IArtifactExtractor getArtifactExtractor(ArtifactTypeToken artifactType) {
      IArtifactExtractor extractor = null;
      if (artifactType.inheritsFrom(CoreArtifactTypes.GeneralDocument)) {
         extractor = new NativeDocumentExtractor();
      } else {
         extractor = new WholeWordDocumentExtractor();
      }
      return extractor;
   }
}
