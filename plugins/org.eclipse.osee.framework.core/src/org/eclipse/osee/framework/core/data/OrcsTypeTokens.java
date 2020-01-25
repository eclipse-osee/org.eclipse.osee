/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.data;

import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.core.MediaType;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.osee.framework.core.enums.EnumToken;
import org.eclipse.osee.framework.core.enums.RelationSorter;
import org.eclipse.osee.framework.core.enums.RelationTypeMultiplicity;
import org.eclipse.osee.framework.jdk.core.type.ChainingArrayList;
import org.eclipse.osee.framework.jdk.core.type.QuinFunction;
import org.eclipse.osee.framework.jdk.core.type.TriFunction;

/**
 * OSEE type token providers should instantiate a static instance of this class and call the add methods for each type
 * token they create.
 *
 * @author Ryan D. Brooks
 */
public class OrcsTypeTokens {
   private final List<ArtifactTypeToken> artifactTypes = new ArrayList<>();
   private final ChainingArrayList<@NonNull AttributeTypeGeneric<?>> attributeTypes = new ChainingArrayList<>();
   private final ChainingArrayList<@NonNull RelationTypeToken> relationTypes = new ChainingArrayList<>();
   private final NamespaceToken namespace;

   public OrcsTypeTokens() {
      this.namespace = NamespaceToken.OSEE;
   }

   public OrcsTypeTokens(NamespaceToken namespace) {
      this.namespace = namespace;
   }

   public NamespaceToken getNamespace() {
      return namespace;
   }

   public AttributeMultiplicity artifactType(Long id, String name, boolean isAbstract, ArtifactTypeToken... superTypes) {
      return new AttributeMultiplicity(id, namespace, name, isAbstract, superTypes);
   }

   public List<AttributeTypeGeneric<?>> getAttributeTypes() {
      return attributeTypes;
   }

   public ArtifactTypeToken add(AttributeMultiplicity attributeMultiplicity) {
      ArtifactTypeToken artifactType = attributeMultiplicity.get();
      artifactTypes.add(artifactType);
      return artifactType;
   }

   public <V, @NonNull T extends AttributeTypeGeneric<V>> T add(T attributeType) {
      attributeTypes.add(attributeType);
      return attributeType;
   }

   public RelationTypeToken add(long id, String name, RelationTypeMultiplicity relationTypeMultiplicity, RelationSorter order, ArtifactTypeToken artifactTypeA, String sideAName, ArtifactTypeToken artifactTypeB, String sideBName) {
      return relationTypes.addAndReturn(RelationTypeToken.create(id, name, relationTypeMultiplicity, order,
         artifactTypeA, sideAName, artifactTypeB, sideBName));
   }

   public void registerTypes(OrcsTokenService tokenService) {
      artifactTypes.forEach(tokenService::registerArtifactType);
      attributeTypes.forEach(tokenService::registerAttributeType);
      relationTypes.forEach(tokenService::registerRelationType);
   }

   public @NonNull AttributeTypeArtifactId createArtifactId(Long id, String name, String mediaType, String description, TaggerTypeToken taggerType) {
      return attributeTypes.addAndReturn(
         new AttributeTypeArtifactId(id, namespace, name, mediaType, description, taggerType));
   }

   public @NonNull AttributeTypeArtifactId createArtifactId(Long id, String name, String mediaType, String description) {
      return createArtifactId(id, name, mediaType, description, determineTaggerType(mediaType));
   }

   public @NonNull AttributeTypeArtifactId createArtifactIdNoTag(Long id, String name, String mediaType, String description) {
      return createArtifactId(id, name, mediaType, description, TaggerTypeToken.SENTINEL);
   }

   public @NonNull AttributeTypeBoolean createBoolean(Long id, String name, String mediaType, String description, TaggerTypeToken taggerType) {
      return attributeTypes.addAndReturn(
         new AttributeTypeBoolean(id, namespace, name, mediaType, description, taggerType));
   }

