/*********************************************************************
 * Copyright (c) 2023 Boeing
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

package org.eclipse.osee.framework.ui.skynet.render.word;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.ApplicabilityToken;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.core.data.AttributeReadable;
import org.eclipse.osee.framework.core.data.AttributeTypeGeneric;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.ComputedCharacteristicToken;
import org.eclipse.osee.framework.core.data.IAttribute;
import org.eclipse.osee.framework.core.data.IRelationLink;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.jdk.core.util.ToMessage;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * A client side implementation of the {@link ArtifactReadable} interface for {@link Artifact} objects.
 *
 * @implNote This wrapper class is used to consolidate duplicated publishing code between the server and the client.
 * Only the methods needed for the code consolidation have been implemented. The remaining methods throw an
 * {@link UnsupportedOperationException}.
 * @author Loren K. Ashley
 */

public class WordRenderArtifactWrapperClientImpl implements ArtifactReadable, ToMessage {

   /**
    * The wrapped {@link Artifact}.
    */

   private final Artifact artifact;

   /**
    * Creates a new client/server agnostic wrapper for the {@link Artifact}.
    *
    * @param artifact the {@link Artifact} to wrap.
    */

   public WordRenderArtifactWrapperClientImpl(Artifact artifact) {
      this.artifact = artifact;
   }

   /**
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException method has not been implemented.
    */

