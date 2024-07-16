/*********************************************************************
 * Copyright (c) 2015 Boeing
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

package org.eclipse.osee.framework.ui.skynet.explorer.menu;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.framework.core.OrcsTokenService;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.access.AccessControlArtifactUtil;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.StringLabelProvider;
import org.eclipse.osee.framework.ui.skynet.internal.ServiceUtil;
import org.eclipse.osee.framework.ui.skynet.explorer.ArtifactExplorer;
import org.eclipse.osee.framework.ui.skynet.explorer.ArtifactExplorerLinkNode;
import org.eclipse.osee.framework.ui.skynet.explorer.MenuPermissions;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.internal.ServiceUtil;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredTreeArtifactTypeEntryDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredTreeRelationTypeDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.XResultDataDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

/**
 * @author Donald G. Dunne
 */
public class CreateRelatedMenuItem implements SelectionListener {

   private final ArtifactExplorer artifactExplorer;
   private final MenuItem menuItem;
   private final OrcsTokenService tokenService;

   public CreateRelatedMenuItem(Menu parent, ArtifactExplorer artifactExplorer) {
      menuItem = new MenuItem(parent, SWT.PUSH);
      menuItem.setText("&New Related");
      this.artifactExplorer = artifactExplorer;
      tokenService = ServiceUtil.getTokenService();
      menuItem.addSelectionListener(this);
   }

