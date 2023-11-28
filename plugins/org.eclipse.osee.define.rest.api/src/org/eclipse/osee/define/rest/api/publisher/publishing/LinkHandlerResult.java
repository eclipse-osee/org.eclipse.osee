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

package org.eclipse.osee.define.rest.api.publisher.publishing;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.publishing.IdentifierTypeIndicator;
import org.eclipse.osee.framework.core.publishing.PublishingArtifact;

/**
 * This class encapsulates the results form the methods {@link WordMlLinkHandler#link} and
 * {@link WordMlLinkHandler#unlink}.
 *
 * @author Loren K. Ashley
 */

public class LinkHandlerResult {

   /**
    * The artifact's modified content from the linking or unlinking operation. This member is used as a sentinel value
    * to indicate when modifications are allowed to the object. Once the member is set no more modifications are
    * allowed.
    */

   private String content;

   /**
    * The {@link ArtifactId}s of the linked to artifacts that were found.
    */

   private final Set<ArtifactId> knownIdentifiers;

   /**
    * The ambiguous identifier strings of the linked to artifacts that were not found.
    */

   private final Set<String> unknownAmbiguousIdentifierStrings;

   /**
    * Creates a new empty {@link LinkHandlerResult}.
    */

   public LinkHandlerResult() {
      this.content = null;
      this.knownIdentifiers = new HashSet<>();
      this.unknownAmbiguousIdentifierStrings = new HashSet<>();
   }

   /**
    * Sets the modified content of the artifact.
    *
    * @param content the modified artifact content.
    * @throws NullPointerException when <code>content</content> is <code>null</code>.
    * @throws IllegalStateException when the {@link #content} has already been set.
    */

   public void setContent(String content) {

      if (Objects.nonNull(this.content)) {
         throw new IllegalStateException();
      }

      this.content = Objects.requireNonNull(content);
   }

   /**
    * Adds the {@link ArtifactId} of a linked to artifact that was found.
    *
    * @param artifactId the {@link ArtifactId} of a linked artifact.
    * @throws NullPointerException when <code>artifactId</code> is <code>null</code>.
    * @throws IllegalArgumentException when <code>artifactId</code> is {@link ArtifactId#SENTINEL}.
    * @throws IllegalStateException when the {@link #content} has already been set.
    */

   public void addKnownIdentifier(PublishingArtifact artifact) {

      if (Objects.nonNull(this.content)) {
         throw new IllegalStateException();
      }

      Objects.requireNonNull(artifact);

      if (artifact.isInvalid()) {
         throw new IllegalArgumentException();
      }

      this.knownIdentifiers.add(ArtifactId.valueOf(artifact.getId()));
   }

   /**
    * Adds the <code>ambiguousIdentifierString</code> of a linked artifact that was not found.
    *
    * @param ambiguousIdentifierString the ambiguous identifier string of the linked to artifact that was not found.
    * @throws NullPointerException when <code>ambiguousIdentifierString</code> is <code>null</code>.
    * @throws IllegalArgumentException when the {@link IdentifierTypeIndicator} of the
    * <code>ambiguousIdentifierString</code> is {@link IdentifierTypeIndicator#UNKNOWN}.
    * @throws IllegalStateException when the {@link #content} has already been set.
    */

   public void addUnknownIdentifier(String guid) {

      if (Objects.nonNull(this.content)) {
         throw new IllegalStateException();
      }

      Objects.requireNonNull(guid);

      if (!IdentifierTypeIndicator.determine(guid).isGuid()) {
         throw new IllegalArgumentException();
      }

      this.unknownAmbiguousIdentifierStrings.add(guid);
   }

   public void addUnknownIdentifier(ArtifactId artifactIdentifier) {

      if (Objects.nonNull(this.content)) {
         throw new IllegalStateException();
      }

      Objects.requireNonNull(artifactIdentifier);

      if (artifactIdentifier.isInvalid()) {
         throw new IllegalArgumentException();
      }

      this.unknownAmbiguousIdentifierStrings.add(artifactIdentifier.getIdString());
   }

   /**
    * Gets the modified content of the linked or unlinked artifact.
    *
    * @return the artifact's modified content.
    * @throws IllegalStateException when the member {@link #content} has not yet been set.
    */

   public String getContent() {

      if (Objects.isNull(this.content)) {
         throw new IllegalStateException();
      }

      return this.content;
   }

   /**
    * Gets the {@link ArtifactId}s of the linked artifacts that were found.
    *
    * @return an unmodifiable and possibly empty set view of {@link ArtifactId}s.
    * @throws IllegalStateException when the member {@link #content} has not yet been set.
    */

   public Set<ArtifactId> getKnownIdentifiers() {

      if (Objects.isNull(this.content)) {
         throw new IllegalStateException();
      }

      return Collections.unmodifiableSet(this.knownIdentifiers);
   }

   /**
    * Gets the ambiguous identifier strings of the linked artifact that were not found.
    *
    * @return an unmodifiable and possibly empty set view of {@link String}s.
    * @throws IllegalStateException when the member {@link #content} has not yet been set.
    */

   public Set<String> getUnknownAmbiguousIdentifierStrings() {

      if (Objects.isNull(this.content)) {
         throw new IllegalStateException();
      }

      return Collections.unmodifiableSet(this.unknownAmbiguousIdentifierStrings);
   }

   public boolean hasUnknownAmbiguousIdentifierString() {
      return !this.unknownAmbiguousIdentifierStrings.isEmpty();
   }
}

/* EOF */
