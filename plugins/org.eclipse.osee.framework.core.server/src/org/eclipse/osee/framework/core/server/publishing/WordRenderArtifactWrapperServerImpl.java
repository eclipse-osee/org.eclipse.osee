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

package org.eclipse.osee.framework.core.server.publishing;

import java.util.Collection;
import java.util.LinkedList;
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
import org.eclipse.osee.framework.core.publishing.PublishingArtifact;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.util.Message;

/**
 * A server-side implementation of the {@link PublishingArtifact} interface for {@link Artifact} objects.
 *
 * @implNote This wrapper class is used to consolidate duplicated publishing code between the server and the client.
 * Only the {@link ArtifactReadable} super interface methods needed for the code consolidation have been implemented.
 * The remaining methods throw an {@link UnsupportedOperationException}.
 * @author Loren K. Ashley
 */

public class WordRenderArtifactWrapperServerImpl implements PublishingArtifact {

   /**
    * The wrapped {@link Artifact}.
    */

   private final ArtifactReadable artifact;

   /**
    * Set <code>false</code> when this artifact is the last artifact at it's hierarchy level.
    */

   private boolean endsSection;

   /**
    * Set to the hierarchical depth of the artifact.
    */

   private int outlineLevel;

   /**
    * Set <code>true</code> when this artifact is the first artifact at it's hierarchy level.
    */

   private boolean startsSection;

   /**
    * Creates a new client/server agnostic wrapper for the {@link Artifact}.
    *
    * @param artifact the {@link Artifact} to wrap.
    */

   public WordRenderArtifactWrapperServerImpl(ArtifactReadable artifact) {
      this.artifact = artifact;
      this.outlineLevel = 0;
      this.startsSection = false;
      this.endsSection = false;
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
    */

   @Override
   public void clearEndOfSection() {
      this.endsSection = false;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public void clearStartOfSection() {
      this.startsSection = false;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public boolean equals(Object other) {
      return this.artifact.equals(other);
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
      return this.artifact.getApplicability();
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

   public ArtifactReadable getArtifact() {
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
   public String getAttributeValuesAsString(AttributeTypeToken attributeType) {
      return this.artifact.getAttributeValuesAsString(attributeType);
   }

   /**
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException method has not been implemented.
    */

   @Override
   public BranchToken getBranch() {
      return this.artifact.getBranch();
   }

   /**
    * {@inheritDoc}
    *
    * @implNote The returned children are implemented with {@WordRenderArtifactWrapperServerImpl} objects. The outline
    * level is set to one more that the outline level of this artifact. The start of section flag is set for the first
    * child and the end of section flag is set for the last child.
    */

   @Override
   public List<ArtifactReadable> getChildren() {

      final var childOutlineLevel = this.getOutlineLevel() + 1;

      //@formatter:off
      LinkedList<PublishingArtifact> children =
         this.artifact
            .getChildren()
            .stream()
            .map(WordRenderArtifactWrapperServerImpl::new)
            .peek( ( artifact ) -> artifact.setOutlineLevel( childOutlineLevel ) )
            .collect(Collectors.toCollection(LinkedList::new));
      //@formatter:on

      if (!children.isEmpty()) {
         children.getFirst().setStartOfSection();
         children.getLast().setEndOfSection();
      }

      @SuppressWarnings("unchecked")
      var artifactReadableChildren = (List<ArtifactReadable>) (Object) children;

      return artifactReadableChildren;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public List<PublishingArtifact> getChildrenAsPublishingArtifacts() {

      @SuppressWarnings("unchecked")
      var publishingArtifactChildren = (List<PublishingArtifact>) (Object) this.getChildren();

      return publishingArtifactChildren;

   }

   /**
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException method has not been implemented.
    */

   @Override
   public Collection<ArtifactId> getChildrenIds() {
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
            .map(WordRenderArtifactWrapperServerImpl::new)
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
   public int getOutlineLevel() {
      return this.outlineLevel;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public ArtifactReadable getParent() {
      var parent = this.artifact.getParent();

      return (Objects.nonNull(parent)) ? new WordRenderArtifactWrapperServerImpl(parent) : null;
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
      return this.artifact.getTransaction();
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Collection<AttributeTypeToken> getValidAttributeTypes() {
      return this.artifact.getValidAttributeTypes();
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
    */

   @Override
   public int hashCode() {
      return this.artifact.hashCode();
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
   public boolean isEndOfSection() {
      return this.endsSection;
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
   public boolean isStartOfSection() {
      return this.startsSection;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public void setEndOfSection() {
      this.endsSection = true;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public void setOutlineLevel(int level) {
      this.outlineLevel = level;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public void setStartOfSection() {
      this.startsSection = true;
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
         .title( "PublishingArtifact (WordRenderArtifactWrapperServerImpl)" )
         .indentInc()
         .segment( "Identifier",    this.getIdString()         )
         .segment( "Name",          this.getName()             )
         .segment( "Type",          this.getArtifactTypeName() )
         .segment( "Start Section", this.startsSection         )
         .segment( "End Section",   this.endsSection           )
         .segment( "Outline Level", this.outlineLevel          )
         .indentDec()
         ;
      //@formatter:on
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
