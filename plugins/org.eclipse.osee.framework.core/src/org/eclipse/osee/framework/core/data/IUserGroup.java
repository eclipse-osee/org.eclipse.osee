/*********************************************************************
 * Copyright (c) 2019 Boeing
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

package org.eclipse.osee.framework.core.data;

import java.util.Collection;
import java.util.Collections;

/**
 * @author Donald G. Dunne
 */
public interface IUserGroup {

   ArtifactToken getArtifact();

   boolean addMember(UserId user, boolean persist);

   boolean isMember(UserId user);

   boolean isCurrentUserMember();

   boolean removeMember(UserId user, boolean persist);

   Collection<UserToken> getMembers();

   boolean isMember(Long id);

   Long getId();

   public IUserGroup SENTINEL = new UserGroupSentinelImpl();

   static class UserGroupSentinelImpl implements IUserGroup {

      @Override
      public ArtifactToken getArtifact() {
         return ArtifactToken.SENTINEL;
      }

      @Override
      public boolean addMember(UserId user, boolean persist) {
         return false;
      }

      @Override
      public boolean isMember(UserId user) {
         return false;
      }

      @Override
      public boolean isCurrentUserMember() {
         return false;
      }

      @Override
      public boolean removeMember(UserId user, boolean persist) {
         return false;
      }

      @Override
      public Collection<UserToken> getMembers() {
         return Collections.emptyList();
      }

      @Override
      public boolean isMember(Long id) {
         return false;
      }

      @Override
      public Long getId() {
         return -1L;
      }

   }

}