   public @NonNull AttributeTypeBoolean createBoolean(Long id, String name, String mediaType, String description) {
      return createBoolean(id, name, mediaType, description, determineTaggerType(mediaType));
   }

   public @NonNull AttributeTypeBoolean createBooleanNoTag(Long id, String name, String mediaType, String description) {
      return createBoolean(id, name, mediaType, description, TaggerTypeToken.SENTINEL);
   }

   public @NonNull AttributeTypeBranchId createBranchId(Long id, String name, String mediaType, String description, TaggerTypeToken taggerType) {
      return attributeTypes.addAndReturn(
         new AttributeTypeBranchId(id, namespace, name, mediaType, description, taggerType));
   }

   public @NonNull AttributeTypeBranchId createBranchId(Long id, String name, String mediaType, String description) {
      return createBranchId(id, name, mediaType, description, determineTaggerType(mediaType));
   }

   public @NonNull AttributeTypeBranchId createBranchIdNoTag(Long id, String name, String mediaType, String description) {
      return createBranchId(id, name, mediaType, description, TaggerTypeToken.SENTINEL);
   }

   public @NonNull AttributeTypeDate createDate(Long id, String name, String mediaType, String description, TaggerTypeToken taggerType) {
      return attributeTypes.addAndReturn(
         new AttributeTypeDate(id, namespace, name, mediaType, description, taggerType));
   }

   public @NonNull AttributeTypeDate createDate(Long id, String name, String mediaType, String description) {
      return createDate(id, name, mediaType, description, determineTaggerType(mediaType));
   }

   public @NonNull AttributeTypeDate createDateNoTag(Long id, String name, String mediaType, String description) {
      return createDate(id, name, mediaType, description, TaggerTypeToken.SENTINEL);
   }

   public @NonNull AttributeTypeDouble createDouble(Long id, String name, String mediaType, String description, TaggerTypeToken taggerType) {
      return attributeTypes.addAndReturn(
         new AttributeTypeDouble(id, namespace, name, mediaType, description, taggerType));
   }

   public @NonNull AttributeTypeDouble createDouble(Long id, String name, String mediaType, String description) {
      return createDouble(id, name, mediaType, description, determineTaggerType(mediaType));
   }

   public @NonNull AttributeTypeDouble createDoubleNoTag(Long id, String name, String mediaType, String description) {
      return createDouble(id, name, mediaType, description, TaggerTypeToken.SENTINEL);
   }

   public @NonNull AttributeTypeInputStream createInputStream(Long id, String name, String mediaType, String description, TaggerTypeToken taggerType) {
      return attributeTypes.addAndReturn(
         new AttributeTypeInputStream(id, namespace, name, mediaType, description, taggerType));
   }

   public @NonNull AttributeTypeInputStream createInputStream(Long id, String name, String mediaType, String description) {
      return createInputStream(id, name, mediaType, description, determineTaggerType(mediaType));
   }

   public @NonNull AttributeTypeInputStream createInputStreamNoTag(Long id, String name, String mediaType, String description) {
      return createInputStream(id, name, mediaType, description, TaggerTypeToken.SENTINEL);
   }

   public @NonNull AttributeTypeInteger createInteger(Long id, String name, String mediaType, String description, TaggerTypeToken taggerType) {
      return attributeTypes.addAndReturn(
         new AttributeTypeInteger(id, namespace, name, mediaType, description, taggerType));
   }

   public @NonNull AttributeTypeInteger createInteger(Long id, String name, String mediaType, String description) {
      return createInteger(id, name, mediaType, description, determineTaggerType(mediaType));
   }

   public @NonNull AttributeTypeInteger createIntegerNoTag(Long id, String name, String mediaType, String description) {
      return createInteger(id, name, mediaType, description, TaggerTypeToken.SENTINEL);
   }

