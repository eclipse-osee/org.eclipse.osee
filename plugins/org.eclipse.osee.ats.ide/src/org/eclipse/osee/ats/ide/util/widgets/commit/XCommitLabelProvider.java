/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.ats.ide.util.widgets.commit;

import java.util.logging.Level;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.nebula.widgets.xviewer.XViewerLabelProvider;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.commit.CommitConfigItem;
import org.eclipse.osee.ats.api.commit.CommitOverride;
import org.eclipse.osee.ats.api.commit.CommitStatus;
import org.eclipse.osee.ats.api.workflow.IAtsBranchService;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.graphics.Image;

/**
 * @author Donald G. Dunne
 */
public class XCommitLabelProvider extends XViewerLabelProvider {

   private final CommitXManager commitXManager;
   private final IAtsTeamWorkflow teamWf;

   public XCommitLabelProvider(CommitXManager commitXManager, IAtsTeamWorkflow teamWf) {
      super(commitXManager);
      this.commitXManager = commitXManager;
      this.teamWf = teamWf;
   }

   @Override
   public Image getColumnImage(Object element, XViewerColumn xCol, int columnIndex) {
      BranchId branch = null;
      if (element instanceof CommitConfigItem) {
         CommitConfigItem configItem = (CommitConfigItem) element;
         branch = AtsApiService.get().getBranchService().getBranch(configItem);
      } else if (element instanceof TransactionToken) {
         TransactionToken txRecord = (TransactionToken) element;
         branch = txRecord.getBranch();
      } else {
         throw new OseeArgumentException("Unhandled element type [%s]", element.getClass().toString());
      }

      if (xCol.equals(CommitXManagerFactory.Action_Col)) {
         return ImageManager.getImage(FrameworkImage.ARROW_RIGHT_YELLOW);
      }
      if (branch == null) {
         return null;
      }
      if (xCol.equals(CommitXManagerFactory.Status_Col)) {
         try {
            CommitStatus commitStatus = AtsApiService.get().getBranchService().getCommitStatus(
               commitXManager.getXCommitViewer().getTeamArt(), branch);
            if (commitStatus == CommitStatus.Branch_Not_Configured || commitStatus == CommitStatus.Branch_Commit_Disabled ||
            //
               commitStatus == CommitStatus.Commit_Needed || commitStatus == CommitStatus.Working_Branch_Not_Created) {
               return ImageManager.getImage(FrameworkImage.DOT_RED);
            }

            if (commitStatus == CommitStatus.Merge_In_Progress) {
               return ImageManager.getImage(FrameworkImage.DOT_YELLOW);
            }

            if (commitStatus == CommitStatus.Committed || commitStatus == CommitStatus.Committed_With_Merge || commitStatus == CommitStatus.No_Commit_Needed || commitStatus == CommitStatus.Commit_Overridden) {
               return ImageManager.getImage(FrameworkImage.DOT_GREEN);
            }
            return null;
         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      } else if (xCol.equals(CommitXManagerFactory.Merge_Col)) {
         try {
            CommitStatus commitStatus = AtsApiService.get().getBranchService().getCommitStatus(
               commitXManager.getXCommitViewer().getTeamArt(), branch);
            if (commitStatus == CommitStatus.Merge_In_Progress || commitStatus == CommitStatus.Committed_With_Merge) {
               return ImageManager.getImage(FrameworkImage.OUTGOING_MERGED);
            }
            return null;
         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
      return null;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn xCol, int columnIndex) {
      BranchId branch;
      if (element instanceof CommitConfigItem) {
         CommitConfigItem configItem = (CommitConfigItem) element;
         if (!AtsApiService.get().getBranchService().isBranchValid(configItem)) {
            return String.format("Branch not configured for [%s]", element);
         } else {
            branch = configItem.getBaselineBranchId();
         }
      } else if (element instanceof TransactionToken) {
         TransactionToken txRecord = (TransactionToken) element;
         branch = txRecord.getBranch();
      } else {
         throw new OseeArgumentException("Unhandled element type [%s]", element.getClass().toString());
      }

      if (xCol.equals(CommitXManagerFactory.Status_Col)) {
         CommitStatus status = AtsApiService.get().getBranchService().getCommitStatus(
            commitXManager.getXCommitViewer().getTeamArt(), branch);
         if (status == CommitStatus.Commit_Overridden) {
            CommitOverride override =
               AtsApiService.get().getBranchService().getCommitOverrideOps().getCommitOverride(teamWf, branch);

            String userName = AtsApiService.get().getUserService().getUserById(override.getUser()).getName();
            return String.format("%s by %s - Reason: [%s]", status.getDisplayName(), userName, override.getReason());
         }
         return status.getDisplayName();
      } else if (xCol.equals(CommitXManagerFactory.Merge_Col)) {
         return "";
      } else if (xCol.equals(CommitXManagerFactory.Version_Col)) {
         return handleVersionColumn(element);
      } else if (xCol.equals(CommitXManagerFactory.Configuring_Object_Col)) {
         return handleArtifactTypeNameColumn(element);
      } else if (xCol.equals(CommitXManagerFactory.Commit_Date)) {
         return handleCommitDateColumn(branch);
      } else if (xCol.equals(CommitXManagerFactory.Commit_Comment)) {
         return handleCommitCommentColumn(branch);
      } else if (xCol.equals(CommitXManagerFactory.Dest_Branch_Col)) {
         return handleDestBranchColumn(element, branch);
      } else if (xCol.equals(CommitXManagerFactory.Dest_Branch_Create_Date_Col)) {
         return handleDestBranchCreationDateColumn(element, branch);
      } else if (xCol.equals(CommitXManagerFactory.Action_Col)) {
         return handleActionColumn(branch);
      }
      return "unhandled column";
   }

   private String handleVersionColumn(Object element) {
      if (element instanceof CommitConfigItem) {
         return ((CommitConfigItem) element).getConfigObject().getName();
      } else {
         return "";
      }
   }

   private String handleArtifactTypeNameColumn(Object element) {
      if (element instanceof CommitConfigItem) {
         return ((CommitConfigItem) element).getName();
      }
      return "";
   }

   private String handleCommitDateColumn(BranchId branch) {
      TransactionRecord transactionRecord = getTransactionRecord(branch);
      if (transactionRecord.isValid()) {
         new DateUtil();
         return DateUtil.getMMDDYYHHMM(transactionRecord.getTimeStamp());
      }
      return "Not Committed";
   }

   private String handleCommitCommentColumn(BranchId branch) {
      TransactionRecord transactionRecord = getTransactionRecord(branch);
      if (transactionRecord.isValid()) {
         return transactionRecord.getComment();
      }
      return "Not Committed";
   }

   private TransactionRecord getTransactionRecord(BranchId branch) {
      IAtsBranchService service = AtsApiService.get().getBranchService();
      IAtsTeamWorkflow teamWf = commitXManager.getXCommitViewer().getTeamArt();
      return service.getCommitTransactionRecord(teamWf, branch);
   }

   private String handleDestBranchColumn(Object element, BranchId branchToken) {
      BranchToken branch = BranchManager.getBranchToken(branchToken);
      if (element instanceof CommitConfigItem) {
         CommitConfigItem configItem = (CommitConfigItem) element;
         return branch == null ? String.format("Parent Branch Not Configured for %s [%s]",
            configItem.getConfigObject().getArtifactType(), configItem.getConfigObject()) : branch.getShortName();
      } else if (element instanceof TransactionRecord) {
         return branch.getShortName();
      }
      return "";
   }

   private String handleDestBranchCreationDateColumn(Object element, BranchId branch) {
      if (element instanceof CommitConfigItem) {
         CommitConfigItem configItem = (CommitConfigItem) element;
         String configType = configItem.getConfigObject().getArtifactType().getName();
         return getColumnText(configType, element, branch);
      } else if (element instanceof TransactionRecord) {
         return getColumnText(null, element, branch);
      }
      return "";
   }

   private String getColumnText(String elementType, Object element, BranchId branch) {
      if (branch == null) {
         return "Parent Branch Not Configured for " + elementType + " [" + element + "]";
      } else {
         return DateUtil.getMMDDYYHHMM(BranchManager.getBaseTransaction(branch).getTimeStamp());
      }
   }

   private String handleActionColumn(BranchId branch) {
      CommitStatus commitStatus =
         AtsApiService.get().getBranchService().getCommitStatus(commitXManager.getXCommitViewer().getTeamArt(), branch);
      if (commitStatus == CommitStatus.Rebaseline_In_Progress) {
         return "Finish Update";
      } else if (commitStatus == CommitStatus.Branch_Not_Configured) {
         return "Configure Branch";
      } else if (commitStatus == CommitStatus.Branch_Commit_Disabled) {
         return "Enable Branch Commit";
      } else if (commitStatus == CommitStatus.Commit_Needed) {
         return "Start Commit";
      } else if (commitStatus == CommitStatus.No_Commit_Needed) {
         return CommitStatus.No_Commit_Needed.getDisplayName();
      } else if (commitStatus == CommitStatus.Merge_In_Progress) {
         return "Merge Conflicts and Commit";
      } else if (commitStatus == CommitStatus.Committed) {
         return "Show Change Report";
      } else if (commitStatus == CommitStatus.Committed_With_Merge) {
         return "Show Change/Merge Report";
      } else if (commitStatus == CommitStatus.Working_Branch_Not_Created) {
         return "Working Branch Not Created";
      } else if (commitStatus == CommitStatus.Commit_Overridden) {
         return "None";
      }
      return "Error: Need to handle this";
   }

   @Override
   public void dispose() {
      // do nothing
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
}