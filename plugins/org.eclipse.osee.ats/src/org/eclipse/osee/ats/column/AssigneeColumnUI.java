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
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.column.AtsColumnToken;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.util.UserCheckTreeDialog;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsColumnIdColumn;
import org.eclipse.osee.ats.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.FrameworkArtifactImageProvider;
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
         IAtsTeamWorkflow teamWf = null;
         if (treeItem.getData() instanceof IAtsAction) {
            if (AtsClientService.get().getWorkItemService().getTeams(teamWf).size() == 1) {
               teamWf = AtsClientService.get().getWorkItemService().getFirstTeam(teamWf);
            } else {
               return false;
            }
         } else if (treeItem.getData() instanceof IAtsTeamWorkflow) {
            teamWf = (IAtsTeamWorkflow) treeItem.getData();
         }
         if (teamWf == null) {
            return false;
         }
         boolean modified = promptChangeAssignees(Arrays.asList(teamWf), isPersistViewer());
         XViewer xViewer = (XViewer) ((XViewerColumn) treeColumn.getData()).getXViewer();
         if (modified && isPersistViewer(xViewer)) {
            AtsClientService.get().getStoreService().executeChangeSet("persist assignees via alt-left-click", teamWf);
         }
         if (modified) {
            xViewer.update(teamWf.getStoreObject(), null);
            return true;
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }

      return false;
   }

   public static boolean promptChangeAssignees(IAtsWorkItem workItem, boolean persist) {
      return promptChangeAssignees(Arrays.asList(workItem), persist);
   }

   public static boolean promptChangeAssignees(final Collection<? extends IAtsWorkItem> workItems, boolean persist) {
      for (IAtsWorkItem workItem : workItems) {
         if (workItem.isCompleted()) {
            AWorkbench.popup("ERROR",
               "Can't assign completed " + workItem.getArtifactTypeName() + " (" + workItem.getAtsId() + ")");
            return false;
         } else if (workItem.isCancelled()) {
            AWorkbench.popup("ERROR",
               "Can't assign cancelled " + workItem.getArtifactTypeName() + " (" + workItem.getAtsId() + ")");
            return false;
         }
      }
      Collection<IAtsUser> users = AtsClientService.get().getUserService().getActiveAndAssignedInActive(workItems);

      // unassigned is not useful in the selection choice dialog
      users.remove(SystemUser.UnAssigned);
      UserCheckTreeDialog uld =
         new UserCheckTreeDialog("Select Assignees", "Select to assign.\nDeSelect to un-assign.", users);
      uld.setIncludeAutoSelectButtons(true);

      IAtsTeamWorkflow parentWorklfow = workItems.iterator().next().getParentTeamWorkflow();
      if (parentWorklfow != null) {
         uld.setTeamMembers(parentWorklfow.getTeamDefinition().getMembersAndLeads());
      }
      if (workItems.size() == 1) {
         uld.setInitialSelections(workItems.iterator().next().getStateMgr().getAssignees());
      }
      if (uld.open() != 0) {
         return false;
      }
      Collection<IAtsUser> selected = uld.getUsersSelected();
      if (selected.isEmpty()) {
         selected.add(AtsCoreUsers.UNASSIGNED_USER);
      }
      // As a convenience, remove the UnAssigned user if another user is selected
      if (selected.size() > 1) {
         users.remove(AtsCoreUsers.UNASSIGNED_USER);
      }
      for (IAtsWorkItem workItem : workItems) {
         workItem.getStateMgr().setAssignees(selected);
      }
      if (persist) {
         AtsClientService.get().getStoreService().executeChangeSet("Assignee - Prompt Change", workItems);
      }
      return true;
   }

   @Override
   public void handleColumnMultiEdit(TreeColumn treeColumn, Collection<TreeItem> treeItems) {
      try {
         Set<IAtsTeamWorkflow> teamWfs = new HashSet<>();
         for (TreeItem item : treeItems) {
            if (item.getData() instanceof IAtsTeamWorkflow) {
               IAtsTeamWorkflow teamWf = (IAtsTeamWorkflow) item.getData();
               if (teamWf instanceof AbstractWorkflowArtifact) {
                  teamWfs.add(teamWf);
               }
            } else if (item.getData() instanceof IAtsAction) {
               teamWfs.add(AtsClientService.get().getWorkItemService().getFirstTeam(item.getData()));
            }
         }
         if (teamWfs.isEmpty()) {
            AWorkbench.popup("Invalid selection for setting assignees.");
            return;
         }
         promptChangeAssignees(teamWfs, true);
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
