/*
 * Created on Jul 14, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.util.xviewer.column;

import java.sql.SQLException;
import org.eclipse.osee.ats.artifact.ReviewSMArtifact;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.skynet.widgets.XDate;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewer;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerValueColumn;
import org.eclipse.swt.SWT;

/**
 * @author Donald G. Dunne
 */
public class XViewerReviewCompletionDateColumn extends XViewerValueColumn {

   public XViewerReviewCompletionDateColumn(XViewer viewer, int columnNum) {
      super(viewer, "Completion Date", "", 150, 150, SWT.LEFT);
      setOrderNum(columnNum);
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column) throws OseeCoreException, SQLException {
      if (element instanceof ReviewSMArtifact) {
         return XDate.getDateStr(((ReviewSMArtifact) element).getWorldViewCompletedDate(), XDate.MMDDYY);
      }
      return "";
   }

}
