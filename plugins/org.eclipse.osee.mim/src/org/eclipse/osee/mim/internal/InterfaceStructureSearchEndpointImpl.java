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
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.mim.InterfaceStructureApi;
import org.eclipse.osee.mim.InterfaceStructureSearchEndpoint;
import org.eclipse.osee.mim.types.InterfaceStructureToken;

/**
 * @author Luciano T. Vaglienti
 */
public class InterfaceStructureSearchEndpointImpl implements InterfaceStructureSearchEndpoint {

   private final BranchId branch;
   private final UserId account;
   private final InterfaceStructureApi interfaceStructureApi;

   public InterfaceStructureSearchEndpointImpl(BranchId branch, UserId account, InterfaceStructureApi interfaceStructureApi) {
      this.account = account;
      this.branch = branch;
      this.interfaceStructureApi = interfaceStructureApi;
   }

   @Override
   public Collection<InterfaceStructureToken> getAllStructures(long pageNum, long pageSize) {
      return this.interfaceStructureApi.getAll(branch, pageNum, pageSize);
   }

   @Override
   public Collection<InterfaceStructureToken> getFilteredStructures(String filter, long pageNum, long pageSize) {
      return this.interfaceStructureApi.getFiltered(branch, filter, pageNum, pageSize);
   }

}
