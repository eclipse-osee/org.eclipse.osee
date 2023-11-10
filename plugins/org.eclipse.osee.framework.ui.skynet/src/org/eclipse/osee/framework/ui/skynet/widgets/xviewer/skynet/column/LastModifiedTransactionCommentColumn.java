/*********************************************************************
 * Copyright (c) 2016 Boeing
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

package org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column;

import org.eclipse.nebula.widgets.xviewer.XViewerValueColumn;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.skynet.util.LogUtil;

/**
 * @author Donald G. Dunne
 */
public class LastModifiedTransactionCommentColumn extends XViewerValueColumn {

   public static final String FRAMEWORK_LAST_MOD_TRANS = "framework.lastModTransactionComment";
   public static LastModifiedTransactionCommentColumn instance = new LastModifiedTransactionCommentColumn();

   public static LastModifiedTransactionCommentColumn getInstance() {
      return instance;
   }

   public LastModifiedTransactionCommentColumn() {
      this(false);
   }

   public LastModifiedTransactionCommentColumn(boolean show) {
      super(FRAMEWORK_LAST_MOD_TRANS, "Last Modified Transaction Comment", 100, XViewerAlign.Left, show,
         SortDataType.String, false,
         "Retrieves user of last transaction that modified this artifact and shows the comment.");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public LastModifiedTransactionCommentColumn copy() {
      LastModifiedTransactionCommentColumn newXCol = new LastModifiedTransactionCommentColumn(isShow());
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      String toReturn = "";
      try {
         if (element instanceof Artifact) {
            TransactionId transactionId = ((Artifact) element).getTransaction();
            toReturn = TransactionManager.getTransaction(transactionId).getComment();
         } else if (element instanceof Change) {
            TransactionId transactionId = ((Change) element).getChangeArtifact().getTransaction();
            toReturn = TransactionManager.getTransaction(transactionId).getComment();

         }
      } catch (OseeCoreException ex) {
         return LogUtil.getCellExceptionString(ex);
      }
      return toReturn;
   }

}
