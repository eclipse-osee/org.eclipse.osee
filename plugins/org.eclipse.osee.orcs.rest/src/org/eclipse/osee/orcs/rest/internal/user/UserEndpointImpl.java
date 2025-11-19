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
package org.eclipse.osee.orcs.rest.internal.user;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.UserGroupArtifactToken;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.data.UserTokens;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.rest.model.UserEndpoint;

/**
 * @author Donald G. Dunne
 */
public class UserEndpointImpl implements UserEndpoint {

   private final OrcsApi orcsApi;

   public UserEndpointImpl(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   @Override
   public UserTokens get() {
      UserTokens toks = new UserTokens();
      for (ArtifactReadable art : orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andTypeEquals(
         CoreArtifactTypes.User).follow(CoreRelationTypes.Users_Artifact).asArtifacts()) {
         String email = art.getSoleAttributeValue(CoreAttributeTypes.Email, "");
         String userId = art.getSoleAttributeValue(CoreAttributeTypes.UserId, "");
         boolean active = art.getSoleAttributeValue(CoreAttributeTypes.Active, false);
         List<String> loginIds = art.getAttributeValues(CoreAttributeTypes.LoginId);
         UserToken user =
            UserToken.create(art.getId(), art.getName(), email, userId, active, loginIds, new ArrayList<>());
         for (ArtifactReadable roleArt : art.getRelated(CoreRelationTypes.Users_Artifact)) {
            user.getRoles().add(UserGroupArtifactToken.valueOf(roleArt.getId(), roleArt.getName()));
         }
         toks.getUsers().add(user);
      }
      return toks;
   }

}
