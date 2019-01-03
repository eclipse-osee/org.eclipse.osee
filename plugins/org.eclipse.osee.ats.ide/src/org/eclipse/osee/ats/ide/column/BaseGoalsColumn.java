/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.column;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.nebula.widgets.xviewer.IAltLeftClickProvider;
import org.eclipse.nebula.widgets.xviewer.IMultiColumnEditProvider;
import org.eclipse.nebula.widgets.xviewer.IXViewerValueColumn;
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
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.util.xviewer.column.XViewerAtsColumn;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.ide.workflow.goal.GoalCheckTreeDialog;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.skynet.util.LogUtil;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Donald G. Dunne
 * @author David W Miller
 */
public abstract class BaseGoalsColumn extends XViewerAtsColumn implements IXViewerValueColumn, IAltLeftClickProvider, IMultiColumnEditProvider {

   private final WorkItemType goalType = WorkItemType.Goal;
   private final String persistString = "Set Goals";
   private final boolean isBacklogGoal = false;

   protected BaseGoalsColumn() {
      // do nothing
   }

   public BaseGoalsColumn(String id, String name, int width, XViewerAlign align, boolean show, SortDataType sortDataType, boolean multiColumnEditable, String description) {
      super(id, name, width, align, show, sortDataType, multiColumnEditable, description);
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
            Artifact useArt = AtsClientService.get().getQueryServiceClient().getArtifact(treeItem);
            if (useArt.isOfType(AtsArtifactTypes.Action)) {
               if (AtsClientService.get().getWorkItemService().getTeams(useArt).size() == 1) {
                  useArt = (AbstractWorkflowArtifact) AtsClientService.get().getWorkItemService().getFirstTeam(
                     useArt).getStoreObject();
               } else {
                  return false;
               }
            }
            if (!useArt.isOfType(AtsArtifactTypes.AbstractWorkflowArtifact)) {
               return false;
            }
            boolean modified = promptChangeGoals(Arrays.asList(useArt), isPersistViewer());
            XViewer xViewer = (XViewer) ((XViewerColumn) treeColumn.getData()).getXViewer();
            if (modified && isPersistViewer(xViewer)) {
               useArt.persist("persist goals via alt-left-click");
            }
            if (modified) {
               xViewer.update(useArt, null);
               return true;
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }

      return false;
   }

   public boolean promptChangeGoals(final Collection<? extends Artifact> awas, boolean persist) {
      Set<Artifact> selected = new HashSet<>();
      for (Artifact awa : awas) {
         selected.addAll(awa.getRelatedArtifacts(AtsRelationTypes.Goal_Goal));
      }
      Collection<Artifact> allGoals = Collections.castAll(
         AtsClientService.get().getQueryService().createQuery(getWorkItemType()).getResultArtifacts().getList());
      Collection<IAtsWorkItem> allInWork = new ArrayList<>();
      for (Artifact art : allGoals) {
         IAtsGoal goal = AtsClientService.get().getWorkItemService().getGoal(art);
         if (goal != null && goal.isInWork()) {
            allInWork.add(goal);
         }
      }
      GoalCheckTreeDialog dialog = new GoalCheckTreeDialog(allGoals);
      dialog.setInitialSelections(selected);
      if (dialog.open() == 0) {
         for (Artifact awa : awas) {
            awa.setRelations(AtsRelationTypes.Goal_Goal, dialog.getChecked());
         }
         Artifacts.persistInTransaction(getPersistString(), awas);
         return true;
      }
      return false;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      String result = "";
      try {
         result = BacklogColumn.getColumnText(element, AtsClientService.get(), isBacklogGoal());
      } catch (OseeCoreException ex) {
         return LogUtil.getCellExceptionString(ex);
      }
      return result;
   }

   @Override
   public void handleColumnMultiEdit(TreeColumn treeColumn, Collection<TreeItem> treeItems) {
      try {
         Set<AbstractWorkflowArtifact> awas = new HashSet<>();
         for (TreeItem item : treeItems) {
            if (item.getData() instanceof Artifact) {
               Artifact art = AtsClientService.get().getQueryServiceClient().getArtifact(item);
               if (art instanceof AbstractWorkflowArtifact) {
                  awas.add((AbstractWorkflowArtifact) art);
               }
            }
         }
         promptChangeGoals(awas, true);
         return;
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

}
