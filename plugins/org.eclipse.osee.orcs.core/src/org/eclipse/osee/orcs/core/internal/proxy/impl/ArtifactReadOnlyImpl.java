/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.internal.proxy.impl;

import static org.eclipse.osee.framework.core.enums.DeletionFlag.EXCLUDE_DELETED;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.data.RelationTypeId;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.RelationSide;
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
import org.eclipse.osee.orcs.core.internal.relation.RelationNode;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.AttributeReadable;
import org.eclipse.osee.orcs.data.RelationReadable;

/**
 * @author Megumi Telles
 */
public class ArtifactReadOnlyImpl extends AbstractProxied<Artifact> implements ArtifactReadable {

   private final RelationManager relationManager;

   public ArtifactReadOnlyImpl(ExternalArtifactManager proxyManager, RelationManager relationManager, OrcsSession session, Artifact proxiedObject) {
      super(proxyManager, session, proxiedObject);
      this.relationManager = relationManager;
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
      return getProxiedObject().getName();
   }

   @Override
   public boolean matches(Id... identities) {
      return getProxiedObject().matches(identities);
   }

   @Override
   public BranchId getBranch() {
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
   public IArtifactType getArtifactType() throws OseeCoreException {
      return getProxiedObject().getArtifactType();
   }

   @Override
   public boolean isOfType(ArtifactTypeId... otherTypes) throws OseeCoreException {
      return getProxiedObject().isOfType(otherTypes);
   }

   @Override
   public int getAttributeCount(AttributeTypeId type) throws OseeCoreException {
      return getProxiedObject().getAttributeCount(type);
   }

   @Override
   public int getAttributeCount(AttributeTypeId type, DeletionFlag deletionFlag) throws OseeCoreException {
      return getProxiedObject().getAttributeCount(type, deletionFlag);
   }

   @Override
   public boolean isAttributeTypeValid(AttributeTypeId attributeType) throws OseeCoreException {
      return getProxiedObject().isAttributeTypeValid(attributeType);
   }

   @Override
   public Collection<AttributeTypeToken> getValidAttributeTypes() throws OseeCoreException {
      return getProxiedObject().getValidAttributeTypes();
   }

   @Override
   public Collection<AttributeTypeToken> getExistingAttributeTypes() throws OseeCoreException {
      return getProxiedObject().getExistingAttributeTypes();
   }

   @Override
   public <T> T getSoleAttributeValue(AttributeTypeId attributeType) {
      return getProxiedObject().getSoleAttributeValue(attributeType);
   }

   @Override
   public <T> T getSoleAttributeValue(AttributeTypeId attributeType, DeletionFlag flag, T defaultValue) {
      return getProxiedObject().getSoleAttributeValue(attributeType, flag, defaultValue);
   }

   @Override
   public <T> T getSoleAttributeValue(AttributeTypeId attributeType, T defaultValue) throws OseeCoreException {
      return getProxiedObject().getSoleAttributeValue(attributeType, defaultValue);
   }

   @Override
   public String getSoleAttributeAsString(AttributeTypeId attributeType) throws OseeCoreException {
      return getProxiedObject().getSoleAttributeAsString(attributeType);
   }

   @Override
   public String getSoleAttributeAsString(AttributeTypeId attributeType, String defaultValue) throws OseeCoreException {
      return getProxiedObject().getSoleAttributeAsString(attributeType, defaultValue);
   }

   @Override
   public Long getSoleAttributeId(AttributeTypeId attributeType) {
      return getProxiedObject().getSoleAttribute(attributeType).getId();
   }

   @Override
   public <T> List<T> getAttributeValues(AttributeTypeId attributeType) throws OseeCoreException {
      return getProxiedObject().getAttributeValues(attributeType);
   }

   @Override
   public String getAttributeValuesAsString(AttributeTypeId attributeType) {
      List<Attribute<Object>> attributes = getProxiedObject().getAttributes(attributeType);
      return Collections.toString(", ", attributes);
   }

   @Override
   public ResultSet<? extends AttributeReadable<Object>> getAttributes() throws OseeCoreException {
      List<Attribute<Object>> attributes = getProxiedObject().getAttributes();
      return getProxyManager().asExternalAttributes(getSession(), attributes);
   }

   @Override
   public <T> ResultSet<? extends AttributeReadable<T>> getAttributes(AttributeTypeId attributeType) throws OseeCoreException {
      List<Attribute<T>> attributes = getProxiedObject().getAttributes(attributeType);
      return getProxyManager().asExternalAttributes(getSession(), attributes);
   }

   @Override
   public ResultSet<? extends AttributeReadable<Object>> getAttributes(DeletionFlag deletionFlag) throws OseeCoreException {
      List<Attribute<Object>> attributes = getProxiedObject().getAttributes(deletionFlag);
      return getProxyManager().asExternalAttributes(getSession(), attributes);
   }

   @Override
   public <T> ResultSet<? extends AttributeReadable<T>> getAttributes(AttributeTypeId attributeType, DeletionFlag deletionFlag) throws OseeCoreException {
      List<Attribute<T>> attributes = getProxiedObject().getAttributes(attributeType, deletionFlag);
      return getProxyManager().asExternalAttributes(getSession(), attributes);
   }

   @Override
   public AttributeReadable<Object> getAttributeById(AttributeId attributeId) throws OseeCoreException {
      Attribute<Object> attribute = getProxiedObject().getAttributeById(attributeId);
      return getProxyManager().asExternalAttribute(getSession(), attribute);
   }

   @Override
   public int getMaximumRelationAllowed(RelationTypeSide typeAndSide) throws OseeCoreException {
      IRelationType type = typeAndSide.getRelationType();
      RelationSide side = whichSideAmIOn(typeAndSide);
      return getRelationManager().getMaximumRelationAllowed(getSession(), type, getProxiedObject(), side);
   }

   @Override
   public Collection<RelationTypeId> getValidRelationTypes() throws OseeCoreException {
      return getRelationManager().getValidRelationTypes(getSession(), getProxiedObject());
   }

   @Override
   public Collection<RelationTypeId> getExistingRelationTypes() throws OseeCoreException {
      return getRelationManager().getExistingRelationTypes(getSession(), getProxiedObject());
   }

   @Override
   public ArtifactReadable getParent() throws OseeCoreException {
      Artifact parent = getRelationManager().getParent(getSession(), getProxiedObject());
      return getProxyManager().asExternalArtifact(getSession(), parent);
   }

   @Override
   public ResultSet<ArtifactReadable> getChildren() throws OseeCoreException {
      ResultSet<Artifact> children = getRelationManager().getChildren(getSession(), getProxiedObject());
      return getProxyManager().asExternalArtifacts(getSession(), children);
   }

   @Override
   public List<ArtifactReadable> getDescendants() throws OseeCoreException {
      List<ArtifactReadable> descendants = new LinkedList<>();
      getDescendants(descendants);
      return descendants;
   }

   @Override
   public void getDescendants(List<ArtifactReadable> descendants) throws OseeCoreException {
      for (ArtifactReadable child : getChildren()) {
         descendants.add(child);
         child.getDescendants(descendants);
      }
   }

   @Override
   public List<ArtifactReadable> getAncestors() throws OseeCoreException {
      List<ArtifactReadable> ancestors = new ArrayList<>();
      for (ArtifactReadable parent = getParent(); parent != null; parent = parent.getParent()) {
         ancestors.add(parent);
      }
      return ancestors;
   }

   @Override
   public ResultSet<ArtifactReadable> getRelated(RelationTypeSide typeAndSide) throws OseeCoreException {
      return getRelated(typeAndSide, EXCLUDE_DELETED);
   }

   @Override
   public List<ArtifactReadable> getRelated(RelationTypeSide relationTypeSide, ArtifactTypeId artifactType) {
      List<ArtifactReadable> artifacts = new ArrayList<>();
      for (ArtifactReadable artifact : getRelated(relationTypeSide)) {
         if (artifact.isOfType(artifactType)) {
            artifacts.add(artifact);
         }
      }
      return artifacts;
   }

   @Override
   public ResultSet<RelationReadable> getRelations(RelationTypeSide typeAndSide) {
      return new ResultSetList<RelationReadable>(Collections.castAll(
         getRelationManager().getRelations(getSession(), getProxiedObject(), DeletionFlag.EXCLUDE_DELETED)));
   }

   @Override
   public ResultSet<ArtifactReadable> getRelated(RelationTypeSide typeAndSide, DeletionFlag deletionFlag) throws OseeCoreException {
      IRelationType type = typeAndSide.getRelationType();
      RelationSide side = whichSideAmIOn(typeAndSide);
      ResultSet<Artifact> related =
         getRelationManager().getRelated(getSession(), type, getProxiedObject(), side, deletionFlag);
      return getProxyManager().asExternalArtifacts(getSession(), related);
   }

   @Override
   public int getRelatedCount(RelationTypeSide typeAndSide) throws OseeCoreException {
      IRelationType type = typeAndSide.getRelationType();
      RelationSide side = whichSideAmIOn(typeAndSide);
      return getRelationManager().getRelatedCount(getSession(), type, getProxiedObject(), side);
   }

   @Override
   public boolean areRelated(RelationTypeSide typeAndSide, ArtifactReadable readable) throws OseeCoreException {
      IRelationType type = typeAndSide.getRelationType();
      Pair<RelationNode, RelationNode> nodes = asABNodes(typeAndSide.getSide(), readable);
      return getRelationManager().areRelated(getSession(), nodes.getFirst(), type, nodes.getSecond());
   }

   @Override
   public String getRationale(RelationTypeSide typeAndSide, ArtifactReadable readable) throws OseeCoreException {
      IRelationType type = typeAndSide.getRelationType();
      Pair<RelationNode, RelationNode> nodes = asABNodes(typeAndSide.getSide(), readable);
      return getRelationManager().getRationale(getSession(), nodes.getFirst(), type, nodes.getSecond());
   }

   private Pair<RelationNode, RelationNode> asABNodes(RelationSide side, ArtifactReadable readable) throws OseeCoreException {
      Artifact thisArtifact = getProxiedObject();
      Artifact otherArtifact = getProxyManager().asInternalArtifact(readable);

      RelationNode aNode;
      RelationNode bNode;
      if (RelationSide.SIDE_A == side) {
         aNode = otherArtifact;
         bNode = thisArtifact;
      } else {
         aNode = thisArtifact;
         bNode = otherArtifact;
      }
      return new Pair<RelationNode, RelationNode>(aNode, bNode);
   }

   private RelationSide whichSideAmIOn(RelationTypeSide typeAndSide) {
      return typeAndSide.getSide().oppositeSide();
   }

   @Override
   public Long getId() {
      return getProxiedObject().getLocalId().longValue();
   }

   @Override
   public String toString() {
      return String.format("Artifact: Id [%s] Type [%s] Name [%s]", getIdString(), getArtifactType().getName(),
         getName());
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
   public Collection<Long> getChildrentIds() {
      return getRelatedIds(CoreRelationTypes.Default_Hierarchical__Child);
   }

   @Override
   public Collection<Long> getRelatedIds(RelationTypeSide relationTypeSide) {
      List<Long> childIds = new ArrayList<>();
      for (Relation relation : getRelationManager().getRelations(getSession(), getProxiedObject(),
         DeletionFlag.EXCLUDE_DELETED)) {
         boolean relIsSideA = relationTypeSide.getSide().isSideA();
         boolean thisOnCorrectSide =
            (relIsSideA && relation.getArtIdB() == getId().intValue()) || (!relIsSideA && relation.getArtIdA() == getId().intValue());
         if (thisOnCorrectSide && relation.getRelationType().matches(relationTypeSide)) {
            childIds.add(Long.valueOf(relation.getArtIdB()));
         }
      }
      return childIds;
   }

   @Override
   public boolean isHistorical() {
      return getProxiedObject().getOrcsData().getVersion().isHistorical();
   }

   @Override
   public ApplicabilityId getApplicability() {
      return getProxiedObject().getOrcsData().getApplicabilityId();
   }
}