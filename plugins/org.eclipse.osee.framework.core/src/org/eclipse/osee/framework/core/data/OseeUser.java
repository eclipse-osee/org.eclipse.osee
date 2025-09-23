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
package org.eclipse.osee.framework.core.data;

import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.enums.CoreBranches;

/**
 * @author Donald G. Dunne
 */
public interface OseeUser extends UserToken {

   public static final OseeUser SENTINEL = new OseeUser() {

      @Override
      public ArtifactTypeToken getArtifactType() {
         return null;
      }

      @Override
      public BranchToken getBranch() {
         return CoreBranches.COMMON;
      }

      @Override
      public Long getId() {
         return ArtifactId.SENTINEL.getId();
      }

      @Override
      public String toStringFull() {
         return null;
      }

      @Override
      public void setArtifact(ArtifactToken artifact) {
         // do nothing
      }

      @Override
      public boolean isOseeAdmin() {
         return false;
      }

      @Override
      public boolean isActive() {
         return false;
      }

      @Override
      public String getUserId() {
         return null;
      }

      @Override
      public Collection<IUserGroupArtifactToken> getRoles() {
         return null;
      }

      @Override
      public String getPhone() {
         return null;
      }

      @Override
      public List<String> getLoginIds() {
         return null;
      }

      @Override
      public String getEmail() {
         return null;
      }

      @Override
      public ArtifactId getArtifactId() {
         return ArtifactId.SENTINEL;
      }

      @Override
      public ArtifactToken getArtifact() {
         return ArtifactToken.SENTINEL;
      }

   };

}
