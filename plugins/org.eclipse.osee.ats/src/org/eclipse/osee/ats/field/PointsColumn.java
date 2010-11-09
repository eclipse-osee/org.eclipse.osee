/*
 * Created on Oct 27, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.field;

import org.eclipse.osee.ats.artifact.AtsAttributeTypes;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsAttributeValueColumn;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.swt.SWT;

public class PointsColumn extends XViewerAtsAttributeValueColumn {

   public static PointsColumn instance = new PointsColumn();

   public static PointsColumn getInstance() {
      return instance;
   }

   private PointsColumn() {
      super(AtsAttributeTypes.Points, 40, SWT.LEFT, false, SortDataType.Integer, true);
   }

   public PointsColumn(IAttributeType attributeType, int width, int align, boolean show, SortDataType sortDataType, boolean multiColumnEditable, String description) {
      super(attributeType, width, align, show, sortDataType, multiColumnEditable);
      setDescription(description);
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public PointsColumn copy() {
      return new PointsColumn(getAttributeType(), getWidth(), getAlign(), isShow(), getSortDataType(),
         isMultiColumnEditable(), getDescription());
   }

   public static String getPoints(TeamWorkFlowArtifact teamArt) throws OseeCoreException {
      return teamArt.getSoleAttributeValue(AtsAttributeTypes.Points, "");
   }

}
