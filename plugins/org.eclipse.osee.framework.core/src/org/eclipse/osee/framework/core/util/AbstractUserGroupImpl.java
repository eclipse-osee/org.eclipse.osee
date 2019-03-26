/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.util;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.IUserGroup;
import org.eclipse.osee.framework.core.data.UserId;

/**
 * @author Donald G. Dunne
 */
public abstract class AbstractUserGroupImpl implements IUserGroup {

   protected ArtifactToken groupArtifact;
   private final Map<ArtifactToken, Boolean> temporaryOverride = new HashMap<>();

   public AbstractUserGroupImpl(ArtifactToken userGroupArt) {
      this.groupArtifact = userGroupArt;
   }

   @Override
   public ArtifactToken getArtifact() {
      checkGroupExists();
      return groupArtifact;
   }

   @Override
   public boolean isTemporaryOverride(UserId user) {
      if (temporaryOverride.get(groupArtifact) != null) {
         return temporaryOverride.get(groupArtifact);
      }
      return false;
   }

   @Override
   public void setTemporaryOverride(boolean member) {
      temporaryOverride.put(groupArtifact, member);
   }

   @Override
   public void removeTemporaryOverride() {
      temporaryOverride.remove(groupArtifact);
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

}
