package org.eclipse.osee.ats.editor.history.column;

import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerValueColumn;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.swt.SWT;

public class DateColumn extends XViewerValueColumn {

   private static DateColumn instance = new DateColumn();

   public static DateColumn getInstance() {
      return instance;
   }

   public DateColumn() {
      super("ats.history.Date", "Date", 120, SWT.LEFT, true, SortDataType.String, false, "");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public DateColumn copy() {
      DateColumn newXCol = new DateColumn();
      copy(this, newXCol);
      return newXCol;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      if (element instanceof Change) {
         return DateUtil.getMMDDYYHHMM(((Change) element).getTxDelta().getEndTx().getTimeStamp());
      }
      return "";
   }
}
