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

import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Roberto E. Escobar
 */
public enum SystemGroup {

   Everyone, OseeAdmin;

   private final OseeGroup group;

   SystemGroup() {
      this.group = new OseeGroup(this.name());
   }

   public Artifact getArtifact() throws OseeCoreException {
      return group.getGroupArtifact();
   }

   public void addMember(User user) throws OseeCoreException {
      this.group.addMember(user);
   }

   public boolean isMember(User user) throws OseeCoreException {
      return this.group.isMember(user);
   }

   public boolean isCurrentUserMember() throws OseeCoreException {
      return this.group.isCurrentUserMember();
   }
}
