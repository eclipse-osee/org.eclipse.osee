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

package org.eclipse.osee.framework.core.publishing;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchSpecification;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.ToMessage;

//@formatter:off
/**
 * An extension of the {@link ArtifactReadable} interface used by shared Client/Server publishing code.
 * <p>
 * A {@link PublishingArtifact} may be one of the following types:
 * <dl>
 * <dt>Loaded Artifact</dt>
 * <dd>A loaded artifact represents an artifact that was successfully loaded from the database. When the
 * artifact was loaded with an applicability view specified the loaded artifact's applicability is YES.
 * When the artifact was loaded without an applicability view the loaded artifact's applicability is UNKNOWN until
 * it is tested and will then either be set to YES or NO.</dd>
 * <dt>Not Loaded Artifact</dt>
 * <dd>A not loaded artifact represents an attempted load by either artifact identifier or by GUID; and
 * with or without a view specified for the load. A not loaded artifact will have either the artifact identifier
 * set or the GUID but never both. The applicability for a not loaded artifact is always NO.</dd>
 * <dt>Sentinel</dt>
 * <dd>A sentinel artifact has a sentinel artifact identifier and an empty string GUID. The applicability of
 * a sentinel artifact cannot be tested for.</dd>
 * </dl>
 * The table below shows the possible states for Loaded Artifacts and Not Loaded Artifacts and the possible
 * state transitions.
 * <pre>
 *  +-------+-----------+-----------+------------------+--------------+-------------+
 *  | State | has view  |  isFound  || isApplicable    | can try load | State       |
 *  |       |           |           ||                 | without view | Transitions |
 *  +-------+-----------+-----------+------------------+--------------+-------------+
 *  | 1     |   YES     |  YES      ||  YES            |  NO          | terminal    |
 *  +-------+-----------+-----------++-----------------+--------------+-------------+
 *  | 2     |   NO      |  YES      ||  UNKNOWN        |  NO          | 1,3         |
 *  +-------+-----------+-----------++-----------------+--------------+-------------+
 *  | 3     |   NO      |  YES      ||  NO             |  NO          | terminal    |
 *  +-------+-----------+-----------++-----------------+--------------+-------------+
 *  | 4     |   YES     |  NO       ||  NO             |  YES         | 3           |
 *  +-------+-----------+-----------++-----------------+--------------+-------------+
 *  | 5     |   NO      |  NO       ||  NO             |  NO          | terminal    |
 *  +-------+-----------+-----------++-----------------+--------------+-------------+
 * </pre>
 * {@link PublishingArtifact} implementations are expected to implement constructors that implement the interfaces
 * {@link PublishingArtifactLoader.PublishingArtifactFactoryWithoutView} and {@link PublishingArtifactLoader.PublishingArtifactFactoryWithView}.
 * <dl>
 * <dt>Without View Constructor</dt>
 * <dd>This constructor takes an {@link ArtifactReadable} implementation to be wrapped as the sole parameter. The
 * constructor is required to set the {@link PublishingArtifact} branch view to {@link ArtifactId#SENTINEL} and
 * the applicability to {@link PublishingArtifact.Applicability#UNKNOWN}.</dd>
 * <dt>With View Constructor</dt>
 * <dd>This constructor takes an {@link ArtifactReadable} implementation to be wrapped and the {@link BranchSpecification}
 * the {@link ArtifactReadable} implementation was loaded with as parameters. The constructor is required to set the
 * {@link PublishingArtifact} branch view to that in the {@link BranchSpecification}. When the {@link BranchSpecification}
 * applicability view identifier is non-{@link ArtifactId#SENTINEL} the {@link PublishingArtifact}'s applicability must be
 * set to {@link PublishingArtifact.Applicability#YES}; otherwise, set to {@link PublishingArtifact.Applicability#UNKNOWN}.</dd>
 * </dl>
 * @author Loren K. Ashley
 */
//@formatter:on

public interface PublishingArtifact extends ArtifactReadable, ToMessage {

   /**
    * An enumeration to describe the applicability of an artifact.
    */

   public enum Applicability {

      /**
       * The artifact has been determined to not be applicable by testing it's applicability.
       */

      NO,

      /**
       * The artifact has been loaded without a view filter and it's applicability has not been tested.
       */

      UNKNOWN,

      /**
       * The artifact has been determined to be applicable either by having been loaded with a view filter or by testing
       * for it's applicability.
       */

