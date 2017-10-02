/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.data;

import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public interface UserToken extends ArtifactToken, UserId {

   public String getUserId() ;

   public boolean isActive() ;

   public boolean isAdmin() ;

   public String getEmail() ;

   public boolean isCreationRequired();

   public static UserToken create(long id, String guid, String name, String email, String userId, boolean active, boolean admin, boolean creationRequired) {
      final class UserTokenImpl extends NamedIdBase implements UserToken {
         private final String userId;
         private final boolean active;
         private final boolean admin;
         private final String email;
         private final String guid;
         private final boolean creationRequired;

         public UserTokenImpl(long id, String guid, String name, String userId, boolean active, boolean admin, String email, boolean creationRequired) {
            super(id, name);
            this.guid = guid;
            this.userId = userId;
            this.active = active;
            this.admin = admin;
            this.email = email;
            this.creationRequired = creationRequired;
         }

         @Override
         public IArtifactType getArtifactType() {
            return CoreArtifactTypes.User;
         }

         @Override
         public String getUserId() {
            return userId;
         }

         @Override
         public boolean isActive() {
            return active;
         }

         @Override
         public boolean isAdmin() {
            return admin;
         }

         @Override
         public String getEmail() {
            return email;
         }

         @Override
         public boolean isCreationRequired() {
            return creationRequired;
         }

         @Override
         public String toString() {
            return String.format(
               "UserToken [name [%s], userId=[%s], active=[%s], admin=[%s], email=[%s], creationRequired=[%s]",
               getName(), userId, active, admin, email, creationRequired);
         }

         @Override
         public String getGuid() {
            return guid;
         }

         @Override
         public BranchId getBranch() {
            return CoreBranches.COMMON;
         }
      }
      return new UserTokenImpl(id, guid, name, userId, active, admin, email, creationRequired);
   }
}