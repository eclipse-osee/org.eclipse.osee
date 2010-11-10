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
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.ats.artifact.ActionArtifact;
import org.eclipse.osee.ats.artifact.AtsAttributeTypes;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.PromptChangeUtil;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsAttributeValueColumn;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

public class PriorityColumn extends XViewerAtsAttributeValueColumn {

   public static PriorityColumn instance = new PriorityColumn();

   public static PriorityColumn getInstance() {
      return instance;
   }

   private PriorityColumn() {
      super(AtsAttributeTypes.PriorityType, 20, SWT.LEFT, true, SortDataType.String, true);
   }

   public PriorityColumn(IAttributeType attributeType, int width, int align, boolean show, SortDataType sortDataType, boolean multiColumnEditable, String description) {
      super(attributeType, width, align, show, sortDataType, multiColumnEditable);
      setDescription(description);
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public PriorityColumn copy() {
      return new PriorityColumn(getAttributeType(), getWidth(), getAlign(), isShow(), getSortDataType(),
         isMultiColumnEditable(), getDescription());
   }

   public static boolean promptChangePriority(final Collection<? extends TeamWorkFlowArtifact> teams, boolean persist) {

      for (TeamWorkFlowArtifact team : teams) {
         if (team.isReleased() || team.isVersionLocked()) {
            AWorkbench.popup("ERROR",
               "Team Workflow\n \"" + team.getName() + "\"\n version is locked or already released.");
            return false;
         }
      }
      try {
         PromptChangeUtil.promptChangeAttribute(teams, AtsAttributeTypes.PriorityType, persist, false);
         return true;
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, "Can't change priority", ex);
         return false;
      }
   }

   @Override
   public boolean handleAltLeftClick(TreeColumn treeColumn, TreeItem treeItem) {
      try {
         // Only prompt change for sole attribute types
         if (AttributeTypeManager.getType(getAttributeType()).getMaxOccurrences() != 1) {
            return false;
         }
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
            TeamWorkFlowArtifact team = (TeamWorkFlowArtifact) useArt;
            if (team.isReleased() || team.isVersionLocked()) {
               AWorkbench.popup("ERROR",
                  "Team Workflow\n \"" + team.getName() + "\"\n version is locked or already released.");
               return false;
            }

            boolean modified = promptChangePriority(Arrays.asList((TeamWorkFlowArtifact) useArt), isPersistViewer());
            XViewer xViewer = ((XViewerColumn) treeColumn.getData()).getTreeViewer();
            if (modified && isPersistViewer(xViewer)) {
               useArt.persist("persist priority via alt-left-click");
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

   @Override
   public void handleColumnMultiEdit(TreeColumn treeColumn, Collection<TreeItem> treeItems) {
      Set<TeamWorkFlowArtifact> smas = new HashSet<TeamWorkFlowArtifact>();
      for (TreeItem item : treeItems) {
         Artifact art = (Artifact) item.getData();
         if (art instanceof TeamWorkFlowArtifact) {
            smas.add((TeamWorkFlowArtifact) art);
         }
      }
      if (smas.size() == 0) {
         AWorkbench.popup("Must select Team Workflows");
         return;
      }
      promptChangePriority(smas, true);
      return;
   }

   public static String getPriorityStr(Object object) throws OseeCoreException {
      if (object instanceof Artifact) {
         return ((Artifact) object).getSoleAttributeValue(AtsAttributeTypes.PriorityType, "");
      }
      return "";
   }
}
