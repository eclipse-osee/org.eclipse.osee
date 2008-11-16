/*
 * Created on Nov 14, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
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
