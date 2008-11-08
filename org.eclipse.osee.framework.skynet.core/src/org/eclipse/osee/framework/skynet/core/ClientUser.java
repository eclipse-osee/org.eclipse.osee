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
package org.eclipse.osee.framework.skynet.core;

import java.util.logging.Level;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.client.CoreClientActivator;
import org.eclipse.osee.framework.core.data.SystemUser;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.UserNotInDatabase;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.event.AccessControlEventType;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.utility.LoadedArtifacts;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;

/**
 * @author Roberto E. Escobar
 */
final class ClientUser {
   private static final ClientUser instance = new ClientUser();

   private User currentUser;
   private boolean notifiedAsGuest;

   private ClientUser() {
      this.currentUser = null;
      this.notifiedAsGuest = false;
   }

   static synchronized User getMainUser() throws OseeCoreException {
      if (instance.currentUser == null) {
         instance.populateCurrentUser();
      }
      return instance.currentUser;
   }

   private void populateCurrentUser() throws OseeCoreException {
      ClientSessionManager.ensureSessionCreated();
      if (ClientSessionManager.isSessionValid()) {
         String userId = ClientSessionManager.getSession().getUserId();
         try {
            if (userId.equals(SystemUser.BootStrap.getUserID())) {
               setCurrentUser(BootStrapUser.getInstance());
            } else {
               if (ClientSessionManager.isUserCreationRequired()) {
                  SkynetTransaction transaction = new SkynetTransaction(BranchManager.getCommonBranch());
                  UserManager.createMainUser(ClientSessionManager.getCurrentUserInfo(), transaction);
                  transaction.execute();
                  ClientSessionManager.clearUserCreationRequired();
               }
               setCurrentUser(UserManager.getUserByUserId(ClientSessionManager.getCurrentUserInfo().getUserID()));
            }
         } catch (UserNotInDatabase ex) {
            if (currentUser == null) {
               executeGuestLogin();
            }
         }
      } else {
         executeGuestLogin();
      }
   }

   private void setCurrentUser(User newUser) {
      this.currentUser = newUser;
      if (ClientSessionManager.isSessionValid()) {
         notifyListeners();
      }
   }

   private void executeGuestLogin() throws OseeCoreException {
      if (!notifiedAsGuest) {
         notifiedAsGuest = true;
         ClientSessionManager.authenticateAsGuest();
         setCurrentUser(UserManager.getUser(SystemUser.Guest));
         AWorkbench.popup(
               "OSEE Guest Login",
               "You are logged into OSEE as \"Guest\".\n\nIf you do not expect to be logged in as Guest, please report this immediately.");
      }
   }

   private void notifyListeners() {
      SafeRunner.run(new ISafeRunnable() {

         @Override
         public void handleException(Throwable exception) {
            OseeLog.log(CoreClientActivator.class, Level.SEVERE, exception);
         }

         @Override
         public void run() throws Exception {
            OseeEventManager.kickAccessControlArtifactsEvent(this, AccessControlEventType.UserAuthenticated,
                  LoadedArtifacts.EmptyLoadedArtifacts());
         }

      });
   }
}
