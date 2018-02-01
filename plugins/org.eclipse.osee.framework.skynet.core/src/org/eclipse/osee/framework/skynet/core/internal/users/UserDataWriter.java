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

import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import org.eclipse.osee.cache.admin.Cache;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.type.LazyObject;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.skynet.core.utility.DbUtil;

/**
 * @author Roberto E. Escobar
 */
public class UserDataWriter {

   private final LazyObject<Cache<String, User>> cacheProvider;

   public UserDataWriter(LazyObject<Cache<String, User>> cacheProvider) {
      this.cacheProvider = cacheProvider;
   }

   public User createUser(UserToken userToken, String comment) {
      SkynetTransaction transaction = TransactionManager.createTransaction(CoreBranches.COMMON, comment);
      User user = createUser(userToken, transaction);
      Operations.executeWorkAndCheckStatus(transaction);
      return user;
   }

   public User createUser(UserToken userToken, SkynetTransaction transaction) {
      // Determine if user with id has already been created; boot strap issue with dbInit
      User user = cacheProvider.get().getIfPresent(userToken.getUserId());
      if (user != null) {
         user.setName(userToken.getName());
         user.setEmail(userToken.getEmail());
         user.setActive(userToken.isActive());
      } else {
         long uuid = userToken.getId() > 0L ? userToken.getId() : Lib.generateArtifactIdAsInt();
         user = (User) ArtifactTypeManager.addArtifact(CoreArtifactTypes.User, CoreBranches.COMMON, userToken.getName(),
            GUID.create(), uuid);
         user.setActive(userToken.isActive());
         user.setUserID(userToken.getUserId());
         user.setEmail(userToken.getEmail());
         addUserToUserGroups(user);

         cacheUser(user);

         /**
          * Users are auto-created, display stack trace as INFO in client's log to help debug any unexpected creation
          */
         if (!DbUtil.isDbInit() && !"false".equals(System.getProperty("displayCreateUserError"))) {
            Exception ex = new Exception("just wanted the stack trace");
            OseeLog.logf(Activator.class, Level.INFO, ex, "Created user [%s]", user);
         }
      }

      if (transaction != null) {
         user.persist(transaction);
      }
      return user;
   }

   private void cacheUser(final User newUser) {
      Cache<String, User> cache = cacheProvider.get();
      try {
         cache.get(newUser.getUserId(), new Callable<User>() {
            @Override
            public User call() {
               return newUser;
            }
         });
      } catch (Exception ex) {
         OseeCoreException.wrapAndThrow(ex);
      }
   }

   private void addUserToUserGroups(Artifact user) {
      Collection<Artifact> defaultGroups =
         ArtifactQuery.getArtifactListFromAttribute(CoreAttributeTypes.DefaultGroup, "true", CoreBranches.COMMON);

      for (Artifact userGroup : defaultGroups) {
         userGroup.addRelation(CoreRelationTypes.Users_User, user);
      }
   }

}
