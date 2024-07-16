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

package org.eclipse.osee.orcs.core.internal.proxy.impl;

import static org.eclipse.osee.framework.core.enums.DeletionFlag.EXCLUDE_DELETED;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.ApplicabilityToken;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.core.data.AttributeReadable;
import org.eclipse.osee.framework.core.data.AttributeTypeEnum;
import org.eclipse.osee.framework.core.data.AttributeTypeGeneric;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.ComputedCharacteristicToken;
import org.eclipse.osee.framework.core.data.IAttribute;
import org.eclipse.osee.framework.core.data.IRelationLink;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.data.TransactionDetails;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.exception.AttributeDoesNotExist;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.type.ResultSetList;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.Attribute;
import org.eclipse.osee.orcs.core.internal.artifact.Artifact;
import org.eclipse.osee.orcs.core.internal.proxy.ExternalArtifactManager;
import org.eclipse.osee.orcs.core.internal.relation.Relation;
import org.eclipse.osee.orcs.core.internal.relation.RelationManager;

/**
 * @author Megumi Telles
 */
public class ArtifactReadOnlyImpl extends AbstractProxied<Artifact> implements ArtifactReadable {

   private final RelationManager relationManager;
   private final ArtifactTypeToken artifactType;

   public ArtifactReadOnlyImpl(ExternalArtifactManager proxyManager, RelationManager relationManager, OrcsSession session, Artifact proxiedObject, ArtifactTypeToken artifactType) {
      super(proxyManager, session, proxiedObject);
      this.relationManager = relationManager;
      this.artifactType = artifactType;
   }

   private RelationManager getRelationManager() {
      return relationManager;
   }

   @Override
   public String getGuid() {
      return getProxiedObject().getGuid();
   }

   @Override
   public String getName() {
      try {
         return getSoleAttributeAsString(CoreAttributeTypes.Name);
      } catch (AttributeDoesNotExist ex) {
         // do nothing
      }
      return getSafeName();
   }

   @Override
   public boolean matches(Id... identities) {
      return getProxiedObject().matches(identities);
   }

   @Override
   public BranchToken getBranch() {
      return getProxiedObject().getBranch();
   }

   @Override
   public TransactionId getTransaction() {
      return getProxiedObject().getTransaction();
   }

   @Override
   public TransactionId getLastModifiedTransaction() {
      return getProxiedObject().getLastModifiedTransaction();
   }

   @Override
   public ArtifactTypeToken getArtifactType() {
      return artifactType;
   }

   @Override
   public boolean isOfType(ArtifactTypeId... otherTypes) {
      return getProxiedObject().isOfType(otherTypes);
   }

   @Override
   public int getAttributeCount(AttributeTypeToken type) {
      return getProxiedObject().getAttributeCount(type);
   }

   @Override
   public int getAttributeCount(AttributeTypeToken type, DeletionFlag deletionFlag) {
      return getProxiedObject().getAttributeCount(type, deletionFlag);
   }

   @Override
   public boolean isAttributeTypeValid(AttributeTypeId attributeType) {
      return getProxiedObject().isAttributeTypeValid(attributeType);
   }

   @Override
   public Collection<AttributeTypeToken> getValidAttributeTypes() {
      return getProxiedObject().getValidAttributeTypes();
   }

   @Override
   public Collection<AttributeTypeToken> getExistingAttributeTypes() {
      return getProxiedObject().getExistingAttributeTypes();
   }

   @Override
   public <T> T getSoleAttributeValue(AttributeTypeToken attributeType) {
      return getProxiedObject().getSoleAttributeValue(attributeType);
   }

   @Override
   public <T> T getSoleAttributeValue(AttributeTypeToken attributeType, DeletionFlag flag, T defaultValue) {
      return getProxiedObject().getSoleAttributeValue(attributeType, flag, defaultValue);
   }

   @Override
   public <T> T getSoleAttributeValue(AttributeTypeToken attributeType, T defaultValue) {
      return getProxiedObject().getSoleAttributeValue(attributeType, defaultValue);
   }

   @Override
   public String getSoleAttributeAsString(AttributeTypeToken attributeType) {
      return getProxiedObject().getSoleAttributeAsString(attributeType);
   }

   @Override
   public String getSoleAttributeAsString(AttributeTypeToken attributeType, String defaultValue) {
      return getProxiedObject().getSoleAttributeAsString(attributeType, defaultValue);
   }

   @Override
   public AttributeId getSoleAttributeId(AttributeTypeToken attributeType) {
      return getProxiedObject().getSoleAttribute(attributeType);
   }

   @Override
   public Long getSoleAttributeId(AttributeTypeToken attributeType, Long defaultValue) {
      throw new UnsupportedOperationException();
   }

   @Override
   public <T> List<T> getAttributeValues(AttributeTypeToken attributeType) {
      return getProxiedObject().getAttributeValues(attributeType);
   }

   @Override
   public <T> List<T> getAttributeValues(AttributeTypeToken attributeType, DeletionFlag deletionFlag) {
      return getProxiedObject().getAttributeValues(attributeType, deletionFlag);
   }

