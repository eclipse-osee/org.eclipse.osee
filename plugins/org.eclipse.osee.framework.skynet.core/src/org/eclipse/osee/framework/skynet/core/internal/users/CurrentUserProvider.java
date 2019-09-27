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
import org.eclipse.osee.framework.core.data.IdeClientSession;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.core.exception.UserNotInDatabase;
import org.eclipse.osee.framework.jdk.core.type.LazyObject;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.model.AccessTopicEvent;
import org.eclipse.osee.framework.skynet.core.event.model.AccessTopicEventPayload;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.jaxrs.client.JaxRsClient;

/**
 * @author Roberto E. Escobar
 */
public class CurrentUserProvider extends LazyObject<User> {
   private final LazyObject<Cache<String, User>> cacheProvider;
   private final UserDataWriter writer;

   private final AtomicBoolean duringCreateUser = new AtomicBoolean(false);
   private final AtomicBoolean isAnonymousAuthenticationAllowed = new AtomicBoolean(true);
   private final AtomicBoolean isAnonymousNotificationAllowed = new AtomicBoolean(true);

   public CurrentUserProvider(LazyObject<Cache<String, User>> cacheProvider, UserDataWriter writer) {
      this.cacheProvider = cacheProvider;
      this.writer = writer;
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
            String userId = ClientSessionManager.getSession().getUserId();
            if (SystemUser.BootStrap.getUserId().equals(userId)) {
               currentUser = BootStrapUser.getInstance();
            } else {
               UserToken currentUserToken = ClientSessionManager.getCurrentUserToken();
               try {
                  currentUser = getUser(currentUserToken);
               } catch (UserNotInDatabase ex) {
                  if (isAnonymousAuthenticationAllowed.compareAndSet(true, false)) {
                     ClientSessionManager.authenticateAsAnonymous();
                     currentUser = getUser(SystemUser.Anonymous);
                  }
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
            IdeClientSession session = ClientSessionManager.getSession();
            try {
               String userId = session.getUserId();
               if (Strings.isValid(userId) && !"bootstrap".equals(userId)) {
                  User user = UserManager.getUserByUserId(userId);
                  if (user != null) {
                     JaxRsClient.setAccountId(user.getUuid());
                  }
               }
            } catch (Exception ex) {
               OseeLog.log(Activator.class, Level.WARNING, "Error setting jax-rs accountId credentials.", ex);
            }
            try {
               JaxRsClient.setClientId(Long.valueOf(session.getId().hashCode()));
            } catch (Exception ex) {
               OseeLog.log(Activator.class, Level.WARNING, "Error setting jax-rs clientId credentials.", ex);
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
            OseeCoreException.wrapAndThrow(ex);
         }
         return user;
      }

   }

   public boolean isDuringCurrentUserCreation() {
      return duringCreateUser.get();
   }
}