   @Override
   public void widgetSelected(SelectionEvent e) {
      try {

         IStructuredSelection selection = (IStructuredSelection) artifactExplorer.getTreeViewer().getSelection();
         Object obj = selection.getFirstElement();
         if (obj instanceof ArtifactExplorerLinkNode) {
            ArtifactExplorerLinkNode linkNode = (ArtifactExplorerLinkNode) obj;
            Artifact existingArtifact = linkNode.getArtifact();
            RelationTypeToken relationType = linkNode.getRelationType();
            RelationTypeSide relationTypeSide = null;
            RelationSide relationSide = null;
            if (linkNode.isParentIsOnSideA()) {
               relationSide = RelationSide.SIDE_B;
               relationTypeSide = RelationTypeSide.create(relationType, RelationSide.SIDE_B);
            } else {
               relationSide = RelationSide.SIDE_A;
               relationTypeSide = RelationTypeSide.create(relationType, RelationSide.SIDE_A);
            }

            XResultData rd = ServiceUtil.accessControlService().hasRelationTypePermission(existingArtifact,
               relationTypeSide, null, PermissionEnum.WRITE, AccessControlArtifactUtil.getXResultAccessHeader("New Related",
                  Collections.singleton(existingArtifact), relationTypeSide));

            if (rd.isSuccess()) {
               handleCreateRelated(existingArtifact, relationType, relationTypeSide, relationSide);
            } else {
               XResultDataDialog.open(rd, "New Child Error",
                  "You do not have permissions to add a New Related artifact");
            }
         } else if (obj instanceof Artifact) {
            Artifact parentArt = (Artifact) obj;

            boolean hasPermission = ServiceUtil.accessControlService().hasRelationTypePermission(parentArt,
               CoreRelationTypes.DefaultHierarchical_Child, null, PermissionEnum.WRITE, null).isSuccess();

            if (!hasPermission) {
               MessageDialog.openError(AWorkbench.getActiveShell(), "New Child Error",
                  "Access control has restricted this action. The current user does not have sufficient permission to create relations on this artifact.");
               return;
            }

            List<RelationTypeSide> validRelationTypes = new LinkedList<>();
            for (RelationTypeToken relType : tokenService.getRelationTypes()) {
               if (relType.isArtifactTypeAllowed(RelationSide.SIDE_A, parentArt.getArtifactType())) {
                  validRelationTypes.add(new RelationTypeSide(relType, RelationSide.SIDE_B));
               }
               if (relType.isArtifactTypeAllowed(RelationSide.SIDE_B, parentArt.getArtifactType())) {
                  validRelationTypes.add(new RelationTypeSide(relType, RelationSide.SIDE_A));
               }
            }

            FilteredTreeRelationTypeDialog dialog = new FilteredTreeRelationTypeDialog("Select Relation Type",
               String.format(
                  "Select Relation Type where:\n<Selected> = %s\nand\n<New Artifact> is newly created artifact from this operation.",
                  parentArt.toStringWithId()),
               validRelationTypes, new RelationTypeSideLabelProvider());
            int result = dialog.open();
            if (result == 0) {
               RelationTypeSide relationType = dialog.getSelectedFirst();
               if (relationType != null) {
                  List<ArtifactTypeToken> validArtifactTypes = new LinkedList<>();
                  for (ArtifactTypeToken artifactType : getArtifactTypesFromRelationType(relationType)) {
                     if (!artifactType.isAbstract() && ArtifactTypeManager.isUserCreationAllowed(artifactType)) {
                        validArtifactTypes.add(artifactType);
                     }
                  }
                  ArtifactExplorerMenu.handleCreateChild(parentArt, validArtifactTypes,
                     artifactExplorer.getTreeViewer(), relationType);
               }
            }

         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }

   }

   private void handleCreateRelated(Artifact existingArtifact, RelationTypeToken relationType, RelationTypeSide relationTypeSide, RelationSide relationSide) {
      // get valid artifact types for this relation
      List<ArtifactTypeToken> artifactTypes = new ArrayList<>();
      BranchToken branch = BranchManager.getBranchToken(artifactExplorer.getBranch());
      for (ArtifactTypeToken artifactType : tokenService.getConcreteArtifactTypes()) {
         if (relationType.isArtifactTypeAllowed(relationSide,
            artifactType) && ArtifactTypeManager.isUserCreationAllowed(artifactType)) {
            artifactTypes.add(artifactType);
         }
      }

      // determine which dialog to display
      ArtifactTypeToken type = null;
      String name = null;
      if (artifactTypes.size() > 1) {
         FilteredTreeArtifactTypeEntryDialog dialog = getDialog(artifactTypes);
         if (dialog.open() == Window.OK) {
            type = dialog.getSelection();
            name = dialog.getEntryValue();
         }
      } else if (artifactTypes.size() == 1) {
         type = artifactTypes.iterator().next();
         EntryDialog dialog =
            new EntryDialog("New Related", String.format("Enter name of new artifact of type [%s]", type.getName()));
         if (dialog.open() == Window.OK) {
            name = dialog.getEntry();
         }
      }

      // create new artifact if name is valid
      if (Strings.isValid(name)) {
         createRelatedArtifact(existingArtifact, relationTypeSide, branch, type, name);
      }
   }

   private void createRelatedArtifact(Artifact existingArtifact, RelationTypeSide relationTypeSide, BranchToken branch, ArtifactTypeToken type, String name) {
      SkynetTransaction transaction = TransactionManager.createTransaction(branch,
         String.format("Created new %s \"%s\" in artifact explorer", type.getName(), name));
      Artifact newArtifact = ArtifactTypeManager.addArtifact(type, branch, name);
      existingArtifact.addRelation(relationTypeSide, newArtifact);
      existingArtifact.persist(transaction);
      newArtifact.persist(transaction);
      transaction.execute();
      RendererManager.open(newArtifact, PresentationType.GENERALIZED_EDIT);
      artifactExplorer.getTreeViewer().refresh();
      artifactExplorer.getTreeViewer().refresh(false);
   }

   private FilteredTreeArtifactTypeEntryDialog getDialog(List<ArtifactTypeToken> artifactTypes) {
      FilteredTreeArtifactTypeEntryDialog dialog = new FilteredTreeArtifactTypeEntryDialog("New Related",
         "Enter name and select Artifact type to create", "Artifact Name", artifactTypes);
      return dialog;
   }

   public void setCreateRelatedEnabled(Object obj) {
      if (obj instanceof ArtifactExplorerLinkNode) {
         ArtifactExplorerLinkNode linkNode = (ArtifactExplorerLinkNode) obj;
         Artifact artifact = linkNode.getArtifact();
         RelationTypeToken relationType = linkNode.getRelationType();
         RelationTypeSide relationSide = null;
         if (linkNode.isParentIsOnSideA()) {
            relationSide = RelationTypeSide.create(relationType, RelationSide.SIDE_B);
         } else {
            relationSide = RelationTypeSide.create(relationType, RelationSide.SIDE_A);
         }

         MenuPermissions permiss = new MenuPermissions(artifact);

         boolean hasPermission = ServiceUtil.accessControlService().hasRelationTypePermission(artifact,
            relationSide, Collections.emptyList(), PermissionEnum.WRITE, null).isSuccess();

         menuItem.setEnabled(permiss.isWritePermission() && hasPermission);
      } else if (obj instanceof Artifact) {
         Artifact artifact = (Artifact) obj;
         MenuPermissions permiss = new MenuPermissions(artifact);
         menuItem.setEnabled(permiss.isWritePermission());
      } else {
         menuItem.setEnabled(false);
      }
   }

   @Override
   public void widgetDefaultSelected(SelectionEvent e) {
      // do nothing
   }

   public MenuItem getMenuItem() {
      return menuItem;
   }

   public class RelationTypeSideLabelProvider extends StringLabelProvider {

      @Override
      public String getText(Object arg0) {
         if (arg0 instanceof RelationTypeSide) {
            RelationTypeSide relationTypeSide = (RelationTypeSide) arg0;
            RelationTypeToken relationType = relationTypeSide.getRelationType();
            if (relationTypeSide.getSide().isSideA()) {
               return String.format(
                  "Relation: [%s]  <New Artifact> of type [%s] and name [%s] <---> <Selected> of type [%s] and name [%s]",
                  relationType.getName(), relationType.getArtifactType(RelationSide.SIDE_B),
                  relationType.getSideName(RelationSide.SIDE_B), relationType.getArtifactType(RelationSide.SIDE_A),
                  relationType.getSideName(RelationSide.SIDE_A));
            } else {
               return String.format(
                  "Relation: [%s]  <Selected> of type [%s] and name [%s] <---> <New Artifact> of type [%s] and name [%s]",
                  relationType.getName(), relationType.getArtifactType(RelationSide.SIDE_A),
                  relationType.getSideName(RelationSide.SIDE_A), relationType.getArtifactType(RelationSide.SIDE_B),
                  relationType.getSideName(RelationSide.SIDE_B));
            }
         }
         return arg0.toString();
      }
   }

   private Collection<ArtifactTypeToken> getArtifactTypesFromRelationType(RelationTypeToken relationType) {
      ArtifactTypeToken artifactTypeSideB = relationType.getArtifactType(RelationSide.SIDE_B);
      List<ArtifactTypeToken> artifactTypes = artifactTypeSideB.getAllDescendantTypes();
      artifactTypes.add(artifactTypeSideB);
      return artifactTypes;
   }
}