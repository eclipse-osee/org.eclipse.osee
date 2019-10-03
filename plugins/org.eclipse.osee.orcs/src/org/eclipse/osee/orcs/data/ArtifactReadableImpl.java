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
package org.eclipse.osee.orcs.data;

import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.Name;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.RelationTypeId;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.exception.AttributeDoesNotExist;
import org.eclipse.osee.framework.core.exception.MultipleAttributesExist;
import org.eclipse.osee.framework.jdk.core.type.BaseId;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.type.ResultSetList;
import org.eclipse.osee.orcs.search.QueryFactory;

/**
 * @author Ryan D. Brooks
 */
public final class ArtifactReadableImpl extends BaseId implements ArtifactReadable {
   private final HashCollection<AttributeTypeToken, Object> attributes = new HashCollection<>();
   private final HashCollection<RelationTypeToken, ArtifactReadable> relationsSideA = new HashCollection<>();
   private final HashCollection<RelationTypeToken, ArtifactReadable> relationsSideB = new HashCollection<>();
   private final ArtifactTypeToken artifactType;
   private final BranchId branch;
   private final ArtifactId view;
   private final QueryFactory queryFactory;
   private final ApplicabilityId applicability;
   private final ArtifactTypes artifactTypes;
   private final TransactionId txId;
   private final ModificationType modType;

   public ArtifactReadableImpl(Long id, ArtifactTypeToken artifactType, BranchId branch, ArtifactId view, ApplicabilityId applicability, TransactionId txId, ModificationType modType, QueryFactory queryFactory, ArtifactTypes artifactTypes) {
      super(id);
      this.artifactType = artifactType;
      this.branch = branch;
      this.view = view;
      this.applicability = applicability;
      this.txId = txId;
      this.modType = modType;
      this.queryFactory = queryFactory;
      this.artifactTypes = artifactTypes;
   }

   @Override
   public String getName() {
      if (attributes.isEmpty()) {
         return "Name not loaded";
      }
      return getSoleAttributeValue(Name);
   }

   @Override
   public BranchId getBranch() {
      return branch;
   }

   @Override
   public TransactionId getTransaction() {
      return txId;
   }

   @Override
   public ModificationType getModificationType() {
      return modType;
   }

   @Override
   public ArtifactTypeToken getArtifactType() {
      return artifactType;
   }

   @Override
   public TransactionId getLastModifiedTransaction() {
      return TransactionId.SENTINEL;
   }

   @Override
   public boolean isOfType(ArtifactTypeId... otherTypes) {
      return artifactTypes.inheritsFrom(artifactType, otherTypes);
   }

   @Override
   public int getAttributeCount(AttributeTypeToken type) {
      return attributes.sizeByKey(type);
   }

   @Override
   public int getAttributeCount(AttributeTypeToken type, DeletionFlag deletionFlag) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean isAttributeTypeValid(AttributeTypeToken attributeType) {
      throw new UnsupportedOperationException();
   }

   @Override
   public Collection<AttributeTypeToken> getValidAttributeTypes() {
      throw new UnsupportedOperationException();
   }

   @Override
   public Collection<AttributeTypeToken> getExistingAttributeTypes() {
      return attributes.keySet();
   }

   @Override
   public <T> T getSoleAttributeValue(AttributeTypeToken attributeType) {
      List<T> values = getAttributeValues(attributeType);
      ensureSole(attributeType, values);
      return values.iterator().next();
   }

   private void ensureSole(AttributeTypeToken attributeType, Collection<?> values) {
      if (values == null || values.size() < 1) {
         throw new AttributeDoesNotExist("Attribute of type [%s] could not be found on [%s]", attributeType,
            getIdString());
      }
      ensureNotMoreThanOne(attributeType, values.size());
   }

   private void ensureNotMoreThanOne(AttributeTypeToken attributeType, int size) {
      if (size > 1) {
         throw new MultipleAttributesExist("[%s] attributes of type [%s] on [%s], but only 1 instance is allowed",
            attributeType, size, getIdString());
      }
   }

   @Override
   public <T> T getSoleAttributeValue(AttributeTypeToken attributeType, DeletionFlag flag, T defaultValue) {
      throw new UnsupportedOperationException();
   }

   @Override
   public <T> T getSoleAttributeValue(AttributeTypeToken attributeType, T defaultValue) {
      List<T> values = getAttributeValues(attributeType);
      if (values == null) {
         return defaultValue;
      }
      ensureNotMoreThanOne(attributeType, values.size());
      if (values.size() == 1) {
         T value = values.iterator().next();
         return value == null ? defaultValue : value;
      } else {
         return defaultValue;
      }
   }

   @Override
   public String getSoleAttributeAsString(AttributeTypeToken attributeType) {
      return getSoleAttributeValue(attributeType).toString();
   }

   @Override
   public String getSoleAttributeAsString(AttributeTypeToken attributeType, String defaultValue) {
      Object value = getSoleAttributeValue(attributeType, null);
      if (value == null) {
         return defaultValue;
      }
      return value.toString();
   }

   @Override
   public Long getSoleAttributeId(AttributeTypeToken attributeType) {
      throw new UnsupportedOperationException();
   }

