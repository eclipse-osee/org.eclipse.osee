/*********************************************************************
 * Copyright (c) 2022 Boeing
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
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.mim.InterfaceSubMessageApi;
import org.eclipse.osee.mim.InterfaceSubMessageFilterEndpoint;
import org.eclipse.osee.mim.types.InterfaceSubMessageToken;

/**
 * @author Luciano T. Vaglienti
 */
public class InterfaceSubMessageFilterEndpointImpl implements InterfaceSubMessageFilterEndpoint {

   private final BranchId branch;
   private final InterfaceSubMessageApi submessageApi;
   public InterfaceSubMessageFilterEndpointImpl(BranchId branch, InterfaceSubMessageApi interfaceSubMessageApi) {
      this.branch = branch;
      this.submessageApi = interfaceSubMessageApi;
   }

   @Override
   public Collection<InterfaceSubMessageToken> getSubMessages(long pageNum, long pageSize,
      AttributeTypeToken orderByAttributeType) {
      return this.submessageApi.getAll(branch, pageNum, pageSize, orderByAttributeType);
   }

   @Override
   public Collection<InterfaceSubMessageToken> getSubMessages(String filter, long pageNum, long pageSize,
      AttributeTypeToken orderByAttributeType) {
      return this.submessageApi.getAll(branch, pageNum, pageSize, orderByAttributeType);
   }

   @Override
   public Collection<InterfaceSubMessageToken> getSubMessagesByName(String name, long pageNum, long pageSize,
      AttributeTypeToken orderByAttributeType) {
      return this.submessageApi.getAllByName(branch, name, pageNum, pageSize);
   }

   @Override
   public int getSubMessagesByNameCount(String name) {
      return this.submessageApi.getAllByNameCount(branch, name);
   }

}
