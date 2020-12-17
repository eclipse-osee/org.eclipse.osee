/*********************************************************************
 * Copyright (c) 2019 Boeing
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

package org.eclipse.osee.framework.core.data;

import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.core.MediaType;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.osee.framework.core.OrcsTokenService;
import org.eclipse.osee.framework.core.enums.EnumToken;
import org.eclipse.osee.framework.core.enums.RelationSorter;
import org.eclipse.osee.framework.core.enums.RelationTypeMultiplicity;
import org.eclipse.osee.framework.jdk.core.type.ChainingArrayList;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.QuinFunction;

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
   private final List<OrcsTypeJoin<?, ?>> orcsTypeJoins = new ArrayList<>();
   private final NamespaceToken namespace;

   /**
    * @param namespace all type token registered with this object will be for this namespace
    */
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

   public List<ArtifactTypeToken> getArtifactTypes() {
      return artifactTypes;
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

      for (OrcsTypeJoin<?, ?> typeJoin : orcsTypeJoins) {
         if (typeJoin instanceof ArtifactTypeJoin) {
            tokenService.registerArtifactTypeJoin((ArtifactTypeJoin) typeJoin);
         } else if (typeJoin instanceof AttributeTypeJoin) {
            tokenService.registerAttributeTypeJoin((AttributeTypeJoin) typeJoin);
         } else if (typeJoin instanceof RelationTypeJoin) {
            tokenService.registerRelationTypeJoin((RelationTypeJoin) typeJoin);
         } else {
            throw new OseeArgumentException("Unexpected join type: ", typeJoin);
         }
      }
   }

   /**
    * Methods for creating ArtifactId AttributeType
    */
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

   /**
    * Methods for creating Boolean AttributeType
    */
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

   /**
    * Methods for creating BranchId AttributeType
    */
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

   /**
    * Methods for creating Date AttributeType
    */
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

   /**
    * Methods for creating Double AttributeType
    */
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

   /**
    * Methods for creating InputStream AttributeType
    */
   public @NonNull AttributeTypeInputStream createInputStream(Long id, String name, String mediaType, String description, TaggerTypeToken taggerType, String fileExtension) {
      return attributeTypes.addAndReturn(
         new AttributeTypeInputStream(id, namespace, name, mediaType, description, taggerType, fileExtension));
   }

   public @NonNull AttributeTypeInputStream createInputStream(Long id, String name, String mediaType, String description) {
      return createInputStream(id, name, mediaType, description, determineTaggerType(mediaType),
         defaultFileExtension(mediaType));
   }

   public @NonNull AttributeTypeInputStream createInputStream(Long id, String name, String mediaType, String description, String fileExtension) {
      return createInputStream(id, name, mediaType, description, determineTaggerType(mediaType), fileExtension);
   }

   public @NonNull AttributeTypeInputStream createInputStreamNoTag(Long id, String name, String mediaType, String description) {
      return createInputStream(id, name, mediaType, description, TaggerTypeToken.SENTINEL,
         defaultFileExtension(mediaType));
   }

   public @NonNull AttributeTypeInputStream createInputStreamNoTag(Long id, String name, String mediaType, String description, String fileExtension) {
      return createInputStream(id, name, mediaType, description, TaggerTypeToken.SENTINEL, fileExtension);
   }

   /**
    * Methods for creating Integer AttributeType
    */
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

   /**
    * Methods for creating Long AttributeType
    */
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

   /**
    * Methods for creating Enumerated AttributeType
    */

   public <T extends AttributeTypeEnum<? extends EnumToken>> T createEnum(T attributeType) {
      return attributeTypes.addAndReturn(attributeType);
   }

   public <T extends AttributeTypeEnum<? extends EnumToken>> T createEnumNoTag(T attributeType) {
      return attributeTypes.addAndReturn(attributeType);
   }

   public <T extends AttributeTypeEnum<? extends EnumToken>> T createEnum(QuinFunction<Long, String, TaggerTypeToken, String, NamespaceToken, T> attributeEnumConstructor, Long id, String name, String mediaType) {
      return attributeTypes.addAndReturn(
         attributeEnumConstructor.apply(id, name, determineTaggerType(mediaType), mediaType, namespace));
   }

   public <T extends AttributeTypeEnum<? extends EnumToken>> T createEnumNoTag(QuinFunction<Long, String, TaggerTypeToken, String, NamespaceToken, T> attributeEnumConstructor, Long id, String name, String mediaType) {
      return attributeTypes.addAndReturn(
         attributeEnumConstructor.apply(id, name, determineTaggerType(mediaType), mediaType, namespace));
   }

   public @NonNull DynamicEnumAttributeType createEnum(Long id, String name, String mediaType, String description, TaggerTypeToken taggerType) {
      return attributeTypes.addAndReturn(
         new DynamicEnumAttributeType(id, namespace, name, mediaType, description, taggerType));
   }

   public @NonNull DynamicEnumAttributeType createEnum(Long id, String name, String mediaType, String description) {
      return createEnum(id, name, mediaType, description, determineTaggerType(mediaType));
   }

   /**
    * Methods for creating String AttributeType
    */
   public @NonNull AttributeTypeString createString(Long id, String name, String mediaType, String description, TaggerTypeToken taggerType, String fileExtension, DisplayHint... displayHints) {
      return attributeTypes.addAndReturn(
         new AttributeTypeString(id, namespace, name, mediaType, description, taggerType, fileExtension, displayHints));
   }

   public @NonNull AttributeTypeString createString(Long id, String name, String mediaType, String description, DisplayHint... displayHints) {
      return createString(id, name, mediaType, description, determineTaggerType(mediaType),
         defaultFileExtension(mediaType), displayHints);
   }

   public @NonNull AttributeTypeString createString(Long id, String name, String mediaType, String description, String fileExtension, DisplayHint... displayHints) {
      return createString(id, name, mediaType, description, determineTaggerType(mediaType), fileExtension,
         displayHints);
   }

   public @NonNull AttributeTypeString createStringNoTag(Long id, String name, String mediaType, String description, DisplayHint... displayHints) {
      return createString(id, name, mediaType, description, TaggerTypeToken.SENTINEL, defaultFileExtension(mediaType));
   }

   public @NonNull AttributeTypeString createStringNoTag(Long id, String name, String mediaType, String description, String fileExtension) {
      return createString(id, name, mediaType, description, TaggerTypeToken.SENTINEL, fileExtension);
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

   public static String defaultFileExtension(String mediaType) {
      switch (mediaType) {
         case MediaType.TEXT_PLAIN:
         case AttributeTypeToken.APPLICATION_ZIP:
            return "txt";
         case MediaType.TEXT_XML:
         case AttributeTypeToken.APPLICATION_MSWORD:
            return "xml";
         case MediaType.TEXT_HTML:
            return "html";
         case MediaType.APPLICATION_OCTET_STREAM:
         case AttributeTypeToken.IMAGE:
            return "bin";
         default:
            return "";
      }
   }

   public int size() {
      return artifactTypes.size() + attributeTypes.size() + relationTypes.size();
   }

   @Override
   public String toString() {
      return namespace + ": " + size();
   }

   public ArtifactTypeJoin artifactTypeJoin(String name, ArtifactTypeToken... artifactTypes) {
      ArtifactTypeJoin typeJoin = new ArtifactTypeJoin(name, artifactTypes);
      orcsTypeJoins.add(typeJoin);
      return typeJoin;
   }

   public AttributeTypeJoin attributeTypeJoin(String name, AttributeTypeToken... attributeTypes) {
      AttributeTypeJoin typeJoin = new AttributeTypeJoin(name, attributeTypes);
      orcsTypeJoins.add(typeJoin);
      return typeJoin;
   }

   public RelationTypeJoin relationTypeJoin(String name, RelationTypeToken... relationTypes) {
      RelationTypeJoin typeJoin = new RelationTypeJoin(name, relationTypes);
      orcsTypeJoins.add(typeJoin);
      return typeJoin;
   }
}