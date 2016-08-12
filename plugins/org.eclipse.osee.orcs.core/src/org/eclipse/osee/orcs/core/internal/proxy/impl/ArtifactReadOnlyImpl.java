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

import static org.eclipse.osee.orcs.core.internal.relation.RelationUtil.asRelationType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.data.IRelationTypeSide;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.internal.artifact.Artifact;
import org.eclipse.osee.orcs.core.internal.attribute.Attribute;
import org.eclipse.osee.orcs.core.internal.proxy.ExternalArtifactManager;
import org.eclipse.osee.orcs.core.internal.relation.RelationManager;
import org.eclipse.osee.orcs.core.internal.relation.RelationNode;
import org.eclipse.osee.orcs.core.internal.relation.RelationUtil;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.AttributeReadable;

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
   public Integer getLocalId() {
      return getProxiedObject().getLocalId();
   }

   @Override
   public Long getBranchId() {
      return getProxiedObject().getBranchId();
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
   public boolean isOfType(IArtifactType... otherTypes) throws OseeCoreException {
      return getProxiedObject().isOfType(otherTypes);
   }

   @Override
   public int getAttributeCount(IAttributeType type) throws OseeCoreException {
      return getProxiedObject().getAttributeCount(type);
   }

   @Override
   public int getAttributeCount(IAttributeType type, DeletionFlag deletionFlag) throws OseeCoreException {
      return getProxiedObject().getAttributeCount(type, deletionFlag);
   }

   @Override
   public boolean isAttributeTypeValid(IAttributeType attributeType) throws OseeCoreException {
      return getProxiedObject().isAttributeTypeValid(attributeType);
   }

   @Override
   public Collection<? extends IAttributeType> getValidAttributeTypes() throws OseeCoreException {
      return getProxiedObject().getValidAttributeTypes();
   }

   @Override
   public Collection<? extends IAttributeType> getExistingAttributeTypes() throws OseeCoreException {
      return getProxiedObject().getExistingAttributeTypes();
   }

   @Override
   public <T> T getSoleAttributeValue(IAttributeType attributeType) {
      return getProxiedObject().getSoleAttributeValue(attributeType);
   }

   @Override
   public <T> T getSoleAttributeValue(IAttributeType attributeType, DeletionFlag flag, T defaultValue) {
      return getProxiedObject().getSoleAttributeValue(attributeType, flag, defaultValue);
   }

   @Override
   public <T> T getSoleAttributeValue(IAttributeType attributeType, T defaultValue) throws OseeCoreException {
      return getProxiedObject().getSoleAttributeValue(attributeType, defaultValue);
   }

   @Override
   public String getSoleAttributeAsString(IAttributeType attributeType) throws OseeCoreException {
      return getProxiedObject().getSoleAttributeAsString(attributeType);
   }

   @Override
   public String getSoleAttributeAsString(IAttributeType attributeType, String defaultValue) throws OseeCoreException {
      return getProxiedObject().getSoleAttributeAsString(attributeType, defaultValue);
   }

   @Override
   public Long getSoleAttributeId(IAttributeType attributeType) {
      return (long) getProxiedObject().getSoleAttribute(attributeType).getLocalId();
   }

   @Override
   public <T> List<T> getAttributeValues(IAttributeType attributeType) throws OseeCoreException {
      return getProxiedObject().getAttributeValues(attributeType);
   }

   @Override
   public ResultSet<? extends AttributeReadable<Object>> getAttributes() throws OseeCoreException {
      List<Attribute<Object>> attributes = getProxiedObject().getAttributes();
      return getProxyManager().asExternalAttributes(getSession(), attributes);
   }

   @Override
   public <T> ResultSet<? extends AttributeReadable<T>> getAttributes(IAttributeType attributeType) throws OseeCoreException {
      List<Attribute<T>> attributes = getProxiedObject().getAttributes(attributeType);
      return getProxyManager().asExternalAttributes(getSession(), attributes);
   }

   @Override
   public ResultSet<? extends AttributeReadable<Object>> getAttributes(DeletionFlag deletionFlag) throws OseeCoreException {
      List<Attribute<Object>> attributes = getProxiedObject().getAttributes(deletionFlag);
      return getProxyManager().asExternalAttributes(getSession(), attributes);
   }

   @Override
   public <T> ResultSet<? extends AttributeReadable<T>> getAttributes(IAttributeType attributeType, DeletionFlag deletionFlag) throws OseeCoreException {
      List<Attribute<T>> attributes = getProxiedObject().getAttributes(attributeType, deletionFlag);
      return getProxyManager().asExternalAttributes(getSession(), attributes);
   }

   @Override
   public AttributeReadable<Object> getAttributeById(Integer attributeId) throws OseeCoreException {
      Attribute<Object> attribute = getProxiedObject().getAttributeById(attributeId);
      return getProxyManager().asExternalAttribute(getSession(), attribute);
   }

   @Override
   public boolean isHardDeleted() {
      return getProxiedObject().isHardDeleted();
   }

   @Override
   public int getMaximumRelationAllowed(IRelationTypeSide typeAndSide) throws OseeCoreException {
      IRelationType type = asRelationType(typeAndSide);
      RelationSide side = whichSideAmIOn(typeAndSide);
      return getRelationManager().getMaximumRelationAllowed(getSession(), type, getProxiedObject(), side);
   }

   @Override
   public Collection<? extends IRelationType> getValidRelationTypes() throws OseeCoreException {
      return getRelationManager().getValidRelationTypes(getSession(), getProxiedObject());
   }

   @Override
   public Collection<? extends IRelationType> getExistingRelationTypes() throws OseeCoreException {
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
   public ResultSet<ArtifactReadable> getRelated(IRelationTypeSide typeAndSide) throws OseeCoreException {
      IRelationType type = asRelationType(typeAndSide);
      RelationSide side = whichSideAmIOn(typeAndSide);
      ResultSet<Artifact> related = getRelationManager().getRelated(getSession(), type, getProxiedObject(), side);
      return getProxyManager().asExternalArtifacts(getSession(), related);
   }

   @Override
   public int getRelatedCount(IRelationTypeSide typeAndSide) throws OseeCoreException {
      IRelationType type = asRelationType(typeAndSide);
      RelationSide side = whichSideAmIOn(typeAndSide);
      return getRelationManager().getRelatedCount(getSession(), type, getProxiedObject(), side);
   }

   @Override
   public boolean areRelated(IRelationTypeSide typeAndSide, ArtifactReadable readable) throws OseeCoreException {
      IRelationType type = asRelationType(typeAndSide);
      Pair<RelationNode, RelationNode> nodes = asABNodes(typeAndSide.getSide(), readable);
      return getRelationManager().areRelated(getSession(), nodes.getFirst(), type, nodes.getSecond());
   }

   @Override
   public String getRationale(IRelationTypeSide typeAndSide, ArtifactReadable readable) throws OseeCoreException {
      IRelationType type = asRelationType(typeAndSide);
      Pair<RelationNode, RelationNode> nodes = asABNodes(typeAndSide.getSide(), readable);
      return getRelationManager().getRationale(getSession(), nodes.getFirst(), type, nodes.getSecond());
   }

   private Pair<RelationNode, RelationNode> asABNodes(RelationSide side, ArtifactReadable readable) throws OseeCoreException {
      Artifact thisArtifact = getProxiedObject();
      Artifact otherArtifact = getProxyManager().asInternalArtifact(readable);
      return RelationUtil.asABNodes(thisArtifact, otherArtifact, side);
   }

   private RelationSide whichSideAmIOn(IRelationTypeSide typeAndSide) {
      return typeAndSide.getSide().oppositeSide();
   }

   @Override
   public Long getUuid() {
      return Long.valueOf(getLocalId());
   }

   @Override
   public String toString() {
      return String.format("Artifact: Id [%s] Type [%s] Name [%s]", getId().toString(), getArtifactType().getName(),
         getName());
   }
}
