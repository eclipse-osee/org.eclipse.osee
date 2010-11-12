/*
 * Created on Oct 27, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.column;

import org.eclipse.osee.ats.artifact.AtsAttributeTypes;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsAttributeValueColumn;
import org.eclipse.swt.SWT;

public class UserCommunityColumn extends XViewerAtsAttributeValueColumn {

   public static UserCommunityColumn instance = new UserCommunityColumn();

   public static UserCommunityColumn getInstance() {
      return instance;
   }

   private UserCommunityColumn() {
      super(AtsAttributeTypes.UserCommunity, 60, SWT.LEFT, false, SortDataType.String, false, "");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public UserCommunityColumn copy() {
      UserCommunityColumn newXCol = new UserCommunityColumn();
      copy(this, newXCol);
      return newXCol;
   }

}
