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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.nebula.widgets.xviewer.XViewerCells;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerLabelProvider;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.swt.graphics.Image;

/**
 * @author Jeff C. Phillips
 */
public class XBranchLabelProvider extends XViewerLabelProvider {
   private final static DateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
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
         } else if (element instanceof TransactionRecord) {
            columnText = getTransactionText((TransactionRecord) element, cCol, columnIndex);
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

      while (headCursor instanceof List<?> && !((List<?>) headCursor).isEmpty()) {
         headCursor = ((List<?>) headCursor).get(0);
      }
      while (tailCursor instanceof List<?> && !((List<?>) tailCursor).isEmpty()) {
         List<?> list = (List<?>) tailCursor;
         tailCursor = list.get(list.size() - 1);
      }

      if (headCursor instanceof TransactionRecord && tailCursor instanceof TransactionRecord) {
         TransactionRecord headTransaction = (TransactionRecord) headCursor;
         TransactionRecord tailTransaction = (TransactionRecord) tailCursor;

         if (columnIndex == 0) {
            columnText = String.valueOf(headTransaction.getId() + "..." + tailTransaction.getId());
         } else if (columnIndex == 1) {
            columnText = DATE_FORMAT.format(headTransaction.getTimeStamp());
         }
      } else {
         columnText =
               "Unexpected aggregation of " + headCursor.getClass().getSimpleName() + " and " + tailCursor.getClass().getSimpleName();
      }
      return columnText;
   }

   private String getBranchText(Branch branch, XViewerColumn cCol, int columnIndex) {
      if (cCol.equals(BranchXViewerFactory.branchName)) {
         return branch.getName();
      } else if (cCol.equals(BranchXViewerFactory.archivedState)) {
         return branch.getArchiveState().toString();
      } else if (cCol.equals(BranchXViewerFactory.timeStamp)) {
         try {
            return DATE_FORMAT.format(branch.getBaseTransaction().getTimeStamp());
         } catch (OseeCoreException ex) {
            return XViewerCells.getCellExceptionString(ex);
         }
      } else if (cCol.equals(BranchXViewerFactory.author)) {
         try {
            return UserManager.getUserNameById(branch.getBaseTransaction().getAuthor());
         } catch (OseeCoreException ex) {
            return XViewerCells.getCellExceptionString(ex);
         }
      } else if (cCol.equals(BranchXViewerFactory.comment)) {
         try {
            return branch.getBaseTransaction().getComment();
         } catch (OseeCoreException ex) {
            return XViewerCells.getCellExceptionString(ex);
         }
      } else if (cCol.equals(BranchXViewerFactory.associatedArtifact)) {
         try {
            if (branch.getAssociatedArtifact() != null) {
               return branch.getAssociatedArtifact().getName();
            }
         } catch (OseeCoreException ex) {
            return XViewerCells.getCellExceptionString(ex);
         }
      } else if (cCol.equals(BranchXViewerFactory.branchState)) {
         return branch.getBranchState().name();
      } else if (cCol.equals(BranchXViewerFactory.branchType)) {
         return branch.getBranchType().name();
      } else if (cCol.equals(BranchXViewerFactory.branchId)) {
         return String.valueOf(branch.getId());
      } else if (cCol.equals(BranchXViewerFactory.branchGuid)) {
         return String.valueOf(branch.getGuid());
      } else if (cCol.equals(BranchXViewerFactory.parentBranch)) {
         try {
            return branch.hasParentBranch() ? branch.getParentBranch().getName() : "none";
         } catch (OseeCoreException ex) {
            return XViewerCells.getCellExceptionString(ex);
         }
      }
      return "";
   }

   private String getTransactionText(TransactionRecord transaction, XViewerColumn cCol, int columnIndex) {
      String columnText = "";

      if (cCol.equals(BranchXViewerFactory.branchName)) {
         columnText = String.valueOf(transaction.getId());
      }
      if (cCol.equals(BranchXViewerFactory.timeStamp)) {
         columnText = DATE_FORMAT.format(transaction.getTimeStamp());
      } else if (cCol.equals(BranchXViewerFactory.author)) {
         columnText = UserManager.getUserNameById(transaction.getAuthor());
      } else if (cCol.equals(BranchXViewerFactory.comment)) {
         columnText = transaction.getComment();
      } else if (cCol.equals(BranchXViewerFactory.associatedArtifact)) {
         try {
            if (transaction.getCommit() == 0) {
               return "";
            }
            Artifact art = ArtifactQuery.getArtifactFromId(transaction.getCommit(), BranchManager.getCommonBranch());
            if (art != null) {
               columnText = art.getName();
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
                  return ImageManager.getImage((Artifact) ((Branch) element).getAssociatedArtifact());
               }
            } catch (OseeCoreException ex) {
               OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
            }
         } else if (element instanceof TransactionRecord) {
            try {
               if (((TransactionRecord) element).getCommit() == 0) {
                  return null;
               }
               Artifact artifact =
                     ArtifactQuery.getArtifactFromId(((TransactionRecord) element).getCommit(),
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

   @Override
   public void dispose() {
   }
}
