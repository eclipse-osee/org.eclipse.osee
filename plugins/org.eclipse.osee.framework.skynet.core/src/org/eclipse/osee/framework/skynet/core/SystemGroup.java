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

import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Roberto E. Escobar
 */
public enum SystemGroup {

   Everyone(CoreArtifactTokens.Everyone),
   OseeAccessAdmin(CoreArtifactTokens.OseeAccessAdmin), // Ability to change Access Control on any object
   OseeAdmin(CoreArtifactTokens.OseeAdmin);

   private final OseeGroup group;

   SystemGroup(ArtifactToken token) {
      group = new OseeGroup(token);
   }

   public Artifact getArtifact() {
      return group.getGroupArtifact();
   }

   public void addMember(User user) {
      group.addMember(user);
   }

   public boolean isMember(User user) {
      return group.isMember(user);
   }

   public boolean isCurrentUserMember() {
      return group.isCurrentUserMember();
   }

   public boolean isTemporaryOverride(User user) {
      return group.isTemporaryOverride(user);
   }

   /**
    * Allow user to temporarily override group membership. Value of "member" will be returned until
    * .removeTemporaryOverride() is called
    */
   public void setTemporaryOverride(boolean member) {
      group.setTemporaryOverride(member);
   }

   public void removeTemporaryOverride() {
      group.removeTemporaryOverride();
   }

   public boolean isCurrentUserTemporaryOverride() {
      return group.isCurrentUserTemporaryOverride();
   }
}