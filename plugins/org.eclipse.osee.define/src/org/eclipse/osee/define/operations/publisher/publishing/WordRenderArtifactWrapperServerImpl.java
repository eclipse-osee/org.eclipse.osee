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

package org.eclipse.osee.define.operations.publisher.publishing;

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
import org.eclipse.osee.framework.core.data.AttributeTypeEnum;
import org.eclipse.osee.framework.core.data.AttributeTypeGeneric;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchSpecification;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.ComputedCharacteristicToken;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.data.IAttribute;
import org.eclipse.osee.framework.core.data.IRelationLink;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.data.TransactionDetails;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.EnumToken;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.publishing.PublishingArtifact;
import org.eclipse.osee.framework.core.publishing.PublishingArtifactBase;
import org.eclipse.osee.framework.core.publishing.PublishingArtifactLoader;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
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

public class WordRenderArtifactWrapperServerImpl extends PublishingArtifactBase {

   /**
    * Saves the status of the artifact's applicability as yes, no, or unknown.
    */

   PublishingArtifact.Applicability applicable;

   private final ArtifactReadable artifact;
   private BranchToken branchToken;

   /**
    * Creates a new client/server agnostic wrapper for the {@link ArtifactReadable} implementation. This constructor
    * implements the functional interface {@link PublishingArtifactLoader.PublishingArtifactFactoryWithoutView}.
    *
    * @param artifact the {@link ArtifactReadable} to wrap.
    * @throws NullPointerException when <code>artifact</code> is <code>null</code>.
    * @throws IllegalArgumentException when <code>artifact</code> is not an implementation of {@link ArtifactReadable}
    * or is sentinel.
    */

   public WordRenderArtifactWrapperServerImpl(Object artifact) {

      Objects.requireNonNull(artifact,
         "WordRenderArtifactWrapperServerImpl::new, parameter \"artifact\" cannont be null.");

      if (!(artifact instanceof ArtifactReadable)) {
         throw new IllegalArgumentException(
            "WordRenderArtifactWrapperServerImpl::new, parameter \"artifact\" must be an instance of \"ArtifactReadable\".");
      }

      if (((ArtifactReadable) artifact).isInvalid()) {
         throw new IllegalArgumentException(
            "WordRenderArtifactWrapperServerImpl::new, parameter \"artifact\" cannont be sentinel.");
      }

      this.artifact = (ArtifactReadable) artifact;

      var artifactBranchToken = this.artifact.getBranch();

      this.branchToken =
         BranchToken.create(artifactBranchToken.getId(), artifactBranchToken.getName(), ArtifactId.SENTINEL);

      this.applicable = PublishingArtifact.Applicability.YES;
   }

   /**
    * Creates a new client/server agnostic wrapper for the {@link ArtifactReadable} implementation. This constructor
    * implements the functional interface {@link PublishingArtifactLoader.PublishingArtifactFactoryWithView}.
    *
    * @param artifact the {@link ArtifactReadable} to wrap.
    * @param branchSpecification the {@link BranchSpecification} the artifact was loaded with.
    * @throws NullPointerException when <code>artifact</code> is <code>null</code>.
    * @throws IllegalArgumentException when <code>artifact</code> is not an implementation of {@link ArtifactReadable}
    * or is sentinel.
    */

