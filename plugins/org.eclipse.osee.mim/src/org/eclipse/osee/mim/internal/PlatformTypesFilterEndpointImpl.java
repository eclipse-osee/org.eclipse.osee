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
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.mim.InterfacePlatformTypeApi;
import org.eclipse.osee.mim.PlatformTypesFilterEndpoint;
import org.eclipse.osee.mim.types.PlatformTypeToken;

/**
 * A new instance of this REST endpoint is created for each REST call so this class does not require a thread-safe
 * design
 *
 * @author Luciano T. Vaglienti
 */
public class PlatformTypesFilterEndpointImpl implements PlatformTypesFilterEndpoint {

   private final BranchId branch;
   private final UserId account;
   private final InterfacePlatformTypeApi platformApi;

   public PlatformTypesFilterEndpointImpl(BranchId branch, UserId account, InterfacePlatformTypeApi interfacePlatformTypeApi) {
      this.account = account;
      this.branch = branch;
      this.platformApi = interfacePlatformTypeApi;
   }

   @Override
   public Collection<PlatformTypeToken> getPlatformTypes(String filter) {
      try {
         return platformApi.getAccessor().getAllByFilter(branch, filter, PlatformTypeToken.class);
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
         System.out.println(ex);
         return null;
      }
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

}