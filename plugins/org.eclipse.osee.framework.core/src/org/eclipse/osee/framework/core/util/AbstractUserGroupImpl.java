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

package org.eclipse.osee.framework.core.util;

import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.IUserGroup;
import org.eclipse.osee.framework.core.data.IUserGroupArtifactToken;

/**
 * @author Donald G. Dunne
 */
public abstract class AbstractUserGroupImpl implements IUserGroup, IUserGroupArtifactToken {

   protected ArtifactToken groupArtifact;

   public AbstractUserGroupImpl(ArtifactToken userGroupArt) {
      this.groupArtifact = userGroupArt;
   }

   @Override
   public ArtifactToken getArtifact() {
      checkGroupExists();
      return groupArtifact;
   }

   protected void checkGroupExists() {
      if (groupArtifact == null) {
         groupArtifact = getOrCreateGroupArtifact(groupArtifact);
      }
   }

   @Override
   public String toString() {
      return "UserGroup: " + getArtifact().toStringWithId();
   }

   protected abstract ArtifactToken getOrCreateGroupArtifact(ArtifactToken groupArtifact2);

   @Override
   public Long getId() {
      return groupArtifact.getId();
   }

}
