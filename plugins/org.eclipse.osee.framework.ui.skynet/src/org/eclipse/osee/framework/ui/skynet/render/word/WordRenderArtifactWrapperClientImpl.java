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
import org.eclipse.osee.framework.core.data.BranchSpecification;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.ComputedCharacteristicToken;
import org.eclipse.osee.framework.core.data.IAttribute;
import org.eclipse.osee.framework.core.data.IRelationLink;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.data.TransactionDetails;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.publishing.PublishingArtifact;
import org.eclipse.osee.framework.core.publishing.PublishingArtifactBase;
import org.eclipse.osee.framework.core.publishing.PublishingArtifactLoader;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * A client-side implementation of the {@link PublishingArtifact} interface for {@link Artifact} objects.
 *
 * @implNote This wrapper class is used to consolidate duplicated publishing code between the server and the client.
 * Only the {@link ArtifactReadable} super interface methods needed for the code consolidation have been implemented.
 * The remaining methods throw an {@link UnsupportedOperationException}.
 * @author Loren K. Ashley
 */

public class WordRenderArtifactWrapperClientImpl extends PublishingArtifactBase {

   /**
    * Saves the status of the artifact's applicability as yes, no, or unknown.
    */

   PublishingArtifact.Applicability applicable;

   /**
    * The wrapped {@link Artifact}.
    */

   private final Artifact artifact;

   /**
    * Saves the branch the wrapped {@link Artifact} is from. The {@link BranchToken} will be set to {@link Id#SENTINEL}
    * when the applicability status of the artifact is no or unknown.
    */

   private BranchToken branchToken;

   /**
    * Creates a new client/server agnostic wrapper for the {@link Artifact}.
    *
    * @param artifact the {@link Artifact} to wrap.
    * @throws NullPointerException when <code>artifact</code> is <code>null</code>.
    * @throws IllegalArgumentException when <code>artifact</code> is not an instance of {@link Artifact} or is sentinel.
    */

   public WordRenderArtifactWrapperClientImpl(Object artifact) {

      Objects.requireNonNull(artifact,
         "WordRenderArtifactWrapperClientImpl::new, parameter \"artifact\" cannont be null.");

      if (artifact instanceof Artifact) {
         this.artifact = (Artifact) artifact;
      } else if (artifact instanceof WordRenderArtifactWrapperClientImpl) {
         this.artifact = ((WordRenderArtifactWrapperClientImpl) artifact).getArtifact();
      } else {
         throw new IllegalArgumentException(
            "WordRenderArtifactWrapperClientImpl::new, parameter \"artifact\" must be an instance of \"Artifact\" or \"WordRenderArtifactWrapperClientImpl\".");
      }

      if (this.artifact.isInvalid()) {
         throw new IllegalArgumentException(
            "WordRenderArtifactWrapperClientImpl::new, parameter \"artifact\" cannont be sentinel.");
      }

      var artifactBranchToken = this.artifact.getBranch();

      this.branchToken =
         BranchToken.create(artifactBranchToken.getId(), artifactBranchToken.getName(), ArtifactId.SENTINEL);

      this.applicable = PublishingArtifact.Applicability.YES;
   }

   /**
    * Creates a new client/server agnostic wrapper for the {@link Artifact} implementation. This constructor implements
    * the functional interface {@link PublishingArtifactLoader.PublishingArtifactFactoryWithView}.
    *
    * @param artifact the {@link Artifact} to wrap.
    * @param branchSpecification the {@link BranchSpecification} the artifact was loaded with.
    * @throws NullPointerException when <code>artifact</code> is <code>null</code>.
    * @throws IllegalArgumentException when <code>artifact</code> is not an implementation of {@link Artifact} or is
    * sentinel.
    */

