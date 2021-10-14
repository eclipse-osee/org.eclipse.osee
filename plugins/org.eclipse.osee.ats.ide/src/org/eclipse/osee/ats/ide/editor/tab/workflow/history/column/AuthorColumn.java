/*********************************************************************
 * Copyright (c) 2011 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.ide.editor.tab.workflow.history.column;

import java.util.logging.Level;
import org.eclipse.nebula.widgets.xviewer.XViewerValueColumn;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;

/**
 * @author Donald G. Dunne
 */
public class AuthorColumn extends XViewerValueColumn {
   private static AuthorColumn instance = new AuthorColumn();

   public static AuthorColumn getInstance() {
      return instance;
   }

   public AuthorColumn() {
      super("ats.history.Author", "Author", 100, XViewerAlign.Left, true, SortDataType.String, false, "");
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
      String name = "";
      if (element instanceof Change) {
         try {
            TransactionRecord endTx = TransactionManager.getTransaction(((Change) element).getTxDelta().getEndTx());
            return endTx.getAuthor().getName();
         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
            name = "exception " + ex.getLocalizedMessage();
         }
      }
      return name;
   }
}