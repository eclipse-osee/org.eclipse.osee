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

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;

public class ArtifactGuis {
   public ArtifactGuis() {
      super();
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