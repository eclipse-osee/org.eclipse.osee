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
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.mim.InterfaceEnumerationApi;
import org.eclipse.osee.mim.InterfaceEnumerationSetApi;
import org.eclipse.osee.mim.InterfacePlatformTypeApi;
import org.eclipse.osee.mim.PlatformTypesEndpoint;
import org.eclipse.osee.mim.types.InterfaceEnumeration;
import org.eclipse.osee.mim.types.InterfaceEnumerationSet;
import org.eclipse.osee.mim.types.PlatformTypeToken;

/**
 * A new instance of this REST endpoint is created for each REST call so this class does not require a thread-safe
 * design
 *
 * @author Luciano T. Vaglienti
 */
public class PlatformTypesEndpointImpl implements PlatformTypesEndpoint {

   private final BranchId branch;
   private final UserId account;
   private final InterfacePlatformTypeApi platformApi;
   private final InterfaceEnumerationSetApi enumSetApi;
   private final InterfaceEnumerationApi enumApi;

   public PlatformTypesEndpointImpl(BranchId branch, UserId account, InterfacePlatformTypeApi api, InterfaceEnumerationSetApi enumSetApi, InterfaceEnumerationApi enumApi) {
      this.account = account;
      this.branch = branch;
      this.platformApi = api;
      this.enumSetApi = enumSetApi;
      this.enumApi = enumApi;
   }

   @Override
   public Collection<PlatformTypeToken> getPlatformTypes(long pageNum, long pageSize) {
      return platformApi.getAllWithEnumSet(branch, pageNum, pageSize);
   }

   @Override
   public PlatformTypeToken getPlatformType(ArtifactId typeId) {
      return platformApi.get(branch, typeId);
   }

   @Override
   public InterfaceEnumerationSet getRelatedEnumerationSet(ArtifactId typeId) {
      try {
         List<InterfaceEnumerationSet> enumSets =
            (List<InterfaceEnumerationSet>) enumSetApi.getAccessor().getAllByRelation(branch,
               CoreRelationTypes.InterfacePlatformTypeEnumeration_Element, typeId, InterfaceEnumerationSet.class);
         for (InterfaceEnumerationSet set : enumSets) {
            set.setEnumerations((List<InterfaceEnumeration>) this.enumApi.getAccessor().getAllByRelation(branch,
               CoreRelationTypes.InterfaceEnumeration_EnumerationSet, ArtifactId.valueOf(set.getId()),
               InterfaceEnumeration.class));
         }
         return enumSets.get(0);
      } catch (Exception ex) {
         System.out.println(ex);
      }
      return null;
   }

}