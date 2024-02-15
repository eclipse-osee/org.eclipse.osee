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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Implements the shared members and methods for the Client and Server {@link PublishingArtifact} implementations.
 *
 * @author Loren K. Ashley
 */

abstract public class PublishingArtifactBase implements PublishingArtifact {

   /**
    * Flag to indicate if the artifact's children have been loaded and cached.
    */

   private boolean areChildrenCached;

   /**
    * Flag to indicate if the publish content for the artifact has been book marked.
    */

   private boolean bookmarked;

   /**
    * Flag to indicate if the artifact is different between the publishing branch and the product line branch.
    */

   private boolean changed;

   /**
    * Set <code>true</code> when this artifact is the last artifact at it's hierarchy level.
    */

   private boolean endsSection;

   /**
    * A list of the artifact's index position at each level of it's hierarchical position on the branch. This doesn't
    * necessary match the hierarchical position of the artifact in the document being published.
    */

   private List<Integer> hierarchyPosition;

   /**
    * Flag to indicate the hierarchical position on the branch of the artifact has been determined.
    */

   private boolean hierarchyPositionSet;

   /**
    * A set of the artifact's that have hyper-links to this artifact.
    */

   private final Set<PublishingArtifact> hyperlinkFrom;

   /**
    * A set of the artifacts that are hyper-linked from this artifact.
    */

   private final Set<PublishingArtifact> hyperlinkTo;

   /**
    * Set to the hierarchical depth of the artifact.
    */

   private int outlineLevel;

   /**
    * Flag to indicate that the main publishing content of the artifact has been processed and cached or appended to the
    * document being generated.
    */

   private boolean processed;

   /**
    * When the main publishing content is cached before being appended to the document, it is saved in this member.
    */

   private String publishingContent;

   /**
    * Counts the number of attributes that have been rendered for the publish. Used to determine if the artifact has
    * been included in the publish.
    */

   private int renderedAttributeCount;

   /**
    * Set <code>true</code> when this artifact is the first artifact at it's hierarchy level.
    */

   private boolean startsSection;

   /**
    * Creates and initializes the shared Client and Server {@link PublishingArtifact} implementation members.
    */

   public PublishingArtifactBase() {

      this.areChildrenCached = false;
      this.bookmarked = false;
      this.changed = false;
      this.endsSection = false;
      this.hierarchyPosition = null;
      this.hierarchyPositionSet = false;
      this.hyperlinkFrom = new HashSet<>();
      this.hyperlinkTo = new HashSet<>();
      this.outlineLevel = 0;
      this.processed = false;
      this.publishingContent = null;
      this.renderedAttributeCount = 0;
      this.startsSection = false;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public boolean areChildrenCached() {
      return this.areChildrenCached;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public void clearBookmarked() {
      this.bookmarked = false;
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
   public void clearHyperlinkFrom(PublishingArtifact artifact) {
      this.hyperlinkFrom.remove(artifact);
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public void clearHyperlinkTo(PublishingArtifact artifact) {
      this.hyperlinkTo.remove(artifact);
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public void clearProcessed() {
      this.processed = false;
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
   public List<Integer> getHierarchyPosition() {
      if (!this.hierarchyPositionSet) {
         throw new IllegalStateException();
      }

      return this.hierarchyPosition;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public int getOutlineLevel() {
      //@formatter:off
      return
         this.hierarchyPosition != null
            ? this.hierarchyPosition.size()
            : 0;
      //@formatter:on
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Optional<String> getPublishingContent() {
      return Optional.ofNullable(this.publishingContent);
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public void incrementRenderedAttributeCount() {
      this.renderedAttributeCount++;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public boolean isBookmarked() {
      return this.bookmarked;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public boolean isChanged() {
      return this.changed;
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
   public boolean isHierarchyPositionSet() {
      return this.hierarchyPositionSet;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public boolean isHyperlinked() {
      return !this.hyperlinkFrom.isEmpty();
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public boolean isHyperlinkedFrom(PublishingArtifact artifact) {
      return this.hyperlinkFrom.contains(artifact);
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public boolean isHyperlinkedTo(PublishingArtifact artifact) {
      return this.hyperlinkTo.contains(artifact);
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public boolean isProcessed() {
      return this.processed;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public boolean isPublished() {
      return this.renderedAttributeCount > 0;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public boolean isPublishingContentCached() {
      return Objects.nonNull(this.publishingContent);
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
   public void pushHierarchyPosition(Integer position) {
      if (this.hierarchyPositionSet) {
         throw new IllegalStateException();
      }

      //@formatter:off
      (
         Objects.isNull(this.hierarchyPosition)
            ? this.hierarchyPosition = new ArrayList<>()
            : this.hierarchyPosition
      ).add(0, position);
      //@formatter:on
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public void setBookmarked() {
      this.bookmarked = true;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public void setChanged() {
      this.changed = true;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public void setChildrenLoadedAndCached() {
      this.areChildrenCached = true;
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
   public void setHierarchyPosition() {
      this.hierarchyPositionSet = true;
      this.hierarchyPosition = Collections.unmodifiableList(this.hierarchyPosition);
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public void setHierarchyPosition(List<Integer> parentHierarcyPosition) {
      this.hierarchyPositionSet = true;
      var hierarchyPosition = new ArrayList<>(parentHierarcyPosition);
      if (Objects.nonNull(this.hierarchyPosition)) {
         hierarchyPosition.addAll(this.hierarchyPosition);
      }
      this.hierarchyPosition = Collections.unmodifiableList(hierarchyPosition);
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public void setHyperlinkFrom(PublishingArtifact artifact) {
      this.hyperlinkFrom.add(artifact);
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public void setHyperlinkTo(PublishingArtifact artifact) {
      this.hyperlinkTo.add(artifact);
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
   public void setProcessed() {
      this.processed = true;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public void setPublishingContent(String content) {
      this.publishingContent = Objects.requireNonNull(content);
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
   public Stream<PublishingArtifact> streamHyperlinkedFrom() {
      return this.hyperlinkFrom.stream();
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Stream<PublishingArtifact> streamHyperlinkedTo() {
      return this.hyperlinkTo.stream();
   }

}

/* EOF */
