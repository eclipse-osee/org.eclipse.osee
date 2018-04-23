/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.column;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.nebula.widgets.xviewer.IAltLeftClickProvider;
import org.eclipse.nebula.widgets.xviewer.IMultiColumnEditProvider;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.column.AtsColumnToken;
import org.eclipse.osee.ats.core.users.AtsCoreUsers;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsColumnIdColumn;
import org.eclipse.osee.ats.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.FrameworkArtifactImageProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.UserCheckTreeDialog;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Donald G. Dunne
 */
public class AssigneeColumnUI extends XViewerAtsColumnIdColumn implements IAltLeftClickProvider, IMultiColumnEditProvider {

   public static AssigneeColumnUI instance = new AssigneeColumnUI();

   public static AssigneeColumnUI getInstance() {
      return instance;
   }

   private AssigneeColumnUI() {
      super(AtsColumnToken.AssigneeColumn);
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public XViewerAtsColumnIdColumn copy() {
      XViewerAtsColumnIdColumn newXCol = new AssigneeColumnUI();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public boolean handleAltLeftClick(TreeColumn treeColumn, TreeItem treeItem) {
      try {
         if (treeItem.getData() instanceof Artifact) {
            Artifact useArt = (Artifact) treeItem.getData();
            if (useArt.isOfType(AtsArtifactTypes.Action)) {
               if (AtsClientService.get().getWorkItemService().getTeams(useArt).size() == 1) {
                  useArt = (Artifact) AtsClientService.get().getWorkItemService().getFirstTeam(useArt).getStoreObject();
               } else {
                  return false;
               }
            }
            if (!(useArt instanceof AbstractWorkflowArtifact)) {
               return false;
            }
            AbstractWorkflowArtifact awa = (AbstractWorkflowArtifact) useArt;
            boolean modified = promptChangeAssignees(Arrays.asList(awa), isPersistViewer());
            XViewer xViewer = (XViewer) ((XViewerColumn) treeColumn.getData()).getXViewer();
            if (modified && isPersistViewer(xViewer)) {
               AtsClientService.get().getStoreService().executeChangeSet("persist assignees via alt-left-click", awa);
            }
            if (modified) {
               xViewer.update(awa, null);
               return true;
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }

      return false;
   }

   public static boolean promptChangeAssignees(AbstractWorkflowArtifact sma, boolean persist) {
      return promptChangeAssignees(Arrays.asList(sma), persist);
   }

   public static boolean promptChangeAssignees(final Collection<? extends AbstractWorkflowArtifact> awas, boolean persist) {
      for (AbstractWorkflowArtifact awa : awas) {
         if (awa.isCompleted()) {
            AWorkbench.popup("ERROR",
               "Can't assign completed " + awa.getArtifactTypeName() + " (" + awa.getAtsId() + ")");
            return false;
         } else if (awa.isCancelled()) {
            AWorkbench.popup("ERROR",
               "Can't assign cancelled " + awa.getArtifactTypeName() + " (" + awa.getAtsId() + ")");
            return false;
         }
      }
      User unassigned = AtsClientService.get().getUserServiceClient().getOseeUser(AtsCoreUsers.UNASSIGNED_USER);
      Collection<User> oseeUsers = AtsClientService.get().getUserServiceClient().getOseeUsers(
         AtsClientService.get().getUserService().getUsers(Active.Active));
      AbstractWorkflowArtifact awaI = awas.iterator().next();
      TeamWorkFlowArtifact twa = awaI != null ? awaI.getParentTeamWorkflow() : null;
      Collection<User> teamMembers = twa != null ? AtsClientService.get().getUserServiceClient().getOseeUsers(
         twa.getTeamDefinition().getMembersAndLeads()) : null;
      Collection<User> selected =
         awas.size() == 1 && awaI != null ? AtsClientService.get().getUserServiceClient().getOseeUsers(
            awaI.getStateMgr().getAssignees()) : null;

      // unassigned is not useful in the selection choice dialog
      oseeUsers.remove(unassigned);
      if (teamMembers != null) {
         teamMembers.remove(unassigned);
      }
      UserCheckTreeDialog uld =
         new UserCheckTreeDialog("Select Assignees", "Select to assign.\nDeSelect to un-assign.", oseeUsers);
      uld.setIncludeAutoSelectButtons(true);
      if (teamMembers != null) {
         uld.setTeamMembers(teamMembers);
      }
      if (selected != null) {
         uld.setInitialSelections(selected);
      }
      if (uld.open() != 0) {
         return false;
      }
      Collection<IAtsUser> users = AtsClientService.get().getUserServiceClient().getAtsUsers(uld.getUsersSelected());
      if (users.isEmpty()) {
         users.add(AtsCoreUsers.UNASSIGNED_USER);
      }
      // As a convenience, remove the UnAssigned user if another user is selected
      if (users.size() > 1) {
         users.remove(AtsCoreUsers.UNASSIGNED_USER);
      }
      for (AbstractWorkflowArtifact awa : awas) {
         awa.getStateMgr().setAssignees(users);
      }
      if (persist) {
         AtsClientService.get().getStoreService().executeChangeSet("Assignee - Prompt Change", awas);
      }
      return true;
   }

   @Override
   public void handleColumnMultiEdit(TreeColumn treeColumn, Collection<TreeItem> treeItems) {
      try {
         Set<AbstractWorkflowArtifact> awas = new HashSet<>();
         for (TreeItem item : treeItems) {
            Artifact art = (Artifact) item.getData();
            if (art instanceof AbstractWorkflowArtifact) {
               awas.add((AbstractWorkflowArtifact) art);
            }
            if (art.isOfType(
               AtsArtifactTypes.Action) && AtsClientService.get().getWorkItemService().getTeams(art).size() == 1) {
               awas.add((AbstractWorkflowArtifact) AtsClientService.get().getWorkItemService().getFirstTeam(
                  art).getStoreObject());
            }
         }
         if (awas.isEmpty()) {
            AWorkbench.popup("Invalid selection for setting assignees.");
            return;
         }
         promptChangeAssignees(awas, true);
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   @Override
   public Image getColumnImage(Object element, XViewerColumn xCol, int columnIndex) {
      try {
         if (element instanceof Artifact) {
            return AssigneeColumnUI.getAssigneeImage((Artifact) element);
         }
      } catch (Exception ex) {
         // do nothing
      }
      return null;
   }

   public static Image getAssigneeImage(Artifact artifact) {
      if (artifact.isDeleted()) {
         return null;
      }
      if (artifact instanceof AbstractWorkflowArtifact) {
         return FrameworkArtifactImageProvider.getUserImage(AtsClientService.get().getUserServiceClient().getOseeUsers(
            ((AbstractWorkflowArtifact) artifact).getStateMgr().getAssignees()));
      }
      if (artifact.isOfType(AtsArtifactTypes.Action)) {
         for (IAtsTeamWorkflow team : AtsClientService.get().getWorkItemService().getTeams(artifact)) {
            Image image = AssigneeColumnUI.getAssigneeImage((Artifact) team.getStoreObject());
            if (image != null) {
               return image;
            }
         }
      }
      return null;

   }

}
