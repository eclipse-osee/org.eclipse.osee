/*********************************************************************
 * Copyright (c) 2021 Boeing
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
package org.eclipse.osee.mim.internal;

import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.mim.MimApi;
import org.eclipse.osee.mim.MimUserPreferenceEndpoint;
import org.eclipse.osee.mim.types.MimUserPreference;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Luciano T. Vaglienti
 */
public class MimUserPreferenceEndpointImpl implements MimUserPreferenceEndpoint {

   private final MimApi mimApi;

   public MimUserPreferenceEndpointImpl(MimApi mimApi) {
      this.mimApi = mimApi;
   }

   @Override
   public MimUserPreference getPreferences(BranchId branch, UserId accountId) {
      ArtifactReadable user =
         mimApi.getOrcsApi().getQueryFactory().fromBranch(CoreBranches.COMMON).andId(accountId).asArtifact();
      BranchToken selectedBranch = mimApi.getOrcsApi().getQueryFactory().branchQuery().andId(branch).getOneOrSentinel();
      boolean hasWriteAccess = !mimApi.getOrcsApi().getAccessControlService().hasBranchPermission(user, selectedBranch,
         PermissionEnum.WRITE, null).isErrors();
      return new MimUserPreference(user, branch, hasWriteAccess);
   }

   @Override
   public List<String> getBranchPreferences(UserId accountId) {
      List<String> prefs = new LinkedList<String>();
      List<String> tempPrefs = mimApi.getOrcsApi().getQueryFactory().fromBranch(CoreBranches.COMMON).andId(
         ArtifactId.valueOf(accountId.getId())).asArtifact().getAttributeValues(
            CoreAttributeTypes.MimBranchPreferences);
      prefs.addAll(tempPrefs);
      return prefs;
   }
}