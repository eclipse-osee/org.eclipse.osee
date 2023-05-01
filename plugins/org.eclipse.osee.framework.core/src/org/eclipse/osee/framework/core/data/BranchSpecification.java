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
import org.eclipse.osee.framework.jdk.core.util.ToMessage;

/**
 * A wrapper on {@link BranchId} that guarantees the encapsulated {@link BranchId} implementation contains only a
 * non-<code>null</code> branch identifier and a non-<code>null</code> view artifact identifier.
 *
 * @author Loren K. Ashley
 */

public class BranchSpecification implements ToMessage {

   /**
    * Saves the branch identifier and the optional view artifact identifier. This member will only contain the
    * identifiers, it will not be implemented with a {@link BranchToken} or {@link Branch}.
    */

   protected final BranchId branchId;

   /**
    * Creates an {@Link ArtifactId} implementation that only contains the artifact identifier. When
    * <code>artifactId</code> is <code>null</code>, the returned {@Link ArtifactId} will be {@link ArtifactId#SENTINEL}.
    *
    * @param artifactId the {@Link ArtifactId} implementation to extract the artifact identifier from. This parameter
    * may be <code>null</code>.
    * @return when <code>artifactId</code> is non-<code>null</code> and not {@link ArtifactId#SENTINEL}, an
    * {@link ArtifactId} implementation containing only the artifact identifier extracted from <code>artifactId</code>;
    * otherwise, {@link ArtifactId#SENTINEL}.
    */

   static ArtifactId cleanArtifactId(ArtifactId artifactId) {
      //@formatter:off
      return
         Objects.nonNull( artifactId )
            ? ArtifactId.SENTINEL.equals( artifactId )
                 ? ArtifactId.SENTINEL
                 : ArtifactId.create( artifactId )
            : ArtifactId.SENTINEL;
      //@formatter:on
   }

   /**
    * Creates an {@link BranchId} implementation that only contains the branch identifier and the view artifact
    * identifier. Both identifiers are guaranteed to not be <code>null</code>.
    *
    * @param branchId the branch identifier with an optional view artifact identifier. This parameter may be
    * <code>null</code>.
    * @param viewId the artifact view identifier. This parameter may be <code>null</code>.
    * @return a {@link BranchId} implementation as follows:
    * <ul>
    * <li>The <code>branchId</code> is {@link BranchId#SENTINEL}; <code>branchId.getViewId()</code> is <code>null</code>
    * or {@link ArtifactId#SENTINEL}; and <code>viewId</code> is <code>null</code> or {@link ArtifactId#SENTINEL} the
    * method returns {@link BranchId#SENTINEL}.</li>
    * <li>The <code>branchId</code> is non-<code>null</code> and not {@link BranchId#SENTINEL}; and
    * <code>branchId.getViewId()</code> is non-<code>null</code> and not {@link ArtifactId#SENTINEL} the method returns
    * a {@link BranchId} implementation with just the branch identifier and view identifier from
    * <code>branchId</code>.</li>
    * <li>The <code>branchId</code> is non-<code>null</code> and not {@link BranchId#SENTINEL}; and <code>viewId</code>
    * is non-<code>null</code> and not {@link ArtifactId#SENTINEL} the method returns a {@link BranchId} implementation
    * with just the branch identifier from <code>branchId</code> and the view identifier from <code>viewId</code>.</li>
    * <li>The <code>branchId</code> is non-<code>null</code> and not {@link BranchId#SENTINEL}; the
    * <code>branchId.getViewId() is <code>null</code> or {@link ArtifactId#SENTINEL}; and <code>viewId</code> is
    * <code>null</code> or {@link ArtifactId#SENTINEL} the method returns a {@link BranchId} implementation with just
    * the branch identifier from <code>branchId</code> and a view identifier set to {@link ArtifactId#SENTINEL}.</li>
    * </ul>
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

   @SuppressWarnings("null")
   static BranchId cleanBranchId(BranchId branchId, ArtifactId viewId) {

      var branchIdIsValid = Objects.nonNull(branchId) && !Id.SENTINEL.equals(branchId.getId());
      var viewIdIsValid = Objects.nonNull(viewId) && !ArtifactId.SENTINEL.equals(viewId);

      if (!branchIdIsValid && viewIdIsValid) {
         //@formatter:off
         throw
            new IllegalArgumentException
                   (
                      new Message()
                             .title( "BranchSpecification::cleanBranchId, branchId is invalid and viewId is valid.")
                             .indentInc()
                             .segment( "branchId", Objects.isNull(branchId) ? "(null)" : branchId.getIdString() )
                             .segment( "viewId", viewId.getIdString() )
                             .toString()
                   );
         //@formatter:on
      }

      //@formatter:off
      var branchIdViewIdIsValid =
            Objects.nonNull( branchId )
         && Objects.nonNull( branchId.getViewId() )
         && !ArtifactId.SENTINEL.equals( branchId.getViewId() );
      //@formatter:on

      if (!branchIdIsValid && branchIdViewIdIsValid) {
         //@formatter:off
         throw
            new IllegalArgumentException
                   (
                      new Message()
                             .title( "BranchSpecification::cleanBranchId, branchId is invalid and view identifier in branchId is valid.")
                             .indentInc()
                             .segment( "branchId", Objects.isNull(branchId) ? "(null)" : branchId.getIdString() )
                             .segment( "branchId.getViewId()", branchId.getViewId().getIdString() )
                             .toString()
                   );
         //@formatter:on
      }

      if (branchIdIsValid && branchIdViewIdIsValid && viewIdIsValid && !branchId.getViewId().equals(viewId)) {
         //@formatter:off
         throw
            new IllegalArgumentException
                   (
                      new Message()
                             .title( "BranchSpecification::cleanBranchId, view in branchId conflicts with viewId.")
                             .indentInc()
                             .segment( "branchId", branchId.getIdString() )
                             .segment( "branchId.getViewId()", branchId.getViewId().getIdString() )
                             .segment( "viewId", viewId.getIdString() )
                             .toString()
                   );
         //@formatter:on
      }

      if (!branchIdIsValid) {
         return BranchId.SENTINEL;
      }

      var branchIdLong = branchId.getId();

      if (branchIdViewIdIsValid) {
         return BranchId.create(branchIdLong, BranchSpecification.cleanArtifactId(branchId.getViewId()));
      }

      if (viewIdIsValid) {
         return BranchId.create(branchIdLong, BranchSpecification.cleanArtifactId(viewId));
      }

      return BranchId.create(branchIdLong, ArtifactId.SENTINEL);
   }

   /**
    * Creates an {@link BranchId} implementation that only contains the branch identifier and the view artifact
    * identifier. Both identifiers are guaranteed to not be <code>null</code>.
    *
    * @param branchId the branch identifier with an optional view artifact identifier. This parameter may be
    * <code>null</code>.
    * @return a {@link BranchSpecification} with the encapsulated {@link BranchId} set according to
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

   public BranchSpecification(BranchId branchId) {
      this(branchId, ArtifactId.SENTINEL);
   }

   /**
    * Creates an {@link BranchId} implementation that only contains the branch identifier and the view artifact
    * identifier. Both identifiers are guaranteed to not be <code>null</code>.
    *
    * @param branchId the branch identifier with an optional view artifact identifier. This parameter may be
    * <code>null</code>.
    * @param viewId the artifact view identifier. This parameter may be <code>null</code>.
    * @return a {@link BranchSpecification} with the encapsulated {@link BranchId} set according to
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

   public BranchSpecification(BranchId branchId, ArtifactId viewId) {
      this.branchId = BranchSpecification.cleanBranchId(branchId, viewId);
   }

   /**
    * Gets the encapsulated {@link BranchId}. The returned value will be non-<code>null</code> but may be
    * {@link BranchId#SENTINEL}.
    *
    * @return the encapsulated {@link BranchId}.
    */

