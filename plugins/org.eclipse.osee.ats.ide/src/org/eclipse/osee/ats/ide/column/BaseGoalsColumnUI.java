/*********************************************************************
 * Copyright (c) 2017 Boeing
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
import java.util.Map;
import java.util.Set;
import org.eclipse.jface.window.Window;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.workflow.IAtsGoal;
import org.eclipse.osee.ats.api.workflow.WorkItemType;
import org.eclipse.osee.ats.core.column.BacklogColumn;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.ide.workflow.goal.GoalCheckTreeDialog;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.skynet.util.LogUtil;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Donald G. Dunne
 * @author David W Miller
 */
public abstract class BaseGoalsColumnUI extends BackgroundLoadingPreComputedColumnUI {

   private final WorkItemType goalType = WorkItemType.Goal;
   private final String persistString = "Set Goals";
   private final boolean isBacklogGoal = false;

   public BaseGoalsColumnUI(String id, String name, int width, XViewerAlign align, boolean show, SortDataType sortDataType, boolean multiColumnEditable, String description) {
      super(id, name, width, align, show, sortDataType, multiColumnEditable, description);
   }

   @Override
   public String getValue(IAtsWorkItem workItem, Map<Long, String> idToValueMap) {
      String result = "";
      try {
         result = BacklogColumn.getColumnText(workItem, AtsApiService.get(), isBacklogGoal());
      } catch (OseeCoreException ex) {
         result = LogUtil.getCellExceptionString(ex);
      }
      return result;
   }

   protected WorkItemType getWorkItemType() {
      return goalType;
   }

   protected String getPersistString() {
      return persistString;
   }

   protected boolean isBacklogGoal() {
      return isBacklogGoal;
   }

   @Override
   public boolean handleAltLeftClick(TreeColumn treeColumn, TreeItem treeItem) {
      try {
         if (treeItem.getData() instanceof Artifact) {
            Artifact useArt = AtsApiService.get().getQueryServiceIde().getArtifact(treeItem);
            if (useArt.isOfType(AtsArtifactTypes.Action)) {
               if (AtsApiService.get().getWorkItemService().getTeams(useArt).size() == 1) {
                  useArt = (AbstractWorkflowArtifact) AtsApiService.get().getWorkItemService().getFirstTeam(
                     useArt).getStoreObject();
               } else {
                  return false;
               }
            }
            if (!useArt.isOfType(AtsArtifactTypes.AbstractWorkflowArtifact)) {
               return false;
            }
            boolean modified = promptChangeGoals(Arrays.asList(useArt));
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

   public boolean promptChangeGoals(final Collection<? extends Artifact> awas) {
      Set<Artifact> selected = new HashSet<>();
      for (Artifact awa : awas) {
         selected.addAll(awa.getRelatedArtifacts(AtsRelationTypes.Goal_Goal));
      }
      Collection<Artifact> allGoals = Collections.castAll(
         AtsApiService.get().getQueryService().createQuery(getWorkItemType()).getResultArtifacts().getList());
      Collection<IAtsWorkItem> allInWork = new ArrayList<>();
      for (Artifact art : allGoals) {
         IAtsGoal goal = AtsApiService.get().getWorkItemService().getGoal(art);
         if (goal != null && goal.isInWork()) {
            allInWork.add(goal);
         }
      }
      GoalCheckTreeDialog dialog = new GoalCheckTreeDialog(allGoals);
      dialog.setInitialSelections(selected);
      if (dialog.open() == Window.OK) {
         for (Artifact awa : awas) {
            awa.setRelations(AtsRelationTypes.Goal_Goal, dialog.getChecked());
         }
         TransactionManager.persistInTransaction(getPersistString(), awas);
         return true;
      }
      return false;
   }

   @Override
   public void handleColumnMultiEdit(TreeColumn treeColumn, Collection<TreeItem> treeItems) {
      try {
         Set<AbstractWorkflowArtifact> awas = new HashSet<>();
         for (TreeItem item : treeItems) {
            if (item.getData() instanceof Artifact) {
               Artifact art = AtsApiService.get().getQueryServiceIde().getArtifact(item);
               if (art instanceof AbstractWorkflowArtifact) {
                  awas.add((AbstractWorkflowArtifact) art);
               }
            }
         }
         promptChangeGoals(awas);
         return;
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

}
