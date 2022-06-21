/*********************************************************************
 * Copyright (c) 2013 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.skynet.core.internal.users;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import org.eclipse.osee.cache.admin.Cache;
import org.eclipse.osee.framework.core.access.AccessTopicEventPayload;
import org.eclipse.osee.framework.core.client.AccessTopicEvent;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.core.exception.OseeAuthenticationException;
import org.eclipse.osee.framework.core.exception.UserNotInDatabase;
import org.eclipse.osee.framework.jdk.core.type.LazyObject;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.internal.Activator;

/**
 * @author Roberto E. Escobar
 */
public class CurrentUserProvider extends LazyObject<User> {
   private final LazyObject<Cache<String, User>> cacheProvider;

   private final AtomicBoolean duringCreateUser = new AtomicBoolean(false);
   private final AtomicBoolean isAnonymousAuthenticationAllowed = new AtomicBoolean(true);
   private final AtomicBoolean isAnonymousNotificationAllowed = new AtomicBoolean(true);

   public CurrentUserProvider(LazyObject<Cache<String, User>> cacheProvider) {
      this.cacheProvider = cacheProvider;
   }

   @Override
   protected FutureTask<User> createLoaderTask() {
      duringCreateUser.set(false);
      Callable<User> callable = new CurrentUserCreationCallable();
      return new FutureTask<>(callable);
   }

   private final class CurrentUserCreationCallable implements Callable<User> {

      @Override
      public User call() throws Exception {
         User currentUser = null;
         ClientSessionManager.ensureSessionCreated();
         if (ClientSessionManager.isSessionValid()) {
            UserToken currentUserToken = ClientSessionManager.getCurrentUserToken();
            try {
               currentUser = getUser(currentUserToken);
            } catch (UserNotInDatabase ex) {
               if (isAnonymousAuthenticationAllowed.compareAndSet(true, false)) {
                  ClientSessionManager.authenticateAsAnonymous();
                  currentUser = getUser(SystemUser.Anonymous);
               }
            }

         } else {
            if (isAnonymousAuthenticationAllowed.compareAndSet(true, false)) {
               ClientSessionManager.authenticateAsAnonymous();
               currentUser = getUser(SystemUser.Anonymous);
            }
         }

         if (currentUser == null) {
            throw new OseeStateException("Setting current user to null.");
         } else {
            if (currentUser.equals(SystemUser.Anonymous)) {
               if (isAnonymousNotificationAllowed.compareAndSet(true, false)) {
                  OseeLog.log(Activator.class, Level.INFO,
                     "You are logged into OSEE as \"Anonymous\".  If this is unexpected notify your OSEE admin");
               }
            }
         }
         if (ClientSessionManager.isSessionValid()) {
            AccessTopicEventPayload payload = new AccessTopicEventPayload();
            OseeEventManager.kickAccessTopicEvent(CurrentUserCreationCallable.class, payload,
               AccessTopicEvent.USER_AUTHENTICATED);
         }
         return currentUser;
      }

      private User getUser(UserToken token) {

         User user = null;
         try {
            user = cacheProvider.get().get(token.getUserId());
         } catch (Exception ex) {
            throw new OseeAuthenticationException(ex, "User [%s] is not authenticated", token.getUserId());
         }
         return user;
      }
   }

   public boolean isDuringCurrentUserCreation() {
      return duringCreateUser.get();
   }
}