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
import java.util.Arrays;
import java.util.List;
import javax.ws.rs.core.MediaType;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.osee.framework.core.OrcsTokenService;
import org.eclipse.osee.framework.core.enums.EnumToken;
import org.eclipse.osee.framework.core.enums.OseeImage;
import org.eclipse.osee.framework.core.enums.RelationSorter;
import org.eclipse.osee.framework.core.enums.RelationTypeMultiplicity;
import org.eclipse.osee.framework.jdk.core.type.ChainingArrayList;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.type.SexFunction;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * OSEE type token providers should instantiate a static instance of this class and call the add methods for each type
 * token they create.
 *
 * @author Ryan D. Brooks
 */
public class OrcsTypeTokens {

   /**
    * Functional interface for a method that creates a specialized instance of the {@link AttributeTypeEnum} class for
    * an enumerated attribute.
    * <p>
    * The functional interface method is:
    * <ul>
    * <li>{@link AttributeEnumConstructor#apply}</li>
    * </ul>
    *
    * @param <T> the class extending {@link AttributeTypeEnum} that is specialized with an extension of the class
    * {@link EnumToken} for the attribute's enumeration members.
    */

   @FunctionalInterface
   public interface AttributeEnumConstructor<T extends AttributeTypeEnum<? extends EnumToken>> {

      /**
       * Creates an implementation of the {@link AttributeTypeToken} interface for an enumerated attribute.
       *
       * @param identifier a unique random long identifier for the attribute type.
       * @param name the display name for the attribute type.
       * @param description a description of the attribute type.
       * @param taggerTypeToken the tagger type for the attribute.
       * @param mediaType the media type for the attribute's value.
       * @param namespaceToken the name space to create the attribute token for.
       * @return the newly created {@link AttributeTypeEnum} object.
       */

      T apply(Long identifier, String name, String description, TaggerTypeToken taggerTypeToken, String mediaType,
         NamespaceToken namespaceToken);
   }

   /**
    * Functional interface for a method that creates a specialized instance of the {@link AttributeTypeEnum} class for
    * an enumerated attribute.
    * <p>
    * The functional interface method is:
    * <ul>
    * <li>{@link AttributeEnumConstructor#apply}</li>
    * </ul>
    *
    * @param <T> the class extending {@link AttributeTypeEnum} that is specialized with an extension of the class
    * {@link EnumToken} for the attribute's enumeration members.
    */

   @FunctionalInterface
   public interface AttributeEnumConstructorNoDescription<T extends AttributeTypeEnum<? extends EnumToken>> {

      /**
       * Creates an implementation of the {@link AttributeTypeToken} interface for an enumerated attribute.
       *
       * @param identifier a unique random long identifier for the attribute type.
       * @param name the display name for the attribute type.
       * @param taggerTypeToken the tagger type for the attribute.
       * @param mediaType the media type for the attribute's value.
       * @param namespaceToken the name space to create the attribute token for.
       * @return the newly created {@link AttributeTypeEnum} object.
       */

      T apply(Long identifier, String name, TaggerTypeToken taggerType, String mediaType,
         NamespaceToken namespaceToken);
   }

   /**
    * Functional interface for a method to obtain an attribute's display name and description.
    */

   @FunctionalInterface
   public interface AttributeDisplayNameAndDescriptionSupplier {

      /**
       * Gets the attribute's display name and description.
       *
       * @return a {@link Pair} where the first string is the attribute name and the second string is the attribute
       * description.
       */

      Pair<String, String> get();
   }

   /**
    * Functional interface for a method to obtain an attribute's display name.
    */

   @FunctionalInterface
   public interface AttributeDisplayNameSupplier {

      /**
       * Gets the attribute's display name.
       *
       * @return the attribute's display name.
       */

      String get();
   }

   private final List<ArtifactTypeToken> artifactTypes = new ArrayList<>();
   private final ChainingArrayList<@NonNull AttributeTypeGeneric<?>> attributeTypes = new ChainingArrayList<>();
   private final ChainingArrayList<@NonNull RelationTypeToken> relationTypes = new ChainingArrayList<>();
   private final ChainingArrayList<@NonNull ComputedCharacteristic<?>> computedCharacteristics =
      new ChainingArrayList<>();
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

