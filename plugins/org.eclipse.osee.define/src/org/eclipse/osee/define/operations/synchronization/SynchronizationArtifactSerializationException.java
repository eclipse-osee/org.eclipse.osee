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

package org.eclipse.osee.define.operations.synchronization;

/**
 * {@link RuntimeException} which is thrown when a {@link SynchronizationArtifact} fails to serialize.
 *
 * @author Loren K. Ashley
 */

public class SynchronizationArtifactSerializationException extends RuntimeException {

   /**
    * Serialization version identifier
    */

   private static final long serialVersionUID = 1L;

   /**
    * Creates a new {@link RuntimeException} with a message describing the serialization failure.
    *
    * @param reason the reason for the serialization failure.
    */

   public SynchronizationArtifactSerializationException(String reason) {
      super(SynchronizationArtifactSerializationException.buildMessage(reason));
   }

   /**
    * Creates a new {@link RuntimeException} with a message describing the serialization failure.
    *
    * @param reason the reason for the serialization failure.
    * @param cause the {@link Throwable} which led to this exception being thrown. This parameter maybe
    * <code>null</code>.
    */

   public SynchronizationArtifactSerializationException(String reason, Throwable cause) {
      this(reason);

      this.initCause(cause);
   }

   /**
    * Builds an error message {@link String} describing the exception.
    *
    * @param reason the reason for the serialization failure.
    * @return {@link String} message describing the exception condition.
    */

   public static String buildMessage(String reason) {
      //@formatter:off
      return
         new StringBuilder( 1024 )
                .append( "\n" )
                .append( "Failed to serialize the Synchronization Artifact." ).append( "\n" )
                .append( "   Reason: " ).append( reason ).append( "\n" )
                .toString();
      //@formatter:on
   }
}

/* EOF */
