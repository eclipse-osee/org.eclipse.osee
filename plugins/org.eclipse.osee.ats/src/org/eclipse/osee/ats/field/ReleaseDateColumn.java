/*
 * Created on Oct 27, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.field;

import java.util.Date;
import org.eclipse.osee.ats.artifact.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.artifact.AtsAttributeTypes;
import org.eclipse.osee.ats.world.WorldXViewerFactory;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.swt.SWT;

public class ReleaseDateColumn extends AbstractWorkflowVersionDateColumn {

   public static final IAttributeType ReleaseDate = new AtsAttributeTypes("AAMFEc3+cGcMDOCdmdAA", "Release Date",
      "Date the changes were made available to the users.");
   public static ReleaseDateColumn instance = new ReleaseDateColumn();

   public static ReleaseDateColumn getInstance() {
      return instance;
   }

   private ReleaseDateColumn() {
      super(WorldXViewerFactory.COLUMN_NAMESPACE + ".releaseDate", ReleaseDate, 80, SWT.LEFT, false, SortDataType.Date,
         true);
   }

   public ReleaseDateColumn(IAttributeType attributeType, int width, int align, boolean show, SortDataType sortDataType, boolean multiColumnEditable, String description) {
      super(attributeType, width, align, show, sortDataType, multiColumnEditable, description);
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public ReleaseDateColumn copy() {
      return new ReleaseDateColumn(getAttributeType(), getWidth(), getAlign(), isShow(), getSortDataType(),
         isMultiColumnEditable(), getDescription());
   }

   public static Date getDateFromWorkflow(Object object) throws OseeCoreException {
      return AbstractWorkflowVersionDateColumn.getDateFromWorkflow(ReleaseDate, object);
   }

   public static Date getDateFromTargetedVersion(Object object) throws OseeCoreException {
      return AbstractWorkflowVersionDateColumn.getDateFromTargetedVersion(ReleaseDate, object);
   }

   public static String getDateStrFromWorkflow(AbstractWorkflowArtifact artifact) throws OseeCoreException {
      return AbstractWorkflowVersionDateColumn.getDateStrFromWorkflow(ReleaseDate, artifact);
   }

   public static String getDateStrFromTargetedVersion(AbstractWorkflowArtifact artifact) throws OseeCoreException {
      return AbstractWorkflowVersionDateColumn.getDateStrFromTargetedVersion(ReleaseDate, artifact);
   }

   public static String getDateStr(AbstractWorkflowArtifact artifact) throws OseeCoreException {
      return AbstractWorkflowVersionDateColumn.getDateStr(ReleaseDate, artifact);
   }
}
