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
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.mim.InterfaceElementApi;
import org.eclipse.osee.mim.InterfaceElementArrayApi;
import org.eclipse.osee.mim.InterfaceElementSearchEndpoint;
import org.eclipse.osee.mim.InterfacePlatformTypeApi;
import org.eclipse.osee.mim.types.ElementPosition;
import org.eclipse.osee.mim.types.InterfaceStructureElementToken;
import org.eclipse.osee.mim.types.PlatformTypeToken;

/**
 * @author Luciano T. Vaglienti
 */
public class InterfaceElementSearchEndpointImpl implements InterfaceElementSearchEndpoint {

   private final BranchId branch;
   private final UserId account;
   private final InterfaceElementApi elementApi;
   private final InterfaceElementArrayApi elementArrayApi;
   private final InterfacePlatformTypeApi platformApi;

   public InterfaceElementSearchEndpointImpl(BranchId branch, UserId account, InterfaceElementApi interfaceElementApi, InterfaceElementArrayApi interfaceElementArrayApi, InterfacePlatformTypeApi interfacePlatformTypeApi) {
      this.account = account;
      this.branch = branch;
      this.elementApi = interfaceElementApi;
      this.elementArrayApi = interfaceElementArrayApi;
      this.platformApi = interfacePlatformTypeApi;
   }

   @Override
   public Collection<InterfaceStructureElementToken> getElements() {
      try {
         List<InterfaceStructureElementToken> elements =
            (List<InterfaceStructureElementToken>) elementApi.getAccessor().getAll(branch,
               InterfaceStructureElementToken.class);
         for (InterfaceStructureElementToken element : elements) {
            PlatformTypeToken platformType = platformApi.getAccessor().getByRelationWithoutId(branch,
               CoreRelationTypes.InterfaceElementPlatformType_Element, ArtifactId.valueOf(element.getId()),
               PlatformTypeToken.class);
            element.setPlatformTypeId(platformType.getId());
            element.setPlatformTypeName(platformType.getName());
         }
         return elements;
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
         return new LinkedList<InterfaceStructureElementToken>();
      }
   }

   @Override
   public Collection<InterfaceStructureElementToken> getElements(String filter) {
      try {
         List<InterfaceStructureElementToken> elements =
            (List<InterfaceStructureElementToken>) elementApi.getAccessor().getAllByFilter(branch, filter,
               InterfaceStructureElementToken.class);
         for (InterfaceStructureElementToken element : elements) {
            PlatformTypeToken platformType = platformApi.getAccessor().getByRelationWithoutId(branch,
               CoreRelationTypes.InterfaceElementPlatformType_Element, ArtifactId.valueOf(element.getId()),
               PlatformTypeToken.class);
            element.setPlatformTypeId(platformType.getId());
            element.setPlatformTypeName(platformType.getName());
         }
         return elements;
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
         return new LinkedList<InterfaceStructureElementToken>();
      }
   }

   @Override
   public Collection<InterfaceStructureElementToken> getElementsOfType(ArtifactId platformTypeId) {
      try {
         return elementApi.getAccessor().getAllByRelation(branch,
            CoreRelationTypes.InterfaceElementPlatformType_PlatformType, platformTypeId,
            InterfaceStructureElementToken.class);
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
         return new LinkedList<InterfaceStructureElementToken>();
      }
   }

   @Override
   public ElementPosition findElement(ArtifactId elementId) {
      //Todo at a later date, this endpoint is responsible for returning the relationship tree of an element so that users may return to the main page from an element search.
      //This ties directly to one of the actions out of a meeting on 6/22, corresponding with new UI, however it could be slightly optional. Likely will be worked during TW19361.
      return new ElementPosition();
   }

}