   @Override
   public boolean areRelated(RelationTypeSide typeAndSide, ArtifactReadable artifact) {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException method has not been implemented.
    */

   @Override
   public List<String> fetchAttributesAsStringList(AttributeTypeToken attributeType) {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public ApplicabilityId getApplicability() {
      return this.artifact.getApplicablityId();
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public ApplicabilityToken getApplicabilityToken() {
      throw new UnsupportedOperationException();
   }

   /**
    * Gets the wrapped {@link Artifact}.
    *
    * @return the wrapped {@link Artifact}.
    */

   public Artifact getArtifact() {
      return this.artifact;
   }

   /**
    * {@inheritDoc}
    */

   public ArtifactId getArtifactId() {
      return ArtifactId.create(this.artifact);
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public ArtifactTypeToken getArtifactType() {
      return this.artifact.getArtifactType();
   }

   /**
    * {@inheritDoc}
    */

   public String getArtifactTypeName() {
      return this.artifact.getArtifactType().getName();
   }

   /**
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException method has not been implemented.
    */

   @Override
   public AttributeReadable<Object> getAttributeById(AttributeId attributeId) {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException method has not been implemented.
    */

   @Override
   public int getAttributeCount(AttributeTypeToken type) {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException method has not been implemented.
    */

   @Override
   public int getAttributeCount(AttributeTypeToken type, DeletionFlag deletionFlag) {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException method has not been implemented.
    */

   @Override
   public Iterable<Collection<? extends AttributeReadable<Object>>> getAttributeIterable() {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException method has not been implemented.
    */

   @Override
   public <T> List<IAttribute<T>> getAttributeList(AttributeTypeGeneric<T> attributeType) {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException method has not been implemented.
    */

   @Override
   public ResultSet<? extends AttributeReadable<Object>> getAttributes() {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException method has not been implemented.
    */

   @Override
   public <T> ResultSet<? extends AttributeReadable<T>> getAttributes(AttributeTypeToken attributeType) {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException method has not been implemented.
    */

   @Override
   public <T> ResultSet<? extends AttributeReadable<T>> getAttributes(AttributeTypeToken attributeType, DeletionFlag deletionFlag) {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException method has not been implemented.
    */

   @Override
   public ResultSet<? extends AttributeReadable<Object>> getAttributes(DeletionFlag deletionFlag) {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException method has not been implemented.
    */

   @Override
   public String getAttributeValuesAsString(AttributeTypeToken attributeType) {
      return this.artifact.getAttributesToString(attributeType);
   }

   /**
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException method has not been implemented.
    */

   @Override
   public <T> List<T> getAttributeValues(AttributeTypeToken attributeType) {
      return this.artifact.getAttributeValues(attributeType);
   }

   /**
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException method has not been implemented.
    */

   @Override
   public <T> List<T> getAttributeValues(AttributeTypeToken attributeType, DeletionFlag deletionFlag) {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException method has not been implemented.
    */

   @Override
   public BranchToken getBranch() {
      return this.artifact.getBranchToken();
   }

   /**
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException method has not been implemented.
    */

   @Override
   public List<ArtifactReadable> getChildren() {
      //@formatter:off
      return
         this.artifact
            .getChildren()
            .stream()
            .map(WordRenderArtifactWrapperClientImpl::new)
            .collect(Collectors.toList());
      //@formatter:on
   }

   /**
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException method has not been implemented.
    */

   @Override
   public Collection<ArtifactId> getChildrentIds() {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException method has not been implemented.
    */

   @Override
   public <T> T getComputedCharacteristicValue(ComputedCharacteristicToken<T> computedCharacteristic) {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException method has not been implemented.
    */

   @Override
   public List<ArtifactReadable> getDescendants() {
      //@formatter:off
      return
         this.artifact
            .getDescendants()
            .stream()
            .map(WordRenderArtifactWrapperClientImpl::new)
            .collect(Collectors.toList());
      //@formatter:on
   }

   /**
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException method has not been implemented.
    */

   @Override
   public void getDescendants(List<ArtifactReadable> descendants) {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException method has not been implemented.
    */

   @Override
   public Collection<AttributeTypeToken> getExistingAttributeTypes() {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException method has not been implemented.
    */

   @Override
   public Collection<RelationTypeToken> getExistingRelationTypes() {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Long getId() {
      return this.artifact.getId();
   }

   /**
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException method has not been implemented.
    */

   @Override
   public TransactionId getLastModifiedTransaction() {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException method has not been implemented.
    */

   @Override
   public int getMaximumRelationAllowed(RelationTypeSide relationTypeSide) {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException method has not been implemented.
    */

   @Override
   public ModificationType getModificationType() {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public String getName() {
      return this.artifact.getName();
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public ArtifactReadable getParent() {
      var parent = this.artifact.getParent();

      return (Objects.nonNull(parent)) ? new WordRenderArtifactWrapperClientImpl(parent) : null;
   }

   /**
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException method has not been implemented.
    */

   @Override
   public String getRationale(RelationTypeSide typeAndSide, ArtifactReadable readable) {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException method has not been implemented.
    */

   @Override
   public ResultSet<ArtifactReadable> getRelated(RelationTypeSide relationTypeSide) {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException method has not been implemented.
    */

   @Override
   public List<ArtifactReadable> getRelated(RelationTypeSide relationTypeSide, ArtifactTypeToken artifactType, DeletionFlag deletionFlag) {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException method has not been implemented.
    */

   @Override
   public List<ArtifactReadable> getRelated(RelationTypeSide relationTypeSide, DeletionFlag deletionFlag) {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException method has not been implemented.
    */

   @Override
   public int getRelatedCount(RelationTypeSide typeAndSide) {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException method has not been implemented.
    */

   @Override
   public List<ArtifactId> getRelatedIds(RelationTypeSide relationTypeSide) {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException method has not been implemented.
    */

   @Override
   public List<ArtifactReadable> getRelatedList(RelationTypeSide relationTypeSide) {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException method has not been implemented.
    */

   @Override
   public ResultSet<IRelationLink> getRelations(RelationTypeSide relationTypeSide) {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException method has not been implemented.
    */

   @Override
   public String getSafeName() {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException method has not been implemented.
    */

   @Override
   public String getSoleAttributeAsString(AttributeTypeToken attributeType) {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException method has not been implemented.
    */

   @Override
   public String getSoleAttributeAsString(AttributeTypeToken attributeTypeToken, String defaultValue) {
      return this.artifact.getSoleAttributeValue(attributeTypeToken, defaultValue);
   }

   /**
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException method has not been implemented.
    */

   @Override
   public AttributeId getSoleAttributeId(AttributeTypeToken attributeType) {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException method has not been implemented.
    */

   @Override
   public Long getSoleAttributeId(AttributeTypeToken attributeType, Long defaultValue) {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException method has not been implemented.
    */

   @Override
   public <T> T getSoleAttributeValue(AttributeTypeToken attributeType) {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException method has not been implemented.
    */

   @Override
   public <T> T getSoleAttributeValue(AttributeTypeToken attributeType, DeletionFlag flag, T defaultValue) {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public <T> T getSoleAttributeValue(AttributeTypeToken attributeTypeToken, T defaultValue) {
      return this.artifact.getSoleAttributeValue(attributeTypeToken, defaultValue);
   }

   /**
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException method has not been implemented.
    */

   @Override
   public TransactionId getTransaction() {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException method has not been implemented.
    */

   @Override
   public Collection<AttributeTypeToken> getValidAttributeTypes() {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException method has not been implemented.
    */

   @Override
   public Collection<RelationTypeToken> getValidRelationTypes() {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException method has not been implemented.
    */

   @Override
   public boolean isAttributeTypeValid(AttributeTypeId attributeType) {
      return this.artifact.isAttributeTypeValid(attributeType);
   }

   /**
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException method has not been implemented.
    */

   @Override
   public boolean isDescendantOf(ArtifactToken parent) {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public boolean isHistorical() {
      return this.artifact.isHistorical();
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public boolean isInvalid() {
      return this.artifact.isInvalid();
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public boolean isOfType(ArtifactTypeId... artifactTypeId) {
      return this.artifact.isOfType(artifactTypeId);
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Message toMessage(int indent, Message message) {
      var outMessage = Objects.isNull(message) ? new Message() : message;
      //@formatter:off
      outMessage
         .indent( indent )
         .title( "ArtifactReadable (WordRenderArtifactWrapperClientImpl)" )
         .indentInc()
         .segment( "Identifier", this.getIdString()         )
         .segment( "Name",       this.getName()             )
         .segment( "Type",       this.getArtifactTypeName() )
         .indentDec()
         ;


      return outMessage;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public String toString() {
      return this.toMessage(0, null).toString();
   }

}

/* EOF */
