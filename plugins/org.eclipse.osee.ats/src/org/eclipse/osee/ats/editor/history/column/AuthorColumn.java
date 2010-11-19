package org.eclipse.osee.ats.editor.history.column;

import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerValueColumn;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.swt.SWT;

public class AuthorColumn extends XViewerValueColumn {
   private static AuthorColumn instance = new AuthorColumn();

   public static AuthorColumn getInstance() {
      return instance;
   }

   public AuthorColumn() {
      super("ats.history.Author", "Author", 150, SWT.LEFT, true, SortDataType.String, false, "");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public AuthorColumn copy() {
      AuthorColumn newXCol = new AuthorColumn();
      copy(this, newXCol);
      return newXCol;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      if (element instanceof Change) {
         return UserManager.getUserNameById(((Change) element).getTxDelta().getEndTx().getAuthor());
      }
      return "";
   }
}