   public WordRenderArtifactWrapperServerImpl(Object artifact, BranchSpecification branchSpecification) {

      Objects.requireNonNull(artifact,
         "WordRenderArtifactWrapperServerImpl::new, parameter \"artifact\" cannont be null.");

      if (!(artifact instanceof ArtifactReadable)) {
         throw new IllegalArgumentException(
            "WordRenderArtifactWrapperServerImpl::new, parameter \"artifact\" must be an instance of \"ArtifactReadable\".");
      }

      if (((ArtifactReadable) artifact).isInvalid()) {
         throw new IllegalArgumentException(
            "WordRenderArtifactWrapperServerImpl::new, parameter \"artifact\" cannont be sentinel.");
      }

      Objects.requireNonNull(branchSpecification);

      this.artifact = (ArtifactReadable) artifact;

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

   @Override
   public boolean areRelated(RelationTypeSide typeAndSide, ArtifactReadable artifact) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean branchHasView() {
      var viewId = this.branchToken.getViewId();
      return Objects.nonNull(viewId) && viewId.isValid();
   }

   @Override
   public void clearBranchView() {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean equals(Object other) {
      return this.artifact.equals(other);
   }

   @Override
   public List<String> fetchAttributesAsStringList(AttributeTypeToken attributeType) {
      throw new UnsupportedOperationException();
   }

   @Override
   public ApplicabilityId getApplicability() {
      return this.artifact.getApplicability();
   }

   @Override
   public ApplicabilityToken getApplicabilityToken() {
      throw new UnsupportedOperationException();
   }

   public ArtifactReadable getArtifact() {
      return this.artifact;
   }

   @Override
   public ArtifactId getArtifactId() {
      return ArtifactId.create(this.artifact);
   }

   @Override
   public ArtifactTypeToken getArtifactType() {
      return this.artifact.getArtifactType();
   }

   public String getArtifactTypeName() {
      return this.artifact.getArtifactType().getName();
   }

   @Override
   public AttributeReadable<Object> getAttributeById(AttributeId attributeId) {
      throw new UnsupportedOperationException();
   }

   @Override
   public int getAttributeCount(AttributeTypeToken type) {
      return this.artifact.getAttributeCount(type);
   }

   @Override
   public int getAttributeCount(AttributeTypeToken type, DeletionFlag deletionFlag) {
      throw new UnsupportedOperationException();
   }

   @Override
   public Iterable<Collection<? extends AttributeReadable<Object>>> getAttributeIterable() {
      throw new UnsupportedOperationException();
   }

   @Override
   public <T> List<IAttribute<T>> getAttributeList(AttributeTypeGeneric<T> attributeType) {
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
   public <T> ResultSet<? extends AttributeReadable<T>> getAttributes(AttributeTypeToken attributeType,
      DeletionFlag deletionFlag) {
      throw new UnsupportedOperationException();
   }

   @Override
   public ResultSet<? extends AttributeReadable<Object>> getAttributes(DeletionFlag deletionFlag) {
      throw new UnsupportedOperationException();
   }

   @Override
   public <T> List<T> getAttributeValues(AttributeTypeToken attributeType) {

      //@formatter:off
      if(    ( attributeType instanceof AttributeTypeEnum )
          && ( this.artifact instanceof ArtifactReadableImpl ) ) {
         @SuppressWarnings("unchecked")
         var list =
            (List<T>) this.artifact
               .getAttributeValues( attributeType )
               .stream()
               .filter( Objects::nonNull )
               .map( ( enumToken ) -> ((EnumToken) enumToken).getName() )
               .filter( Objects::nonNull )
               .collect( Collectors.toList() );
         return list;
      }
      //@formatter:on
      return this.artifact.getAttributeValues(attributeType);
   }

   @Override
   public <T> List<T> getAttributeValues(AttributeTypeToken attributeType, DeletionFlag deletionFlag) {
      throw new UnsupportedOperationException();
   }

   @Override
   public String getAttributeValuesAsString(AttributeTypeToken attributeType) {
      return this.artifact.getAttributeValuesAsString(attributeType);
   }

   @Override
   public BranchToken getBranch() {
      return this.branchToken;
   }

   /**
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

   @Override
   public List<PublishingArtifact> getChildrenAsPublishingArtifacts() {

      @SuppressWarnings("unchecked")
      var publishingArtifactChildren = (List<PublishingArtifact>) (Object) this.getChildren();

      return publishingArtifactChildren;

   }

   @Override
   public Collection<ArtifactId> getChildrenIds() {
      return this.artifact.getChildrenIds();
   }

   @Override
   public <T> T getComputedCharacteristicValue(ComputedCharacteristicToken<T> computedCharacteristic) {
      throw new UnsupportedOperationException();
   }

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

   @Override
   public void getDescendants(List<ArtifactReadable> descendants) {
      throw new UnsupportedOperationException();
   }

   @Override
   public Collection<AttributeTypeToken> getExistingAttributeTypes() {
      throw new UnsupportedOperationException();
   }

   @Override
   public Collection<RelationTypeToken> getExistingRelationTypes() {
      throw new UnsupportedOperationException();
   }

   @Override
   public String getGuid() {
      return this.artifact.getGuid();
   }

   @Override
   public Long getId() {
      return this.artifact.getId();
   }

   @Override
   public TransactionId getLastModifiedTransaction() {
      throw new UnsupportedOperationException();
   }

   @Override
   public int getMaximumRelationAllowed(RelationTypeSide relationTypeSide) {
      throw new UnsupportedOperationException();
   }

   @Override
   public ModificationType getModificationType() {
      return this.artifact.getModificationType();
   }

   @Override
   public String getName() {
      return this.artifact.getName();
   }

   @Override
   public PublishingArtifact getParent() {
      var parent = this.artifact.getParent();

      return (Objects.nonNull(parent)) ? new WordRenderArtifactWrapperServerImpl(parent) : null;
   }

   @Override
   public String getRationale(RelationTypeSide typeAndSide, ArtifactReadable readable) {
      throw new UnsupportedOperationException();
   }

   @Override
   public ResultSet<ArtifactReadable> getRelated(RelationTypeSide relationTypeSide) {
      throw new UnsupportedOperationException();
   }

   @Override
   public List<ArtifactReadable> getRelated(RelationTypeSide relationTypeSide, ArtifactTypeToken artifactType,
      DeletionFlag deletionFlag) {
      throw new UnsupportedOperationException();
   }

   @Override
   public List<ArtifactReadable> getRelated(RelationTypeSide relationTypeSide, DeletionFlag deletionFlag) {
      return artifact.getRelated(relationTypeSide, deletionFlag);
   }

   @Override
   public int getRelatedCount(RelationTypeSide typeAndSide) {
      throw new UnsupportedOperationException();
   }

   @Override
   public List<ArtifactId> getRelatedIds(RelationTypeSide relationTypeSide) {
      return this.artifact.getRelatedIds(relationTypeSide);
   }

   @Override
   public List<ArtifactReadable> getRelatedList(RelationTypeSide relationTypeSide) {
      throw new UnsupportedOperationException();
   }

   @Override
   public ResultSet<IRelationLink> getRelations(RelationTypeSide relationTypeSide) {
      throw new UnsupportedOperationException();
   }

   @Override
   public String getSafeName() {
      throw new UnsupportedOperationException();
   }

   @Override
   public String getSoleAttributeAsString(AttributeTypeToken attributeType) {
      return this.artifact.getSoleAttributeAsString(attributeType);
   }

   @Override
   public String getSoleAttributeAsString(AttributeTypeToken attributeTypeToken, String defaultValue) {
      return this.artifact.getSoleAttributeAsString(attributeTypeToken, defaultValue);
   }

   @Override
   public AttributeId getSoleAttributeId(AttributeTypeToken attributeType) {
      throw new UnsupportedOperationException();
   }

   @Override
   public Long getSoleAttributeId(AttributeTypeToken attributeType, Long defaultValue) {
      throw new UnsupportedOperationException();
   }

   @Override
   public <T> T getSoleAttributeValue(AttributeTypeToken attributeType) {
      return this.artifact.getSoleAttributeValue(attributeType);
   }

   @Override
   public <T> T getSoleAttributeValue(AttributeTypeToken attributeType, DeletionFlag flag, T defaultValue) {
      return this.artifact.getSoleAttributeValue(attributeType, flag, defaultValue);
   }

   @Override
   public <T> T getSoleAttributeValue(AttributeTypeToken attributeTypeToken, T defaultValue) {
      return this.artifact.getSoleAttributeValue(attributeTypeToken, defaultValue);
   }

   @Override
   public TransactionId getTransaction() {
      return this.artifact.getTransaction();
   }

   @Override
   public Collection<AttributeTypeToken> getValidAttributeTypes() {
      return this.artifact.getValidAttributeTypes();
   }

   @Override
   public Collection<RelationTypeToken> getValidRelationTypes() {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean hasAttributeContent(AttributeTypeToken attributeTypeToken) {
      //@formatter:off
      return
            ( attributeTypeToken != null )
         && this.artifact.isAttributeTypeValid(attributeTypeToken)
         && ( this.artifact.getAttributeCount( attributeTypeToken ) > 0 );
      //@formatter:on
   }

   @Override
   public int hashCode() {
      return this.artifact.hashCode();
   }

   @Override
   public PublishingArtifact.Applicability isApplicable() {
      return this.applicable;
   }

   @Override
   public boolean isAttributeTypeValid(AttributeTypeId attributeType) {
      return this.artifact.isAttributeTypeValid(attributeType);
   }

   @Override
   public boolean isDescendantOf(ArtifactToken parent) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean isFound() {
      return true;
   }

   @Override
   public boolean isHistorical() {
      return this.artifact.isHistorical();
   }

   @Override
   public boolean isInvalid() {
      return this.artifact.isInvalid();
   }

   @Override
   public boolean isOfType(ArtifactTypeId... artifactTypeId) {
      return this.artifact.isOfType(artifactTypeId);
   }

   /**
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
   public void clearApplicable() {
      this.applicable = PublishingArtifact.Applicability.NO;
   }

   @Override
   public void setBranchView(BranchSpecification branchSpecification) {
      //@formatter:off
      assert
              Objects.nonNull( branchSpecification )
           && branchSpecification.hasView()
           && (    ( Objects.isNull( this.branchToken.getViewId() ) || this.branchToken.getViewId().isInvalid() )
                && ( branchSpecification.getBranchId().getId().equals( this.branchToken.getId() ) ) )
         : new Message()
                  .title( "WordRenderArtifactWrapperServerImpl::setBranchView, Artifact and/or branchSpecification have unexpected values." )
                  .indentInc()
                  .segment( "Branch Token Identifier",      this.branchToken.getId() )
                  .segment( "Branch Token View Identifier", Objects.nonNull( this.branchToken.getViewId() ) ? this.branchToken.getViewId().getId() : "(null)" )
                  .segment( "Branch Has View",              this.branchHasView() )
                  .segment( "Is Applicable",                this.isApplicable()  )
                  .toMessage( branchSpecification )
                  .toString();
      //@formatter:on
      this.branchToken = BranchToken.create(branchSpecification.getBranchId().getId(), this.branchToken.getName(),
         branchSpecification.getViewId());
   }

   @Override
   public Message toMessage(int indent, Message message) {
      var outMessage = Objects.isNull(message) ? new Message() : message;
      //@formatter:off
      outMessage
         .indent( indent )
         .title( "PublishingArtifact (WordRenderArtifactWrapperServerImpl)" )
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
         .segmentIfNotNull
            (
               "Hierarchy Position",
               () -> this.getHierarchyPosition().stream().map( ( integer ) -> integer.toString() ).collect( Collectors.joining(",") ),
               this.isHierarchyPositionSet() ? this : null
            )
         .segment( "Is HyperLinked",               this.isHyperlinked()               )
         .segment( "Is Bookmarked",                this.isBookmarked()                )
         .segment( "Is Referenced By Link",        this.isReferencedByLink()          )
         .indentDec()
         ;
      //@formatter:on
      return outMessage;
   }

   @Override
   public String toString() {
      return this.toMessage(0, null).toString();
   }

   @Override
   public HashCollection<AttributeTypeToken, IAttribute<?>> getAttributesHashCollection() {
      throw new UnsupportedOperationException();
   }

   @Override
   public <T> AttributeReadable<T> getSoleAttribute(AttributeTypeToken attributeType) {
      throw new UnsupportedOperationException();
   }

   @Override
   public TransactionDetails getLatestTxDetails() {
      throw new UnsupportedOperationException();
   }

   @Override
   public <T> IAttribute<T> getSoleAttribute(AttributeTypeToken attributeType, T defaultValue) {
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

   @Override
   public GammaId getGamma() {
      throw new UnsupportedOperationException();
   }

   @Override
   public List<IAttribute<?>> getAttributesNew() {
      throw new UnsupportedOperationException();
   }

}

/* EOF */
