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
package org.eclipse.osee.ats.util;

import java.util.Arrays;
import java.util.Collection;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ISubscribableArtifact;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.ui.PlatformUI;

/**
 * @author Donald G. Dunne
 */
public class Subscribe {

   private final Collection<StateMachineArtifact> smas;

   public Subscribe(StateMachineArtifact sma) {
      this(Arrays.asList(sma));
   }

   public Subscribe(Collection<StateMachineArtifact> smas) {
      super();
      this.smas = smas;
   }

   public void toggleSubscribe() {
      toggleSubscribe(true);
   }

   public void toggleSubscribe(boolean prompt) {
      try {
         if (((ISubscribableArtifact) smas.iterator().next()).amISubscribed()) {
            boolean result = true;
            if (prompt) result =
                  MessageDialog.openQuestion(
                        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                        "Un-Subscribe",
                        "You are currently subscribed to receive emails when this artifact transitions." + "\n\nAre You sure you wish to Un-Subscribe?");
            if (result) {
               SkynetTransaction transaction = new SkynetTransaction(AtsPlugin.getAtsBranch());
               for (StateMachineArtifact sma : smas) {
                  ((ISubscribableArtifact) sma).removeSubscribed(UserManager.getUser(), transaction);
               }
               transaction.execute();
            }
         } else {
            boolean result = true;
            if (prompt) result =
                  MessageDialog.openQuestion(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                        "Subscribe",
                        "Are you sure you wish to subscribe to receive emails when this artifact transitions?");
            if (result) {
               SkynetTransaction transaction = new SkynetTransaction(AtsPlugin.getAtsBranch());
               for (StateMachineArtifact sma : smas) {
                  ((ISubscribableArtifact) sma).addSubscribed(UserManager.getUser(), transaction);
               }
               transaction.execute();
            }

         }
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }
}