   public WordRenderArtifactWrapperClientImpl(Object artifact, BranchSpecification branchSpecification) {

      Objects.requireNonNull(artifact,
         "WordRenderArtifactWrapperClientImpl::new, parameter \"artifact\" cannont be null.");

      if (artifact instanceof Artifact) {
         this.artifact = (Artifact) artifact;
      } else if (artifact instanceof WordRenderArtifactWrapperClientImpl) {
         this.artifact = ((WordRenderArtifactWrapperClientImpl) artifact).getArtifact();
      } else {
         throw new IllegalArgumentException(
            "WordRenderArtifactWrapperClientImpl::new, parameter \"artifact\" must be an instance of \"Artifact\" or \"WordRenderArtifactWrapperClientImpl\".");
      }

      if (this.artifact.isInvalid()) {
         throw new IllegalArgumentException(
            "WordRenderArtifactWrapperClientImpl::new, parameter \"artifact\" cannont be sentinel.");
      }

      Objects.requireNonNull(branchSpecification);

      var artifactBranchToken = this.artifact.getBranch();

      //@formatter:off
      assert
           artifactBranchToken.isSameBranch( branchSpecification.getBranchId() )
         : new Message()
                  .title( "WordRenderArtifactWrapperServerImpl::new, parameter \"branchSpecification\" is for a different branch than the parameter \"artifact\"." )
                  .indentInc()
                  .segment( "Artifact Branch",             this.artifact.getBranch().getIdString()         )
                  .segment( "Branch Specification Branch", branchSpecification.getBranchId().getIdString() )
                  .toString();

      this.branchToken = BranchToken.create(artifactBranchToken.getId(), artifactBranchToken.getName(), branchSpecification.getViewId() );

      //@formatter:off
      this.applicable =
         branchSpecification.getViewId().isValid()
            ? PublishingArtifact.Applicability.YES
            : PublishingArtifact.Applicability.UNKNOWN;
      //@formatter:on
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
   public boolean branchHasView() {
      var viewId = this.artifact.getBranch().getViewId();
      return Objects.nonNull(viewId) && viewId.isValid();
   }

   @Override
   public void clearApplicable() {
      this.applicable = PublishingArtifact.Applicability.NO;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public void clearBranchView() {
      throw new UnsupportedOperationException();
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

   @Override
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
    */

   @Override
   public int getAttributeCount(AttributeTypeToken type) {
      return this.artifact.getAttributeCount(type);
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
   public <T> ResultSet<? extends AttributeReadable<T>> getAttributes(AttributeTypeToken attributeType,
      DeletionFlag deletionFlag) {
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

   @Override
   public HashCollection<AttributeTypeToken, IAttribute<?>> getAttributesHashCollection() {
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
      return this.artifact.getAttributesToString(attributeType);
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
    * @implNote The returned children are implemented with {@WordRenderArtifactWrapperClientImpl} objects. The outline
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
            .map(WordRenderArtifactWrapperClientImpl::new)
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
   public String getGuid() {
      return this.artifact.getGuid();
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
   public PublishingArtifact getParent() {
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

   @Override
   public ArtifactReadable getReferenceArtifactByAttrId(AttributeId attributeId) {
      throw new UnsupportedOperationException();
   }

   @Override
   public List<ArtifactReadable> getReferenceArtifactsByType(AttributeTypeToken attributeType) {
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
   public List<ArtifactReadable> getRelated(RelationTypeSide relationTypeSide, ArtifactTypeToken artifactType,
      DeletionFlag deletionFlag) {
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
    */

   @Override
   public TransactionId getTransaction() {
      return this.artifact.getTransaction();
   }

   @Override
   public TransactionDetails getTxDetails() {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Collection<AttributeTypeToken> getValidAttributeTypes() {
      return this.artifact.getAttributeTypes();
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
   public boolean hasAttributeContent(AttributeTypeToken attributeTypeToken) {
      //@formatter:off
      return
            ( attributeTypeToken != null )
         && this.artifact.isAttributeTypeValid(attributeTypeToken)
         && ( this.artifact.getAttributeCount(attributeTypeToken) > 0 );
      //@formatter:on
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public int hashCode() {
      return this.artifact.hashCode();
   }

   @Override
   public PublishingArtifact.Applicability isApplicable() {
      return this.applicable;
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
   public boolean isFound() {
      return true;
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
    *
    * @implNote For a loaded artifact {@link PublishingArtifact} there is no need to reload the artifact.
    */

   @Override
   public PublishingArtifact.TryReload isReloadable() {
      return PublishingArtifact.TryReload.NO;
   }

   @Override
   public void setApplicable() {
      this.applicable = PublishingArtifact.Applicability.YES;
   }

   @Override
   public void setBranchView(BranchSpecification branchSpecification) {
      //@formatter:off
      assert
              Objects.nonNull( branchSpecification )
           && branchSpecification.hasView()
           && (    Objects.isNull( this.branchToken )
                || (    ( Objects.isNull( this.branchToken.getViewId() ) || this.branchToken.getViewId().isInvalid() )
                     && ( branchSpecification.getBranchId().getId().equals( this.branchToken.getId() ) ) ) )
         : new Message()
                  .title( "PublishingArtifact.PublishingArtifactNotFound::setBranchView, Artifact and/or branchSpecification have unexpected values." )
                  .indentInc()
                  .segment( "Not Found Publishing Artifact Branch Token Identifier",      this.branchToken.getId() )
                  .segment( "Not Found Publishing Artifact Branch Token View Identifier", Objects.nonNull( this.branchToken.getViewId() ) ? this.branchToken.getViewId().getId() : "(null)" )
                  .segment( "Branch Specification With View",             branchSpecification )
                  .toString();
      //@formatter:on
      this.branchToken = BranchToken.create(branchSpecification.getBranchId().getId(), this.branchToken.getName(),
         branchSpecification.getViewId());
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
         .title( "PublishingArtifact (WordRenderArtifactWrapperClientImpl)" )
         .indentInc()
         .segment( "Identifier",                   this.getIdString()                 )
         .segment( "GUID",                         this.getGuid()                     )
         .segment( "Name",                         this.getName()                     )
         .segment( "Type",                         this.getArtifactTypeName()         )
         .segment( "Processed",                    this.isProcessed()                 )
         .segment( "Publishing Content Cached",    this.isPublishingContentCached()   )
         .segment( "Branch Identifier",            this.branchToken.getId()           )
         .segment( "Branch View",                  this.branchToken.getViewId()       )
         .segment( "Applicable",                   this.applicable                    )
         .segment( "Wrapped Implementation Class", this.artifact.getClass().getName() )
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
