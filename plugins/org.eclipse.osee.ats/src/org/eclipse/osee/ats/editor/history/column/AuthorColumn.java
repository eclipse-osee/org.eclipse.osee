/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.editor.history.column;

import java.util.logging.Level;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerValueColumn;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.swt.SWT;

/**
 * @author Donald G. Dunne
 */
public class AuthorColumn extends XViewerValueColumn {
   private static AuthorColumn instance = new AuthorColumn();

   public static AuthorColumn getInstance() {
      return instance;
   }

   public AuthorColumn() {
      super("ats.history.Author", "Author", 100, SWT.LEFT, true, SortDataType.String, false, "");
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
         try {
            return UserManager.getSafeUserNameById(((Change) element).getTxDelta().getEndTx().getAuthor());
         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
      return "";
   }
}
