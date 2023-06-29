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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.nebula.widgets.xviewer.IAltLeftClickProvider;
import org.eclipse.nebula.widgets.xviewer.IMultiColumnEditProvider;
import org.eclipse.nebula.widgets.xviewer.IXViewerValueColumn;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.notify.AtsNotificationEventFactory;
import org.eclipse.osee.ats.api.notify.AtsNotifyType;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.AtsEditors;
import org.eclipse.osee.ats.ide.util.widgets.dialog.UserListDialog;
import org.eclipse.osee.ats.ide.util.xviewer.column.XViewerAtsColumn;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.ide.world.WorldXViewerFactory;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.skynet.util.LogUtil;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Donald G. Dunne
 */
public class OriginatorColumn extends XViewerAtsColumn implements IXViewerValueColumn, IAltLeftClickProvider, IMultiColumnEditProvider {

   public static OriginatorColumn instance = new OriginatorColumn();

   public static OriginatorColumn getInstance() {
      return instance;
   }

   private OriginatorColumn() {
      super(WorldXViewerFactory.COLUMN_NAMESPACE + ".originator", "Originator", 80, XViewerAlign.Left, false,
         SortDataType.String, true, null);
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public OriginatorColumn copy() {
      OriginatorColumn newXCol = new OriginatorColumn();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public boolean handleAltLeftClick(TreeColumn treeColumn, TreeItem treeItem) {
      try {
         if (treeItem.getData() instanceof Artifact) {
            Artifact useArt = AtsApiService.get().getQueryServiceIde().getArtifact(treeItem);
            if (useArt.isOfType(AtsArtifactTypes.Action)) {
               if (AtsApiService.get().getWorkItemService().getTeams(useArt).size() == 1) {
                  useArt = AtsApiService.get().getQueryServiceIde().getArtifact(
                     AtsApiService.get().getWorkItemService().getFirstTeam(useArt));
               } else {
                  return false;
               }
            }
            if (!useArt.isOfType(AtsArtifactTypes.TeamWorkflow)) {
               return false;
            }
            boolean modified = promptChangeOriginator(Arrays.asList((TeamWorkFlowArtifact) useArt));
            XViewer xViewer = (XViewer) ((XViewerColumn) treeColumn.getData()).getXViewer();
            if (modified) {
               useArt.persist("persist goals via alt-left-click");
               xViewer.update(useArt, null);
               return true;
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }

      return false;
   }

   public static boolean promptChangeOriginator(IAtsWorkItem workItem) {
      return promptChangeOriginator(Arrays.asList(workItem));
   }

   public static boolean promptChangeOriginator(final Collection<IAtsWorkItem> workItems) {
      UserListDialog dialog =
         new UserListDialog(Displays.getActiveShell(), "Select New Originator", Active.Active, false);
      int result = dialog.open();
      if (result == 0) {
         AtsUser selectedUser = dialog.getSelection();
         IAtsChangeSet changes = AtsApiService.get().createChangeSet("ATS Prompt Change Originator");
         for (IAtsWorkItem workItem : workItems) {
            changes.setCreatedBy(workItem, selectedUser, true, null);
            addOriginatorNotification(workItem, changes);
            changes.add(workItem);
         }
         changes.execute();
         return true;
      }
      return false;
   }

   public static void addOriginatorNotification(IAtsWorkItem workItem, IAtsChangeSet changes) {
      try {
         changes.addWorkItemNotificationEvent(AtsNotificationEventFactory.getWorkItemNotificationEvent(
            AtsApiService.get().getUserService().getCurrentUser(), workItem, AtsNotifyType.Originator));
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, "Error adding ATS Notification Event", ex);
      }

   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      try {
         if (element instanceof AbstractWorkflowArtifact) {
            return ((AbstractWorkflowArtifact) element).getCreatedBy().getName();
         }
         if (Artifacts.isOfType(element, AtsArtifactTypes.Action)) {
            Set<String> strs = new HashSet<>();
            for (IAtsTeamWorkflow team : AtsApiService.get().getWorkItemService().getTeams(element)) {
               strs.add(team.getCreatedBy().getName());
            }
            return Collections.toString("; ", strs);
         }
         return "";
      } catch (OseeCoreException ex) {
         return LogUtil.getCellExceptionString(ex);
      }
   }

   @Override
   public Image getColumnImage(Object element, XViewerColumn xCol, int columnIndex) {
      try {
         if (element instanceof AbstractWorkflowArtifact) {
            return AtsEditors.getImage(Arrays.asList(((AbstractWorkflowArtifact) element).getCreatedBy()));
         }
         if (Artifacts.isOfType(element, AtsArtifactTypes.Action)) {
            Set<AtsUser> users = new HashSet<>();
            for (IAtsTeamWorkflow team : AtsApiService.get().getWorkItemService().getTeams(element)) {
               users.add(team.getCreatedBy());
            }
            return AtsEditors.getImage(users);
         }

      } catch (Exception ex) {
         // do nothing
      }
      return null;
   }

   @Override
   public void handleColumnMultiEdit(TreeColumn treeColumn, Collection<TreeItem> treeItems) {
      try {
         Set<IAtsWorkItem> awas = new HashSet<>();
         for (TreeItem item : treeItems) {
            if (item.getData() instanceof AbstractWorkflowArtifact) {
               Artifact art = AtsApiService.get().getQueryServiceIde().getArtifact(item);
               if (art instanceof AbstractWorkflowArtifact) {
                  awas.add((AbstractWorkflowArtifact) art);
               }
            }
         }
         promptChangeOriginator(awas);
         return;
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

}
