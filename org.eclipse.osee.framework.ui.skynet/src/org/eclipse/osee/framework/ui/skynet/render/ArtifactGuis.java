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
import org.eclipse.osee.framework.db.connection.exception.BranchDoesNotExist;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.revision.RevisionManager;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.ui.PlatformUI;

public class ArtifactGuis {
   public ArtifactGuis() {
      super();
   }

   public static boolean checkOtherEdit(List<Artifact> artifacts) throws OseeDataStoreException, BranchDoesNotExist {
      if (artifacts.size() == 0) throw new IllegalArgumentException("you must pass at least one artifact");

      boolean goAhead = true;

      Set<Branch> otherBranches = new HashSet<Branch>();
      for (Artifact artifact : artifacts) {
         otherBranches.addAll(RevisionManager.getOtherEdittedBranches(artifact));
      }

      if (!otherBranches.isEmpty()) {
         StringBuilder sb = new StringBuilder();

         sb.append("The artifact");
         if (artifacts.size() > 1) sb.append('s');
         sb.append(" about to be editted ");
         if (artifacts.size() > 1)
            sb.append("have");
         else
            sb.append("has");
         sb.append(" already been modified on the following branches:");
         for (Branch branch : otherBranches)
            sb.append("\n\t" + branch.getBranchName());
         sb.append("\n\nDo you still want to proceed?");

         synchronized (sb) {

            AskQuestion question = new AskQuestion(sb, "Confirm Edit", sb.toString());
            Displays.ensureInDisplayThread(question);
            try {
               while (!question.done)
                  sb.wait();
            } catch (InterruptedException e) {
            }
            goAhead = question.isYes();
         }
      }

      return goAhead;
   }

   private static class AskQuestion implements Runnable {

      private Object notifee;
      private String title;
      private String question;
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