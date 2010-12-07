/*
 * Created on Oct 27, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.column;

import org.eclipse.nebula.widgets.xviewer.XViewerCells;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.ats.artifact.AtsAttributeTypes;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsAttributeValueColumn;
import org.eclipse.osee.ats.world.WorldXViewerFactory;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.swt.SWT;

public class GoalOrderVoteColumn extends XViewerAtsAttributeValueColumn {

   public static GoalOrderVoteColumn instance = new GoalOrderVoteColumn();

   public static GoalOrderVoteColumn getInstance() {
      return instance;
   }

   private GoalOrderVoteColumn() {
      super(AtsAttributeTypes.GoalOrderVote, WorldXViewerFactory.COLUMN_NAMESPACE + ".goalOrderVote",
         AtsAttributeTypes.GoalOrderVote.getUnqualifiedName(), 40, SWT.LEFT, false, SortDataType.String, true, "");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public GoalOrderVoteColumn copy() {
      GoalOrderVoteColumn newXCol = new GoalOrderVoteColumn();
      copy(this, newXCol);
      return newXCol;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      try {
         if (element instanceof Artifact) {
            return ((Artifact) element).getSoleAttributeValue(AtsAttributeTypes.GoalOrderVote, "");
         }
      } catch (Exception ex) {
         return XViewerCells.getCellExceptionString(ex);
      }
      return "";
   }

}
