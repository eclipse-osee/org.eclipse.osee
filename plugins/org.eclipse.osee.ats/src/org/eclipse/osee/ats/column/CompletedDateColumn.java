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
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsColumn;
import org.eclipse.osee.ats.world.WorldXViewerFactory;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.swt.SWT;

public class CompletedDateColumn extends XViewerAtsColumn implements IXViewerValueColumn {

   public static CompletedDateColumn instance = new CompletedDateColumn();

   public static CompletedDateColumn getInstance() {
      return instance;
   }

   private CompletedDateColumn() {
      super(WorldXViewerFactory.COLUMN_NAMESPACE + ".completedDate", "Completed Date", 80, SWT.CENTER, false,
         SortDataType.Date, false, null);
   }

   public CompletedDateColumn(String id, String name, int width, int align, boolean show, SortDataType sortDataType, boolean multiColumnEditable, String description) {
      super(id, name, width, align, show, sortDataType, multiColumnEditable, description);
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public CompletedDateColumn copy() {
      return new CompletedDateColumn(getId(), getName(), getWidth(), getAlign(), isShow(), getSortDataType(),
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
         getDate(((ActionArtifact) object).getTeamWorkFlowArtifacts().iterator().next());
      } else if (object instanceof AbstractWorkflowArtifact) {
         AbstractWorkflowArtifact awa = (AbstractWorkflowArtifact) object;
         if (((AbstractWorkflowArtifact) object).isCompleted()) {
            Date date = ((AbstractWorkflowArtifact) object).getCompletedDate();
            if (date == null) {
               OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP,
                  "Completed with no date => " + awa.getHumanReadableId());
            }
            return date;
         }
      }
      return null;
   }

   public static String getDateStr(Object object) throws OseeCoreException {
      if (object instanceof ActionArtifact) {
         Set<String> strs = new HashSet<String>();
         for (TeamWorkFlowArtifact team : ((ActionArtifact) object).getTeamWorkFlowArtifacts()) {
            String str = getDateStr(team);
            if (Strings.isValid(str)) {
               strs.add(str);
            }
         }
         return Collections.toString(";", strs);
      }
      return DateUtil.getMMDDYYHHMM(getDate(object));
   }

}
