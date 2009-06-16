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
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.swt.graphics.Image;

/**
 * @author Jeff C. Phillips
 */
public class XBranchLabelProvider extends XViewerLabelProvider {

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
      if (cCol.equals(BranchXViewerFactory.branch_name)) {
         StringBuilder stringBuilder = new StringBuilder();
         try {
            if (AccessControlManager.isOseeAdmin()) {
               stringBuilder.append("(" + branch.getBranchId() + ") " + branch.getBranchName());
            } else {
               stringBuilder.append(branch.getBranchName());
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
         }
         if (branch.isArchived()) {
            stringBuilder.insert(0, "[Archived] - ");
         }
         return stringBuilder.toString();
      } else if (cCol.equals(BranchXViewerFactory.time_stamp)) {
         return String.valueOf(branch.getCreationDate());
      } else if (cCol.equals(BranchXViewerFactory.author)) {
         return UserManager.getUserNameById(branch.getAuthorId());
      } else if (cCol.equals(BranchXViewerFactory.comment)) {
         return branch.getCreationComment();
      } else if (cCol.equals(BranchXViewerFactory.associatedArtifact)) {
         try {
            if (branch.getAssociatedArtifact() != null) {
               return branch.getAssociatedArtifact().getDescriptiveName();
            }
         } catch (OseeCoreException ex) {
            return XViewerCells.getCellExceptionString(ex);
         }
      } else if (cCol.equals(BranchXViewerFactory.branchState)) {
         return branch.getBranchState().name();
      } else if (cCol.equals(BranchXViewerFactory.branchType)) {
         return branch.getBranchType().name();
      }
      return "";
   }

   private String getTransactionText(TransactionId transaction, XViewerColumn cCol, int columnIndex) {
      String columnText = "";

      if (cCol.equals(BranchXViewerFactory.branch_name)) {
         columnText = String.valueOf(transaction.getTransactionNumber());
      }
      if (cCol.equals(BranchXViewerFactory.time_stamp)) {
         columnText = String.valueOf(transaction.getTime());
      } else if (cCol.equals(BranchXViewerFactory.author)) {
         columnText = UserManager.getUserNameById(transaction.getAuthorArtId());
      } else if (cCol.equals(BranchXViewerFactory.comment)) {
         columnText = transaction.getComment();
      } else if (cCol.equals(BranchXViewerFactory.associatedArtifact)) {
         try {
            if (transaction.getCommitArtId() == 0) return "";
            Artifact art =
                  ArtifactQuery.getArtifactFromId(transaction.getCommitArtId(), BranchManager.getCommonBranch());
            if (art != null) {
               columnText = art.getDescriptiveName();
            }
         } catch (OseeCoreException ex) {
            return XViewerCells.getCellExceptionString(ex);
         }
      }
      return columnText;
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
      if (xCol.equals(BranchXViewerFactory.associatedArtifact)) {
         if (element instanceof Branch) {
            try {
               if (((Branch) element).getAssociatedArtifact() != null) {
                  return ImageManager.getImage(((Branch) element).getAssociatedArtifact());
               }
            } catch (OseeCoreException ex) {
               OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
            }
         } else if (element instanceof TransactionId) {
            try {
               if (((TransactionId) element).getCommitArtId() == 0) return null;
               Artifact artifact =
                     ArtifactQuery.getArtifactFromId(((TransactionId) element).getCommitArtId(),
                           BranchManager.getCommonBranch());
               if (artifact != null) {
                  return ImageManager.getImage(artifact);
               }
            } catch (OseeCoreException ex) {
               OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
            }
         }
      }
      Image returnImage = BranchViewImageHandler.getImage(element, columnIndex);
      return returnImage;
   }

   /* (non-Javadoc)
    * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
    */
   @Override
   public void dispose() {
   }
}
