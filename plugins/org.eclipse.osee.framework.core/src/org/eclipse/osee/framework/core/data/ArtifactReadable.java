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

package org.eclipse.osee.framework.core.data;

import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.Artifact;
import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.EnumToken;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.AttributeDoesNotExist;
import org.eclipse.osee.framework.core.exception.MultipleAttributesExist;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.Named;
import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.util.Collections;

/**
 * @author Megumi Telles
 * @author Roberto E. Escobar
 * @author Andrew M. Finkbeiner
 * @author Ryan D. Brooks
 */

@JsonSerialize(using = ArtifactReadableSerializer.class)
public interface ArtifactReadable extends ArtifactToken, HasTransaction, OrcsReadable {
   ArtifactReadable SENTINEL = new ArtifactReadableImpl();

   TransactionId getLastModifiedTransaction();

   int getAttributeCount(AttributeTypeToken type);

   int getAttributeCount(AttributeTypeToken type, DeletionFlag deletionFlag);

   Collection<AttributeTypeToken> getValidAttributeTypes();

   Collection<AttributeTypeToken> getExistingAttributeTypes();

   /**
    * Gets the non-deleted attribute values for the specified attribute type and asserts that one and only one attribute
    * value is present.
    *
    * @param <T> The type of value returned.
    * @param attributeType the type of attribute to get the value of.
    * @return the sole value of the specified attribute type.
    * @throws AttributeDoesNotExist when a value is not present for the specified attribute type.
    * @throws MultipleAttributesExist when more than one value is present for the specified attribute.
    */

   <T> T getSoleAttributeValue(AttributeTypeToken attributeType);

   <T> T getSoleAttributeValue(AttributeTypeToken attributeType, DeletionFlag flag, T defaultValue);

   <T> T getSoleAttributeValue(AttributeTypeToken attributeType, T defaultValue);

   String getSoleAttributeAsString(AttributeTypeToken attributeType);

   String getSoleAttributeAsString(AttributeTypeToken attributeType, String defaultValue);

   AttributeId getSoleAttributeId(AttributeTypeToken attributeType);

   Long getSoleAttributeId(AttributeTypeToken attributeType, Long defaultValue);

   /**
    * Gets the non-deleted attributes values for the specified attribute type.
    *
    * @param <T> The type of attribute value returned.
    * @param attributeType the type of attribute to the values of.
    * @return List of attribute values of non-deleted attributes of the type attributeType or an empty list if no such
    * attributes exist
    */
   <T> List<T> getAttributeValues(AttributeTypeToken attributeType);

   Iterable<Collection<? extends AttributeReadable<Object>>> getAttributeIterable();

   @SuppressWarnings("unchecked")
   default <T extends EnumToken> boolean attributeMatches(AttributeTypeEnum<T> attributeType, T... values) {
      T enumValue = getSoleAttributeValue(attributeType);
      return enumValue.matches(values);
   }

   ////////////////////

   AttributeReadable<Object> getAttributeById(AttributeId attributeId);

   ResultSet<? extends AttributeReadable<Object>> getAttributes();

   <T> ResultSet<? extends AttributeReadable<T>> getAttributes(AttributeTypeToken attributeType);

   <T> List<IAttribute<T>> getAttributeList(AttributeTypeGeneric<T> attributeType);

   ResultSet<? extends AttributeReadable<Object>> getAttributes(DeletionFlag deletionFlag);

   public <T> ResultSet<? extends AttributeReadable<T>> getAttributes(AttributeTypeToken attributeType, DeletionFlag deletionFlag);

   default String getAttributeValuesAsString(AttributeTypeToken attributeType) {
      return Collections.toString(", ", getAttributeValues(attributeType));
   }

   ////////////////////

   <T> T getComputedCharacteristicValue(ComputedCharacteristicToken<T> computedCharacteristic);

   ////////////////////

   int getMaximumRelationAllowed(RelationTypeSide relationTypeSide);

   Collection<RelationTypeToken> getValidRelationTypes();

   Collection<RelationTypeToken> getExistingRelationTypes();

   default ArtifactReadable getParent() {
      return org.eclipse.osee.framework.jdk.core.util.Collections.exactlyOne(
         getRelated(CoreRelationTypes.DefaultHierarchical_Parent, ArtifactTypeToken.SENTINEL));
   }

