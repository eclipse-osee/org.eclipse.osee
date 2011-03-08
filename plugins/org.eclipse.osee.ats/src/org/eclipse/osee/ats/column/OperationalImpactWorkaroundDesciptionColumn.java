/*
 * Created on Oct 27, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.column;

import java.util.logging.Level;
import org.eclipse.nebula.widgets.xviewer.XViewerCells;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerValueColumn;
import org.eclipse.osee.ats.artifact.AtsAttributeTypes;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.ActionManager;
import org.eclipse.osee.ats.util.AtsArtifactTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.swt.SWT;

public class OperationalImpactWorkaroundDesciptionColumn extends XViewerValueColumn {

   public static OperationalImpactWorkaroundDesciptionColumn instance =
      new OperationalImpactWorkaroundDesciptionColumn();

   public static OperationalImpactWorkaroundDesciptionColumn getInstance() {
      return instance;
   }

   private OperationalImpactWorkaroundDesciptionColumn() {
      super("ats.Operational Impact Workaround Description", "Operational Impact Workaround Description", 150,
         SWT.LEFT, false, SortDataType.String, true,
         "What is the workaround for the operational impact to the product.");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public OperationalImpactWorkaroundDesciptionColumn copy() {
      OperationalImpactWorkaroundDesciptionColumn newXCol = new OperationalImpactWorkaroundDesciptionColumn();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      try {
         if (element instanceof TeamWorkFlowArtifact) {
            return ((TeamWorkFlowArtifact) element).getArtifact().getSoleAttributeValue(
               AtsAttributeTypes.OperationalImpactWorkaroundDescription, "");
         }
         if (Artifacts.isOfType(element, AtsArtifactTypes.Action) && ActionManager.getTeams(element).size() == 1) {
            return getColumnText(ActionManager.getFirstTeam(element), column, columnIndex);
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
         return XViewerCells.getCellExceptionString(ex);
      }
      return "";
   }
}
