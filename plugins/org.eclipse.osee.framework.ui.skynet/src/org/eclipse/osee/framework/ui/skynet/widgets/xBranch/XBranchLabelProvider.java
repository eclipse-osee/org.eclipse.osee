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

import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.nebula.widgets.xviewer.XViewerCells;
import org.eclipse.nebula.widgets.xviewer.XViewerLabelProvider;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IAccessContextId;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.services.CmAccessControl;
import org.eclipse.osee.framework.core.util.OsgiUtil;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.skynet.ArtifactImageManager;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
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
   public String getColumnText(Object element, XViewerColumn cCol, int columnIndex) {
      String columnText = "";
      try {
         if (element instanceof BranchId) {
            columnText = getBranchText((BranchId) element, cCol, columnIndex);
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

   private String getBranchText(BranchId branchId, XViewerColumn cCol, int columnIndex) {
      Branch branch = BranchManager.getBranch(branchId);
      if (cCol.equals(BranchXViewerFactory.branchName)) {
         return branch.getName();
      } else if (cCol.equals(BranchXViewerFactory.archivedState)) {
         return BranchManager.getArchivedStr(branch);
      } else if (cCol.equals(BranchXViewerFactory.timeStamp)) {
         try {
            String date = "";
            if (branch.getBaseTransaction() != null) {
               date = DATE_FORMAT.format(branch.getBaseTransaction().getTimeStamp());
            }
            return date;
         } catch (OseeCoreException ex) {
            return XViewerCells.getCellExceptionString(ex);
         }
      } else if (cCol.equals(BranchXViewerFactory.author)) {
         String userName = "";
         try {
            if (branch.getBaseTransaction() != null) {
               userName = UserManager.getSafeUserNameById(branch.getBaseTransaction().getAuthor());
            }
            return userName;
         } catch (OseeCoreException ex) {
            return XViewerCells.getCellExceptionString(ex);
         }
      } else if (cCol.equals(BranchXViewerFactory.comment)) {
         try {
            String branchComment = "";
            if (branch.getBaseTransaction() != null) {
               branchComment = branch.getBaseTransaction().getComment();
            }
            return branchComment;
         } catch (OseeCoreException ex) {
            return XViewerCells.getCellExceptionString(ex);
         }
      } else if (cCol.equals(BranchXViewerFactory.associatedArtifact)) {
         try {
            return BranchManager.getAssociatedArtifact(branch).getName();
         } catch (OseeCoreException ex) {
            return XViewerCells.getCellExceptionString(ex);
         }
      } else if (cCol.equals(BranchXViewerFactory.branchState)) {
         return BranchManager.getState(branch).getName();
      } else if (cCol.equals(BranchXViewerFactory.branchType)) {
         return BranchManager.getType(branch).getName();
      } else if (cCol.equals(BranchXViewerFactory.branchId)) {
         return branch.getIdString();
      } else if (cCol.equals(BranchXViewerFactory.branchId)) {
         return branch.getIdString();
      } else if (cCol.equals(BranchXViewerFactory.parentBranch)) {
         try {
            return branch.equals(CoreBranches.SYSTEM_ROOT) ? "none" : BranchManager.getBranchName(
               BranchManager.getParentBranch(branch));
         } catch (OseeCoreException ex) {
            return XViewerCells.getCellExceptionString(ex);
         }
      } else if (cCol.equals(BranchXViewerFactory.inheritAccessControl)) {
         return String.valueOf(branch.isInheritAccessControl());
      } else if (cCol.equals(BranchXViewerFactory.branchAccessContextId)) {
         return getBranchAccessContextId(branch);
      }
      return "";
   }

   private String getBranchAccessContextId(Branch branch) {
      String accessContextId = "";
      try {
         CmAccessControl cmAccessControl = OsgiUtil.getService(getClass(), CmAccessControl.class);
         Collection<? extends IAccessContextId> ids = cmAccessControl.getContextId(UserManager.getUser(), branch);
         accessContextId = ids.toString();
      } catch (Exception ex) {
         return String.format("Exception %s", ex.getLocalizedMessage());
      }
      return accessContextId;
   }

   protected String getTransactionText(TransactionRecord transaction, XViewerColumn cCol, int columnIndex) {
      String columnText = "";

      if (cCol.equals(BranchXViewerFactory.branchName) || cCol.equals(BranchXViewerFactory.transaction)) {
         columnText = String.valueOf(transaction.getId());
      }
      if (cCol.equals(BranchXViewerFactory.timeStamp)) {
         columnText = DATE_FORMAT.format(transaction.getTimeStamp());
      } else if (cCol.equals(BranchXViewerFactory.author)) {
         try {
            columnText = UserManager.getSafeUserNameById(transaction.getAuthor());
         } catch (Exception ex) {
            return XViewerCells.getCellExceptionString(ex);
         }
      } else if (cCol.equals(BranchXViewerFactory.comment)) {
         columnText = transaction.getComment();
      } else if (cCol.equals(BranchXViewerFactory.associatedArtifact)) {
         try {
            if (transaction.getCommit() == 0) {
               return "";
            }
            Artifact art = ArtifactQuery.getArtifactFromId(transaction.getCommit(), COMMON);
            if (art != null) {
               columnText = art.getName();
            }
         } catch (OseeCoreException ex) {
            return XViewerCells.getCellExceptionString(ex);
         }
      }
      return columnText;
   }

   @Override
   public boolean isLabelProperty(Object element, String property) {
      return false;
   }

   @Override
   public void addListener(ILabelProviderListener listener) {
      // do nothing
   }

   @Override
   public void removeListener(ILabelProviderListener listener) {
      // do nothing
   }

   public BranchXViewer getTreeViewer() {
      return branchXViewer;
   }

   @Override
   public Image getColumnImage(Object element, XViewerColumn xCol, int columnIndex) {
      if (xCol.equals(BranchXViewerFactory.associatedArtifact)) {
         if (element instanceof BranchId) {
            try {
               Artifact associatedArtifact = BranchManager.getAssociatedArtifact((BranchId) element);
               if (associatedArtifact.notEqual(Artifact.SENTINEL)) {
                  return ArtifactImageManager.getImage(associatedArtifact);
               }
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         } else if (element instanceof TransactionRecord) {
            try {
               if (((TransactionRecord) element).getCommit() == 0) {
                  return null;
               }
               Artifact artifact = ArtifactQuery.getArtifactFromId(((TransactionRecord) element).getCommit(), COMMON);
               if (artifact != null) {
                  return ArtifactImageManager.getImage(artifact);
               }
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }
      }
      Image returnImage = BranchViewImageHandler.getImage(element, columnIndex);
      return returnImage;
   }

   @Override
   public Object getBackingData(Object element, XViewerColumn xCol, int columnIndex) throws Exception {
      if (xCol.getId().equals(BranchXViewerFactory.timeStamp.getId())) {
         if (element instanceof BranchId) {
            TransactionRecord tx = BranchManager.getBaseTransaction((BranchId) element);
            Date date = null;
            if (tx != null) {
               date = tx.getTimeStamp();
            }
            return date;
         }
      }
      return super.getBackingData(element, xCol, columnIndex);
   }

   @Override
   public void dispose() {
      // do nothing
   }
}
