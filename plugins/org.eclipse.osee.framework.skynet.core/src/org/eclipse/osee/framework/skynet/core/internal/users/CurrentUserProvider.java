/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.internal.users;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import org.eclipse.osee.cache.admin.Cache;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.data.IUserToken;
import org.eclipse.osee.framework.core.data.LazyObject;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.core.exception.UserNotInDatabase;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.model.AccessControlEvent;
import org.eclipse.osee.framework.skynet.core.event.model.AccessControlEventType;
import org.eclipse.osee.framework.skynet.core.internal.Activator;

/**
 * @author Roberto E. Escobar
 */
public class CurrentUserProvider extends LazyObject<User> {
   private final LazyObject<Cache<String, User>> cacheProvider;
   private final UserDataWriter writer;

   private final AtomicBoolean duringCreateUser = new AtomicBoolean(false);
   private final AtomicBoolean isGuestAuthenticationAllowed = new AtomicBoolean(true);
   private final AtomicBoolean isGuestNotificationAllowed = new AtomicBoolean(true);

   public CurrentUserProvider(LazyObject<Cache<String, User>> cacheProvider, UserDataWriter writer) {
      this.cacheProvider = cacheProvider;
      this.writer = writer;
   }

   @Override
   protected FutureTask<User> createLoaderTask() {
      duringCreateUser.set(false);
      Callable<User> callable = new CurrentUserCreationCallable();
      return new FutureTask<User>(callable);
   }

   private final class CurrentUserCreationCallable implements Callable<User> {

      @Override
      public User call() throws Exception {
         User currentUser = null;
         ClientSessionManager.ensureSessionCreated();
         if (ClientSessionManager.isSessionValid()) {
            String userId = ClientSessionManager.getSession().getUserId();
            if (SystemUser.BootStrap.getUserId().equals(userId)) {
               currentUser = BootStrapUser.getInstance();
            } else {
               IUserToken currentUserToken = ClientSessionManager.getCurrentUserToken();
               if (ClientSessionManager.isUserCreationRequired()) {
                  try {
                     duringCreateUser.set(true);
                     currentUser = writer.createUser(currentUserToken, "Populate current user");
                     ClientSessionManager.clearUserCreationRequired();
                  } finally {
                     duringCreateUser.set(false);
                  }
               } else {
                  try {
                     currentUser = getUser(currentUserToken);
                  } catch (UserNotInDatabase ex) {
                     if (isGuestAuthenticationAllowed.compareAndSet(true, false)) {
                        ClientSessionManager.authenticateAsGuest();
                        currentUser = getUser(SystemUser.Guest);
                     }
                  }
               }
            }
         } else {
            if (isGuestAuthenticationAllowed.compareAndSet(true, false)) {
               ClientSessionManager.authenticateAsGuest();
               currentUser = getUser(SystemUser.Guest);
            }
         }

         if (currentUser == null) {
            throw new OseeStateException("Setting current user to null.");
         } else {
            if (currentUser.getName().equals(SystemUser.Guest.getName())) {
               if (isGuestNotificationAllowed.compareAndSet(true, false)) {
                  OseeLog.log(Activator.class, Level.INFO,
                     "You are logged into OSEE as \"Guest\".  If this is unexpected notify your OSEE admin");
               }
            }
         }
         if (ClientSessionManager.isSessionValid()) {
            AccessControlEvent event = new AccessControlEvent();
            event.setEventType(AccessControlEventType.UserAuthenticated);
            OseeEventManager.kickAccessControlArtifactsEvent(this, event);
         }
         return currentUser;
      }

      private User getUser(IUserToken token) throws OseeCoreException {
         User user = null;
         try {
            user = cacheProvider.get().get(token.getUserId());
         } catch (Exception ex) {
            OseeExceptions.wrapAndThrow(ex);
         }
         return user;
      }

   }

   public boolean isDuringCurrentUserCreation() {
      return duringCreateUser.get();
   }
}