   List<ArtifactReadable> getDescendants();

   void getDescendants(List<ArtifactReadable> descendants);

   boolean isDescendantOf(ArtifactToken parent);

   default List<ArtifactReadable> getAncestors() {
      List<ArtifactReadable> ancestors = new ArrayList<>();
      for (ArtifactReadable parent = getParent(); parent != null; parent = parent.getParent()) {
         ancestors.add(parent);
      }
      return ancestors;
   }

   List<ArtifactReadable> getChildren();

   default ArtifactReadable getChild() {
      return Collections.exactlyOne(getChildren());
   }

   ResultSet<ArtifactReadable> getRelated(RelationTypeSide relationTypeSide);

   default List<ArtifactReadable> getRelated(RelationTypeSide relationTypeSide, ArtifactTypeToken artifactType) {
      List<ArtifactReadable> artifacts = new ArrayList<>();
      for (ArtifactReadable artifact : getRelated(relationTypeSide)) {
         if (artifact.isOfType(artifactType)) {
            artifacts.add(artifact);
         }
      }
      return artifacts;
   }

   default List<ArtifactToken> getRelatedIds(RelationTypeSide relationTypeSide, ArtifactTypeToken artifactType) {
      return Collections.cast(getRelated(relationTypeSide, artifactType));
   }

   List<ArtifactReadable> getRelated(RelationTypeSide relationTypeSide, DeletionFlag deletionFlag);

   List<ArtifactReadable> getRelated(RelationTypeSide relationTypeSide, ArtifactTypeToken artifactType, DeletionFlag deletionFlag);

   boolean areRelated(RelationTypeSide typeAndSide, ArtifactReadable artifact);

   int getRelatedCount(RelationTypeSide typeAndSide);

   String getRationale(RelationTypeSide typeAndSide, ArtifactReadable readable);

   ResultSet<IRelationLink> getRelations(RelationTypeSide relationTypeSide);

   Collection<ArtifactId> getChildrentIds();

   List<ArtifactId> getRelatedIds(RelationTypeSide relationTypeSide);

   boolean isHistorical();

   ApplicabilityId getApplicability();

   ApplicabilityToken getApplicabilityToken();

   String getSafeName();

   List<ArtifactReadable> getRelatedList(RelationTypeSide relationTypeSide);

   <T> List<T> getAttributeValues(AttributeTypeToken attributeType, DeletionFlag deletionFlag);

   List<String> fetchAttributesAsStringList(AttributeTypeToken attributeType);

   default Collection<String> getTags() {
      return Collections.castAll(getAttributeValues(CoreAttributeTypes.StaticId));
   }

   public static class ArtifactReadableImpl extends NamedIdBase implements ArtifactReadable {

      public ArtifactReadableImpl() {
         super(Id.SENTINEL, Named.SENTINEL);
      }

      @Override
      public ModificationType getModificationType() {
         return ModificationType.SENTINEL;
      }

      @Override
      public BranchToken getBranch() {
         return COMMON;
      }

      @Override
      public TransactionId getTransaction() {
         return TransactionId.SENTINEL;
      }

      @Override
      public ArtifactTypeToken getArtifactType() {
         return Artifact;
      }

      @Override
      public TransactionId getLastModifiedTransaction() {
         return TransactionId.SENTINEL;
      }

      @Override
      public int getAttributeCount(AttributeTypeToken type) {
         return 0;
      }

      @Override
      public int getAttributeCount(AttributeTypeToken type, DeletionFlag deletionFlag) {
         return 0;
      }

      @Override
      public Collection<AttributeTypeToken> getValidAttributeTypes() {
         return null;
      }

      @Override
      public Collection<AttributeTypeToken> getExistingAttributeTypes() {
         return null;
      }

      @Override
      public <T> T getSoleAttributeValue(AttributeTypeToken attributeType) {
         return null;
      }

      @Override
      public <T> T getSoleAttributeValue(AttributeTypeToken attributeType, DeletionFlag flag, T defaultValue) {
         return null;
      }

      @Override
      public <T> T getSoleAttributeValue(AttributeTypeToken attributeType, T defaultValue) {
         return null;
      }

      @Override
      public String getSoleAttributeAsString(AttributeTypeToken attributeType) {
         return null;
      }

