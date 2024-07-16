/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.osee.ats.ide.column;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.nebula.widgets.xviewer.IAltLeftClickProvider;
import org.eclipse.nebula.widgets.xviewer.IMultiColumnEditProvider;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.column.AtsColumnTokens;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.UserCheckTreeDialog;
import org.eclipse.osee.ats.ide.util.xviewer.column.XViewerAtsColumnIdColumn;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
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
      super(AtsColumnTokens.AssigneeColumn);
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
         IAtsWorkItem workItem = null;
         boolean modified = false;
         XViewer xViewer = null;
         if (treeItem.getData() instanceof IAtsAction) {
            if (AtsApiService.get().getWorkItemService().getTeams(workItem).size() == 1) {
               workItem = AtsApiService.get().getWorkItemService().getFirstTeam(workItem);
            } else {
               return false;
            }
         } else if (treeItem.getData() instanceof IAtsWorkItem) {
            workItem = (IAtsWorkItem) treeItem.getData();
         }

         if (workItem == null) {
            return false;
         }

         modified = promptChangeAssignees(Arrays.asList(workItem));
         xViewer = (XViewer) ((XViewerColumn) treeColumn.getData()).getXViewer();

         if (modified) {
            AtsApiService.get().getStoreService().executeChangeSet("persist assignees via alt-left-click", workItem);
         }
         if (modified) {
            xViewer.update(workItem.getStoreObject(), null);
            return true;
         }

      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }

      return false;
   }

   public static boolean promptChangeAssignees(IAtsWorkItem workItem) {
      return promptChangeAssignees(Arrays.asList(workItem));
   }

   public static boolean promptChangeAssignees(final Collection<? extends IAtsWorkItem> workItems) {
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
      Collection<AtsUser> users = AtsApiService.get().getUserService().getActiveAndAssignedInActive(workItems);

      // unassigned is not useful in the selection choice dialog
      users.remove(AtsCoreUsers.UNASSIGNED_USER);
      UserCheckTreeDialog uld =
         new UserCheckTreeDialog("Select Assignees", "Select to assign.\nDeSelect to un-assign.", users);
      uld.setIncludeAutoSelectButtons(true);

      IAtsTeamWorkflow parentWorklfow = workItems.iterator().next().getParentTeamWorkflow();
      if (parentWorklfow != null) {
         uld.setTeamMembers(
            AtsApiService.get().getTeamDefinitionService().getMembersAndLeads(parentWorklfow.getTeamDefinition()));
      }
      if (workItems.size() == 1) {
         uld.setInitialSelections(workItems.iterator().next().getStateMgr().getAssignees());
      }
      if (uld.open() != 0) {
         return false;
      }
      Collection<AtsUser> selected = uld.getUsersSelected();
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
      AtsApiService.get().getStoreService().executeChangeSet("Assignee - Prompt Change", workItems);
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
               teamWfs.add(AtsApiService.get().getWorkItemService().getFirstTeam(item.getData()));
            }
         }
         if (teamWfs.isEmpty()) {
            AWorkbench.popup("Invalid selection for setting assignees.");
            return;
         }
         promptChangeAssignees(teamWfs);
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   @Override
   public Image getColumnImage(Object element, XViewerColumn xCol, int columnIndex) {
      try {
         if (element instanceof Artifact) {
            return AssigneeColumnUI.getAssigneeImage(AtsApiService.get().getQueryServiceIde().getArtifact(element));
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
         List<User> users = new ArrayList<>();
         for (AtsUser aUser : ((AbstractWorkflowArtifact) artifact).getStateMgr().getAssignees()) {
            User user = UserManager.getUserByArtId(aUser);
            if (user != null) {
               users.add(user);
            }
         }

         return FrameworkArtifactImageProvider.getUserImage(users);
      }
      if (artifact.isOfType(AtsArtifactTypes.Action)) {
         for (IAtsTeamWorkflow team : AtsApiService.get().getWorkItemService().getTeams(artifact)) {
            Image image = AssigneeColumnUI.getAssigneeImage(AtsApiService.get().getQueryServiceIde().getArtifact(team));
            if (image != null) {
               return image;
            }
         }
      }
      return null;

   }

}
