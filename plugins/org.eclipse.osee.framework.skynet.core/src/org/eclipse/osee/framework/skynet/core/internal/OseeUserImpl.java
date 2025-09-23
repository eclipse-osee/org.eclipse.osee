/*********************************************************************
 * Copyright (c) 2025 Boeing
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

package org.eclipse.osee.framework.skynet.core.internal;

import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.IUserGroupArtifactToken;
import org.eclipse.osee.framework.core.data.OseeUser;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

public class OseeUserImpl implements OseeUser {

   private final UserToken tok;
   private Artifact art;

   public OseeUserImpl(UserToken tok) {
      this.tok = tok;
   }

   @Override
   public String toString() {
      return tok.getName();
   }

   @Override
   public String toStringFull() {
      return null;
   }

   @Override
   public String getUserId() {
      return tok.getUserId();
   }

   @Override
   public boolean isActive() {
      return tok.isActive();
   }

   @Override
   public boolean isOseeAdmin() {
      return false;
   }

   @Override
   public String getPhone() {
      return tok.getPhone();
   }

   @Override
   public String getEmail() {
      return tok.getEmail();
   }

   @Override
   public Collection<IUserGroupArtifactToken> getRoles() {
      return null;
   }

   @Override
   public List<String> getLoginIds() {
      return tok.getLoginIds();
   }

   @Override
   public ArtifactToken getArtifact() {
      if (art == null) {
         art = ArtifactQuery.getArtifactFromId(getId(), getBranch());
      }
      return art;
   }

   @Override
   public void setArtifact(ArtifactToken artifact) {
      // do nothing
   }

   @Override
   public ArtifactId getArtifactId() {
      return ArtifactId.valueOf(getId());
   }

   @Override
   public Long getId() {
      return tok.getId();
   }

   @Override
   public BranchToken getBranch() {
      return CoreBranches.COMMON;
   }

   @Override
   public ArtifactTypeToken getArtifactType() {
      return CoreArtifactTypes.User;
   }

   @Override
   public int hashCode() {
      return getId().hashCode();
   }

   @Override
   public boolean equals(Object obj) {
      boolean equal = super.equals(obj);
      if (equal) {
         return true;
      }
      if (obj instanceof Id) {
         return getId().equals(((Id) obj).getId());
      }
      return false;
   }

}
