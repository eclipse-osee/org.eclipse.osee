/*
 * Created on Oct 6, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.column;

public class ActivityIdColumn extends WorkPackageColumn {
   public static ActivityIdColumn instance = new ActivityIdColumn();

   public static ActivityIdColumn getInstance() {
      return instance;
   }

   public ActivityIdColumn() {
      super();
      setName("Activity Id");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public ActivityIdColumn copy() {
      ActivityIdColumn newXCol = new ActivityIdColumn();
      super.copy(this, newXCol);
      return newXCol;
   }

}
