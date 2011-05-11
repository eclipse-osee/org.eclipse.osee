/*
 * Created on Oct 27, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.column;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.nebula.widgets.xviewer.IAltLeftClickProvider;
import org.eclipse.nebula.widgets.xviewer.IMultiColumnEditProvider;
import org.eclipse.nebula.widgets.xviewer.IXViewerValueColumn;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.XViewerCells;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.ats.artifact.ActionManager;
import org.eclipse.osee.ats.core.config.TeamDefinitionArtifact;
import org.eclipse.osee.ats.core.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.type.AtsArtifactTypes;
import org.eclipse.osee.ats.core.type.AtsRelationTypes;
import org.eclipse.osee.ats.core.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.goal.GoalCheckTreeDialog;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsColumn;
import org.eclipse.osee.ats.world.WorldXViewerFactory;
import org.eclipse.osee.ats.world.search.GoalSearchItem;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

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
            if (!(useArt.isOfType(AtsArtifactTypes.TeamWorkflow))) {
               return false;
            }
            boolean modified = promptChangeGoals(Arrays.asList((TeamWorkFlowArtifact) useArt), isPersistViewer());
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
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }

      return false;
   }

   public static boolean promptChangeGoals(Artifact sma, boolean persist) throws OseeCoreException {
      return promptChangeGoals(Arrays.asList(sma), persist);
   }

   public static boolean promptChangeGoals(final Collection<? extends Artifact> awas, boolean persist) throws OseeCoreException {
      Set<Artifact> selected = new HashSet<Artifact>();
      for (Artifact awa : awas) {
         selected.addAll(awa.getRelatedArtifacts(AtsRelationTypes.Goal_Goal));
      }
      Collection<Artifact> allGoals =
         new GoalSearchItem("", new ArrayList<TeamDefinitionArtifact>(), false, null).performSearchGetResults();
      GoalCheckTreeDialog dialog = new GoalCheckTreeDialog(allGoals);
      dialog.setInitialSelections(selected.toArray());
      if (dialog.open() == 0) {
         SkynetTransaction transaction = new SkynetTransaction(AtsUtil.getAtsBranch(), "Set Goals");
         for (Artifact awa : awas) {
            awa.setRelations(AtsRelationTypes.Goal_Goal, dialog.getSelection());
            awa.persist(transaction);
         }
         transaction.execute();
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
         return XViewerCells.getCellExceptionString(ex);
      }
      return "";
   }

   @Override
   public void handleColumnMultiEdit(TreeColumn treeColumn, Collection<TreeItem> treeItems) {
      try {
         Set<AbstractWorkflowArtifact> awas = new HashSet<AbstractWorkflowArtifact>();
         for (TreeItem item : treeItems) {
            Artifact art = (Artifact) item.getData();
            if (art instanceof AbstractWorkflowArtifact) {
               awas.add((AbstractWorkflowArtifact) art);
            }
         }
         promptChangeGoals(awas, true);
         return;
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

}
