/*
 * Created on Oct 27, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.field;

import java.util.HashSet;
import java.util.Set;
import org.eclipse.nebula.widgets.xviewer.IXViewerValueColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerCells;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.ats.artifact.ActionArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsColumn;
import org.eclipse.osee.ats.world.WorldXViewerFactory;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.swt.SWT;

public class BranchStatusColumn extends XViewerAtsColumn implements IXViewerValueColumn {

   public static BranchStatusColumn instance = new BranchStatusColumn();

   public static BranchStatusColumn getInstance() {
      return instance;
   }

   private BranchStatusColumn() {
      super(WorldXViewerFactory.COLUMN_NAMESPACE + ".branchStatus", "Branch Status", 40, SWT.CENTER, false,
         SortDataType.String, false, null);
   }

   public BranchStatusColumn(String id, String name, int width, int align, boolean show, SortDataType sortDataType, boolean multiColumnEditable, String description) {
      super(id, name, width, align, show, sortDataType, multiColumnEditable, description);
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public BranchStatusColumn copy() {
      return new BranchStatusColumn(getId(), getName(), getWidth(), getAlign(), isShow(), getSortDataType(),
         isMultiColumnEditable(), getDescription());
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      try {
         if (element instanceof ActionArtifact) {
            Set<String> strs = new HashSet<String>();
            for (TeamWorkFlowArtifact team : ((ActionArtifact) element).getTeamWorkFlowArtifacts()) {
               String str = getColumnText(team, column, columnIndex);
               if (Strings.isValid(str)) {
                  strs.add(str);
               }
            }
            return Collections.toString(", ", strs);
         }
         if (element instanceof TeamWorkFlowArtifact) {
            TeamWorkFlowArtifact teamArt = (TeamWorkFlowArtifact) element;
            try {
               if (teamArt.getBranchMgr().isWorkingBranchInWork()) {
                  return "Working";
               } else if (teamArt.getBranchMgr().isCommittedBranchExists()) {
                  if (!teamArt.getBranchMgr().isAllObjectsToCommitToConfigured() || !teamArt.getBranchMgr().isBranchesAllCommitted()) {
                     return "Needs Commit";
                  }
                  return "Committed";
               }
               return "";
            } catch (Exception ex) {
               return "Exception: " + ex.getLocalizedMessage();
            }
         }
      } catch (OseeCoreException ex) {
         XViewerCells.getCellExceptionString(ex);
      }
      return "";
   }
}
