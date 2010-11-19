package org.eclipse.osee.ats.editor.history.column;

import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerValueColumn;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.swt.SWT;

public class TransactionColumn extends XViewerValueColumn {

   private static TransactionColumn instance = new TransactionColumn();

   public static TransactionColumn getInstance() {
      return instance;
   }

   public TransactionColumn() {
      super("ats.history.Transaction", "Transaction", 80, SWT.LEFT, true, SortDataType.Integer, false, "");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public TransactionColumn copy() {
      TransactionColumn newXCol = new TransactionColumn();
      copy(this, newXCol);
      return newXCol;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      if (element instanceof Change) {
         return String.valueOf(((Change) element).getTxDelta().getEndTx().getId());
      }
      return "";
   }
}