      YES;

      /**
       * Predicate to test if the enumeration member is the {@link PublishingArtifact.Applicability#NO} member.
       *
       * @return <code>true</code> when the member is the {@link PublishingArtifact.Applicability#NO} member; otherwise,
       * <code>false</code>.
       */

      public boolean no() {
         return this == NO;
      }

      /**
       * Predicate to test if the enumeration member is the {@link PublishingArtifact.Applicability#UNKNOWN} member.
       *
       * @return <code>true</code> when the member is the {@link PublishingArtifact.Applicability#UNKNOWN} member;
       * otherwise, <code>false</code>.
       */

      public boolean unknown() {
         return this == UNKNOWN;
      }

      /**
       * Predicate to test if the enumeration member is the {@link PublishingArtifact.Applicability#YES} member.
       *
       * @return <code>true</code> when the member is the {@link PublishingArtifact.Applicability#YES} member;
       * otherwise, <code>false</code>.
       */

      public boolean yes() {
         return this == YES;
      }
   }

   public static class PublishingArtifactNotFound extends PublishingArtifactSentinel {

      /**
       * The branch and optional view the failed artifact load attempt was performed on.
       */

      BranchSpecification branchSpecification;

      /**
       * Saves a {@link BranchToken} with the branch identifier and view identifier from {@link #branchSpecification}
       * and the name "Not Loaded".
       */

      BranchToken branchToken;

      /**
       * When the failed load was by GUID, saves the GUID of the artifact that did not load. An
       * {@link PublishingArtifactNotFound} will have an {@link ArtifactId#SENTINEL} identifier when the GUID is
       * present. When an {@link ArtifactId} is present the GUID will be an empty string.
       */

      String guid;

      PublishingArtifactNotFound(BranchSpecification branchSpecification) {
         this.id = ArtifactId.SENTINEL.getId();
         this.guid = Strings.emptyString();
         this.branchSpecification = branchSpecification;
         this.branchToken = BranchToken.create(branchSpecification.getBranchId().getId(), "Not Loaded",
            branchSpecification.getViewId());
      }

      /**
       * Creates a not found marker for an artifact that was not found by {@link ArtifactId}.
       *
       * @param branchSpecification the branch with optional view the artifact load attempt was performed on.
       * @param artifactIdentifier the {@link ArtifactId} of the artifact that failed to load.
       */

      PublishingArtifactNotFound(BranchSpecification branchSpecification, ArtifactId artifactIdentifier) {
         this.id = artifactIdentifier.getId();
         this.guid = Strings.emptyString();
         this.branchSpecification = branchSpecification;
         this.branchToken = BranchToken.create(branchSpecification.getBranchId().getId(), "Not Loaded",
            branchSpecification.getViewId());
      }

      /**
       * Creates a not found marker for an artifact that was not found by GUID.
       *
       * @param branchSpecification the branch with optional view the artifact load attempt was performed on.
       * @param guid the GUID of the artifact that failed to load.
       */

      PublishingArtifactNotFound(BranchSpecification branchSpecification, String guid) {
         this.id = ArtifactId.SENTINEL.getId();
         this.guid = guid;
         this.branchSpecification = branchSpecification;
         this.branchToken = BranchToken.create(branchSpecification.getBranchId().getId(), "Not Loaded",
            branchSpecification.getViewId());
      }

      @Override
      public boolean branchHasView() {
         return this.branchSpecification.hasView();
      }

      /**
       * Removes the optional view identifier from the not found marker's {@link #branchSpecification} and
       * {@link #branchToken}. When an artifact's failed load was performed with a view specified, the artifact might be
       * found on the branch without a view specified. This method should be invoked when the artifact was still not
       * found after a reload attempt without a view specification.
       */

      @Override
      public void clearBranchView() {
         this.branchSpecification =
            new BranchSpecification(BranchId.valueOf(this.branchSpecification.getBranchId().getId()));
         this.branchToken =
            BranchToken.create(this.branchSpecification.getBranchId().getId(), "Not Loaded", ArtifactId.SENTINEL);
      }

      @Override
      public BranchToken getBranch() {
         return this.branchToken;
      }

      @Override
      public String getGuid() {
         return this.guid;
      }

      @Override
      public boolean isBookmarked() {
         return false;
      }

      @Override
      public boolean isFound() {
         return false;
      }

