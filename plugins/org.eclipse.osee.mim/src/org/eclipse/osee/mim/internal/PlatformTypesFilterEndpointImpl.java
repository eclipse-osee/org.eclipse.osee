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
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
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

   private List<AttributeTypeId> createAttributeList() {
      List<AttributeTypeId> attributes = new LinkedList<AttributeTypeId>();
      attributes.add(CoreAttributeTypes.Name);
      attributes.add(CoreAttributeTypes.Description);
      attributes.add(CoreAttributeTypes.InterfaceLogicalType);
      attributes.add(CoreAttributeTypes.InterfacePlatformType2sComplement);
      attributes.add(CoreAttributeTypes.InterfacePlatformTypeAnalogAccuracy);
      attributes.add(CoreAttributeTypes.InterfacePlatformTypeBitSize);
      attributes.add(CoreAttributeTypes.InterfacePlatformTypeBitsResolution);
      attributes.add(CoreAttributeTypes.InterfacePlatformTypeCompRate);
      attributes.add(CoreAttributeTypes.InterfacePlatformTypeDefaultValue);
      attributes.add(CoreAttributeTypes.InterfaceElementEnumLiteral);
      attributes.add(CoreAttributeTypes.InterfacePlatformTypeMaxval);
      attributes.add(CoreAttributeTypes.InterfacePlatformTypeMinval);
      attributes.add(CoreAttributeTypes.InterfacePlatformTypeMsbValue);
      attributes.add(CoreAttributeTypes.InterfacePlatformTypeUnits);
      attributes.add(CoreAttributeTypes.InterfacePlatformTypeValidRangeDescription);
      return attributes;
   }

   @Override
   public Collection<PlatformTypeToken> getPlatformTypes(String filter, long pageNum, long pageSize, AttributeTypeToken orderByAttributeTypeId) {
      List<AttributeTypeId> attributes = this.createAttributeList();
      try {
         return platformApi.getAccessor().getAllByFilter(branch, filter, attributes, pageNum, pageSize,
            orderByAttributeTypeId);
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
         System.out.println(ex);
         return null;
      }
   }

   @Override
   public Collection<PlatformTypeToken> getPlatformTypes(long pageNum, long pageSize, AttributeTypeToken orderByAttributeTypeId) {
      return platformApi.getAll(branch, pageNum, pageSize, orderByAttributeTypeId);
   }

}