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
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.ui.PlatformUI;

public final class ArtifactGuis {
   private ArtifactGuis() {
   }

   private static final String OTHER_EDIT_SQL =
         "select br.branch_id from osee_attribute att, osee_txs txs, osee_branch br where att.art_id = ? and att.gamma_id = txs.gamma_id and txs.branch_id = br.branch_id and txs.transaction_id <> br.baseline_transaction_id and br.branch_id <> ? and br.parent_branch_id = ? and br.archived = ?";

   /**
    * Returns non-archived sibling branches that this artifact's attributes have been edited on
    * 
    * @param artifact
    * @throws OseeCoreException
    */
   private static Collection<Branch> getOtherEdittedBranches(Artifact artifact) throws OseeCoreException {
      Collection<Branch> otherBranches = new LinkedList<Branch>();

      // Can only be on other branches it has already been saved
      if (artifact.isInDb()) {

         IOseeStatement chStmt = ConnectionHandler.getStatement();
         try {
            Branch branch = artifact.getBranch();
            chStmt.runPreparedQuery(OTHER_EDIT_SQL, artifact.getArtId(), branch.getId(),
                  branch.getParentBranch().getId(), BranchArchivedState.UNARCHIVED.getValue());

            while (chStmt.next()) {
               otherBranches.add(BranchManager.getBranch(chStmt.getInt("branch_id")));
            }
         } finally {
            chStmt.close();
         }
      }
      return otherBranches;
   }

   public static boolean checkOtherEdit(List<Artifact> artifacts) throws OseeCoreException {
      if (artifacts.size() == 0) {
         throw new IllegalArgumentException("you must pass at least one artifact");
      }

      boolean goAhead = true;

      Set<Branch> otherBranches = new HashSet<Branch>();
      for (Artifact artifact : artifacts) {
         otherBranches.addAll(getOtherEdittedBranches(artifact));
      }

      if (!otherBranches.isEmpty()) {
         StringBuilder sb = new StringBuilder();

         sb.append("The artifact");
         if (artifacts.size() > 1) {
            sb.append('s');
         }
         sb.append(" about to be editted ");
         if (artifacts.size() > 1) {
            sb.append("have");
         } else {
            sb.append("has");
         }
         sb.append(" already been modified on the following branches:");
         for (Branch branch : otherBranches) {
            sb.append("\n\t" + branch.getName());
         }
         sb.append("\n\nDo you still want to proceed?");

         synchronized (sb) {

            AskQuestion question = new AskQuestion(sb, "Confirm Edit", sb.toString());
            Displays.ensureInDisplayThread(question);
            try {
               while (!question.done) {
                  sb.wait();
               }
            } catch (InterruptedException e) {
            }
            goAhead = question.isYes();
         }
      }

      return goAhead;
   }

   private static class AskQuestion implements Runnable {

      private final Object notifee;
      private final String title;
      private final String question;
      private boolean yes;
      private boolean done;

      /**
       * @param notifee
       * @param question
       */
      public AskQuestion(Object notifee, String title, String question) {
         super();
         this.notifee = notifee;
         this.title = title;
         this.question = question;
         this.yes = false;
         this.done = false;
      }

      public void run() {
         synchronized (notifee) {
            yes =
                  MessageDialog.openQuestion(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), title,
                        question);
            done = true;
            notifee.notifyAll();
         }
      }

      /**
       * @return Returns the yes.
       */
      public boolean isYes() {
         return yes;
      }

      /**
       * @return Returns the done.
       */
      public boolean isDone() {
         return done;
      }
   }
}