   public @NonNull AttributeTypeLong createLong(Long id, String name, String mediaType, String description, TaggerTypeToken taggerType) {
      return attributeTypes.addAndReturn(
         new AttributeTypeLong(id, namespace, name, mediaType, description, taggerType));
   }

   public @NonNull AttributeTypeLong createLong(Long id, String name, String mediaType, String description) {
      return createLong(id, name, mediaType, description, determineTaggerType(mediaType));
   }

   public @NonNull AttributeTypeLong createLongNoTag(Long id, String name, String mediaType, String description) {
      return createLong(id, name, mediaType, description, TaggerTypeToken.SENTINEL);
   }

   public <T extends AttributeTypeEnum<? extends EnumToken>> T createEnum(TriFunction<TaggerTypeToken, String, NamespaceToken, T> attributeEnumConstructor, String mediaType) {
      return attributeTypes.addAndReturn(
         attributeEnumConstructor.apply(OrcsTypeTokens.determineTaggerType(mediaType), mediaType, namespace));
   }

   public <T extends AttributeTypeEnum<? extends EnumToken>> T createEnumNoTag(TriFunction<TaggerTypeToken, String, NamespaceToken, T> attributeEnumConstructor, String mediaType) {
      return attributeTypes.addAndReturn(
         attributeEnumConstructor.apply(TaggerTypeToken.SENTINEL, mediaType, namespace));
   }

   public <T extends AttributeTypeEnum<? extends EnumToken>> T createEnum(QuinFunction<Long, String, TaggerTypeToken, String, NamespaceToken, T> attributeEnumConstructor, Long id, String name, String mediaType) {
      return attributeTypes.addAndReturn(
         attributeEnumConstructor.apply(id, name, OrcsTypeTokens.determineTaggerType(mediaType), mediaType, namespace));
   }

   public <T extends AttributeTypeEnum<? extends EnumToken>> T createEnumNoTag(QuinFunction<Long, String, TaggerTypeToken, String, NamespaceToken, T> attributeEnumConstructor, Long id, String name, String mediaType) {
      return attributeTypes.addAndReturn(
         attributeEnumConstructor.apply(id, name, OrcsTypeTokens.determineTaggerType(mediaType), mediaType, namespace));
   }

   public @NonNull AttributeTypeEnum createEnum(Long id, String name, String mediaType, String description, TaggerTypeToken taggerType) {
      return attributeTypes.addAndReturn(
         new AttributeTypeEnum(id, namespace, name, mediaType, description, taggerType));
   }

   public @NonNull AttributeTypeEnum createEnum(Long id, String name, String mediaType, String description) {
      return createEnum(id, name, mediaType, description, determineTaggerType(mediaType));
   }

   public @NonNull AttributeTypeEnum createEnumNoTag(Long id, String name, String mediaType, String description) {
      return createEnum(id, name, mediaType, description, TaggerTypeToken.SENTINEL);
   }

   public @NonNull AttributeTypeString createString(Long id, String name, String mediaType, String description, TaggerTypeToken taggerType) {
      return attributeTypes.addAndReturn(
         new AttributeTypeString(id, namespace, name, mediaType, description, taggerType));
   }

   public @NonNull AttributeTypeString createString(Long id, String name, String mediaType, String description) {
      return createString(id, name, mediaType, description, determineTaggerType(mediaType));
   }

   public @NonNull AttributeTypeString createStringNoTag(Long id, String name, String mediaType, String description) {
      return createString(id, name, mediaType, description, TaggerTypeToken.SENTINEL);
   }

   /**
    * return the default tagger for the given mediaType
    */
   public static TaggerTypeToken determineTaggerType(String mediaType) {
      switch (mediaType) {
         case "application/msword":
         case MediaType.TEXT_HTML:
            return TaggerTypeToken.XmlTagger;
         default:
            return TaggerTypeToken.PlainTextTagger;
      }
   }
}