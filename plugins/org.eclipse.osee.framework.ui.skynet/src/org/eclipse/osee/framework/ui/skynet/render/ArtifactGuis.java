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

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.swt.widgets.Display;

public final class ArtifactGuis {

   private ArtifactGuis() {
      // this empty constructor exists to prevent the default constructor from allowing public construction
   }

   private static final String OTHER_EDIT_SQL =
         "select br.branch_id, att.gamma_id, att.attr_id from osee_attribute att, osee_txs txs, osee_branch br where att.art_id = ? and att.gamma_id = txs.gamma_id and txs.branch_id = br.branch_id and txs.transaction_id <> br.baseline_transaction_id and br.branch_id <> ? and br.parent_branch_id = ? and br.archived = ?";

   public static boolean checkOtherEdit(List<Artifact> artifacts) throws OseeCoreException {
      int modifiedCount = 0;
      if (artifacts.isEmpty()) {
         throw new OseeArgumentException("you must pass at least one artifact");
      }

      Set<String> otherBranches = new HashSet<String>();
      for (Artifact artifact : artifacts) {
         modifiedCount += getOtherEdittedBranches(artifact, otherBranches);
      }
      StringBuilder branchesStrb = new StringBuilder();
      for (String branchName : otherBranches) {
         branchesStrb.append("\n\t");
         branchesStrb.append(branchName);
      }

      String message =
            "%d of the %d artifacts about to be edited have already been modified on the following branches:%s\n\nDo you still wish to proceed?";
      if (!otherBranches.isEmpty()) {
         message = String.format(message, modifiedCount, artifacts.size(), branchesStrb.toString());
         return MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), "Confirm Edit", message);
      }
      return true;
   }

   /**
    * Returns non-archived sibling branches that this artifact's attributes have been edited on
    * 
    * @throws OseeCoreException
    */
   private static int getOtherEdittedBranches(Artifact artifact, Set<String> otherBranches) throws OseeCoreException {
      int modifiedCount = 0;
      // Can only be on other branches if it has already been saved
      if (artifact.isInDb()) {

         IOseeStatement chStmt = ConnectionHandler.getStatement();
         try {
            Branch branch = artifact.getBranch();
            chStmt.runPreparedQuery(OTHER_EDIT_SQL, artifact.getArtId(), branch.getId(),
                  branch.getParentBranch().getId(), BranchArchivedState.UNARCHIVED.getValue());

            while (chStmt.next()) {
               if (artifact.getAttributeById(chStmt.getInt("attr_id"), false).getGammaId() != chStmt.getInt("gamma_id")) {
                  otherBranches.add(BranchManager.getBranch(chStmt.getInt("branch_id")).getShortName());
                  modifiedCount++;
               }
            }
         } finally {
            chStmt.close();
         }
      }
      return modifiedCount;
   }
}