      /**
       * {@inheritDoc}
       *
       * @implNote For a {@link PublishingArtifactNotFound} that was created in response to a load with an applicability
       * view specified it might be found by a load without a view applicability specified.
       */

      @Override
      public TryReload isReloadable() {
         //@formatter:off
         return
            this.branchSpecification.hasView()
               ? TryReload.NO
               : TryReload.YES;
         //@formatter:on
      }

      @Override
      public Message toMessage(int indent, Message message) {
         var outMessage = Objects.nonNull(message) ? message : new Message();

         //@formatter:off
         outMessage
            .indent( indent )
            .title( "PublishingArtifact (PublishingArtifactNotFound)" )
            .indentInc()
            .segment( "Identifier",                   this.getIdString()                 )
            .segment( "GUID",                         this.getGuid()                     )
            .segment( "Branch Identifier",            this.branchToken.getId()           )
            .segment( "Branch View",                  this.branchToken.getViewId()       )
            .indentDec()
            ;
         //@formatter:on
         return outMessage;
      }

      @Override
      public String toString() {
         return this.toMessage(0, null).toString();
      }
   }

   public static class PublishingArtifactSentinel extends ArtifactReadable.ArtifactReadableImpl implements PublishingArtifact {

      @Override
      public boolean areChildrenCached() {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean branchHasView() {
         throw new UnsupportedOperationException();
      }

      @Override
      public void clearApplicable() {
         throw new UnsupportedOperationException();
      }

      @Override
      public void clearBookmarked() {
         throw new UnsupportedOperationException();
      }

      @Override
      public void clearBranchView() {
         throw new UnsupportedOperationException();
      }

      @Override
      public void clearEndOfSection() {
         throw new UnsupportedOperationException();
      }

      @Override
      public void clearHyperlinkFrom(PublishingArtifact artifact) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void clearHyperlinkTo(PublishingArtifact artifact) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void clearProcessed() {
         throw new UnsupportedOperationException();
      }

      @Override
      public void clearStartOfSection() {
         throw new UnsupportedOperationException();
      }

      @Override
      public List<PublishingArtifact> getChildrenAsPublishingArtifacts() {
         throw new UnsupportedOperationException();
      }

      @Override
      public List<Integer> getHierarchyPosition() {
         throw new UnsupportedOperationException();
      }

      @Override
      public int getOutlineLevel() {
         throw new UnsupportedOperationException();
      }

      @Override
      public PublishingArtifact getParent() {
         throw new UnsupportedOperationException();
      }

      @Override
      public Optional<String> getPublishingContent() {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean hasAttributeContent(AttributeTypeToken attributeTypeToken) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void incrementRenderedAttributeCount() {
         throw new UnsupportedOperationException();
      }

      @Override
      public Applicability isApplicable() {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean isBookmarked() {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean isChanged() {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean isEndOfSection() {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean isFound() {
         return false;
      }

      @Override
      public boolean isHierarchyPositionSet() {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean isHyperlinked() {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean isHyperlinkedFrom(PublishingArtifact artifact) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean isHyperlinkedTo(PublishingArtifact artifact) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean isProcessed() {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean isPublished() {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean isPublishingContentCached() {
         throw new UnsupportedOperationException();
      }

      @Override
      public TryReload isReloadable() {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean isStartOfSection() {
         throw new UnsupportedOperationException();
      }

      @Override
      public void pushHierarchyPosition(Integer position) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void setApplicable() {
         throw new UnsupportedOperationException();
      }

      @Override
      public void setBookmarked() {
         throw new UnsupportedOperationException();
      }

      @Override
      public void setBranchView(BranchSpecification branchSpecification) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void setChanged() {
         throw new UnsupportedOperationException();
      }

      @Override
      public void setChildrenLoadedAndCached() {
         throw new UnsupportedOperationException();

      }

      @Override
      public void setEndOfSection() {
         throw new UnsupportedOperationException();
      }

      @Override
      public void setHierarchyPosition() {
         throw new UnsupportedOperationException();
      }

      @Override
      public void setHierarchyPosition(List<Integer> parentHierarchyPosition) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void setHyperlinkFrom(PublishingArtifact artifact) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void setHyperlinkTo(PublishingArtifact artifact) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void setOutlineLevel(int level) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void setProcessed() {
         throw new UnsupportedOperationException();
      }

      @Override
      public void setPublishingContent(String content) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void setStartOfSection() {
         throw new UnsupportedOperationException();
      }

      @Override
      public Stream<PublishingArtifact> streamHyperlinkedFrom() {
         throw new UnsupportedOperationException();
      }

      @Override
      public Stream<PublishingArtifact> streamHyperlinkedTo() {
         throw new UnsupportedOperationException();
      }

      @Override
      public void setReferencedByLink(Boolean flag) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean isReferencedByLink() {
         return false;
      }

      @Override
      public Message toMessage(int indent, Message message) {
         var outMessage = Objects.nonNull(message) ? message : new Message();

         //@formatter:off
         outMessage
            .indent( indent )
            .title( "PublishingArtifact (PublishingArtifactSentinel)" )
            .indentInc()
            .segment( "Identifier",                   this.getIdString()                 )
            .segment( "GUID",                         this.getGuid()                     )
            .indentDec()
            ;
         //@formatter:on
         return outMessage;
      }

      @Override
      public String toString() {
         return this.toMessage(0, null).toString();
      }

   }

   /**
    * An enumeration used to indicate that attempting to reload an artifact without a view specification might or might
    * not be successful.
    */

   public enum TryReload {

      /**
       * The artifact has already been loaded or attempting to reload the artifact without a view specification will not
       * be successful.
       */

      NO,

      /**
       * The artifact was not found with a view specification and attempted to reload the artifact without a view
       * specification may be successful.
       */

      YES;

      /**
       * Predicate to test if the enumeration member is the {@link PublishingArtifact.TryReload#NO} member.
       *
       * @return <code>true</code> when the member is the {@link PublishingArtifact.TryReload#NO} member; otherwise,
       * <code>false</code>.
       */

      public boolean no() {
         return this == NO;
      }

      /**
       * Predicate to test if the enumeration member is the {@link PublishingArtifact.TryReload#YES} member.
       *
       * @return <code>true</code> when the member is the {@link PublishingArtifact.TryReload#YES} member; otherwise,
       * <code>false</code>.
       */

      public boolean yes() {
         return this == YES;
      }
   }

   PublishingArtifact SENTINEL = new PublishingArtifactSentinel();

   /**
    * Predicate to determine if the {@link PublishingArtifact}'s children have been loaded and cached as children of
    * this artifact.
    *
    * @return <code>true</code> when the children have been loaded and cached as children of this artifact; otherwise
    * <code>false</code>.
    */

   boolean areChildrenCached();

   /**
    * Predicate to determine if the database request for the artifact contained a view filter.
    *
    * @return <code>true</code> when the artifact was requested with a view filter; otherwise, <code>false</code>.
    */

   boolean branchHasView();

   void clearApplicable();

   void clearBookmarked();

   /**
    * Removes the view artifact identifier from the artifact's branch.
    *
    * @implSpec Implementations for a loaded artifact and for sentinel artifacts are expected to throw an
    * {@link UnsupportedOperationException}.
    * @implSpec Artifact not found marker implementations are expected to implement this method.
    * @implNote Clearing the view artifact identifier from a not found marker is done to indicate that the artifact will
    * not be found with a database request without a view filter.
    */

   void clearBranchView();

   /**
    * Clears the end of section flag for an artifact.
    */

   void clearEndOfSection();

   void clearHyperlinkFrom(PublishingArtifact artifact);

   void clearHyperlinkTo(PublishingArtifact artifact);

   void clearProcessed();

   /**
    * Clears the start of section flag for an artifact.
    */

   void clearStartOfSection();

   /**
    * Gets the immediate hierarchical children of the artifact as {@link PublishingArtifact}s. The outline level of the
    * returned artifacts will be one greater than the parent artifact. The first child's start of section flag will be
    * set and last child's end of section flags will be set.
    *
    * @return a list of the immediate hierarchical children.
    */

   List<PublishingArtifact> getChildrenAsPublishingArtifacts();

   List<Integer> getHierarchyPosition();

   /**
    * The outlining level is the hierarchical depth of the artifact. The top level is level 0.
    *
    * @return the artifact outline level.
    */

   int getOutlineLevel();

   @Override
   PublishingArtifact getParent();

   Optional<String> getPublishingContent();

   /**
    * Predicate to determine if the artifact has attribute content of the specified attribute type.
    *
    * @param attributeTypeToken the type of attribute to check for content.
    * @return <code>true</code> when the artifact has content in an attribute of the type specified by
    * <code>attributeTypeToken</code>; otherwise, <code>false</code>.
    */

   boolean hasAttributeContent(AttributeTypeToken attributeTypeToken);

   /**
    * A counter is incremented each time an attribute of the artifact is rendered. This is used to determine if an
    * artifact has been included in a publish.
    *
    * @return the number of time an attribute of the artifact has been rendered.
    */

   void incrementRenderedAttributeCount();

   /**
    * Predicate to determine if the artifact is applicable to the publishing view.
    *
    * @return one of the following:
    * <dl>
    * <dt>{@link PublishingArtifact.Applicability#YES}:</dt>
    * <dd>The artifact has been determined to be applicable to the publishing view.</dd>
    * <dt>{@link PublishingArtifact.Applicability#NO}:</dt>
    * <dd>The artifact has been determined to not be applicable to the publishing view.</dd>
    * <dt>{@link PublishingArtifact.Applicability#UNKNOWN}:</dt>
    * <dd>The applicability of the artifact has not been determined or the publish is not specific to a view.</dd>
    * </dl>
    */

   PublishingArtifact.Applicability isApplicable();

   boolean isBookmarked();

   boolean isChanged();

   /**
    * Predicate to determine if the artifact is the last artifact under a parent at it's hierarchy level.
    *
    * @return end of section flag.
    */

   boolean isEndOfSection();

   boolean isFound();

   boolean isHierarchyPositionSet();

   /**
    * Predicate to determine if another artifact hyper links to this artifact.
    *
    * @return <code>true</code> when hyper linked from another artifact; otherwise, <code>false</code>.
    */

   boolean isHyperlinked();

   boolean isHyperlinkedFrom(PublishingArtifact artifact);

   boolean isHyperlinkedTo(PublishingArtifact artifact);

   /**
    * Predicate to determine if the publishing content for the artifact has been rendered to the output.
    *
    * @return <code>true</code> when the publishing content for the artifact has been rendered to the output; otherwise,
    * <code>false</code>.
    */
   boolean isProcessed();

   boolean isPublished();

   /**
    * Predicate to determine if the publishing content for the artifact has been generated and cached.
    *
    * @return <code>true</code> when the publishing content for the artifact has been generated and cached; otherwise,
    * <code>false</code>.
    */

   boolean isPublishingContentCached();

   /**
    * Predicate to determine if attempting a reload of the artifact without a view specification may be successful.
    *
    * @return {@link TryReload#YES} when a reload attempt might succeed; otherwise, {@link TryReload#NO}.
    */

   TryReload isReloadable();

   /**
    * Predicate to determine if the artifact is the first artifact under a parent at it's hierarchy level.
    *
    * @return start of section flag.
    */

   boolean isStartOfSection();

   void pushHierarchyPosition(Integer position);

   void setApplicable();

   void setBookmarked();

   void setBranchView(BranchSpecification branchSpecification);

   void setChanged();

   /**
    * Sets a flag in the {@link PublishingArtifact} to indicate that it's children have been loaded and cached as
    * children of this artifact.
    */

   void setChildrenLoadedAndCached();

   /**
    * Sets the artifact's end of section flag.
    */

   void setEndOfSection();

   void setHierarchyPosition();

   public void setHierarchyPosition(List<Integer> parentHierarchyPosition);

   public void setHyperlinkFrom(PublishingArtifact artifact);

   public void setHyperlinkTo(PublishingArtifact artifact);

   /**
    * Sets the artifact's outlining level.
    *
    * @param level the outlining level to set.
    */

   void setOutlineLevel(int level);

   void setProcessed();

   void setPublishingContent(String content);

   /**
    * Sets the artifacts start of section flag.
    */

   void setStartOfSection();

   Stream<PublishingArtifact> streamHyperlinkedFrom();

   Stream<PublishingArtifact> streamHyperlinkedTo();

   /**
    * Sets whether this artifact is referenced by a link from (within the content of) another artifact, thus requiring a
    * bookmark.
    */
   void setReferencedByLink(Boolean flag);

   /**
    * Checks if this artifact is referenced by a link from (within the content of) another artifact, thus requiring a
    * bookmark.
    */
   boolean isReferencedByLink();
}

/* EOF */
