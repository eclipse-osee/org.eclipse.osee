/*
 * Created on Oct 27, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.column;

import org.eclipse.osee.ats.artifact.AtsAttributeTypes;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsAttributeValueColumn;
import org.eclipse.osee.ats.world.WorldXViewerFactory;
import org.eclipse.swt.SWT;

public class NotesColumn extends XViewerAtsAttributeValueColumn {

   public static NotesColumn instance = new NotesColumn();

   public static NotesColumn getInstance() {
      return instance;
   }

   private NotesColumn() {
      super(AtsAttributeTypes.SmaNote, WorldXViewerFactory.COLUMN_NAMESPACE + ".notes", "Notes", 80, SWT.LEFT, true,
         SortDataType.String, true, "");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public NotesColumn copy() {
      NotesColumn newXCol = new NotesColumn();
      copy(this, newXCol);
      return newXCol;
   }

   @Override
   public boolean isMultiLineStringAttribute() {
      return true;
   }

}
