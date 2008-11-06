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
import org.eclipse.osee.framework.core.client.BaseCredentialProvider;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.client.CoreClientActivator;
import org.eclipse.osee.framework.core.data.OseeCredential;
import org.eclipse.osee.framework.core.data.SystemUser;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.UserNotInDatabase;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.event.AccessControlEventType;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
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

   static ClientUser getInstance() {
      return instance;
   }

   synchronized User getMainUser() {
      if (currentUser == null) {
         populateCurrentUser();
      }
      return currentUser;
   }

   private void populateCurrentUser() {
      try {
         ensureSessionCreated();
         if (ClientSessionManager.isSessionValid()) {
            String userId = ClientSessionManager.getSession().getUserId();
            try {
               if (userId.equals(SystemUser.BootStrap.getUserID())) {
                  currentUser = BootStrapUser.getInstance();
               } else {
                  currentUser = UserCache.getUserByUserId(userId);
               }
            } catch (UserNotInDatabase ex) {
               if (currentUser == null) {
                  executeGuestLogin();
               }
            }
         } else {
            executeGuestLogin();
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetActivator.class, Level.SEVERE, ex);
      }
   }

   private void ensureSessionCreated() throws OseeCoreException {
      if (!ClientSessionManager.isSessionValid()) {
         ClientSessionManager.authenticate(new BaseCredentialProvider() {
            public OseeCredential getCredential() throws OseeCoreException {
               OseeCredential credential = super.getCredential();
               credential.setUserId("");
               credential.setDomain("");
               credential.setPassword("");
               credential.setAuthenticationProtocol(OseeProperties.getAuthenticationProtocol());
               return credential;
            }
         });

         if (ClientSessionManager.isSessionValid()) {
            notifyListeners();
         }
      }
   }

   private void executeGuestLogin() throws OseeCoreException {
      if (!notifiedAsGuest) {
         notifiedAsGuest = true;
         ClientSessionManager.authenticateAsGuest();
         currentUser = UserCache.getUser(SystemUser.Guest);
         if (ClientSessionManager.isSessionValid()) {
            notifyListeners();
         }
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
