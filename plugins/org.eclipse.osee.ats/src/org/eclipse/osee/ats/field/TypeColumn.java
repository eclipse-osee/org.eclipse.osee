/*
 * Created on Oct 27, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.field;

import java.util.logging.Level;
import org.eclipse.nebula.widgets.xviewer.IXViewerValueColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.ats.artifact.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsColumn;
import org.eclipse.osee.ats.world.WorldXViewerFactory;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.ArtifactImageManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;

public class TypeColumn extends XViewerAtsColumn implements IXViewerValueColumn {

   public static TypeColumn instance = new TypeColumn();

   public static TypeColumn getInstance() {
      return instance;
   }

   private TypeColumn() {
      super(WorldXViewerFactory.COLUMN_NAMESPACE + ".type", "Type", 150, SWT.LEFT, true, SortDataType.String, false,
         null);
   }

   public TypeColumn(String id, String name, int width, int align, boolean show, SortDataType sortDataType, boolean multiColumnEditable, String description) {
      super(id, name, width, align, show, sortDataType, multiColumnEditable, description);
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public TypeColumn copy() {
      return new TypeColumn(getId(), getName(), getWidth(), getAlign(), isShow(), getSortDataType(),
         isMultiColumnEditable(), getDescription());
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      if (element instanceof AbstractWorkflowArtifact) {
         return ((AbstractWorkflowArtifact) element).getType();
      } else if (element instanceof Artifact) {
         return ((Artifact) element).getArtifactTypeName();
      }
      return "";
   }

   @Override
   public Image getColumnImage(Object element, XViewerColumn xCol, int columnIndex) {
      try {
         return ArtifactImageManager.getImage((Artifact) element);
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
      return null;
   }

}
