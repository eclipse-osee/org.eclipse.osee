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

package org.eclipse.osee.define.rest.synchronization;

/**
 * {@link RuntimeException} which is thrown when a Synchronization Artifact Builder is not found for the requested
 * Synchronization Artifact type.
 *
 * @author Loren K. Ashley
 */

public class UnknownSynchronizationArtifactTypeException extends RuntimeException {

   /**
    * Serialization version identifier
    */

   private static final long serialVersionUID = 1L;

   /**
    * Creates a new {@link RuntimeException} with a message describing the unknown Synchronization Artifact type.
    *
    * @param unknownArtifactType the {@link String} identifier for the Synchronization Artifact type that was not found.
    */

   public UnknownSynchronizationArtifactTypeException(String unknownArtifactType) {
      //@formatter:off
      super(
         new StringBuilder()
            .append( "Request for a Synchronization Artifact with an unknown artifact type." ).append( "\n" )
            .append( "   Artifact Type: " ).append( unknownArtifactType.isEmpty() ? "(empty)" : unknownArtifactType ).append( "\n" )
            .toString()
         );
      //@formatter:on
   }

   /**
    * Creates a new {@link RuntimeException} with a message describing the unknown Synchronization Artifact type.
    *
    * @param unknownArtifactType the {@link String} identifier for the Synchronization Artifact type that was not found.
    * @param cause the {@link Throwable} which led to this exception being thrown. This parameter maybe
    * <code>null</code>.
    */

   public UnknownSynchronizationArtifactTypeException(String unknownArtifactType, Throwable cause) {
      this(unknownArtifactType);

      this.initCause(cause);
   }
}

/* EOF */