   @Override
   public <T> List<T> getAttributeValues(AttributeTypeToken attributeType) {
      if (attributes.isEmpty()) {
         throw new OseeStateException("attributes not loaded for artifact [%s]", getIdString());
      }
      return (List<T>) attributes.getValues(attributeType);
   }

   public void putAttributeValue(AttributeTypeToken attributeType, Object value) {
      attributes.put(attributeType, value);
   }

   public void putRelation(RelationTypeToken relationType, RelationSide side, ArtifactReadable artifact) {
      (side.isSideA() ? relationsSideA : relationsSideB).put(relationType, artifact);
   }

   @Override
   public Iterable<Collection<? extends AttributeReadable<Object>>> getAttributeIterable() {
      throw new UnsupportedOperationException();
   }

   @Override
   public AttributeReadable<Object> getAttributeById(AttributeId attributeId) {
      throw new UnsupportedOperationException();
   }

   @Override
   public ResultSet<? extends AttributeReadable<Object>> getAttributes() {
      throw new UnsupportedOperationException();
   }

   @Override
   public <T> ResultSet<? extends AttributeReadable<T>> getAttributes(AttributeTypeId attributeType) {
      throw new UnsupportedOperationException();
   }

   @Override
   public ResultSet<? extends AttributeReadable<Object>> getAttributes(DeletionFlag deletionFlag) {
      throw new UnsupportedOperationException();
   }

   @Override
   public <T> ResultSet<? extends AttributeReadable<T>> getAttributes(AttributeTypeToken attributeType, DeletionFlag deletionFlag) {
      throw new UnsupportedOperationException();
   }

   @Override
   public int getMaximumRelationAllowed(RelationTypeSide relationTypeSide) {
      throw new UnsupportedOperationException();
   }

   @Override
   public Collection<RelationTypeId> getValidRelationTypes() {
      throw new UnsupportedOperationException();
   }

   @Override
   public Collection<RelationTypeId> getExistingRelationTypes() {
      throw new UnsupportedOperationException();
   }

   @Override
   public List<ArtifactReadable> getDescendants() {
      return queryFactory.fromBranch(branch, view).andRelatedRecursive(CoreRelationTypes.DefaultHierarchical_Child,
         this).asArtifacts();
   }

   @Override
   public void getDescendants(List<ArtifactReadable> descendants) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean isDescendantOf(ArtifactToken parent) {
      throw new UnsupportedOperationException();
   }

   @Override
   public List<ArtifactReadable> getChildren() {
      return getRelated(CoreRelationTypes.DefaultHierarchical_Child, ArtifactTypeId.SENTINEL);
   }

   @Override
   public ResultSet<ArtifactReadable> getRelated(RelationTypeSide relationTypeSide) {
      return new ResultSetList<>(getRelated(relationTypeSide, ArtifactTypeId.SENTINEL));
   }

   @Override
   public List<ArtifactReadable> getRelated(RelationTypeSide relationTypeSide, ArtifactTypeId artifactType) {
      return getRelated(relationTypeSide, artifactType, DeletionFlag.EXCLUDE_DELETED);
   }

   @Override
   public List<ArtifactReadable> getRelated(RelationTypeSide relationTypeSide, ArtifactTypeId artifactType, DeletionFlag deletionFlag) {

      List<ArtifactReadable> related =
         (relationTypeSide.getSide().isSideA() ? relationsSideA : relationsSideB).getValues(
            relationTypeSide.getRelationType());
      if (related == null) {
         return Collections.emptyList();
      }

      Predicate<ArtifactReadable> filter = artifact -> {
         return modType.isIncluded(deletionFlag) && (artifactType.isInvalid() || artifactTypes.inheritsFrom(
            artifact.getArtifactType(), artifactType));
      };

      if (artifactType.isValid() || deletionFlag.equals(DeletionFlag.EXCLUDE_DELETED)) {
         return related.stream().filter(filter).collect(Collectors.toList());
      }
      return related;
   }

   @Override
   public List<ArtifactReadable> getRelated(RelationTypeSide relationTypeSide, DeletionFlag deletionFlag) {
      return getRelated(relationTypeSide, ArtifactTypeId.SENTINEL, deletionFlag);
   }

   @Override
   public boolean areRelated(RelationTypeSide typeAndSide, ArtifactReadable artifact) {
      return getRelated(typeAndSide, ArtifactTypeId.SENTINEL).contains(artifact);
   }

   @Override
   public int getRelatedCount(RelationTypeSide typeAndSide) {
      return getRelated(typeAndSide, ArtifactTypeId.SENTINEL).size();
   }

   @Override
   public String getRationale(RelationTypeSide typeAndSide, ArtifactReadable readable) {
      throw new UnsupportedOperationException();
   }

   @Override
   public ResultSet<RelationReadable> getRelations(RelationTypeSide relationTypeSide) {
      throw new UnsupportedOperationException();
   }

   @Override
   public Collection<Long> getChildrentIds() {
      throw new UnsupportedOperationException();
   }

   @Override
   public Collection<Long> getRelatedIds(RelationTypeSide relationTypeSide) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean isHistorical() {
      return false;
   }

   @Override
   public ApplicabilityId getApplicability() {
      return applicability;
   }

   @Override
   public String getSafeName() {
      return getSoleAttributeValue(CoreAttributeTypes.Name, DeletionFlag.INCLUDE_DELETED,
         "Unknown Name: " + getIdString());
   }
}