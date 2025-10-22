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
import java.util.Collections;
import java.util.List;
import org.eclipse.osee.framework.core.enums.CoreBranches;

/**
 * @author Donald G. Dunne
 */
public interface OseeUser extends UserToken {

   String getAbridgedEmail();

   public static final OseeUser SENTINEL = new OseeUser() {

      @Override
      public ArtifactTypeToken getArtifactType() {
         return ArtifactTypeToken.SENTINEL;
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
         return "";
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
         return "";
      }

      @Override
      public Collection<IUserGroupArtifactToken> getRoles() {
         return Collections.emptyList();
      }

      @Override
      public String getPhone() {
         return "";
      }

      @Override
      public List<String> getLoginIds() {
         return Collections.emptyList();
      }

      @Override
      public String getEmail() {
         return "";
      }

      @Override
      public String getAbridgedEmail() {
         return "";
      }

      @Override
      public ArtifactId getArtifactId() {
         return ArtifactId.SENTINEL;
      }

   };

}
