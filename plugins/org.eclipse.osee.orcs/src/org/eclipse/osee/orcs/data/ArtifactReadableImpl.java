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

package org.eclipse.osee.orcs.data;

import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.Name;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.ApplicabilityToken;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.core.data.AttributeReadable;
import org.eclipse.osee.framework.core.data.AttributeTypeEnum;
import org.eclipse.osee.framework.core.data.AttributeTypeGeneric;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.ComputedCharacteristicToken;
import org.eclipse.osee.framework.core.data.IAttribute;
import org.eclipse.osee.framework.core.data.IRelationLink;
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
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.type.ResultSetList;
import org.eclipse.osee.orcs.search.QueryFactory;

/**
 * @author Ryan D. Brooks
 */
public final class ArtifactReadableImpl extends BaseId implements ArtifactReadable {
   private final HashCollection<AttributeTypeToken, IAttribute<?>> attributes = new HashCollection<>();
   private final HashCollection<RelationTypeToken, ArtifactReadable> relationsSideA = new HashCollection<>();
   private final HashCollection<RelationTypeToken, ArtifactReadable> relationsSideB = new HashCollection<>();
   private final ArtifactTypeToken artifactType;
   private final BranchToken branch;
   private final ArtifactId view;
   private final QueryFactory queryFactory;
   private final ApplicabilityToken applicability;
   private final TransactionId txId;
   private final ModificationType modType;

   public ArtifactReadableImpl(Long id, ArtifactTypeToken artifactType, BranchToken branch, ArtifactId view, ApplicabilityToken applicability, TransactionId txId, ModificationType modType, QueryFactory queryFactory) {
      super(id);
      this.artifactType = artifactType;
      this.branch = branch;
      this.view = view;
      this.applicability = applicability;
      this.txId = txId;
      this.modType = modType;
      this.queryFactory = queryFactory;
   }

   public ArtifactReadableImpl(Long id, ArtifactTypeToken artifactType, BranchToken branch, ArtifactId view, ApplicabilityId applicability, TransactionId txId, ModificationType modType, QueryFactory queryFactory) {
      super(id);
      this.artifactType = artifactType;
      this.branch = branch;
      this.view = view;
      this.applicability = ApplicabilityToken.valueOf(applicability.getId(), "");
      this.txId = txId;
      this.modType = modType;
      this.queryFactory = queryFactory;
   }

   @Override
   public String getName() {
      if (attributes.isEmpty()) {
         return "Name not loaded";
      }
      return getSoleAttributeValue(Name);
   }

