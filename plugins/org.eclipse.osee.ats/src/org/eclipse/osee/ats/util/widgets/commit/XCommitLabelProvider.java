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
package org.eclipse.osee.ats.util.widgets.commit;

import java.util.logging.Level;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.nebula.widgets.xviewer.XViewerLabelProvider;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.commit.CommitStatus;
import org.eclipse.osee.ats.api.commit.ICommitConfigItem;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workflow.IAtsBranchService;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IOseeBranch;
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

   public XCommitLabelProvider(CommitXManager commitXManager) {
      super(commitXManager);
      this.commitXManager = commitXManager;
   }

   @Override
   public Image getColumnImage(Object element, XViewerColumn xCol, int columnIndex) {
      BranchId branch = null;
      if (element instanceof ICommitConfigItem) {
         ICommitConfigItem configArt = (ICommitConfigItem) element;
         branch = AtsClientService.get().getBranchService().getBranch(configArt);
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
            CommitStatus commitStatus = AtsClientService.get().getBranchService().getCommitStatus(
               commitXManager.getXCommitViewer().getTeamArt(), branch);
            if (commitStatus == CommitStatus.Branch_Not_Configured || commitStatus == CommitStatus.Branch_Commit_Disabled ||
            //
               commitStatus == CommitStatus.Commit_Needed || commitStatus == CommitStatus.Working_Branch_Not_Created) {
               return ImageManager.getImage(FrameworkImage.DOT_RED);
            }

            if (commitStatus == CommitStatus.Merge_In_Progress) {
               return ImageManager.getImage(FrameworkImage.DOT_YELLOW);
            }

            if (commitStatus == CommitStatus.Committed || commitStatus == CommitStatus.Committed_With_Merge || commitStatus == CommitStatus.No_Commit_Needed) {
               return ImageManager.getImage(FrameworkImage.DOT_GREEN);
            }
            return null;
         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      } else if (xCol.equals(CommitXManagerFactory.Merge_Col)) {
         try {
            CommitStatus commitStatus = AtsClientService.get().getBranchService().getCommitStatus(
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
      if (element instanceof ICommitConfigItem) {
         ICommitConfigItem configArt = (ICommitConfigItem) element;
         if (!AtsClientService.get().getBranchService().isBranchValid(configArt)) {
            return String.format("Branch not configured for [%s]", element);
         } else {
            branch = configArt.getBaselineBranchId();
         }
      } else if (element instanceof TransactionToken) {
         TransactionToken txRecord = (TransactionToken) element;
         branch = txRecord.getBranch();
      } else {
         throw new OseeArgumentException("Unhandled element type [%s]", element.getClass().toString());
      }

      if (xCol.equals(CommitXManagerFactory.Status_Col)) {
         return AtsClientService.get().getBranchService().getCommitStatus(
            commitXManager.getXCommitViewer().getTeamArt(), branch).getDisplayName();
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
      if (element instanceof ICommitConfigItem) {
         return ((ICommitConfigItem) element).getName();
      } else {
         return "";
      }
   }

   private String handleArtifactTypeNameColumn(Object element) {
      if (element instanceof ICommitConfigItem) {
         return ((ICommitConfigItem) element).getTypeName();
      } else {
         return "";
      }
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
      IAtsBranchService service = AtsClientService.get().getBranchService();
      IAtsTeamWorkflow teamWf = commitXManager.getXCommitViewer().getTeamArt();
      return service.getCommitTransactionRecord(teamWf, branch);
   }

   private String handleDestBranchColumn(Object element, BranchId branchToken) {
      IOseeBranch branch = BranchManager.getBranchToken(branchToken);
      if (element instanceof IAtsVersion) {
         return branch == null ? "Parent Branch Not Configured for Version [" + element + "]" : branch.getShortName();
      } else if (element instanceof IAtsTeamDefinition) {
         return branch == null ? "Parent Branch Not Configured for Team Definition [" + element + "]" : branch.getShortName();
      } else if (element instanceof TransactionRecord) {
         return branch.getShortName();
      }
      return "";
   }

   private String handleDestBranchCreationDateColumn(Object element, BranchId branch) {
      if (element instanceof IAtsVersion) {
         return getColumnText("Version", element, branch);
      } else if (element instanceof IAtsTeamDefinition) {
         return getColumnText("Team Definition", element, branch);
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
      CommitStatus commitStatus = AtsClientService.get().getBranchService().getCommitStatus(
         commitXManager.getXCommitViewer().getTeamArt(), branch);
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
