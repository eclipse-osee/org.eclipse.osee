/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.explorer.menu;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IRelationTypeSide;
import org.eclipse.osee.framework.core.data.TokenFactory;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.access.PermissionStatus;
import org.eclipse.osee.framework.core.model.type.ArtifactType;
import org.eclipse.osee.framework.core.model.type.RelationType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.AccessPolicy;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.explorer.ArtifactExplorer;
import org.eclipse.osee.framework.ui.skynet.explorer.ArtifactExplorerLinkNode;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.internal.ServiceUtil;
import org.eclipse.osee.framework.ui.skynet.menu.GlobalMenuPermissions;
import org.eclipse.osee.framework.ui.skynet.render.PresentationType;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredTreeArtifactTypeEntryDialog;
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

   public CreateRelatedMenuItem(Menu parent, ArtifactExplorer artifactExplorer) {
      menuItem = new MenuItem(parent, SWT.PUSH);
      menuItem.setText("&New Related");
      this.artifactExplorer = artifactExplorer;
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
            RelationType relationType = linkNode.getRelationType();
            IRelationTypeSide relationTypeSide = null;
            RelationSide relationSide = null;
            if (linkNode.isParentIsOnSideA()) {
               relationSide = RelationSide.SIDE_B;
               relationTypeSide =
                  TokenFactory.createRelationTypeSide(RelationSide.SIDE_B, relationType.getId(),
                     relationType.getSideBName());
            } else {
               relationSide = RelationSide.SIDE_A;
               relationTypeSide =
                  TokenFactory.createRelationTypeSide(RelationSide.SIDE_A, relationType.getId(),
                     relationType.getSideAName());
            }

            AccessPolicy service = ServiceUtil.getAccessPolicy();

            // check permissions
            PermissionStatus status =
               service.canRelationBeModified(existingArtifact, null, relationTypeSide, Level.FINE);
            if (status.matched()) {
               handleCreateRelated(existingArtifact, relationType, relationTypeSide, relationSide);
            } else {
               MessageDialog.openError(
                  AWorkbench.getActiveShell(),
                  "New Child Error",
                  "Access control has restricted this action. The current user does not have sufficient permission to create relations on this artifact.");
            }
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }

   }

   private void handleCreateRelated(Artifact existingArtifact, RelationType relationType, IRelationTypeSide relationTypeSide, RelationSide relationSide) {
      // get valid artifact types for this relation
      List<IArtifactType> artifactTypes = new ArrayList<>();
      Branch branch = artifactExplorer.getBranch();
      for (ArtifactType artifactType : ArtifactTypeManager.getConcreteArtifactTypes(branch)) {
         if (relationType.isArtifactTypeAllowed(relationSide, artifactType) && ArtifactTypeManager.isUserCreationAllowed(artifactType)) {
            artifactTypes.add(artifactType);
         }
      }

      // determine which dialog to display
      IArtifactType type = null;
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

   private void createRelatedArtifact(Artifact existingArtifact, IRelationTypeSide relationTypeSide, Branch branch, IArtifactType type, String name) {
      SkynetTransaction transaction =
         TransactionManager.createTransaction(branch,
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

   private FilteredTreeArtifactTypeEntryDialog getDialog(List<IArtifactType> artifactTypes) throws OseeCoreException {
      FilteredTreeArtifactTypeEntryDialog dialog =
         new FilteredTreeArtifactTypeEntryDialog("New Related", "Enter name and select Artifact type to create",
            "Artifact Name", artifactTypes);
      return dialog;
   }

   public void setCreateRelatedEnabled(Object obj, AccessPolicy service) {
      if (obj instanceof ArtifactExplorerLinkNode) {
         ArtifactExplorerLinkNode linkNode = (ArtifactExplorerLinkNode) obj;
         Artifact artifact = linkNode.getArtifact();
         RelationType relationType = linkNode.getRelationType();
         IRelationTypeSide relationSide = null;
         if (linkNode.isParentIsOnSideA()) {
            relationSide =
               TokenFactory.createRelationTypeSide(RelationSide.SIDE_B, relationType.getId(),
                  relationType.getSideBName());
         } else {
            relationSide =
               TokenFactory.createRelationTypeSide(RelationSide.SIDE_A, relationType.getId(),
                  relationType.getSideAName());
         }

         GlobalMenuPermissions permiss = new GlobalMenuPermissions(artifact);

         boolean canModifyRelation = service.canRelationBeModified(artifact, null, relationSide, Level.FINE).matched();
         menuItem.setEnabled(permiss.isWritePermission() && canModifyRelation);
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

}
