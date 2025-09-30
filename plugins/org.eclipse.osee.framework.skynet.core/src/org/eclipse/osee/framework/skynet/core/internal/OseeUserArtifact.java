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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.IUserGroupArtifactToken;
import org.eclipse.osee.framework.core.data.OseeUser;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.CoreUserGroups;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.skynet.core.OseeApiService;
import org.eclipse.osee.framework.skynet.core.access.UserGroupImpl;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public class OseeUserArtifact implements OseeUser {

   private final ArtifactToken art;
   private List<IUserGroupArtifactToken> userGrps;

   public OseeUserArtifact(ArtifactToken art) {
      this.art = art;
   }

   @Override
   public String toString() {
      return art.getName();
   }

   @Override
   public String toStringFull() {
      return toString();
   }

   @Override
   public String getUserId() {
      return OseeApiService.userSvc().getUserId(art);
   }

   @Override
   public boolean isActive() {
      return OseeApiService.userSvc().isActive(art);
   }

   @Override
   public boolean isOseeAdmin() {
      return OseeApiService.userSvc().isInUserGroup(CoreUserGroups.OseeAdmin);
   }

   @Override
   public String getPhone() {
      return ((Artifact) art).getSoleAttributeValue(CoreAttributeTypes.Phone, "");
   }

   @Override
   public String getEmail() {
      return OseeApiService.userSvc().getEmail(art);
   }

   @Override
   public Collection<IUserGroupArtifactToken> getRoles() {
      if (userGrps == null) {
         userGrps = new ArrayList<>();
         for (Artifact userGrp : OseeApiService.userArt().getRelatedArtifacts(CoreRelationTypes.Users_Artifact)) {
            userGrps.add(new UserGroupImpl(userGrp));
         }
      }
      return userGrps;
   }

   @Override
   public List<String> getLoginIds() {
      return ((Artifact) art).getAttributesToStringList(CoreAttributeTypes.LoginId);
   }

   @Override
   public ArtifactToken getArtifact() {
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
      return art.getId();
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

   @Override
   public String getAbridgedEmail() {
      return ((Artifact) getArtifact()).getSoleAttributeValue(CoreAttributeTypes.AbridgedEmail, "");
   }

}