   public BranchId getBranchId() {
      return this.branchId;
   }

   /**
    * Gets the encapsulated view {@link ArtifactId}. The returned value will be non-<code>null</code> but may be
    * {@link ArtifactId#SENTINEL}.
    *
    * @return the encapsulated view {@link ArtifactId}.
    */

   public ArtifactId getViewId() {
      return this.branchId.getViewId();
   }

   /**
    * Predicate to determine if the encapsulated view {@link ArtifactId} is not {@link ArtifactId#SENTINEL}.
    *
    * @return <code>true</code> when the encapsulated view {@link ArtifactId} is not {@link ArtifactId#SENTINEL};
    * otherwise, <code>false</code>.
    */

   public boolean hasView() {
      return !this.branchId.getViewId().equals(ArtifactId.SENTINEL);
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public int hashCode() {

      var a = this.branchId.getId();
      var b = this.branchId.getViewId().getId();

      var r = Long.rotateLeft((a << 5) - a, 24) ^ (b * b - b);

      return (int) ((r >> 32) ^ r) & 0xFFFFFFFF;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public boolean equals(Object other) {

      if (!(other instanceof BranchSpecification)) {
         return false;
      }

      var otherBranchSpecification = (BranchSpecification) other;

      //@formatter:off
      return
            this.branchId.getId().equals( otherBranchSpecification.branchId.getId() )
         && this.branchId.getViewId().getId().equals( otherBranchSpecification.getViewId().getId() );
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Message toMessage(int indent, Message message) {
      var outMessage = Objects.nonNull(message) ? message : new Message();
      //@formatter:off
      outMessage
         .title( "Branch Specification" )
         .indentInc()
         .segment( "Branch Identifier", this.branchId.getId().equals( Id.SENTINEL )
                                           ? "SENTINEL"
                                           : this.branchId.getIdString() )
         .segment( "View Identifier",   this.branchId.getViewId().getId().equals(Id.SENTINEL)
                                           ? "SENTINEL"
                                           : this.branchId.getViewId().getIdString() )
         .indentDec();
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
