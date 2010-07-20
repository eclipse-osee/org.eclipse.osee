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
import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.jdk.core.type.MutableBoolean;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.widgets.Display;

public final class ArtifactGuis {

   private ArtifactGuis() {
      // this private empty constructor exists to prevent the default constructor from allowing public construction
   }

   private static final String OTHER_EDIT_SQL =
      "select br.branch_id, att.gamma_id, att.attr_id from osee_attribute att, osee_txs txs, osee_branch br where att.art_id = ? and att.gamma_id = txs.gamma_id and txs.branch_id = br.branch_id and txs.transaction_id <> br.baseline_transaction_id and br.branch_id <> ? and br.parent_branch_id = ? and br.archived = ?";

   private static final String EDIT_MESSAGE =
      "%d of the %d artifacts about to be edited have already been modified on the following branches:%s\n\nDo you still wish to proceed?";

   public static boolean checkOtherEdit(Collection<Artifact> artifacts) throws OseeCoreException {
      Conditions.checkNotNull(artifacts, "artifacts to check");
      Conditions.checkExpressionFailOnTrue(artifacts.isEmpty(), "Must have at least one artifact for checking");

      int modifiedCount = 0;
      Set<String> otherBranches = new HashSet<String>();
      for (Artifact artifact : artifacts) {
         boolean wasModified = addBranchesWhereArtifactHasBeenModified(artifact, otherBranches);
         if (wasModified) {
            modifiedCount++;
         }
      }

      if (modifiedCount > 0) {
         StringBuilder branchMessage = new StringBuilder();
         for (String branchName : otherBranches) {
            branchMessage.append("\n\t");
            branchMessage.append(branchName);
         }

         String message = String.format(EDIT_MESSAGE, modifiedCount, artifacts.size(), branchMessage);
         return confirmEdit(message);
      }
      return true;
   }

   private static boolean confirmEdit(final String message) {
      final MutableBoolean editAllowed = new MutableBoolean(false);
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            editAllowed.setValue(MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), "Confirm Edit",
               message));
         }
      }, true);
      return editAllowed.getValue();
   }

   /**
    * Returns non-archived sibling branches that this artifact's attributes have been edited on
    * 
    * @throws OseeCoreException
    */
   private static boolean addBranchesWhereArtifactHasBeenModified(Artifact artifact, Set<String> otherBranches) throws OseeCoreException {
      boolean wasModified = false;
      // Can only be on other branches if it has already been saved
      if (artifact.isInDb()) {

         IOseeStatement chStmt = ConnectionHandler.getStatement();
         try {
            Branch branch = artifact.getBranch();
            chStmt.runPreparedQuery(OTHER_EDIT_SQL, artifact.getArtId(), branch.getId(),
               branch.getParentBranch().getId(), BranchArchivedState.UNARCHIVED.getValue());

            while (chStmt.next()) {
               int modifiedAttrId = chStmt.getInt("attr_id");
               long modifiedGammaId = chStmt.getInt("gamma_id");
               int modifiedOnBranchId = chStmt.getInt("branch_id");

               Attribute<?> attribute = artifact.getAttributeById(modifiedAttrId, false);
               if (attribute == null || attribute.getGammaId() != modifiedGammaId) {
                  otherBranches.add(BranchManager.getBranch(modifiedOnBranchId).getShortName());
                  wasModified = true;
               }
            }
         } finally {
            chStmt.close();
         }
      }
      return wasModified;
   }
}