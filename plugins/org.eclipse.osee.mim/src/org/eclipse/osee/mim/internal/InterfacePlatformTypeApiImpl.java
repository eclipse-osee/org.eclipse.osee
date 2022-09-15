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
import org.eclipse.osee.mim.InterfacePlatformTypeApi;
import org.eclipse.osee.mim.types.ArtifactMatch;
import org.eclipse.osee.mim.types.InterfaceEnumerationSet;
import org.eclipse.osee.mim.types.MimAttributeQuery;
import org.eclipse.osee.mim.types.PlatformTypeToken;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Luciano T. Vaglienti
 */
public class InterfacePlatformTypeApiImpl implements InterfacePlatformTypeApi {

   private ArtifactAccessor<PlatformTypeToken> accessor;
   private final List<AttributeTypeId> attributes;
   private final List<RelationTypeSide> affectedRelations;

   InterfacePlatformTypeApiImpl(OrcsApi orcsApi) {
      this.setAccessor(new PlatformTypeAccessor(orcsApi));
      this.attributes = this.createAttributeList();
      this.affectedRelations = this.createAffectedRelationTypeSideList();
   }

   @Override
   public ArtifactAccessor<PlatformTypeToken> getAccessor() {
      return accessor;
   }

   private List<RelationTypeSide> createAffectedRelationTypeSideList() {
      List<RelationTypeSide> relations = new LinkedList<RelationTypeSide>();
      relations.add(CoreRelationTypes.InterfaceElementPlatformType_PlatformType);
      return relations;
   }

   /**
    * @param accessor the accessor to set
    */
   private void setAccessor(ArtifactAccessor<PlatformTypeToken> accessor) {
      this.accessor = accessor;
   }

   @Override
   public Collection<PlatformTypeToken> query(BranchId branch, MimAttributeQuery query) {
      return this.query(branch, query, false);
   }

   @Override
   public PlatformTypeToken get(BranchId branch, ArtifactId platformTypeId) {
      try {
         return this.getAccessor().get(branch, platformTypeId,
            Arrays.asList(CoreRelationTypes.InterfacePlatformTypeEnumeration_EnumerationSet,
               CoreRelationTypes.InterfaceEnumeration_EnumerationState),
            PlatformTypeToken.class);
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
         System.out.println(ex);
      }
      return PlatformTypeToken.SENTINEL;
   }

   @Override
   public List<PlatformTypeToken> getAll(BranchId branch) {
      try {
         return (List<PlatformTypeToken>) this.getAccessor().getAll(branch, PlatformTypeToken.class);
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
         return new LinkedList<>();
      }
   }

   @Override
   public PlatformTypeToken getWithElementRelations(BranchId branch, ArtifactId platformTypeId) {
      try {
         return this.getAccessor().get(branch, platformTypeId,
            Arrays.asList(CoreRelationTypes.InterfaceElementPlatformType_Element), PlatformTypeToken.class);
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
         System.out.println(ex);
      }
      return PlatformTypeToken.SENTINEL;
   }

   @Override
   public PlatformTypeToken getWithRelations(BranchId branch, ArtifactId platformTypeId, List<RelationTypeSide> relationTypes) {
      try {
         return this.getAccessor().get(branch, platformTypeId, relationTypes, PlatformTypeToken.class);
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
         System.out.println(ex);
      }
      return PlatformTypeToken.SENTINEL;
   }

   @Override
   public PlatformTypeToken getWithAllParentRelations(BranchId branch, ArtifactId platformTypeId) {
      List<RelationTypeSide> relations = Arrays.asList(CoreRelationTypes.InterfaceElementPlatformType_Element,
         CoreRelationTypes.InterfaceStructureContent_Structure, CoreRelationTypes.InterfaceSubMessageContent_SubMessage,
         CoreRelationTypes.InterfaceMessageSubMessageContent_Message,
         CoreRelationTypes.InterfaceConnectionContent_Connection);
      return getWithRelations(branch, platformTypeId, relations);
   }

   @Override
   public List<PlatformTypeToken> getAllFromEnumerationSet(InterfaceEnumerationSet enumSet) {
      return enumSet.getArtifactReadable().getRelatedList(
         CoreRelationTypes.InterfacePlatformTypeEnumeration_Element).stream().filter(
            a -> !a.getExistingAttributeTypes().isEmpty()).map(a -> new PlatformTypeToken(a)).collect(
               Collectors.toList());
   }

   @Override
   public List<PlatformTypeToken> getAllWithRelations(BranchId branch, List<RelationTypeSide> relationTypes) {
      try {
         return (List<PlatformTypeToken>) this.getAccessor().getAll(branch, relationTypes, PlatformTypeToken.class);
      } catch (Exception ex) {
         //
      }
      return new LinkedList<PlatformTypeToken>();
   }

   @Override
   public List<PlatformTypeToken> getFilteredWithRelations(BranchId branch, String filter, List<RelationTypeSide> relationTypes) {
      try {
         return (List<PlatformTypeToken>) this.getAccessor().getAllByFilter(branch, filter, attributes, relationTypes,
            PlatformTypeToken.class);
      } catch (Exception ex) {
         //
      }
      return new LinkedList<PlatformTypeToken>();
   }

   @Override
   public List<PlatformTypeToken> getAllWithElementRelations(BranchId branch) {
      return this.getAllWithRelations(branch, Arrays.asList(CoreRelationTypes.InterfaceElementPlatformType_Element,
         CoreRelationTypes.InterfaceStructureContent_Structure, CoreRelationTypes.InterfaceSubMessageContent_SubMessage,
         CoreRelationTypes.InterfaceMessageSubMessageContent_Message,
         CoreRelationTypes.InterfaceConnectionContent_Connection));
   }

   @Override
   public List<PlatformTypeToken> getFilteredWithElementRelations(BranchId branch, String filter) {
      return this.getFilteredWithRelations(branch, filter,
         Arrays.asList(CoreRelationTypes.InterfaceElementPlatformType_Element,
            CoreRelationTypes.InterfaceStructureContent_Structure,
            CoreRelationTypes.InterfaceSubMessageContent_SubMessage,
            CoreRelationTypes.InterfaceMessageSubMessageContent_Message,
            CoreRelationTypes.InterfaceConnectionContent_Connection));
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
   public Collection<PlatformTypeToken> queryExact(BranchId branch, MimAttributeQuery query) {
      return this.query(branch, query, true);
   }

   @Override
   public Collection<PlatformTypeToken> query(BranchId branch, MimAttributeQuery query, boolean isExact) {
      try {
         return this.getAccessor().getAllByQuery(branch, query,
            Arrays.asList(CoreRelationTypes.InterfacePlatformTypeEnumeration_EnumerationSet,
               CoreRelationTypes.InterfaceEnumeration_EnumerationState),
            isExact, PlatformTypeToken.class);
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
         System.out.println(ex);
      }
      return new LinkedList<PlatformTypeToken>();
   }

   @Override
   public List<PlatformTypeToken> getAllWithEnumSet(BranchId branch) {
      return this.getAllWithRelations(branch,
         Arrays.asList(CoreRelationTypes.InterfacePlatformTypeEnumeration_EnumerationSet));
   }

   @Override
   public Collection<ArtifactMatch> getAffectedArtifacts(BranchId branch, ArtifactId relatedId) {
      try {
         return this.getAccessor().getAffectedArtifacts(branch, relatedId, affectedRelations);
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
         //
      }
      return new LinkedList<ArtifactMatch>();
   }

}