   public AttributeMultiplicity artifactType(Long id, String name, boolean isAbstract, OseeImage image,
      ArtifactTypeToken... superTypes) {
      return new AttributeMultiplicity(id, namespace, name, isAbstract, image, superTypes);
   }

   public AttributeMultiplicity artifactType(Long id, String name, boolean isAbstract,
      ArtifactTypeToken... superTypes) {
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

   public void validateString(String value) {
      if (Strings.isInvalidOrBlank(value)) {
         throw new OseeArgumentException("Invalid Parameter");
      }
   }

   public RelationTypeToken addNewRelationType(long id, String name, RelationTypeMultiplicity relationTypeMultiplicity,
      RelationSorter order, ArtifactTypeToken artifactTypeA, String sideAName, ArtifactTypeToken artifactTypeB,
      String sideBName) {
      validateString(name);
      return relationTypes.addAndReturn(RelationTypeToken.create(id, name, relationTypeMultiplicity, order,
         artifactTypeA, sideAName, artifactTypeB, sideBName, ArtifactTypeToken.SENTINEL, true));
   }

   public RelationTypeToken addNewRelationType(long id, String name, RelationTypeMultiplicity relationTypeMultiplicity,
      RelationSorter order, ArtifactTypeToken artifactTypeA, String sideAName, ArtifactTypeToken artifactTypeB,
      String sideBName, ArtifactTypeToken relationArtifactType) {
      validateString(name);
      return relationTypes.addAndReturn(RelationTypeToken.create(id, name, relationTypeMultiplicity, order,
         artifactTypeA, sideAName, artifactTypeB, sideBName, relationArtifactType, true));
   }

   public RelationTypeToken add(long id, String name, RelationTypeMultiplicity relationTypeMultiplicity,
      RelationSorter order, ArtifactTypeToken artifactTypeA, String sideAName, ArtifactTypeToken artifactTypeB,
      String sideBName) {
      validateString(name);
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
   public @NonNull AttributeTypeArtifactId createArtifactId(Long id, String name, String mediaType, String description,
      TaggerTypeToken taggerType, DisplayHint... displayHints) {
      validateString(name);
      AttributeTypeArtifactId attrType = attributeTypes.addAndReturn(
         new AttributeTypeArtifactId(id, namespace, name, mediaType, description, taggerType));
      if (attrType.getDisplayHints().contains(DisplayHint.MultiLine)) {
         attrType.addDisplayHint(DisplayHint.SingleLine);
      }
      for (DisplayHint hint : displayHints) {
         attrType.getDisplayHints().add(hint);
      }
      return attrType;
   }

   public @NonNull AttributeTypeArtifactId createArtifactId(Long id, String name, String mediaType, String description,
      DisplayHint... displayHints) {
      return createArtifactId(id, name, mediaType, description, determineTaggerType(mediaType), displayHints);
   }

   public @NonNull AttributeTypeArtifactId createArtifactIdNoTag(Long id, String name, String mediaType,
      String description, DisplayHint... displayHints) {
      return createArtifactId(id, name, mediaType, description, TaggerTypeToken.SENTINEL, displayHints);
   }

   /**
    * Methods for creating Boolean AttributeType
    */
   public @NonNull AttributeTypeBoolean createBoolean(Long id, String name, String mediaType, String description,
      TaggerTypeToken taggerType, DisplayHint... displayHints) {
      validateString(name);
      return attributeTypes.addAndReturn(
         new AttributeTypeBoolean(id, namespace, name, mediaType, description, taggerType, displayHints));
   }

   public @NonNull AttributeTypeBoolean createBoolean(Long id, String name, String mediaType, String description,
      DisplayHint... displayHints) {
      return createBoolean(id, name, mediaType, description, determineTaggerType(mediaType), displayHints);
   }

   public @NonNull AttributeTypeBoolean createBooleanNoTag(Long id, String name, String mediaType, String description,
      DisplayHint... displayHints) {
      return createBoolean(id, name, mediaType, description, TaggerTypeToken.SENTINEL, displayHints);
   }

   /**
    * Methods for creating BranchId AttributeType
    */
   public @NonNull AttributeTypeBranchId createBranchId(Long id, String name, String mediaType, String description,
      TaggerTypeToken taggerType) {
      validateString(name);
      return attributeTypes.addAndReturn(
         new AttributeTypeBranchId(id, namespace, name, mediaType, description, taggerType));
   }

   public @NonNull AttributeTypeBranchId createBranchId(Long id, String name, String mediaType, String description) {
      return createBranchId(id, name, mediaType, description, determineTaggerType(mediaType));
   }

   public @NonNull AttributeTypeBranchId createBranchIdNoTag(Long id, String name, String mediaType,
      String description) {
      return createBranchId(id, name, mediaType, description, TaggerTypeToken.SENTINEL);
   }

   /**
    * Methods for creating Date AttributeType
    */
   public @NonNull AttributeTypeDate createDate(Long id, String name, String mediaType, String description,
      TaggerTypeToken taggerType, DisplayHint... displayHints) {
      validateString(name);
      AttributeTypeDate type =
         attributeTypes.addAndReturn(new AttributeTypeDate(id, namespace, name, mediaType, description, taggerType));
      for (DisplayHint hint : displayHints) {
         type.addDisplayHint(hint);
      }
      return type;

   }

   public @NonNull AttributeTypeDate createDate(Long id, String name, String mediaType, String description,
      DisplayHint... displayHints) {
      return createDate(id, name, mediaType, description, determineTaggerType(mediaType), displayHints);
   }

   public @NonNull AttributeTypeDate createDateNoTag(Long id, String name, String mediaType, String description,
      DisplayHint... displayHints) {
      return createDate(id, name, mediaType, description, TaggerTypeToken.SENTINEL, displayHints);
   }

   /**
    * Methods for creating Double AttributeType
    */
   public @NonNull AttributeTypeDouble createDouble(Long id, String name, String mediaType, String description,
      TaggerTypeToken taggerType, DisplayHint... displayHints) {
      validateString(name);
      AttributeTypeDouble attrType = attributeTypes.addAndReturn(
         new AttributeTypeDouble(id, namespace, name, mediaType, description, taggerType, displayHints));
      if (attrType.getDisplayHints().contains(DisplayHint.MultiLine)) {
         attrType.addDisplayHint(DisplayHint.SingleLine);
      }
      return attrType;
   }

   public @NonNull AttributeTypeDouble createDouble(Long id, String name, String mediaType, String description,
      DisplayHint... displayHints) {
      return createDouble(id, name, mediaType, description, determineTaggerType(mediaType), displayHints);
   }

   public @NonNull AttributeTypeDouble createDoubleNoTag(Long id, String name, String mediaType, String description,
      DisplayHint... displayHints) {
      return createDouble(id, name, mediaType, description, TaggerTypeToken.SENTINEL, displayHints);
   }

   /**
    * Methods for creating InputStream AttributeType
    */
   public @NonNull AttributeTypeInputStream createInputStream(Long id, String name, String mediaType,
      String description, TaggerTypeToken taggerType, String fileExtension) {
      validateString(name);
      return attributeTypes.addAndReturn(
         new AttributeTypeInputStream(id, namespace, name, mediaType, description, taggerType, fileExtension));
   }

   public @NonNull AttributeTypeInputStream createInputStream(Long id, String name, String mediaType,
      String description) {
      return createInputStream(id, name, mediaType, description, determineTaggerType(mediaType),
         defaultFileExtension(mediaType));
   }

   public @NonNull AttributeTypeInputStream createInputStream(Long id, String name, String mediaType,
      String description, String fileExtension) {
      return createInputStream(id, name, mediaType, description, determineTaggerType(mediaType), fileExtension);
   }

   public @NonNull AttributeTypeInputStream createInputStreamNoTag(Long id, String name, String mediaType,
      String description) {
      return createInputStream(id, name, mediaType, description, TaggerTypeToken.SENTINEL,
         defaultFileExtension(mediaType));
   }

   public @NonNull AttributeTypeInputStream createInputStreamNoTag(Long id, String name, String mediaType,
      String description, String fileExtension) {
      return createInputStream(id, name, mediaType, description, TaggerTypeToken.SENTINEL, fileExtension);
   }

   /**
    * Methods for creating Integer AttributeType
    */
   public @NonNull AttributeTypeInteger createInteger(Long id, String name, String mediaType, String description,
      TaggerTypeToken taggerType, DisplayHint... displayHints) {
      validateString(name);
      AttributeTypeInteger attrType =
         attributeTypes.addAndReturn(new AttributeTypeInteger(id, namespace, name, mediaType, description, taggerType));
      if (attrType.getDisplayHints().contains(DisplayHint.MultiLine)) {
         attrType.addDisplayHint(DisplayHint.SingleLine);
      }
      for (DisplayHint hint : displayHints) {
         attrType.addDisplayHint(hint);
      }
      return attrType;
   }

   public @NonNull AttributeTypeInteger createInteger(Long id, String name, String mediaType, String description,
      DisplayHint... displayHints) {
      return createInteger(id, name, mediaType, description, determineTaggerType(mediaType), displayHints);
   }

   public @NonNull AttributeTypeInteger createIntegerNoTag(Long id, String name, String mediaType, String description,
      DisplayHint... displayHints) {
      return createInteger(id, name, mediaType, description, TaggerTypeToken.SENTINEL, displayHints);
   }

   /**
    * Methods for creating Long AttributeType
    */
   public @NonNull AttributeTypeLong createLong(Long id, String name, String mediaType, String description,
      TaggerTypeToken taggerType, DisplayHint... displayHints) {
      validateString(name);
      AttributeTypeLong attrType =
         attributeTypes.addAndReturn(new AttributeTypeLong(id, namespace, name, mediaType, description, taggerType));
      if (attrType.getDisplayHints().contains(DisplayHint.MultiLine)) {
         attrType.addDisplayHint(DisplayHint.SingleLine);
      }
      for (DisplayHint hint : displayHints) {
         attrType.addDisplayHint(hint);
      }
      return attrType;
   }

   public @NonNull AttributeTypeLong createLong(Long id, String name, String mediaType, String description,
      DisplayHint... displayHints) {
      return createLong(id, name, mediaType, description, determineTaggerType(mediaType), displayHints);
   }

   public @NonNull AttributeTypeLong createLongNoTag(Long id, String name, String mediaType, String description,
      DisplayHint... displayHints) {
      return createLong(id, name, mediaType, description, TaggerTypeToken.SENTINEL, displayHints);
   }

   /**
    * Methods for creating Enumerated AttributeType
    */

   public @NonNull <T extends AttributeTypeEnum<? extends EnumToken>> T createEnum(T attributeType,
      DisplayHint... displayHints) {
      T type = attributeTypes.addAndReturn(attributeType);
      for (DisplayHint hint : displayHints) {
         type.addDisplayHint(hint);
      }
      return type;
   }

   public @NonNull <T extends AttributeTypeEnum<? extends EnumToken>> T createEnumNoTag(T attributeType,
      DisplayHint... displayHints) {
      T type = attributeTypes.addAndReturn(attributeType);
      for (DisplayHint hint : displayHints) {
         type.addDisplayHint(hint);
      }
      return type;
   }

   public @NonNull DynamicEnumAttributeType createEnum(Long id, String name, String mediaType, String description,
      TaggerTypeToken taggerType, DisplayHint... displayHints) {
      validateString(name);
      DynamicEnumAttributeType type = attributeTypes.addAndReturn(
         new DynamicEnumAttributeType(id, namespace, name, mediaType, description, taggerType));
      for (DisplayHint hint : displayHints) {
         type.addDisplayHint(hint);
      }
      return type;
   }

   public @NonNull DynamicEnumAttributeType createEnum(Long id, String name, String mediaType, String description) {
      return createEnum(id, name, mediaType, description, determineTaggerType(mediaType));
   }

   public @NonNull <T extends AttributeTypeEnum<? extends EnumToken>> T createEnum(
      AttributeEnumConstructor<T> attributeEnumConstructor, Long identifier, String name, String description,
      String mediaType, NamespaceToken namespaceToken, TaggerTypeToken taggerTypeToken, DisplayHint... displayHints) {
      validateString(name);
      @NonNull
      T type = attributeTypes.addAndReturn(
         attributeEnumConstructor.apply(identifier, name, description, taggerTypeToken, mediaType, namespaceToken));
      for (DisplayHint hint : displayHints) {
         type.addDisplayHint(hint);
      }
      return type;
   }

   public @NonNull <T extends AttributeTypeEnum<? extends EnumToken>> T createEnum(
      AttributeEnumConstructorNoDescription<T> attributeEnumConstructor, Long identifier, String name, String mediaType,
      DisplayHint... displayHints) {
      validateString(name);
      T type = attributeTypes.addAndReturn(attributeEnumConstructor.apply(identifier, name,
         OrcsTypeTokens.determineTaggerType(mediaType), mediaType, this.namespace));
      for (DisplayHint hint : displayHints) {
         type.addDisplayHint(hint);
      }
      return type;
   }

   public @NonNull <T extends AttributeTypeEnum<? extends EnumToken>> T createEnum(
      AttributeEnumConstructor<T> attributeEnumConstructor,
      AttributeDisplayNameAndDescriptionSupplier attributeDisplayNameAndDescriptionSupplier, Long identifier,
      String mediaType, NamespaceToken namespaceToken, TaggerTypeToken taggerTypeToken) {
      var nameAndDescription = attributeDisplayNameAndDescriptionSupplier.get();
      var attrType = attributeTypes.addAndReturn(attributeEnumConstructor.apply(identifier,
         nameAndDescription.getFirst(), nameAndDescription.getSecond(), taggerTypeToken, mediaType, namespaceToken));
      attrType.addDisplayHint(DisplayHint.MultiLine);
      return attrType;
   }

   public @NonNull <T extends AttributeTypeEnum<? extends EnumToken>> T createEnum(
      AttributeEnumConstructorNoDescription<T> attributeEnumConstructorNoDescription,
      AttributeDisplayNameSupplier attributeDisplayNameSupplier, Long identifier, String mediaType,
      NamespaceToken namespaceToken, TaggerTypeToken taggerTypeToken, DisplayHint... displayHints) {
      var name = attributeDisplayNameSupplier.get();
      var attrType = attributeTypes.addAndReturn(
         attributeEnumConstructorNoDescription.apply(identifier, name, taggerTypeToken, mediaType, namespaceToken));
      attrType.addDisplayHint(DisplayHint.MultiLine);
      for (DisplayHint hint : displayHints) {
         attrType.addDisplayHint(hint);
      }
      return attrType;
   }

   /**
    * Methods for creating String AttributeType
    */

   public @NonNull AttributeTypeString createString(Long id, String name, String mediaType, String description,
      TaggerTypeToken taggerType, String fileExtension, DisplayHint... displayHints) {
      validateString(name);
      return attributeTypes.addAndReturn(
         new AttributeTypeString(id, namespace, name, mediaType, description, taggerType, fileExtension, displayHints));
   }

   public @NonNull AttributeTypeString createString(Long id, String name, String mediaType, String description,
      DisplayHint... displayHints) {
      return createString(id, name, mediaType, description, determineTaggerType(mediaType),
         defaultFileExtension(mediaType), displayHints);
   }

   public @NonNull AttributeTypeString createString(Long id, String name, String mediaType, String description,
      String fileExtension, DisplayHint... displayHints) {
      return createString(id, name, mediaType, description, determineTaggerType(mediaType), fileExtension,
         displayHints);
   }

   public @NonNull AttributeTypeString createStringNoTag(Long id, String name, String mediaType, String description,
      DisplayHint... displayHints) {
      return createString(id, name, mediaType, description, TaggerTypeToken.SENTINEL, defaultFileExtension(mediaType),
         displayHints);
   }

   public @NonNull AttributeTypeString createStringNoTag(Long id, String name, String mediaType, String description,
      String fileExtension, DisplayHint... displayHints) {
      return createString(id, name, mediaType, description, TaggerTypeToken.SENTINEL, fileExtension, displayHints);
   }

   public @NonNull AttributeTypeString createString(
      AttributeDisplayNameAndDescriptionSupplier attributeNameAndDescriptionSupplier, Long identifier, String mediaType,
      TaggerTypeToken taggerTypeToken, NamespaceToken namespaceToken, String fileExtension,
      DisplayHint... displayHints) {
      var nameAndDescription = attributeNameAndDescriptionSupplier.get();
      return attributeTypes.addAndReturn(
         new AttributeTypeString(identifier, namespaceToken, nameAndDescription.getFirst(), mediaType,
            nameAndDescription.getSecond(), taggerTypeToken, fileExtension, displayHints));
   }

   /*
    * Methods for creating MapEntry AttributeType
    */

   public @NonNull AttributeTypeMapEntry createMapEntry(Long id, String name, String description, String defaultKey,
      String defaultValue) {
      validateString(name);
      return attributeTypes.addAndReturn(
         new AttributeTypeMapEntry(id, this.namespace, name, description, defaultKey, defaultValue));
   }

   /**
    * Methods for creating Computed Characteristics
    */

   public <T, U extends ComputedCharacteristic<T>> U createComp(
      SexFunction<Long, String, TaggerTypeToken, NamespaceToken, String, List<AttributeTypeGeneric<T>>, U> computationCharacteristicConstructor,
      Long id, String name, String description, AttributeTypeGeneric<T>... typesToCompute) {
      validateString(name);
      return computedCharacteristics.addAndReturn(computationCharacteristicConstructor.apply(id, name,
         TaggerTypeToken.PlainTextTagger, namespace, description, Arrays.asList(typesToCompute)));
   }

   public <T, U extends ComputedCharacteristic<T>> U createCompNoTag(
      SexFunction<Long, String, TaggerTypeToken, NamespaceToken, String, List<AttributeTypeGeneric<T>>, U> computationCharacteristicConstructor,
      Long id, String name, String description, AttributeTypeGeneric<T>... typesToCompute) {
      validateString(name);
      return computedCharacteristics.addAndReturn(computationCharacteristicConstructor.apply(id, name,
         TaggerTypeToken.SENTINEL, namespace, description, Arrays.asList(typesToCompute)));
   }

   public <U extends ComputedCharacteristic<EnumToken>> U createComp(
      SexFunction<Long, String, TaggerTypeToken, NamespaceToken, String, List<AttributeTypeGeneric<EnumToken>>, U> computationCharacteristicConstructor,
      Long id, String name, String description, AttributeTypeEnum<?>... typesToCompute) {
      validateString(name);
      return computedCharacteristics.addAndReturn(computationCharacteristicConstructor.apply(id, name,
         TaggerTypeToken.PlainTextTagger, namespace, description, createEnumList(typesToCompute)));
   }

   public <U extends ComputedCharacteristic<EnumToken>> U createCompNoTag(
      SexFunction<Long, String, TaggerTypeToken, NamespaceToken, String, List<AttributeTypeGeneric<EnumToken>>, U> computationCharacteristicConstructor,
      Long id, String name, String description, AttributeTypeEnum<?>... typesToCompute) {
      validateString(name);
      return computedCharacteristics.addAndReturn(computationCharacteristicConstructor.apply(id, name,
         TaggerTypeToken.SENTINEL, namespace, description, createEnumList(typesToCompute)));
   }

   private List<AttributeTypeGeneric<EnumToken>> createEnumList(AttributeTypeEnum<?>[] typesToCompute) {
      List<AttributeTypeGeneric<EnumToken>> enumsToCompute = new ArrayList<>();
      for (AttributeTypeEnum<?> enumType : typesToCompute) {
         enumsToCompute.add(enumType.getAsEnumToken());
      }
      return enumsToCompute;
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
         case "text/markdown":
            return "md";
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