      @Override
      public String getSoleAttributeAsString(AttributeTypeToken attributeType, String defaultValue) {
         return null;
      }

      @Override
      public AttributeId getSoleAttributeId(AttributeTypeToken attributeType) {
         return null;
      }

      @Override
      public Long getSoleAttributeId(AttributeTypeToken attributeType, Long defaultValue) {
         return null;
      }

      @Override
      public <T> List<T> getAttributeValues(AttributeTypeToken attributeType) {
         return null;
      }

      @Override
      public Iterable<Collection<? extends AttributeReadable<Object>>> getAttributeIterable() {
         return null;
      }

      @Override
      public AttributeReadable<Object> getAttributeById(AttributeId attributeId) {
         return null;
      }

      @Override
      public ResultSet<? extends AttributeReadable<Object>> getAttributes() {
         return null;
      }

      @Override
      public <T> ResultSet<? extends AttributeReadable<T>> getAttributes(AttributeTypeToken attributeType) {
         return null;
      }

      @Override
      public <T> List<IAttribute<T>> getAttributeList(AttributeTypeGeneric<T> attributeType) {
         return null;
      }

      @Override
      public ResultSet<? extends AttributeReadable<Object>> getAttributes(DeletionFlag deletionFlag) {
         return null;
      }

      @Override
      public <T> ResultSet<? extends AttributeReadable<T>> getAttributes(AttributeTypeToken attributeType, DeletionFlag deletionFlag) {
         return null;
      }

      @Override
      public <T> T getComputedCharacteristicValue(ComputedCharacteristicToken<T> computedCharacteristic) {
         return null;
      }

      @Override
      public int getMaximumRelationAllowed(RelationTypeSide relationTypeSide) {
         return 0;
      }

      @Override
      public Collection<RelationTypeToken> getValidRelationTypes() {
         return null;
      }

      @Override
      public Collection<RelationTypeToken> getExistingRelationTypes() {
         return null;
      }

      @Override
      public List<ArtifactReadable> getDescendants() {
         return null;
      }

      @Override
      public void getDescendants(List<ArtifactReadable> descendants) {
         // do nothing
      }

      @Override
      public boolean isDescendantOf(ArtifactToken parent) {
         return false;
      }

      @Override
      public List<ArtifactReadable> getChildren() {
         return null;
      }

      @Override
      public ResultSet<ArtifactReadable> getRelated(RelationTypeSide relationTypeSide) {
         return null;
      }

      @Override
      public List<ArtifactReadable> getRelated(RelationTypeSide relationTypeSide, DeletionFlag deletionFlag) {
         return null;
      }

      @Override
      public List<ArtifactReadable> getRelated(RelationTypeSide relationTypeSide, ArtifactTypeToken artifactType, DeletionFlag deletionFlag) {
         return null;
      }

      @Override
      public boolean areRelated(RelationTypeSide typeAndSide, ArtifactReadable artifact) {
         return false;
      }

      @Override
      public int getRelatedCount(RelationTypeSide typeAndSide) {
         return 0;
      }

      @Override
      public String getRationale(RelationTypeSide typeAndSide, ArtifactReadable readable) {
         return null;
      }

      @Override
      public ResultSet<IRelationLink> getRelations(RelationTypeSide relationTypeSide) {
         return null;
      }

      @Override
      public Collection<ArtifactId> getChildrentIds() {
         return null;
      }

      @Override
      public List<ArtifactId> getRelatedIds(RelationTypeSide relationTypeSide) {
         return null;
      }

      @Override
      public boolean isHistorical() {
         return false;
      }

      @Override
      public ApplicabilityId getApplicability() {
         return ApplicabilityId.BASE;
      }

      @Override
      public String getSafeName() {
         return null;
      }

      @Override
      public List<ArtifactReadable> getRelatedList(RelationTypeSide relationTypeSide) {
         return null;
      }

      @Override
      public <T> List<T> getAttributeValues(AttributeTypeToken attributeType, DeletionFlag deletionFlag) {
         return null;
      }

      @Override
      public List<String> fetchAttributesAsStringList(AttributeTypeToken attributeType) {
         return null;
      }

      @Override
      public ApplicabilityToken getApplicabilityToken() {
         return ApplicabilityToken.BASE;
      }
   }

}