   @Override
   public Iterable<Collection<? extends AttributeReadable<Object>>> getAttributeIterable() {
      return getProxiedObject().getAttributeIterable();
   }

   @Override
   public ResultSet<? extends AttributeReadable<Object>> getAttributes() {
      List<Attribute<Object>> attributes = getProxiedObject().getAttributes();
      return getProxyManager().asExternalAttributes(getSession(), attributes);
   }

   @Override
   public <T> List<IAttribute<T>> getAttributeList(AttributeTypeGeneric<T> attributeType) {
      return Collections.transform(getAttributes(attributeType).getList(), a -> (IAttribute<T>) a);
   }

   @Override
   public <T> ResultSet<? extends AttributeReadable<T>> getAttributes(AttributeTypeToken attributeType) {
      List<Attribute<T>> attributes = getProxiedObject().getAttributes(attributeType);
      return getProxyManager().asExternalAttributes(getSession(), attributes);
   }

   @Override
   public ResultSet<? extends AttributeReadable<Object>> getAttributes(DeletionFlag deletionFlag) {
      List<Attribute<Object>> attributes = getProxiedObject().getAttributes(deletionFlag);
      return getProxyManager().asExternalAttributes(getSession(), attributes);
   }

   @Override
   public <T> ResultSet<? extends AttributeReadable<T>> getAttributes(AttributeTypeToken attributeType,
      DeletionFlag deletionFlag) {
      List<Attribute<T>> attributes = getProxiedObject().getAttributes(attributeType, deletionFlag);
      return getProxyManager().asExternalAttributes(getSession(), attributes);
   }

