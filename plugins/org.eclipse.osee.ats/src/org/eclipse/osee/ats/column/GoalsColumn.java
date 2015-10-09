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
import org.eclipse.nebula.widgets.xviewer.IXViewerValueColumn;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.core.client.action.ActionManager;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.goal.GoalCheckTreeDialog;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsColumn;
import org.eclipse.osee.ats.world.WorldXViewerFactory;
import org.eclipse.osee.ats.world.search.GoalSearchItem;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.skynet.util.LogUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Donald G. Dunne
 */
public class GoalsColumn extends XViewerAtsColumn implements IXViewerValueColumn, IAltLeftClickProvider, IMultiColumnEditProvider {

   public static GoalsColumn instance = new GoalsColumn();

   public static GoalsColumn getInstance() {
      return instance;
   }

   private GoalsColumn() {
      super(WorldXViewerFactory.COLUMN_NAMESPACE + ".goals", "Goals", 100, SWT.LEFT, false, SortDataType.String, true,
         "Goals");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public GoalsColumn copy() {
      GoalsColumn newXCol = new GoalsColumn();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public boolean handleAltLeftClick(TreeColumn treeColumn, TreeItem treeItem) {
      try {
         if (treeItem.getData() instanceof Artifact) {
            Artifact useArt = (Artifact) treeItem.getData();
            if (useArt.isOfType(AtsArtifactTypes.Action)) {
               if (ActionManager.getTeams(useArt).size() == 1) {
                  useArt = ActionManager.getFirstTeam(useArt);
               } else {
                  return false;
               }
            }
            if (!(useArt.isOfType(AtsArtifactTypes.AbstractWorkflowArtifact))) {
               return false;
            }
            boolean modified = promptChangeGoals(Arrays.asList(useArt), isPersistViewer());
            XViewer xViewer = ((XViewerColumn) treeColumn.getData()).getTreeViewer();
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

   public static boolean promptChangeGoals(Artifact sma, boolean persist) throws OseeCoreException {
      return promptChangeGoals(Arrays.asList(sma), persist);
   }

   public static boolean promptChangeGoals(final Collection<? extends Artifact> awas, boolean persist) throws OseeCoreException {
      Set<Artifact> selected = new HashSet<>();
      for (Artifact awa : awas) {
         selected.addAll(awa.getRelatedArtifacts(AtsRelationTypes.Goal_Goal));
      }
      Collection<Artifact> allGoals = new GoalSearchItem("", null, false, null).performSearchGetResults();
      GoalCheckTreeDialog dialog = new GoalCheckTreeDialog(allGoals);
      dialog.setInitialSelections(selected);
      if (dialog.open() == 0) {
         for (Artifact awa : awas) {
            awa.setRelations(AtsRelationTypes.Goal_Goal, dialog.getChecked());
         }
         Artifacts.persistInTransaction("Set Goals", awas);
         return true;
      }
      return false;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      try {
         if (element instanceof Artifact) {
            return Artifacts.toString("; ", ((Artifact) element).getRelatedArtifacts(AtsRelationTypes.Goal_Goal));
         }
      } catch (OseeCoreException ex) {
         return LogUtil.getCellExceptionString(ex);
      }
      return "";
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
         }
         promptChangeGoals(awas, true);
         return;
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

}
