/*********************************************************************
 * Copyright (c) 2024 Boeing
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
package org.eclipse.osee.orcs.rest.internal.user.preferences;

import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.rest.model.ArtifactExplorerUserPreferences;
import org.eclipse.osee.orcs.rest.model.UserPreferencesEndpoint;

public class UserPreferencesEndpointImpl implements UserPreferencesEndpoint {

   private final OrcsApi orcsApi;

   public UserPreferencesEndpointImpl(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   @Override
   public ArtifactExplorerUserPreferences getArtifactExplorerUserPreferences() {
      ArtifactReadable prefsArt = this.orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andRelatedTo(
         CoreRelationTypes.UserToArtifactExplorerPrefs_User, orcsApi.userService().getUser()).asArtifactOrSentinel();
      if (prefsArt.isInvalid()) {
         return new ArtifactExplorerUserPreferences();
      }
      return new ArtifactExplorerUserPreferences(prefsArt);
   }

}
