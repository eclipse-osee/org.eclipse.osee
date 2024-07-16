/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.orcs.data;

import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.Artifact;
import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.core.data.AttributeTypeEnum;
import org.eclipse.osee.framework.core.data.AttributeTypeGeneric;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.ComputedCharacteristicToken;
import org.eclipse.osee.framework.core.data.IAttribute;
import org.eclipse.osee.framework.core.data.IRelationLink;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.EnumToken;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.util.Collections;

/**
 * @author Megumi Telles
 * @author Roberto E. Escobar
 * @author Andrew M. Finkbeiner
 */
public interface ArtifactReadable extends ArtifactToken, HasTransaction, OrcsReadable {
   ArtifactReadableImpl SENTINEL = new ArtifactReadableImpl(Id.SENTINEL, Artifact, COMMON, ArtifactId.SENTINEL,
      ApplicabilityId.BASE, TransactionId.SENTINEL, ModificationType.SENTINEL, null);

   TransactionId getLastModifiedTransaction();

   int getAttributeCount(AttributeTypeToken type);

   int getAttributeCount(AttributeTypeToken type, DeletionFlag deletionFlag);

   Collection<AttributeTypeToken> getValidAttributeTypes();

   Collection<AttributeTypeToken> getExistingAttributeTypes();

   <T> T getSoleAttributeValue(AttributeTypeToken attributeType);

   <T> T getSoleAttributeValue(AttributeTypeToken attributeType, DeletionFlag flag, T defaultValue);

   <T> T getSoleAttributeValue(AttributeTypeToken attributeType, T defaultValue);

   String getSoleAttributeAsString(AttributeTypeToken attributeType);

   String getSoleAttributeAsString(AttributeTypeToken attributeType, String defaultValue);

   Long getSoleAttributeId(AttributeTypeToken attributeType);

   Long getSoleAttributeId(AttributeTypeToken attributeType, Long defaultValue);

   /**
    * @return List of attribute values of non-deleted attributes of the type attributeType or an empty list if no such
    * attributes exist
    */
   <T> List<T> getAttributeValues(AttributeTypeToken attributeType);

   Iterable<Collection<? extends AttributeReadable<Object>>> getAttributeIterable();

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

   Collection<Long> getChildrentIds();

   List<Long> getRelatedIds(RelationTypeSide relationTypeSide);

   boolean isHistorical();

   ApplicabilityId getApplicability();

   String getSafeName();

   List<ArtifactReadable> getRelatedList(RelationTypeSide relationTypeSide);

   <T> List<T> getAttributeValues(AttributeTypeToken attributeType, DeletionFlag deletionFlag);

   List<String> fetchAttributesAsStringList(AttributeTypeToken attributeType);
}