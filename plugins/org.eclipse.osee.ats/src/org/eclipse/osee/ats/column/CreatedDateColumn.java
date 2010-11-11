/*
 * Created on Oct 27, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.column;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.nebula.widgets.xviewer.IXViewerValueColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerCells;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.ats.artifact.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.artifact.ActionArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsColumn;
import org.eclipse.osee.ats.world.WorldXViewerFactory;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.swt.SWT;

public class CreatedDateColumn extends XViewerAtsColumn implements IXViewerValueColumn {

   public static CreatedDateColumn instance = new CreatedDateColumn();

   public static CreatedDateColumn getInstance() {
      return instance;
   }

   private CreatedDateColumn() {
      super(WorldXViewerFactory.COLUMN_NAMESPACE + ".createdDate", "Created Date", 80, SWT.LEFT, true,
         SortDataType.Date, false, "Date this workflow was created.");
   }

   public CreatedDateColumn(String id, String name, int width, int align, boolean show, SortDataType sortDataType, boolean multiColumnEditable, String description) {
      super(id, name, width, align, show, sortDataType, multiColumnEditable, description);
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public CreatedDateColumn copy() {
      return new CreatedDateColumn(getId(), getName(), getWidth(), getAlign(), isShow(), getSortDataType(),
         isMultiColumnEditable(), getDescription());
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      try {
         return getDateStr(element);
      } catch (OseeCoreException ex) {
         XViewerCells.getCellExceptionString(ex);
      }
      return "";
   }

   public static Date getDate(Object object) throws OseeCoreException {
      if (object instanceof ActionArtifact) {
         return getDate(((ActionArtifact) object).getTeamWorkFlowArtifacts().iterator().next());
      } else if (object instanceof AbstractWorkflowArtifact) {
         return ((AbstractWorkflowArtifact) object).getLog().getCreationDate();
      }
      return null;
   }

   public static String getDateStr(Object object) throws OseeCoreException {
      Set<String> strs = new HashSet<String>();
      if (object instanceof ActionArtifact) {
         for (TeamWorkFlowArtifact team : ((ActionArtifact) object).getTeamWorkFlowArtifacts()) {
            Date date = getDate(team);
            if (date == null) {
               strs.add("");
            } else {
               strs.add(DateUtil.getMMDDYYHHMM(getDate(team)));
            }
         }
         return Collections.toString(";", strs);

      }
      return DateUtil.getMMDDYYHHMM(getDate(object));
   }

}
