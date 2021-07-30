/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.orcs.core.internal.artifact;

import static org.eclipse.osee.framework.core.enums.DirtyState.APPLICABILITY_ONLY;
import java.util.Collection;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.EditState;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.core.ds.ArtifactData;
import org.eclipse.osee.orcs.core.ds.Attribute;
import org.eclipse.osee.orcs.core.internal.attribute.AttributeFactory;
import org.eclipse.osee.orcs.core.internal.attribute.AttributeManagerImpl;
import org.eclipse.osee.orcs.core.internal.graph.GraphData;
import org.eclipse.osee.orcs.core.internal.relation.order.OrderChange;
import org.eclipse.osee.orcs.data.AttributeReadable;

public class ArtifactImpl extends AttributeManagerImpl implements Artifact {
   private EditState objectEditState;
   private ArtifactData artifactData;
   private GraphData graph;

   public ArtifactImpl(ArtifactData artifactData, AttributeFactory attributeFactory) {
      super(artifactData, attributeFactory);
      this.artifactData = artifactData;
      this.objectEditState = EditState.NO_CHANGE;
   }

   @Override
   public void setGraph(GraphData graph) {
      this.graph = graph;
   }

   @Override
   public GraphData getGraph() {
      return graph;
   }

   @Override
   public ArtifactData getOrcsData() {
      return artifactData;
   }

   @Override
   public void setOrcsData(ArtifactData data) {
      this.artifactData = data;
      objectEditState = EditState.NO_CHANGE;
   }

   @Override
   public ModificationType getModificationType() {
      return getOrcsData().getModType();
   }

   @Override
   public Integer getLocalId() {
      return getId().intValue();
   }

   @Override
   public TransactionId getLastModifiedTransaction() {
      TransactionId maxTransactionId = getOrcsData().getVersion().getTransactionId();
      for (Attribute<?> attribute : getAttributes(DeletionFlag.INCLUDE_DELETED)) {
         TransactionId tx = attribute.getOrcsData().getVersion().getTransactionId();
         if (maxTransactionId.isOlderThan(tx)) {
            maxTransactionId = tx;
         }
      }
      return maxTransactionId;
   }

   @Override
   public TransactionId getTransaction() {
      return graph.getTransaction();
   }

   @Override
   public BranchToken getBranch() {
      return BranchToken.create(artifactData.getVersion().getBranch(), "unknown");
   }

   @Override
   public ArtifactTypeToken getArtifactType() {
      return getOrcsData().getType();
   }

   @Override
   public void setName(String name) {
      setSoleAttributeFromString(CoreAttributeTypes.Name, name);
   }

   @Override
   public void setArtifactType(ArtifactTypeToken artifactType) {
      if (!isTypeEqual(artifactType)) {
         getOrcsData().setType(artifactType);
         objectEditState = EditState.ARTIFACT_TYPE_MODIFIED;
         if (getOrcsData().getVersion().isInStorage()) {
            getOrcsData().setModType(ModificationType.MODIFIED);
         }
      }
   }

   @Override
   public void setNotDirty() {
      setAttributesNotDirty();
      objectEditState = EditState.NO_CHANGE;
      getOrcsData().setModType(ModificationType.MODIFIED);
   }

   @Override
   public boolean isDirty() {
      return areAttributesDirty() || hasDirtyArtifactType() || isReplaceWithVersion() || APPLICABILITY_ONLY == getOrcsData().getDirtyState();
   }

   private boolean isReplaceWithVersion() {
      return getModificationType() == ModificationType.REPLACED_WITH_VERSION || artifactData.isExistingVersionUsed();
   }

   private boolean hasDirtyArtifactType() {
      return objectEditState.isArtifactTypeChange();
   }

   @Override
   public boolean isAttributeTypeValid(AttributeTypeId attributeType) {
      return getArtifactType().isValidAttributeType(attributeType);
   }

   @Override
   public Collection<AttributeTypeToken> getValidAttributeTypes() {
      return getArtifactType().getValidAttributeTypes();
   }

   @Override
   public String getExceptionString() {
      try {
         return String.format("artifact type[%s] id[%s] on branch[%s]", getArtifactType(), getId(),
            getBranch().toStringWithId());
      } catch (OseeCoreException ex) {
         return Lib.exceptionToString(ex);
      }
   }

   @Override
   public void accept(ArtifactVisitor visitor) {
      visitor.visit(this);
      for (Attribute<?> attribute : getAttributes(DeletionFlag.INCLUDE_DELETED)) {
         visitor.visit(attribute);
      }
   }

   @Override
   public void delete() {
      getOrcsData().setModType(ModificationType.DELETED);
      deleteAttributesByArtifact();
   }

   @Override
   public boolean isDeleteAllowed() {
      return !isDeleted();
   }

   @Override
   public void unDelete() {
      getOrcsData().setModType(getOrcsData().getBaseModType());
      unDeleteAttributesByArtifact();
   }

   @Override
   public boolean isAccessible() {
      return !isDeleted();
   }

   @Override
   public String getOrderData() {
      return getSoleAttributeAsString(CoreAttributeTypes.RelationOrder, Strings.emptyString());
   }

   @Override
   public void storeOrderData(OrderChange changeType, String data) {
      if (Strings.isValid(data)) {
         setSoleAttributeFromString(CoreAttributeTypes.RelationOrder, data);
      } else {
         deleteSoleAttribute(CoreAttributeTypes.RelationOrder);
      }
   }

   @Override
   public String toString() {
      try {
         return String.format("artifact [type=[%s] artifact id=[%s] branch=[%s]]", getArtifactType(), getId(),
            getBranch());
      } catch (OseeCoreException ex) {
         return Lib.exceptionToString(ex);
      }
   }

   @Override
   public Iterable<Collection<? extends AttributeReadable<Object>>> getAttributeIterable() {
      return null;
   }

}