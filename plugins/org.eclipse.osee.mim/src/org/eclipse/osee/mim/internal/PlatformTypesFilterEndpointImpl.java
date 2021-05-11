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

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.mim.PlatformTypesFilterEndpoint;
import org.eclipse.osee.mim.types.PlatformTypeToken;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * A new instance of this REST endpoint is created for each REST call so this class does not require a thread-safe
 * design
 *
 * @author Luciano T. Vaglienti
 */
public class PlatformTypesFilterEndpointImpl implements PlatformTypesFilterEndpoint {

   private final OrcsApi orcsApi;
   private final BranchId branch;
   private final UserId account;

   public PlatformTypesFilterEndpointImpl(OrcsApi orcsApi, BranchId branch, UserId account) {
      this.orcsApi = orcsApi;
      this.account = account;
      this.branch = branch;
   }

   @Override
   public Collection<PlatformTypeToken> getPlatformTypes(String filter) {
      List<PlatformTypeToken> pList = new LinkedList<PlatformTypeToken>();
      for (ArtifactReadable p : orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(
         CoreArtifactTypes.InterfacePlatformType).and(PlatformTypeToken.getAttributeTypes(), filter,
            QueryOption.TOKEN_DELIMITER__ANY, QueryOption.CASE__IGNORE,
            QueryOption.TOKEN_MATCH_ORDER__ANY).getResults().getList()) {
         pList.add(new PlatformTypeToken(p));
      }
      return pList;
   }

   @Override
   public Collection<PlatformTypeToken> getPlatformTypes() {
      List<PlatformTypeToken> pList = new LinkedList<PlatformTypeToken>();
      for (ArtifactReadable p : orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(
         CoreArtifactTypes.InterfacePlatformType).getResults().getList()) {
         pList.add(new PlatformTypeToken(p));
      }
      return pList;
   }

}