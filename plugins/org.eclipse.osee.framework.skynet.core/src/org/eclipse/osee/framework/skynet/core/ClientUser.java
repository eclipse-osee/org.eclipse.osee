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
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.data.SystemUser;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.exception.UserNotInDatabase;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.IExceptionableRunnable;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.event.AccessControlEventType;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.utility.LoadedArtifacts;

/**
 * @author Roberto E. Escobar
 */
final class ClientUser {
   private static final ClientUser instance = new ClientUser();

   private User currentUser;
   private boolean isGuestNotificationAllowed;
   private boolean isGuestAuthenticationAllowed;

   private ClientUser() {
      this.currentUser = null;
      this.isGuestAuthenticationAllowed = true;
      this.isGuestNotificationAllowed = true;
   }

   static synchronized User getMainUser() throws OseeCoreException {
      if (!ClientSessionManager.isSessionValid() || instance.currentUser == null) {
         instance.populateCurrentUser();
         if (!instance.currentUser.isActive()) {
            instance.currentUser.setActive(true);
            instance.currentUser.persist();
         }
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
                  SkynetTransaction transaction =
                        new SkynetTransaction(BranchManager.getCommonBranch(), "Populate current user");
                  UserManager.createMainUser(ClientSessionManager.getCurrentUserInfo(), transaction);
                  setCurrentUser(UserManager.getUserByUserId(ClientSessionManager.getCurrentUserInfo().getUserID()));
                  transaction.execute();
                  ClientSessionManager.clearUserCreationRequired();
               } else {
                  setCurrentUser(UserManager.getUserByUserId(ClientSessionManager.getCurrentUserInfo().getUserID()));
               }
            }
         } catch (UserNotInDatabase ex) {
            executeGuestLogin();
         }
      } else {
         executeGuestLogin();
      }
   }

   private void setCurrentUser(User newUser) throws OseeStateException {
      this.currentUser = newUser;

      if (newUser == null) {
         throw new OseeStateException("Setting current user to null.");
      } else {
         if (isGuestNotificationAllowed && newUser.getName().equals(SystemUser.Guest.getName())) {
            isGuestNotificationAllowed = false;
            OseeLog.log(Activator.class, Level.INFO,
                  "You are logged into OSEE as \"Guest\".  If this is unexpected notify your OSEE admin");
         }
      }
      if (ClientSessionManager.isSessionValid()) {
         notifyListeners();
      }
   }

   private void executeGuestLogin() throws OseeCoreException {
      if (isGuestAuthenticationAllowed) {
         ClientSessionManager.authenticateAsGuest();
         setCurrentUser(UserManager.getUser(SystemUser.Guest));
         isGuestAuthenticationAllowed = false;
      }
   }

   private void notifyListeners() {
      Jobs.runInJob("Osee User Authenticated", new IExceptionableRunnable() {

         @Override
         public IStatus run(IProgressMonitor monitor) throws Exception {
            OseeEventManager.kickAccessControlArtifactsEvent(this, AccessControlEventType.UserAuthenticated,
                  LoadedArtifacts.createEmptyLoadedArtifacts());
            return Status.OK_STATUS;
         }

      }, Activator.class, Activator.PLUGIN_ID);

   }
}
