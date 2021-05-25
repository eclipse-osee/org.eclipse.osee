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

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.mim.InterfacePlatformTypeApi;
import org.eclipse.osee.mim.PlatformTypesEndpoint;
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

   public PlatformTypesEndpointImpl(BranchId branch, UserId account, InterfacePlatformTypeApi api) {
      this.account = account;
      this.branch = branch;
      this.platformApi = api;
   }

   @Override
   public Collection<PlatformTypeToken> getPlatformTypes() {
      try {
         return platformApi.getAccessor().getAll(branch, PlatformTypeToken.class);
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
         System.out.println(ex);
         return null;
      }
   }

   @Override
   public XResultData updatePlatformType(PlatformTypeToken platformTypeToken) {
      return platformApi.getInserter().replaceArtifact(platformTypeToken, account, branch);
   }

   @Override
   public XResultData createPlatformType(PlatformTypeToken platformTypeToken) {
      return platformApi.getInserter().addArtifact(platformTypeToken, account, branch);
   }

   @Override
   public PlatformTypeToken getPlatformType(ArtifactId typeId) {
      try {
         return platformApi.getAccessor().get(branch, typeId, PlatformTypeToken.class);
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
         System.out.println(ex);
         return null;
      }
   }

   @Override
   public XResultData removePlatformType(ArtifactId typeId) {
      return platformApi.getInserter().removeArtifact(typeId, account, branch);
   }

   @Override
   public XResultData patchPlatformType(PlatformTypeToken platformTypeToken) {
      return platformApi.getInserter().patchArtifact(platformTypeToken, account, branch);
   }

}