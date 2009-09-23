/*******************************************************************************
 * Copyright (c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.osee.framework.skynet.core.types;

import java.util.logging.Level;
import org.eclipse.osee.framework.core.data.SystemUser;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.internal.Activator;

/**
 * @author Roberto E. Escobar
 */
public class ShallowArtifact implements IArtifact {

   private int artifactId;

   public ShallowArtifact(int artifactId) {
      this.artifactId = artifactId;
   }

   @Override
   public int getArtId() {
      return artifactId;
   }

   @Override
   public ArtifactType getArtifactType() {
      return getArtifact().getArtifactType();
   }

   @Override
   public String getGuid() {
      return getArtifact().getGuid();
   }

   @Override
   public String getName() {
      return getArtifact().getName();
   }

   @Override
   public Branch getBranch() {
      return getArtifact().getBranch();
   }

   private Artifact getArtifact() {
      Artifact artifact = null;
      try {
         artifact = getFullArtifact();
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return artifact;
   }

   @Override
   public final int hashCode() {
      return 37 * getArtId();
   }

   @Override
   public final boolean equals(Object obj) {
      if (obj instanceof IArtifact) {
         IArtifact other = (IArtifact) obj;
         return getArtId() == other.getArtId();
      }
      return false;
   }

   @Override
   public Artifact getFullArtifact() throws OseeCoreException {
      Artifact associatedArtifact = null;
      if (artifactId > 0) {
         associatedArtifact = ArtifactQuery.getArtifactFromId(artifactId, BranchManager.getCommonBranch());
      } else {
         associatedArtifact = UserManager.getUser(SystemUser.OseeSystem);
         artifactId = associatedArtifact.getArtId();
      }
      return associatedArtifact;
   }
}
