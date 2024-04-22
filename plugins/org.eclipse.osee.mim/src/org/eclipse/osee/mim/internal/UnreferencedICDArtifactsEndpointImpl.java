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
package org.eclipse.osee.mim.internal;

import java.util.Collection;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.mim.InterfaceElementApi;
import org.eclipse.osee.mim.InterfaceMessageApi;
import org.eclipse.osee.mim.InterfacePlatformTypeApi;
import org.eclipse.osee.mim.InterfaceStructureApi;
import org.eclipse.osee.mim.InterfaceSubMessageApi;
import org.eclipse.osee.mim.UnreferencedICDArtifactsEndpoint;
import org.eclipse.osee.mim.types.InterfaceMessageToken;
import org.eclipse.osee.mim.types.InterfaceStructureElementToken;
import org.eclipse.osee.mim.types.InterfaceStructureToken;
import org.eclipse.osee.mim.types.InterfaceSubMessageToken;
import org.eclipse.osee.mim.types.PlatformTypeToken;

public class UnreferencedICDArtifactsEndpointImpl implements UnreferencedICDArtifactsEndpoint {
   private final BranchId branch;
   private final InterfacePlatformTypeApi platformTypeApi;
   private final InterfaceElementApi elementApi;
   private final InterfaceStructureApi structureApi;
   private final InterfaceSubMessageApi submessageApi;
   private final InterfaceMessageApi messageApi;

   public UnreferencedICDArtifactsEndpointImpl(BranchId branch, InterfacePlatformTypeApi platformTypeApi, InterfaceElementApi elementApi, InterfaceStructureApi structureApi, InterfaceSubMessageApi submessageApi, InterfaceMessageApi messageApi) {
      this.branch = branch;
      this.platformTypeApi = platformTypeApi;
      this.elementApi = elementApi;
      this.structureApi = structureApi;
      this.submessageApi = submessageApi;
      this.messageApi = messageApi;
   }

   @Override
   public Collection<PlatformTypeToken> getPlatformTypes(String filter, long pageNum, long pageSize) {
      return this.platformTypeApi.getAllwithNoElementRelations(branch, filter, pageNum, pageSize);
   }

   @Override
   public Collection<InterfaceStructureElementToken> getElements(String filter, long pageNum, long pageSize) {
      return this.elementApi.getAllwithNoStructureRelations(branch, filter, pageNum, pageSize);
   }

   @Override
   public Collection<InterfaceStructureToken> getStructures(String filter, long pageNum, long pageSize) {
      return this.structureApi.getAllwithNoSubMessageRelations(branch, filter, pageNum, pageSize);
   }

   @Override
   public Collection<InterfaceSubMessageToken> getSubmessages(String filter, long pageNum, long pageSize) {
      return this.submessageApi.getAllwithNoMessageRelations(branch, filter, pageNum, pageSize);
   }

   @Override
   public Collection<InterfaceMessageToken> getMessages(String filter, long pageNum, long pageSize) {
      return this.messageApi.getAllwithNoConnectionRelations(branch, filter, pageNum, pageSize);
   }

   @Override
   public int getPlatformTypesCount(String filter) {
      return this.platformTypeApi.getAllwithNoElementRelationsCount(branch, filter);
   }

   @Override
   public int getElementsCount(String filter) {
      return this.elementApi.getAllwithNoStructureRelationsCount(branch, filter);
   }

   @Override
   public int getStructuresCount(String filter) {
      return this.structureApi.getAllwithNoSubMessageRelationsCount(branch, filter);
   }

   @Override
   public int getSubmessagesCount(String filter) {
      return this.submessageApi.getAllwithNoMessageRelationsCount(branch, filter);
   }

   @Override
   public int getMessagesCount(String filter) {
      return this.messageApi.getAllwithNoConnectionRelationsCount(branch, filter);
   }

}
