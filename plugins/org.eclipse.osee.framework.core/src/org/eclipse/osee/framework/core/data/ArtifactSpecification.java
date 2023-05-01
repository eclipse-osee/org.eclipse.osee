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

package org.eclipse.osee.framework.core.data;

import java.util.Objects;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.util.Message;

/**
 * A wrapper on {@link BranchId} and {@link ArtifactId} that guarantees the encapsulated {@link BranchId} and
 * {@link ArtifactId} implementations only contain non-<code>null</code> identifiers.
 *
 * @author Loren K. Ashley
 */

public class ArtifactSpecification extends BranchSpecification {

   /**
    * Save the artifact identifier. This member will only contain the artifact's identifier, it will not be implemented
    * with a {@link ArtifactToken}, {@link Artifact}, or {@link ArtifactReadable}.
    */

   protected final ArtifactId artifactId;

   /**
    * Creates an {@link ArtifactSpecification} that only contains the artifact identifier, branch identifier, and branch
    * view artifact identifier. All of the encapsulated identifiers are guaranteed to not be <code>null</code>. The
    * created {@link ArtifactSpecification} will have the branch identifier set to {@link BranchId#SENTINEL} and the
    * branch view artifact identifier set to {@link ArtifactId#SENTINEL}.
    *
    * @param artifactId the {@link ArtifactIdentifier}. This parameter may be <code>null</code>.
    */

   public ArtifactSpecification(ArtifactId artifactId) {
      this(BranchId.SENTINEL, ArtifactId.SENTINEL, artifactId);
   }

   /**
    * Creates an {@link ArtifactSpecification} that only contains the artifact identifier, branch identifier, and branch
    * view artifact identifier. All of the encapsulated identifiers are guaranteed to not be <code>null</code>.
    *
    * @param branchId the branch identifier with an optional view artifact identifier. This parameter may be
    * <code>null</code>.
    * @param artifactId the artifact identifier. This parameter may be <code>null</code>.
    * @return an {@link ArtifactSpecification} with the encapsulated {@link ArtifactId} set according to
    * {@link BranchSpecification#cleanArtifactId} and the encapsulated {@link BranchId} set according to
    * {@link BranchSpecification#cleanBranchId}.
    * @throws IllegalArgumentException when:
    * <ul>
    * <li>The <code>branchId</code> is <code>null</code> or {@link BranchId#SENTINEL}; and <code>viewId</code> is
    * non-<code>null</code> and not {@link ArtifactId#SENTINEL}.</li>
    * <li>The <code>branchId</code> is {@link BranchId#SENTINEL}; and <code>branchId.getViewId()</code> is
    * non-<code>null</code> and not {@link ArtifactId#SENTINEL}.</li>
    * <li>The <code>branchId</code> is non-<code>null</code> and not {@link BranchId#SENTINEL};
    * <code>branchId.getViewId()</code> is non-<code>null</code> and not {@link ArtifactId#SENTINEL}; and viewId is
    * non-<code>null</code> and not {@link ArtifactId#SENTINEL}; and <code>branchId.getViewId()</code> is not equal to
    * <code>viewId</code>.</li>
    * </ul>
    */

   public ArtifactSpecification(BranchId branchId, ArtifactId artifactId) {
      this(branchId, ArtifactId.SENTINEL, artifactId);
   }

   /**
    * Creates an {@link ArtifactSpecification} that only contains the artifact identifier, branch identifier, and branch
    * view artifact identifier. All of the encapsulated identifiers are guaranteed to not be <code>null</code>.
    *
    * @param branchId the branch identifier with an optional view artifact identifier. This parameter may be
    * <code>null</code>.
    * @param artifactId the artifact identifier. This parameter may be <code>null</code>.
    * @return an {@link ArtifactSpecification} with the encapsulated {@link ArtifactId} set according to
    * {@link BranchSpecification#cleanArtifactId} and the encapsulated {@link BranchId} set according to
    * {@link BranchSpecification#cleanBranchId}.
    * @throws IllegalArgumentException when:
    * <ul>
    * <li>The <code>branchId</code> is <code>null</code> or {@link BranchId#SENTINEL}; and <code>viewId</code> is
    * non-<code>null</code> and not {@link ArtifactId#SENTINEL}.</li>
    * <li>The <code>branchId</code> is {@link BranchId#SENTINEL}; and <code>branchId.getViewId()</code> is
    * non-<code>null</code> and not {@link ArtifactId#SENTINEL}.</li>
    * <li>The <code>branchId</code> is non-<code>null</code> and not {@link BranchId#SENTINEL};
    * <code>branchId.getViewId()</code> is non-<code>null</code> and not {@link ArtifactId#SENTINEL}; and viewId is
    * non-<code>null</code> and not {@link ArtifactId#SENTINEL}; and <code>branchId.getViewId()</code> is not equal to
    * <code>viewId</code>.</li>
    * </ul>
    */

   public ArtifactSpecification(BranchId branchId, ArtifactId viewId, ArtifactId artifactId) {
      super(branchId, viewId);
      this.artifactId = BranchSpecification.cleanArtifactId(artifactId);

   }

   /**
    * Gets the encapsulated {@link ArtifactId}. The returned value will be non-<code>null</code> but may be
    * {@link ArtifactId#SENTINEL}.
    *
    * @return the encapsulated {@link ArtifactId}.
    */

   public ArtifactId getArtifactId() {
      return this.artifactId;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public boolean equals(Object other) {

      if (!(other instanceof ArtifactSpecification)) {
         return false;
      }

      var otherArtifactSpecification = (ArtifactSpecification) other;

      //@formatter:off
      return
         !(
               !this.artifactId.getId().equals( otherArtifactSpecification.artifactId.getId() )
            || !this.branchId.getId().equals( otherArtifactSpecification.branchId.getId() )
            || !this.branchId.getViewId().getId().equals( otherArtifactSpecification.getViewId().getId() ) );
      //@formatter:on
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public int hashCode() {
      var a = this.artifactId.getId();
      var b = this.branchId.getId();
      var c = this.branchId.getViewId().getId();

      var d = Long.rotateLeft((a << 5) - a, 24) ^ (b * b - b);
      var r = Long.rotateLeft((d << 5) - d, 24) ^ (c * c - c);

      return (int) ((r >> 32) ^ r) & 0xFFFFFFFF;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Message toMessage(int indent, Message message) {
      var outMessage = Objects.nonNull(message) ? message : new Message();
      //@formatter:off
      outMessage
         .title( "Artifact Specification" )
         .indentInc()
         .segment( "Branch Identifier",   this.branchId.getId().equals( Id.SENTINEL )
                                             ? "SENTINEL"
                                             : this.branchId.getIdString() )
         .segment( "View Identifier",     this.branchId.getViewId().getId().equals(Id.SENTINEL)
                                             ? "SENTINEL"
                                             : this.branchId.getViewId().getIdString() )
         .segment( "Artifact Identifier", this.artifactId.getId().equals( Id.SENTINEL )
                                             ? "SENTINEL"
                                             : this.artifactId.getIdString() )
         .indentDec();
      //@formatter:on
      return outMessage;
   }

}

/* EOF */
