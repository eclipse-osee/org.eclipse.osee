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

package org.eclipse.osee.synchronization.rest;

import java.util.Objects;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.synchronization.util.IndentedString;
import org.eclipse.osee.synchronization.util.ToMessage;

/**
 * Class to encapsulate the branch identifier and artifact identifier that denote the top of an artifact tree to be
 * included in the synchronization artifact.
 */

class Root implements ToMessage {

   /**
    * Identifier of the branch the artifact is on.
    */

   private final BranchId branchId;

   /**
    * Identifier of the artifact that is the top of the artifact tree.
    */

   private final ArtifactId artifactId;

   /**
    * Creates a new {@link Root} object for the branch identifier artifact identifier pair.
    *
    * @param branchId the branch identifier as a {@link BranchId}.
    * @param artifactId the artifact identifier as an {@link ArtifactId}.
    */

   Root(BranchId branchId, ArtifactId artifactId) {

      assert Objects.nonNull(branchId) && Objects.nonNull(artifactId);

      //Get the native OSEE root object for the specification
      this.branchId = branchId;
      this.artifactId = artifactId;
   }

   /**
    * Creates a new {@link Root} object for the branch identifier artifact identifier pair.
    *
    * @param branchIdString the branch identifier as a {@link String}.
    * @param artifactIdString the artifact identifier as a {@link String}.
    */

   Root(String branchIdString, String artifactIdString) {

      assert Objects.nonNull(branchIdString) && Objects.nonNull(artifactIdString);

      //Get the native OSEE root object for the specification
      this.branchId = BranchId.valueOf(Long.parseUnsignedLong(branchIdString, 10));
      this.artifactId = ArtifactId.valueOf(Long.parseUnsignedLong(artifactIdString, 10));
   }

   /**
    * Gets the branch identifier.
    *
    * @return branch identifier.
    */

   BranchId getBranchId() {
      return this.branchId;
   }

   /**
    * Gets the artifact identifier.
    *
    * @return artifact identifier.
    */

   ArtifactId getArtifactId() {
      return this.artifactId;
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

   StringBuilder toText(StringBuilder message) {
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
   public StringBuilder toMessage(int indent, StringBuilder message) {
      var outMessage = (message != null) ? message : new StringBuilder(1 * 1024);
      var indent0 = IndentedString.indentString(indent + 0);
      var indent1 = IndentedString.indentString(indent + 1);

      //@formatter:off
      outMessage
         .append( indent0 ).append( "SpecificationGroveThing Root:" ).append( "\n" )
         .append( indent1 ).append( "Branch Identifier:   ").append( this.branchId   ).append( "\n" )
         .append( indent1 ).append( "Artifact Identifier: ").append( this.artifactId ).append( "\n" )
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
