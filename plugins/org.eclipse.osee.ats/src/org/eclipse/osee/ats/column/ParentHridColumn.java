/*
 * Created on Oct 27, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.column;

import org.eclipse.nebula.widgets.xviewer.IXViewerValueColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerCells;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.ats.core.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.type.AtsArtifactTypes;
import org.eclipse.osee.ats.core.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsColumn;
import org.eclipse.osee.ats.world.WorldXViewerFactory;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.swt.SWT;

public class ParentHridColumn extends XViewerAtsColumn implements IXViewerValueColumn {

   public static ParentHridColumn instance = new ParentHridColumn();

   public static ParentHridColumn getInstance() {
      return instance;
   }

   private ParentHridColumn() {
      super(WorldXViewerFactory.COLUMN_NAMESPACE + ".parenthrid", "Parent HRID", 75, SWT.LEFT, false,
         SortDataType.String, false, "Human Readable ID of Parent Action or Team Workflow");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public ParentHridColumn copy() {
      ParentHridColumn newXCol = new ParentHridColumn();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      try {
         if (Artifacts.isOfType(element, AtsArtifactTypes.TeamWorkflow)) {
            return ((TeamWorkFlowArtifact) element).getParentActionArtifact().getHumanReadableId();
         } else if (element instanceof AbstractWorkflowArtifact && ((AbstractWorkflowArtifact) element).getParentAWA() != null) {
            return ((AbstractWorkflowArtifact) element).getParentAWA().getHumanReadableId();
         }

      } catch (OseeCoreException ex) {
         return XViewerCells.getCellExceptionString(ex);
      }
      return "";
   }

}