   @Override
   public BranchToken getBranch() {
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
   public int getAttributeCount(AttributeTypeToken type) {
      return attributes.sizeByKey(type);
   }

   @Override
   public int getAttributeCount(AttributeTypeToken type, DeletionFlag deletionFlag) {
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

   /**
    * {@inheritDoc}
    *
    * @throws AttributeDoesNotExist {@inheritDoc}
    * @throws MultipleAttributesExist {@inheritDoc}
    * @throws OseeStateException when the attribute values have not been loaded for the artifact.
    */

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
   public AttributeId getSoleAttributeId(AttributeTypeToken attributeType) {
      List<IAttribute<?>> list = attributes.getValues(attributeType);
      ensureNotMoreThanOne(attributeType, list.size());
      return list.iterator().next();
   }

   @Override
   public Long getSoleAttributeId(AttributeTypeToken attributeType, Long defaultValue) {
      List<IAttribute<?>> list = attributes.getValues(attributeType);
      if (list == null) {
         return defaultValue;
      }
      ensureNotMoreThanOne(attributeType, list.size());
      return list.iterator().next().getId();
   }

   /**
    * {@inheritDoc}
    *
    * @throws OseeStateException when attribute values have not been loaded for the artifact.
    */

   @SuppressWarnings("unchecked")
   @Override
   public <T> List<T> getAttributeValues(AttributeTypeToken attributeType) {
      if (attributes.isEmpty()) {
         throw new OseeStateException("attributes not loaded for artifact [%s]", getIdString());
      }
      List<IAttribute<?>> values = attributes.getValues(attributeType);
      if (values == null) {
         return Collections.emptyList();
      }
      return (List<T>) values.stream().map(IAttribute::getValue).collect(Collectors.toList());
   }

   @SuppressWarnings("unchecked")
   @Override
   public <T> List<T> getAttributeValues(AttributeTypeToken attributeType, DeletionFlag deletionFlag) {
      if (attributes.isEmpty()) {
         throw new OseeStateException("attributes not loaded for artifact [%s]", getIdString());
      }
      List<? extends AttributeReadable<Object>> values = getAttributes(attributeType, deletionFlag).getList();
      if (values.isEmpty()) {
         return null;
      }

      return (List<T>) values.stream().map(IAttribute::getValue).collect(Collectors.toList());
   }

   public void putAttributeValue(AttributeTypeGeneric<?> attributeType, IAttribute<?> attribute) {
      attributes.put(attributeType, attribute);
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
   public <T> ResultSet<? extends AttributeReadable<T>> getAttributes(AttributeTypeToken attributeType) {
      throw new UnsupportedOperationException();
   }

   @Override
   public <T> List<IAttribute<T>> getAttributeList(AttributeTypeGeneric<T> attributeType) {
      if (attributes.isEmpty()) {
         throw new OseeStateException("attributes not loaded for artifact [%s]", getIdString());
      }
      List<IAttribute<?>> attributeOfType = attributes.getValues(attributeType);
      if (attributeOfType == null) {
         return Collections.emptyList();
      }
      return org.eclipse.osee.framework.jdk.core.util.Collections.transform(attributeOfType, x -> (IAttribute<T>) x);
   }

   @Override
   public ResultSet<? extends AttributeReadable<Object>> getAttributes(DeletionFlag deletionFlag) {
      throw new UnsupportedOperationException();
   }

   @Override
   public <T> ResultSet<? extends AttributeReadable<T>> getAttributes(AttributeTypeToken attributeType, DeletionFlag deletionFlag) {
      throw new UnsupportedOperationException();
   }

   private <T> List<T> getEnumAttributeValues(AttributeTypeToken attributeType) {
      List<T> attributeValues = new ArrayList<T>();
      if (attributeType.isEnumerated()) {
         List<String> enumAttributeValues = new ArrayList<String>();
         AttributeTypeEnum<?> attributeTypeEnum = (AttributeTypeEnum<?>) attributeType;
         enumAttributeValues.addAll(getAttributeValues(attributeType));
         for (String s : enumAttributeValues) {
            attributeValues.add((T) attributeTypeEnum.valueFromStorageString(s));
         }
      }
      return attributeValues;
   }

   @Override
   public <T> T getComputedCharacteristicValue(ComputedCharacteristicToken<T> computedCharacteristic) {
      List<T> attributeValues = new ArrayList<T>();
      if (!artifactType.isComputedCharacteristicValid(computedCharacteristic)) {
         throw new OseeCoreException(
            "Attribute Types on Artifact Type %s do not have valid multiplicity for computed characteristic %s",
            artifactType.getName(), computedCharacteristic.getName());
      }
      for (AttributeTypeGeneric<T> attributeType : computedCharacteristic.getAttributeTypesToCompute()) {
         if (attributeType.isEnumerated()) {
            attributeValues.addAll(getEnumAttributeValues(attributeType));
         } else {
            attributeValues.addAll(getAttributeValues(attributeType));
         }
      }
      return computedCharacteristic.calculate(attributeValues);
   }

   @Override
   public int getMaximumRelationAllowed(RelationTypeSide relationTypeSide) {
      throw new UnsupportedOperationException();
   }

   @Override
   public Collection<RelationTypeToken> getValidRelationTypes() {
      throw new UnsupportedOperationException();
   }

   @Override
   public Collection<RelationTypeToken> getExistingRelationTypes() {
      Set<RelationTypeToken> relATypes = this.relationsSideA.keySet();
      Set<RelationTypeToken> relBTypes = this.relationsSideB.keySet();
      List<RelationTypeToken> rels = new ArrayList<RelationTypeToken>();
      if (!relATypes.isEmpty()) {
         rels.addAll(relATypes);
      }
      if (!relBTypes.isEmpty()) {
         rels.addAll(relBTypes);
      }
      return rels;
   }

   /**
    * {@inheritDoc}
    *
    * @implNote Performs a database query to obtain all hierarchically subordinate artifacts.
    */

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

   /**
    * {@inheritDoc}
    *
    * @implNote Uses the artifact's relationships to obtain the immediate children of the artifact.
    */

   @Override
   public List<ArtifactReadable> getChildren() {
      return getRelated(CoreRelationTypes.DefaultHierarchical_Child, ArtifactTypeToken.SENTINEL);
   }

   @Override
   public ResultSet<ArtifactReadable> getRelated(RelationTypeSide relationTypeSide) {
      return new ResultSetList<>(getRelated(relationTypeSide, ArtifactTypeToken.SENTINEL));
   }

   @Override
   public List<ArtifactReadable> getRelatedList(RelationTypeSide relationTypeSide) {
      return getRelated(relationTypeSide, ArtifactTypeToken.SENTINEL);
   }

   @Override
   public List<ArtifactReadable> getRelated(RelationTypeSide relationTypeSide, ArtifactTypeToken artifactType) {
      return getRelated(relationTypeSide, artifactType, DeletionFlag.EXCLUDE_DELETED);
   }

   @Override
   public List<ArtifactReadable> getRelated(RelationTypeSide relationTypeSide, ArtifactTypeToken artifactType, DeletionFlag deletionFlag) {

      List<ArtifactReadable> related =
         (relationTypeSide.getSide().isSideA() ? relationsSideA : relationsSideB).getValues(
            relationTypeSide.getRelationType());
      if (related == null) {
         return Collections.emptyList();
      }

      Predicate<ArtifactReadable> filter = artifact -> {
         return modType.isIncluded(
            deletionFlag) && (artifactType.isInvalid() || artifact.getArtifactType().inheritsFrom(artifactType));
      };

      if (artifactType.isValid() || deletionFlag.equals(DeletionFlag.EXCLUDE_DELETED)) {
         return related.stream().filter(filter).collect(Collectors.toList());
      }
      return related;
   }

   @Override
   public List<ArtifactReadable> getRelated(RelationTypeSide relationTypeSide, DeletionFlag deletionFlag) {
      return getRelated(relationTypeSide, ArtifactTypeToken.SENTINEL, deletionFlag);
   }

   @Override
   public boolean areRelated(RelationTypeSide typeAndSide, ArtifactReadable artifact) {
      return getRelated(typeAndSide, ArtifactTypeToken.SENTINEL).contains(artifact);
   }

   @Override
   public int getRelatedCount(RelationTypeSide typeAndSide) {
      return getRelated(typeAndSide, ArtifactTypeToken.SENTINEL).size();
   }

   @Override
   public String getRationale(RelationTypeSide typeAndSide, ArtifactReadable readable) {
      throw new UnsupportedOperationException();
   }

   @Override
   public ResultSet<IRelationLink> getRelations(RelationTypeSide relationTypeSide) {
      throw new UnsupportedOperationException();
   }

   @Override
   public Collection<ArtifactId> getChildrentIds() {
      throw new UnsupportedOperationException();
   }

   @Override
   public List<ArtifactId> getRelatedIds(RelationTypeSide relationTypeSide) {
      List<ArtifactReadable> relatedList = getRelatedList(relationTypeSide);
      return relatedList.stream().map(p -> ArtifactId.valueOf(p.getId())).collect(Collectors.toList());

   }

   @Override
   public boolean isHistorical() {
      return false;
   }

   @Override
   public ApplicabilityToken getApplicabilityToken() {
      return applicability;
   }

   @Override
   public ApplicabilityId getApplicability() {
      return ApplicabilityId.valueOf(applicability.getId());
   }

   /**
    * @return string collection containing of all the attribute values of type attributeType
    */
   @Override
   public List<String> fetchAttributesAsStringList(AttributeTypeToken attributeType) {

      List<String> items = new ArrayList<>();
      List<Object> attributeValues = getAttributeValues(attributeType);
      for (Object object : attributeValues) {
         items.add(object.toString());
      }
      return items;
   }

   @Override
   public String getSafeName() {
      return getSoleAttributeValue(CoreAttributeTypes.Name, DeletionFlag.INCLUDE_DELETED,
         "Unknown Name: " + getIdString());
   }

}