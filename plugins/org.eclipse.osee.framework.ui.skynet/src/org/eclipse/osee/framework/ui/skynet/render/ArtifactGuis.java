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
package org.eclipse.osee.framework.ui.skynet.render;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.jdk.core.type.MutableBoolean;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.utility.ConnectionHandler;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.jdbc.JdbcStatement;

public final class ArtifactGuis {

   private static final String OTHER_EDIT_SQL =
      "select txs.mod_type, br.branch_id from osee_attribute att, osee_txs txs, osee_branch br where att.art_id = ? and att.gamma_id = txs.gamma_id and txs.branch_id = br.branch_id and txs.transaction_id <> br.baseline_transaction_id and txs.tx_current <> 0 and  br.branch_id <> ? and br.parent_branch_id = ? and br.branch_type = ?  AND NOT EXISTS (SELECT 1 FROM osee_txs txs1 WHERE txs1.branch_id = br.branch_id AND txs1.transaction_id = br.baseline_transaction_id AND txs1.gamma_id = txs.gamma_id AND txs1.mod_type = txs.mod_type)";
   private static final String ART_DELETED_ON_PARENT =
      "select txs.mod_type, br.branch_id from osee_artifact art, osee_txs txs, osee_branch br where art.art_id = ? and art.gamma_id = txs.gamma_id and txs.branch_id = br.branch_id and br.branch_id = ? and txs.tx_current in (2,3)";

   private static final String EDIT_MESSAGE =
      "%d of the %d artifacts about to be edited have already been modified and/or deleted on the following branches:";
   private static final String DELETE_EDIT_MESSAGE =
      "%d of %d artifacts has been deleted on the following parent branch: ";
   private static final int BRANCH_NAME_LENGTH = 50;

   private ArtifactGuis() {
      // this private empty constructor exists to prevent the default constructor from allowing public construction
   }

   public static boolean checkDeletedOnParent(Collection<Artifact> artifacts) {
      Conditions.checkNotNull(artifacts, "artifacts to check");
      Conditions.checkExpressionFailOnTrue(artifacts.isEmpty(), "Must have at least one artifact for checking");

      StringBuilder message = new StringBuilder();
      Set<String> otherBranches = new HashSet<String>();
      int modifiedCount = artifactsDeleted(artifacts, otherBranches);
      if (modifiedCount > 0) {
         message.append(String.format(DELETE_EDIT_MESSAGE, modifiedCount, artifacts.size()));
         message.append(otherBranches);
         return confirm("Informational", message.toString());
      }
      return true;
   }

   private static int artifactsDeleted(Collection<Artifact> artifacts, Set<String> otherBranches) {
      int modifiedCount = 0;
      for (Artifact artifact : artifacts) {
         if (addBranchWhereArtifactHasBeenDeleted(artifact, otherBranches)) {
            modifiedCount++;
         }
      }
      return modifiedCount;
   }

   private static boolean addBranchWhereArtifactHasBeenDeleted(Artifact artifact, Set<String> otherBranches) {
      boolean wasModified = false;
      // Can only be on other branches if it has already been saved
      if (artifact.isInDb()) {

         JdbcStatement chStmt = ConnectionHandler.getStatement();
         try {
            BranchId parentBranch = BranchManager.getParentBranch(artifact.getBranch());
            chStmt.runPreparedQuery(ART_DELETED_ON_PARENT, artifact, parentBranch);

            while (chStmt.next()) {
               long modifiedOnBranchId = chStmt.getLong("branch_id");
               StringBuilder branches = new StringBuilder();
               branches.append("\n\t");
               branches.append(BranchManager.getBranchToken(modifiedOnBranchId).getShortName(BRANCH_NAME_LENGTH));
               otherBranches.add(branches.toString());
               wasModified = true;
            }
         } finally {
            chStmt.close();
         }
      }
      return wasModified;
   }

   public static boolean checkOtherEdit(Collection<Artifact> artifacts) {
      Conditions.checkNotNull(artifacts, "artifacts to check");
      Conditions.checkExpressionFailOnTrue(artifacts.isEmpty(), "Must have at least one artifact for checking");

      StringBuilder message = new StringBuilder();
      Set<String> otherBranches = new HashSet<>();
      int modifiedCount = artifactsModified(artifacts, otherBranches);
      if (modifiedCount > 0) {
         message.append(String.format(EDIT_MESSAGE, modifiedCount, artifacts.size()));
         message.append(otherBranches);
         return confirm("Confirm Edit", message.toString());
      }
      return true;
   }

   private static int artifactsModified(Collection<Artifact> artifacts, Set<String> otherBranches) {
      int modifiedCount = 0;
      for (Artifact artifact : artifacts) {
         if (addBranchesWhereArtifactHasBeenModified(artifact, otherBranches)) {
            modifiedCount++;
         }
      }
      return modifiedCount;
   }

   private static boolean confirm(final String title, final String message) {
      final MutableBoolean editAllowed = new MutableBoolean(false);
      Displays.pendInDisplayThread(new Runnable() {
         @Override
         public void run() {
            editAllowed.setValue(MessageDialog.openConfirm(Displays.getActiveShell(), title, message));
         }
      });
      return editAllowed.getValue();
   }

   /**
    * Returns non-archived sibling branches that this artifact's attributes have been edited on
    */
   private static boolean addBranchesWhereArtifactHasBeenModified(Artifact artifact, Set<String> otherBranches) {
      boolean wasModified = false;
      // Can only be on other branches if it has already been saved
      if (artifact.isInDb()) {

         JdbcStatement chStmt = ConnectionHandler.getStatement();
         try {
            BranchId branch = artifact.getBranch();
            chStmt.runPreparedQuery(OTHER_EDIT_SQL, artifact, branch, branch, BranchType.WORKING);

            while (chStmt.next()) {
               long modifiedOnBranchId = chStmt.getLong("branch_id");
               chStmt.getInt("mod_type");
               StringBuilder branches = new StringBuilder();
               branches.append("\n\t");
               branches.append(BranchManager.getBranchToken(modifiedOnBranchId).getShortName(BRANCH_NAME_LENGTH));
               otherBranches.add(branches.toString());
               wasModified = true;
            }
         } finally {
            chStmt.close();
         }
      }
      return wasModified;
   }

}