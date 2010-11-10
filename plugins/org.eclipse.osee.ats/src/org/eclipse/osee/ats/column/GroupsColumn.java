/*
 * Created on Oct 27, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.column;

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
import org.eclipse.osee.ats.artifact.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.artifact.ActionArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsColumn;
import org.eclipse.osee.ats.world.WorldXViewerFactory;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.UniversalGroup;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.UserGroupsCheckTreeDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

public class GroupsColumn extends XViewerAtsColumn implements IXViewerValueColumn, IAltLeftClickProvider, IMultiColumnEditProvider {

   public static GroupsColumn instance = new GroupsColumn();

   public static GroupsColumn getInstance() {
      return instance;
   }

   private GroupsColumn() {
      super(WorldXViewerFactory.COLUMN_NAMESPACE + ".groups", "Groups", 100, SWT.LEFT, false, SortDataType.String,
         true, "Groups");
   }

   public GroupsColumn(String id, String name, int width, int align, boolean show, SortDataType sortDataType, boolean multiColumnEditable, String description) {
      super(id, name, width, align, show, sortDataType, multiColumnEditable, description);
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public GroupsColumn copy() {
      return new GroupsColumn(getId(), getName(), getWidth(), getAlign(), isShow(), getSortDataType(),
         isMultiColumnEditable(), getDescription());
   }

   @Override
   public boolean handleAltLeftClick(TreeColumn treeColumn, TreeItem treeItem) {
      try {
         if (treeItem.getData() instanceof Artifact) {
            Artifact useArt = (Artifact) treeItem.getData();
            if (useArt instanceof ActionArtifact) {
               if (((ActionArtifact) useArt).getTeamWorkFlowArtifacts().size() == 1) {
                  useArt = ((ActionArtifact) useArt).getTeamWorkFlowArtifacts().iterator().next();
               } else {
                  return false;
               }
            }
            if (!(useArt instanceof TeamWorkFlowArtifact)) {
               return false;
            }
            boolean modified = promptChangeGroups(Arrays.asList((TeamWorkFlowArtifact) useArt), isPersistViewer());
            XViewer xViewer = ((XViewerColumn) treeColumn.getData()).getTreeViewer();
            if (modified && isPersistViewer(xViewer)) {
               useArt.persist("persist groups via alt-left-click");
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

   public static boolean promptChangeGroups(AbstractWorkflowArtifact sma, boolean persist) throws OseeCoreException {
      return promptChangeGroups(Arrays.asList(sma), persist);
   }

   public static boolean promptChangeGroups(final Collection<? extends AbstractWorkflowArtifact> smas, boolean persist) throws OseeCoreException {
      Set<Artifact> selected = new HashSet<Artifact>();
      for (AbstractWorkflowArtifact sma : smas) {
         selected.addAll(sma.getRelatedArtifacts(CoreRelationTypes.Universal_Grouping__Group));
      }
      Collection<Artifact> allGroups = UniversalGroup.getGroupsNotRoot(AtsUtil.getAtsBranch());
      UserGroupsCheckTreeDialog dialog = new UserGroupsCheckTreeDialog(allGroups);
      dialog.setTitle("Select Groups");
      dialog.setInitialSelections(selected.toArray());
      if (dialog.open() == 0) {
         SkynetTransaction transaction = new SkynetTransaction(AtsUtil.getAtsBranch(), "Set Groups");
         for (AbstractWorkflowArtifact sma : smas) {
            sma.setRelations(CoreRelationTypes.Universal_Grouping__Group, dialog.getSelection());
            sma.persist(transaction);
         }
         transaction.execute();
         return true;
      }
      return false;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      try {
         if (element instanceof ActionArtifact) {
            Set<Artifact> groups = new HashSet<Artifact>();
            ActionArtifact actionArt = (ActionArtifact) element;
            groups.addAll(actionArt.getRelatedArtifacts(CoreRelationTypes.Universal_Grouping__Group));
            // Roll up if same for all children
            for (TeamWorkFlowArtifact team : actionArt.getTeamWorkFlowArtifacts()) {
               groups.addAll(team.getRelatedArtifacts(CoreRelationTypes.Universal_Grouping__Group));
            }
            return Artifacts.toString("; ", groups);
         }
         if (element instanceof Artifact) {
            return Artifacts.toString("; ",
               ((Artifact) element).getRelatedArtifacts(CoreRelationTypes.Universal_Grouping__Group));
         }
      } catch (OseeCoreException ex) {
         return XViewerCells.getCellExceptionString(ex);
      }
      return "";
   }

   @Override
   public void handleColumnMultiEdit(TreeColumn treeColumn, Collection<TreeItem> treeItems) {
      try {
         Set<AbstractWorkflowArtifact> smas = new HashSet<AbstractWorkflowArtifact>();
         for (TreeItem item : treeItems) {
            Artifact art = (Artifact) item.getData();
            if (art instanceof TeamWorkFlowArtifact) {
               smas.add((AbstractWorkflowArtifact) art);
            }
         }
         promptChangeGroups(smas, true);
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

}
