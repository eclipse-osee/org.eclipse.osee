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
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.mim.ArtifactAccessor;
import org.eclipse.osee.mim.InterfaceSubMessageApi;
import org.eclipse.osee.mim.types.InterfaceStructureToken;
import org.eclipse.osee.mim.types.InterfaceSubMessageToken;
import org.eclipse.osee.mim.types.MimAttributeQuery;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Luciano T. Vaglienti Api for accessing interface sub messages
 */
public class InterfaceSubMessageApiImpl implements InterfaceSubMessageApi {

   private ArtifactAccessor<InterfaceSubMessageToken> accessor;
   private final List<AttributeTypeId> subMessageAttributes;

   InterfaceSubMessageApiImpl(OrcsApi orcsApi) {
      this.setAccessor(new InterfaceSubMessageAccessor(orcsApi));
      this.subMessageAttributes = createSubmessageAttributes();
   }

   private void setAccessor(InterfaceSubMessageAccessor interfaceSubMessageAccessor) {
      this.accessor = interfaceSubMessageAccessor;
   }

   private List<AttributeTypeId> createSubmessageAttributes() {
      List<AttributeTypeId> attributes = new LinkedList<AttributeTypeId>();
      attributes.add(CoreAttributeTypes.Name);
      attributes.add(CoreAttributeTypes.Description);
      attributes.add(CoreAttributeTypes.InterfaceSubMessageNumber);
      return attributes;
   }

   @Override
   public ArtifactAccessor<InterfaceSubMessageToken> getAccessor() {
      return this.accessor;
   }

   @Override
   public Collection<InterfaceSubMessageToken> query(BranchId branch, MimAttributeQuery query) {
      try {
         return this.getAccessor().getAllByQuery(branch, query, InterfaceSubMessageToken.class);
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
         System.out.println(ex);
      }
      return new LinkedList<InterfaceSubMessageToken>();
   }

   @Override
   public Collection<InterfaceSubMessageToken> getAllByRelation(BranchId branch, ArtifactId messageId) {
      try {
         return this.getAccessor().getAllByRelation(branch, CoreRelationTypes.InterfaceMessageSubMessageContent_Message,
            messageId, InterfaceSubMessageToken.class);
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
         System.out.println(ex);
      }
      return new LinkedList<InterfaceSubMessageToken>();
   }

   @Override
   public Collection<InterfaceSubMessageToken> getAllByFilter(BranchId branch, String filter) {
      try {
         return this.getAccessor().getAllByFilter(branch, filter, subMessageAttributes, InterfaceSubMessageToken.class);
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
         System.out.println(ex);
      }
      return new LinkedList<InterfaceSubMessageToken>();
   }

   @Override
   public InterfaceSubMessageToken get(BranchId branch, ArtifactId subMessageId) {
      try {
         return this.getAccessor().get(branch, subMessageId, InterfaceSubMessageToken.class);
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
         System.out.println(ex);
      }
      return InterfaceSubMessageToken.SENTINEL;
   }

   @Override
   public List<InterfaceSubMessageToken> getAllRelatedFromStructure(InterfaceStructureToken structure) {
      return structure.getArtifactReadable().getRelated(
         CoreRelationTypes.InterfaceSubMessageContent_SubMessage).getList().stream().filter(
            a -> !a.getExistingAttributeTypes().isEmpty()).map(a -> new InterfaceSubMessageToken(a)).collect(
               Collectors.toList());
   }

   @Override
   public InterfaceSubMessageToken getWithAllParentRelations(BranchId branch, ArtifactId subMessageId) {
      try {
         List<RelationTypeSide> parentRelations =
            Arrays.asList(CoreRelationTypes.InterfaceMessageSubMessageContent_Message,
               CoreRelationTypes.InterfaceConnectionContent_Connection);
         return this.getAccessor().get(branch, subMessageId, parentRelations, InterfaceSubMessageToken.class);
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
         System.out.println(ex);
      }
      return InterfaceSubMessageToken.SENTINEL;
   }

}
