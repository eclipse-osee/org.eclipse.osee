/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.widgets.xBranch;

import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.nebula.widgets.xviewer.XViewerCells;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerLabelProvider;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

/**
 * @author Jeff C. Phillips
 */
public class XBranchLabelProvider extends XViewerLabelProvider {

   Font font = null;
   private final BranchXViewer branchXViewer;

   public XBranchLabelProvider(BranchXViewer branchXViewer) {
      super(branchXViewer);
      this.branchXViewer = branchXViewer;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn cCol, int columnIndex) throws OseeCoreException {
      String columnText = "";
      try {
         if (element instanceof Branch) {
            columnText = getBranchText((Branch) element, cCol, columnIndex);
         } else if (element instanceof TransactionId) {
            columnText = getTransactionText((TransactionId) element, cCol, columnIndex);
         } else if (element instanceof Collection<?>) {
            columnText = getAggrTransactionList((Collection<?>) element, columnIndex);
         }
      } catch (Exception ex) {
         return XViewerCells.getCellExceptionString(ex);
      }
      return columnText;
   }

   private String getAggrTransactionList(Collection<?> collection, int columnIndex) {
      Object headCursor = collection;
      Object tailCursor = collection;
      String columnText = "";

      while (headCursor instanceof List && !((List<?>) headCursor).isEmpty()) {
         headCursor = ((List<?>) headCursor).get(0);
      }
      while (tailCursor instanceof List && !((List<?>) tailCursor).isEmpty()) {
         List<?> list = (List<?>) tailCursor;
         tailCursor = list.get(list.size() - 1);
      }

      if (headCursor instanceof TransactionId && tailCursor instanceof TransactionId) {
         TransactionId headTransaction = (TransactionId) headCursor;
         TransactionId tailTransaction = (TransactionId) tailCursor;

         if (columnIndex == 0) {
            columnText =
                  String.valueOf(headTransaction.getTransactionNumber() + "..." + tailTransaction.getTransactionNumber());
         } else if (columnIndex == 1) {
            columnText = String.valueOf(headTransaction.getTime());
         }
      } else {
         columnText =
               "Unexpected aggregation of " + headCursor.getClass().getSimpleName() + " and " + tailCursor.getClass().getSimpleName();
      }
      return columnText;
   }

   private String getBranchText(Branch branch, XViewerColumn cCol, int columnIndex) {
      String columnText = "";
      String branchName = "";

      try {
         if (AccessControlManager.isOseeAdmin()) {
            branchName = "(" + branch.getBranchId() + ") " + branch.getBranchName();
         } else {
            branchName = branch.getBranchName();
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }

      if(branch.isArchived()){
         branchName = branchName + " (Archived)";
      }
      
      if (cCol.equals(BranchXViewerFactory.branch_name)) {
         columnText = branchName;
      } else if (cCol.equals(BranchXViewerFactory.time_stamp)) {
         columnText = String.valueOf(branch.getCreationDate());
      } else if (cCol.equals(BranchXViewerFactory.author)) {
         columnText = UserManager.getUserNameById(branch.getAuthorId());
      } else if (cCol.equals(BranchXViewerFactory.comment)) {
         columnText = branch.getCreationComment();
      }
      return columnText;
   }

   private String getTransactionText(TransactionId transaction, XViewerColumn cCol, int columnIndex) {
      String columnText = "";

      if (cCol.equals(BranchXViewerFactory.branch_name)) {
         columnText = String.valueOf(transaction.getTransactionNumber());
      } else if (cCol.equals(BranchXViewerFactory.time_stamp)) {
         columnText = String.valueOf(transaction.getTime());
      } else if (cCol.equals(BranchXViewerFactory.author)) {
         columnText = UserManager.getUserNameById(transaction.getAuthorArtId());
      } else if (cCol.equals(BranchXViewerFactory.comment)) {
         columnText = transaction.getComment();
      }
      return columnText;
   }

   public void dispose() {
      if (font != null) font.dispose();
      font = null;
   }

   public boolean isLabelProperty(Object element, String property) {
      return false;
   }

   public void addListener(ILabelProviderListener listener) {
   }

   public void removeListener(ILabelProviderListener listener) {
   }

   public BranchXViewer getTreeViewer() {
      return branchXViewer;
   }

   @Override
   public Image getColumnImage(Object element, XViewerColumn xCol, int columnIndex) throws OseeCoreException {
      Image returnImage = BranchViewImageHandler.getImage(element, columnIndex);
      return returnImage;
   }
}
