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
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.database.core.ConnectionHandlerStatement;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.ui.PlatformUI;

public class ArtifactGuis {
   public ArtifactGuis() {
      super();
   }

   private static final String OTHER_EDIT_SQL =
         "select distinct t3.branch_id from osee_artifact_version t1, osee_txs t2, osee_tx_details t3, (select min(transaction_id) as min_tx_id, branch_id from osee_tx_details group by branch_id) t4, osee_branch t5 where t1.art_id = ? and t1.gamma_id = t2.gamma_id and t2.transaction_id <> t4.min_tx_id and t2.transaction_id = t3.transaction_id and t3.branch_id = t4.branch_id and t4.branch_id <> ? and t5.parent_branch_id = ? and t4.branch_id = t5.branch_id and t5.archived = 0";

   /**
    * Returns all the other branches this artifact has been editted on, besides modifications to program branch.
    * 
    * @param artifact
    * @throws OseeCoreException
    */
   private static Collection<Branch> getOtherEdittedBranches(Artifact artifact) throws OseeCoreException {
      Collection<Branch> otherBranches = new LinkedList<Branch>();

      // Can only be on other branches it has already been saved
      if (artifact.isInDb()) {

         ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
         try {
            chStmt.runPreparedQuery(OTHER_EDIT_SQL, artifact.getArtId(), artifact.getBranch().getBranchId(),
                  artifact.getBranch().getId());

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