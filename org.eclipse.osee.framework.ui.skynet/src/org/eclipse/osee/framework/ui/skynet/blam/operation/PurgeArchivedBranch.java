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
package org.eclipse.osee.framework.ui.skynet.blam.operation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.core.data.SystemUser;
import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.results.XResultData;
import org.eclipse.swt.widgets.Display;

/**
 * @author Megumi Telles
 */
public class PurgeArchivedBranch extends AbstractBlam {
   private static final String SELECT_ARCHIVED_BRANCHES =
         "select * from osee_branch where associated_art_id = ? and archived = ? and branch_state IN (?, ?)";

   private static final String SELECT_UNUSUAL_ARCHIVED_BRANCHES =
         "select * from osee_branch where associated_art_id = ? and archived = ? and branch_state NOT IN (?,?,?)";

   private static int systemUserArtId;
   private static boolean purgeBranches = false;

   @Override
   public String getName() {
      return "Purge Archived Branches";
   }

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      purgeBranches = variableMap.getBoolean("Purge Branches");
      Displays.ensureInDisplayThread(new Runnable() {
         public void run() {
            try {
               List<BranchInfo> branches = new ArrayList<BranchInfo>();
               List<BranchInfo> unusualBranches = new ArrayList<BranchInfo>();
               systemUserArtId = UserManager.getUser(SystemUser.OseeSystem).getArtId();
               branches = purgeSelectedBranches();
               if (purgeBranches) {
                  confirmPurgeArchivedBranch(branches);
               }
               unusualBranches = checkUnusualArchivedBranches();
               displayReport(unusualBranches, "Unusual Archived Branches",
                     "List of archived branches with unusual branch states: ");
               displayReport(branches, "Archived Branches", "List of archived branches to be purged: ");
            } catch (Exception ex) {
               OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE, ex);
            }
         }
      });
   }

   private List<BranchInfo> purgeSelectedBranches() throws Exception {
      final List<BranchInfo> branches = new ArrayList<BranchInfo>();
      IOseeStatement chStmt = ConnectionHandler.getStatement();
      try {
         chStmt.runPreparedQuery(SELECT_ARCHIVED_BRANCHES, systemUserArtId, BranchArchivedState.ARCHIVED.getValue(),
               BranchState.REBASELINED.getValue(), BranchState.DELETED.getValue());
         while (chStmt.next()) {
            BranchInfo purgedBranch =
                  new BranchInfo(chStmt.getString("branch_name"), chStmt.getInt("branch_id"),
                        chStmt.getInt("archived"), BranchState.getBranchState(chStmt.getInt("branch_state")).name());
            branches.add(purgedBranch);
         }
      } finally {
         chStmt.close();
      }
      return branches;
   }

   private void confirmPurgeArchivedBranch(List<BranchInfo> branches) throws OseeCoreException {
      if (MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), "Purge Confirmation",
            "Do you wish to purge the archived branches: " + "?")) {
         //only delete archived branches that are not changed managed, rebaselined and deleted 
         for (BranchInfo purgeBranch : branches) {
            try {
               BranchManager.purgeBranch(BranchManager.getBranch(Integer.valueOf(purgeBranch.getId())));
            } catch (OseeArgumentException ex) {
               OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
            }
         }
      }
   }

   private List<BranchInfo> checkUnusualArchivedBranches() throws Exception {
      // check to make sure archived branches are not in states other than Committed, Deleted, Rebaselined
      List<BranchInfo> branches = new ArrayList<BranchInfo>();
      IOseeStatement chStmt = ConnectionHandler.getStatement();
      try {
         chStmt.runPreparedQuery(SELECT_UNUSUAL_ARCHIVED_BRANCHES, systemUserArtId,
               BranchArchivedState.ARCHIVED.getValue(), BranchState.COMMITTED.getValue(),
               BranchState.REBASELINED.getValue(), BranchState.DELETED.getValue());
         while (chStmt.next()) {
            BranchInfo unusualBranch =
                  new BranchInfo(chStmt.getString("branch_name"), chStmt.getInt("branch_id"),
                        chStmt.getInt("archived"), BranchState.getBranchState(chStmt.getInt("branch_state")).name());
            branches.add(unusualBranch);
         }
      } finally {
         chStmt.close();
      }
      return branches;
   }

   private void displayReport(List<BranchInfo> branches, String name, String heading) throws OseeCoreException {
      XResultData rd = new XResultData();
      if (branches.size() > 0) {
         try {
            String[] columnHeaders = new String[] {"Branch Name", "Branch Id", "Archive State", "Branch State"};
            rd.addRaw(AHTML.heading(3, heading));
            rd.addRaw(AHTML.beginMultiColumnTable(100, 1));
            rd.addRaw(AHTML.addHeaderRowMultiColumnTable(columnHeaders));
            for (BranchInfo purgedBranch : branches) {
               rd.addRaw(AHTML.addRowMultiColumnTable(new String[] {purgedBranch.getName(), purgedBranch.getId(),
                     purgedBranch.getArchived(), purgedBranch.getState()}));
            }
            rd.addRaw(AHTML.endMultiColumnTable());

         } finally {
            rd.report(name);
         }
      }
   }

   @Override
   public Collection<String> getCategories() {
      return Arrays.asList("Admin");
   }

   @Override
   public String getXWidgetsXml() {
      return "<xWidgets><XWidget xwidgetType=\"XCheckBox\" displayName=\"Purge Branches\" defaultValue=\"false\" labelAfter=\"true\" horizontalLabel=\"true\" /></xWidgets>";
   }

   @Override
   public String getDescriptionUsage() {
      return "Purge archived branches that are deleted, rebaselined and not changed managed";
   }

   private class BranchInfo {
      private final String name;
      private final int id;
      private final int archived;
      private final String state;

      public BranchInfo(String name, int id, int archived, String state) {
         this.name = name;
         this.id = id;
         this.archived = archived;
         this.state = state;
      }

      public String getName() {
         return name;
      }

      public String getId() {
         return Integer.toString(id);
      }

      public String getArchived() {
         return Integer.toString(archived);
      }

      public String getState() {
         return state;
      }

   }
}