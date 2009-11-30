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

import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.cache.BranchCache;
import org.eclipse.osee.framework.core.data.SystemUser;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.ArtifactType;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeSide;
import org.eclipse.osee.framework.skynet.core.utility.DbUtil;

/**
 * @author Roberto E. Escobar
 */
public class ShallowArtifact implements IArtifact {

   private int artifactId;
   private final BranchCache cache;
   private Artifact associatedArtifact;

   public ShallowArtifact(BranchCache cache, int artifactId) {
      this.artifactId = artifactId;
      this.cache = cache;
      associatedArtifact = null;
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
      Branch branch = null;
      if (!DbUtil.isDbInit()) {
         try {
            branch = cache.getCommonBranch();
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
      return branch;
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
      int hashCode = 11;
      hashCode = hashCode * 37 + getArtId();
      hashCode = hashCode * 37 + (getBranch() != null ? getBranch().hashCode() : 0);
      return hashCode;
   }

   @Override
   public final boolean equals(Object obj) {
      if (obj instanceof IArtifact) {
         IArtifact other = (IArtifact) obj;
         boolean result = getArtId() == other.getArtId();
         if (result) {
            if (getBranch() != null && other.getBranch() != null) {
               result = getBranch().equals(other.getBranch());
            } else {
               result = getBranch() == null && other.getBranch() == null;
            }
         }
         return result;
      }
      return false;
   }

   @Override
   public String toString() {
      Artifact artifact = getArtifact();
      return artifact != null ? artifact.toString() : String.format("ArtId:[%s]", getArtId());
   }

   @Override
   public Artifact getFullArtifact() throws OseeCoreException {
      if (associatedArtifact == null) {
         if (getArtId() > 0) {
            associatedArtifact = ArtifactQuery.getArtifactFromId(getArtId(), getBranch());
         } else {
            associatedArtifact = UserManager.getUser(SystemUser.OseeSystem);
            artifactId = associatedArtifact.getArtId();
         }
      }
      return associatedArtifact;
   }

   @Override
   public List<? extends IArtifact> getRelatedArtifacts(RelationTypeSide relationTypeSide) throws OseeCoreException {
      return getFullArtifact().getRelatedArtifacts(relationTypeSide);
   }
}
