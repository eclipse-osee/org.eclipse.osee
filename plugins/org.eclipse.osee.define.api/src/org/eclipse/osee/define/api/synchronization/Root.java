/*********************************************************************
 * Copyright (c) 2022 Boeing
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

package org.eclipse.osee.define.api.synchronization;

import java.util.Objects;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.jdk.core.util.ToMessage;

/**
 * Class to encapsulate the branch identifier and artifact identifier that denote the top of an artifact tree to be
 * included in the Synchronization Artifact.
 *
 * @author Loren K. Ashley
 */

public class Root implements ToMessage {

   /**
    * Identifier of the OSEE branch the document root is on.
    */

   private BranchId branchId;

   /**
    * Identifier of the OSEE artifact that is the document root.
    */

   private ArtifactId artifactId;

   /**
    * Creates a new empty {@link Root} object for JSON deserialization.
    */

   public Root() {
      this.branchId = null;
      this.artifactId = null;
   }

   /**
    * Creates a new {@link Root} object with data for serialization (client) or for making a Synchronization Artifact
    * service call (server).
    *
    * @param branchId the branch identifier as a {@link BranchId}.
    * @param artifactId the artifact identifier as an {@link ArtifactId}.
    * @throws NullPointerException when either of the parameters <code>branchId</code> or <code>artifactId</code> is
    * <code>null</code>.
    */

   public Root(BranchId branchId, ArtifactId artifactId) {

      this.branchId = Objects.requireNonNull(branchId, "Root::new, the parameter \"branchId\" is null.");
      this.artifactId = Objects.requireNonNull(artifactId, "Root::new, the parameter \"artifactId\" is null.");
   }

   /**
    * Gets the artifact identifier.
    *
    * @return artifact identifier.
    * @throws IllegalStateException when an attempt is made to get the {@link #artifactId} for a {@link Root} where the
    * artifact identifier has not been set.
    */

   public ArtifactId getArtifactId() {
      if (Objects.isNull(this.artifactId)) {
         //@formatter:off
         var message =
               new Message()
                  .title( "Root::getArtifactId, the member \"artifactId\" has not been set." )
                  .blank();
         //@formatter:on
         this.toMessage(1, message);
         throw new IllegalStateException(message.toString());
      }
      return this.artifactId;
   }

   /**
    * Gets the branch identifier.
    *
    * @return branch identifier.
    * @throws IllegalStateException when an attempt is made to get the {@link #banchId} for a {@link Root} where the
    * branch identifier has not been set.
    */

   public BranchId getBranchId() {
      if (Objects.isNull(this.branchId)) {
         //@formatter:off
         var message =
               new Message()
                  .title( "Root::getBranchId, the member \"branchId\" has not been set." )
                  .blank();
         //@formatter:on
         this.toMessage(1, message);
         throw new IllegalStateException(message.toString());
      }
      return this.branchId;
   }

   /**
    * Predicate to test the validity of the {@link Root} object.
    *
    * @return <code>true</code>, when member {@link #artifactId} is non-<code>null</code> and member {@link #branchId}
    * is non-<code>null</code>; otherwise, <code>false</code>.
    */

   public boolean isValid() {
      //@formatter:off
      return
            Objects.nonNull( this.artifactId )
         && Objects.nonNull( this.branchId );
      //@formatter:on
   }

   /**
    * Set the artifact identifier. This method is provided for the deserialization of a JSON message into a {@link Root}
    * object.
    *
    * @param artifactId the artifact identifier for the document root.
    * @throws NullPointerException when the parameter <code>artifactId</code> is <code>null</code>.
    * @throws NullPointerException when the parameters <code>artifactId</code> is <code>null</code>.
    * @throws IllegalStateException when an attempt is made to set the {@link #artifactId} of a {@link Root} object that
    * already has a non-<code>null</code> value.
    */

   public void setArtifactId(ArtifactId artifactId) {
      if (Objects.nonNull(this.artifactId)) {
         //@formatter:off
         var message =
            new Message()
                   .title( "Root::setArtifactId, the member \"artifactId\" has alreday been set." )
                   .blank()
                   ;
         //@formatter:off
         this.toMessage(1, message);
         throw new IllegalStateException( message.toString() );
      }
      this.artifactId = Objects.requireNonNull(artifactId, "Root::setArtifactId, the parameter \"artifactId\" is null.");
   }

   /**
    * Set the branch identifier. This method is provided for the deserialization of a JSON message into a {@link Root}
    * object.
    *
    * @param branchId the branch identifier for the document root.
    * @throws NullPointerException when the parameter <code>branchId</code> is <code>null</code>.
    * @throws NullPointerException when the parameter <code>branchId</code> is <code>null</code>.
    * @throws IllegalStateException when an attempt is made to set the {@link #branchId} of a {@link Root} object that
    * already has a non-<code>null</code> value.
    */

   public void setBranchId(BranchId branchId) {
      if (Objects.nonNull(this.branchId)) {
         //@formatter:off
         var message =
            new Message()
                   .title( "Root::setBranchId, the member \"branchId\" has alreday been set." )
                   .blank()
                   ;
         //@formatter:off
         this.toMessage(1, message);
         throw new IllegalStateException( message.toString() );
      }
      this.branchId = Objects.requireNonNull(branchId, "Root::setBranchId, the parameter \"branchId\" is null.");
   }

   /**
    * Adds a textual message to the provided {@link StringBuilder} or a new {@link StringBuilder} representing the
    * Synchronization Artifact {@link Root}. The message is formatted as follows:
    * <ul style="list-style-type:none">
    * <li>"BranchId(" &lt;branch-id&gt; ") ArtifactId(" &lt;artifact-id&gt; ")"</li>
    * </ul>
    *
    * @param message when not null the message is appended to this {@link StringBuilder}.
    * @return the provided {@link StringBuilder} when not null; otherwise, a new {@link StringBuilder}.
    */

   public StringBuilder toText(StringBuilder message) {
      var outMessage = (message != null) ? message : new StringBuilder(256);

      //@formatter:off
      outMessage
         .append( "BranchId( "   ).append( this.branchId   ).append( " ) ")
         .append( "ArtifactId( " ).append( this.artifactId ).append( " )" )
         ;
      //@formatter:on

      return outMessage;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Message toMessage(int indent, Message message) {
      var outMessage = (message != null) ? message : new Message();

      //@formatter:off
      outMessage
         .indent( indent )
         .title( "Root" )
         .indentInc()
         .segment( "Branch Identifier",   this.branchId   )
         .segment( "Artifact Identifier", this.artifactId )
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