   @Override
   public AttributeReadable<Object> getAttributeById(AttributeId attributeId) {
      Attribute<Object> attribute = getProxiedObject().getAttributeById(attributeId);
      return getProxyManager().asExternalAttribute(getSession(), attribute);
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
   public int getMaximumRelationAllowed(RelationTypeSide typeAndSide) {
      RelationTypeToken type = typeAndSide.getRelationType();
      RelationSide side = whichSideAmIOn(typeAndSide);
      return getRelationManager().getMaximumRelationAllowed(type, getProxiedObject(), side);
   }

   @Override
   public Collection<RelationTypeToken> getValidRelationTypes() {
      return getRelationManager().getValidRelationTypes(getProxiedObject());
   }

   @Override
   public Collection<RelationTypeToken> getExistingRelationTypes() {
      return getRelationManager().getExistingRelationTypes(getProxiedObject());
   }

   @Override
   public ArtifactReadable getParent() {
      Artifact parent = getRelationManager().getParent(getSession(), getProxiedObject());
      return getProxyManager().asExternalArtifact(getSession(), parent);
   }

   @Override
   public List<ArtifactReadable> getChildren() {
      ResultSet<Artifact> children = getRelationManager().getChildren(getSession(), getProxiedObject());
      return getProxyManager().asExternalArtifacts(getSession(), children).getList();
   }

   @Override
   public List<ArtifactReadable> getDescendants() {
      List<ArtifactReadable> descendants = new LinkedList<>();
      getDescendants(descendants);
      return descendants;
   }

   @Override
   public void getDescendants(List<ArtifactReadable> descendants) {
      for (ArtifactReadable child : getChildren()) {
         descendants.add(child);
         child.getDescendants(descendants);
      }
   }

   @Override
   public boolean isDescendantOf(ArtifactToken parent) {
      for (ArtifactReadable nextParent = getParent(); nextParent != null; nextParent = nextParent.getParent()) {
         if (nextParent.equals(parent)) {
            return true;
         }
      }
      return false;
   }

   /**
    * Use the ArtifactReadableImpl implementation of getRelated instead, as this may cause a stack overflow error
    * depending on usage.
    */
   @Deprecated
   @Override
   public ResultSet<ArtifactReadable> getRelated(RelationTypeSide typeAndSide) {
      return getRelatedResultSet(typeAndSide, EXCLUDE_DELETED);
   }

   @Override
   public ResultSet<IRelationLink> getRelations(RelationTypeSide typeAndSide) {
      List<IRelationLink> rels = new ArrayList<>();
      for (IRelationLink link : getRelationManager().getRelations(getProxiedObject(), DeletionFlag.EXCLUDE_DELETED)) {
         if (link.getRelationType().equals(typeAndSide.getRelationType())) {
            rels.add(link);
         }
      }
      return new ResultSetList<>(Collections.castAll(rels));
   }

   @Override
   public List<ArtifactReadable> getRelated(RelationTypeSide typeAndSide, DeletionFlag deletionFlag) {
      return getRelatedResultSet(typeAndSide, deletionFlag).getList();
   }

   private ResultSet<ArtifactReadable> getRelatedResultSet(RelationTypeSide typeAndSide, DeletionFlag deletionFlag) {
      RelationTypeToken type = typeAndSide.getRelationType();
      RelationSide side = whichSideAmIOn(typeAndSide);
      ResultSet<Artifact> related =
         getRelationManager().getRelated(getSession(), type, getProxiedObject(), side, deletionFlag);
      return getProxyManager().asExternalArtifacts(getSession(), related);
   }

   @Override
   public int getRelatedCount(RelationTypeSide typeAndSide) {
      RelationTypeToken type = typeAndSide.getRelationType();
      RelationSide side = whichSideAmIOn(typeAndSide);
      return getRelationManager().getRelatedCount(type, getProxiedObject(), side);
   }

   @Override
   public boolean areRelated(RelationTypeSide typeAndSide, ArtifactReadable artifact) {
      RelationTypeToken type = typeAndSide.getRelationType();
      Pair<Artifact, Artifact> nodes = asABNodes(typeAndSide.getSide(), artifact);
      return getRelationManager().areRelated(nodes.getFirst(), type, nodes.getSecond());
   }

   @Override
   public String getRationale(RelationTypeSide typeAndSide, ArtifactReadable readable) {
      RelationTypeToken type = typeAndSide.getRelationType();
      Pair<Artifact, Artifact> nodes = asABNodes(typeAndSide.getSide(), readable);
      return getRelationManager().getRationale(nodes.getFirst(), type, nodes.getSecond());
   }

   private Pair<Artifact, Artifact> asABNodes(RelationSide side, ArtifactReadable readable) {
      Artifact thisArtifact = getProxiedObject();
      Artifact otherArtifact = getProxyManager().asInternalArtifact(readable);

      Artifact aNode;
      Artifact bNode;
      if (RelationSide.SIDE_A == side) {
         aNode = otherArtifact;
         bNode = thisArtifact;
      } else {
         aNode = thisArtifact;
         bNode = otherArtifact;
      }
      return new Pair<>(aNode, bNode);
   }

   private RelationSide whichSideAmIOn(RelationTypeSide typeAndSide) {
      return typeAndSide.getSide().oppositeSide();
   }

   @Override
   public Long getId() {
      return getProxiedObject().getId();
   }

   @Override
   public String toString() {
      return String.format("Artifact: Id [%s] Type [%s] Name [%s]", getIdString(), getArtifactType().getName(),
         getSafeName());
   }

   @Override
   public boolean isDeleted() {
      return getProxiedObject().isDeleted();
   }

   @Override
   public ModificationType getModificationType() {
      return getProxiedObject().getModificationType();
   }

   @Override
   public Collection<ArtifactId> getChildrenIds() {
      return getRelatedIds(CoreRelationTypes.DefaultHierarchical_Child);
   }

   @Override
   public List<ArtifactId> getRelatedIds(RelationTypeSide relationTypeSide) {
      List<ArtifactId> relatedIds = new ArrayList<>();
      for (Relation relation : getRelationManager().getRelations(getProxiedObject(), DeletionFlag.EXCLUDE_DELETED)) {
         if (relation.getRelationType().equals(relationTypeSide.getRelationType())) {
            boolean aSide = relationTypeSide.getSide().isSideA();
            if (aSide && relation.getArtifactIdB().equals(this)) {
               relatedIds.add(relation.getArtifactIdA());
            } else if (!aSide && relation.getArtifactIdA().equals(this)) {
               relatedIds.add(relation.getArtifactIdB());
            }
         }
      }
      return relatedIds;
   }

   @Override
   public boolean isHistorical() {
      return getProxiedObject().getOrcsData().getVersion().isHistorical();
   }

   @Override
   public ApplicabilityToken getApplicabilityToken() {
      return ApplicabilityToken.valueOf(getProxiedObject().getOrcsData().getApplicabilityId().getId(), "");

   }

   @Override
   public ApplicabilityId getApplicability() {
      return getProxiedObject().getOrcsData().getApplicabilityId();

   }

   @Override
   public List<ArtifactReadable> getRelated(RelationTypeSide relationTypeSide, ArtifactTypeToken artifactType,
      DeletionFlag deletionFlag) {
      throw new UnsupportedOperationException();
   }

   @Override
   public String getSafeName() {
      return getSoleAttributeValue(CoreAttributeTypes.Name, DeletionFlag.INCLUDE_DELETED,
         "Unknown Name: " + getIdString());
   }

   @Override
   public List<ArtifactReadable> getRelatedList(RelationTypeSide relationTypeSide) {
      return getRelated(relationTypeSide).getList();
   }

   @Override
   public List<String> fetchAttributesAsStringList(AttributeTypeToken attributeType) {
      throw new UnsupportedOperationException();
   }

   @Override
   public HashCollection<AttributeTypeToken, IAttribute<?>> getAttributesHashCollection() {
      throw new UnsupportedOperationException();
   }

   @Override
   public TransactionDetails getTxDetails() {
      throw new UnsupportedOperationException();
   }

   @Override
   public List<ArtifactReadable> getReferenceArtifactsByType(AttributeTypeToken attributeType) {
      throw new UnsupportedOperationException();
   }

   @Override
   public ArtifactReadable getReferenceArtifactByAttrId(AttributeId attributeId) {
      throw new UnsupportedOperationException();
   }
}