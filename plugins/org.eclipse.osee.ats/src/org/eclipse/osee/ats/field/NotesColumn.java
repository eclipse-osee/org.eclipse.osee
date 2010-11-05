/*
 * Created on Oct 27, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.field;

import org.eclipse.nebula.widgets.xviewer.IMultiColumnEditProvider;
import org.eclipse.osee.ats.artifact.AtsAttributeTypes;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsAttributeValueColumn;
import org.eclipse.osee.ats.world.WorldXViewerFactory;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.swt.SWT;

public class NotesColumn extends XViewerAtsAttributeValueColumn implements IMultiColumnEditProvider {

   public static final IAttributeType SmaNote = new AtsAttributeTypes("AAMFEdm7ywte8qayfbAA", "SMA Note",
      "Notes applicable to ATS object");
   public static NotesColumn instance = new NotesColumn();

   public static NotesColumn getInstance() {
      return instance;
   }

   private NotesColumn() {
      super(WorldXViewerFactory.COLUMN_NAMESPACE + ".notes", SmaNote, "Notes", 80, SWT.LEFT, true, SortDataType.String,
         true);
   }

   public NotesColumn(String id, IAttributeType attributeType, String name, int width, int align, boolean show, SortDataType sortDataType, boolean multiColumnEditable, String description) {
      super(id, attributeType, name, width, align, show, sortDataType, multiColumnEditable);
      setDescription(description);
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public NotesColumn copy() {
      return new NotesColumn(getId(), getAttributeType(), getName(), getWidth(), getAlign(), isShow(),
         getSortDataType(), isMultiColumnEditable(), getDescription());
   }

   @Override
   public boolean isMultiLineStringAttribute() {
      return true;
   }

}
