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

package org.eclipse.osee.define.rest.synchronization.forest.morphology;

import java.util.Arrays;
import java.util.Objects;
import org.eclipse.osee.define.rest.synchronization.forest.GroveThing;
import org.eclipse.osee.define.rest.synchronization.identifier.Identifier;

/**
 * {@link RuntimeException} thrown when the creation of a {@link GroveThing} fails.
 *
 * @author Loren K. Ashley
 */

public class GroveThingCreationException extends RuntimeException {

   /**
    * Serialization version identifier
    */

   private static final long serialVersionUID = 1L;

   /**
    * Creates a new {@link RuntimeException} with a message describing the failure creation of the {@link GroveThing}.
    *
    * @param reason the failure reason.
    * @param identifier the {@link Identifier} for the {@link GroveThing} being created.
    * @param primaryRank the primary rank of the {@link GroveThing} being created.
    * @param nativeRank the native rank of the {@link GroveThing} being created.
    * @param parents the parent {@link GroveThing}s of the {@link GroveThing} being created.
    */

   public GroveThingCreationException(String reason, Identifier identifier, int primaryRank, int nativeRank, GroveThing... parents) {
      super(GroveThingCreationException.buildMessage(reason, identifier, primaryRank, nativeRank, parents));
   }

   /**
    * Creates a new {@link RuntimeException} with a message describing the failure creation of the {@link GroveThing}.
    *
    * @param cause the {@link Throwable} which led to this exception being thrown. This parameter maybe
    * <code>null</code>.
    * @param reason the failure reason.
    * @param identifier the {@link Identifier} for the {@link GroveThing} being created.
    * @param primaryRank the primary rank of the {@link GroveThing} being created.
    * @param nativeRank the native rank of the {@link GroveThing} being created.
    * @param parents the parent {@link GroveThing}s of the {@link GroveThing} being created.
    */

   public GroveThingCreationException(Throwable cause, String reason, Identifier identifier, int primaryRank, int nativeRank, GroveThing... parents) {
      this(reason, identifier, primaryRank, nativeRank, parents);

      this.initCause(cause);
   }

   /**
    * Builds an error message {@link String} describing the exception.
    *
    * @param reason the failure reason.
    * @param identifier the {@link Identifier} for the {@link GroveThing} being created.
    * @param primaryRank the primary rank of the {@link GroveThing} being created.
    * @param nativeRank the native rank of the {@link GroveThing} being created.
    * @param parents the parent {@link GroveThing}s of the {@link GroveThing} being created.
    */

   public static String buildMessage(String reason, Identifier identifier, int primaryRank, int nativeRank, GroveThing... parents) {
      //@formatter:off
      var message =
         new StringBuilder( 1024 )
                .append( "\n" )
                .append( "Bad Creation Parameters For A GroveThing." ).append( "\n" )
                .append( "   Identifier:      " ).append( identifier ).append( "\n" )
                .append( "   Identifier Type: " ).append( Objects.nonNull( identifier ) ? identifier.getType() : "(no identifier)" ).append( "\n" )
                .append( "   Primary Rank:    " ).append( primaryRank ).append( "\n")
                .append( "   Native Rank:     " ).append( nativeRank ).append( "\n" )
                .append( "   Parents:         " );
      //@formatter:on

      if (Objects.nonNull(parents) && (parents.length > 0)) {
         message.append("\n");
         Arrays.stream(parents).forEach((groveThing) -> groveThing.toMessage(2, message));
      } else {
         message.append("(no parents)").append("\n");
      }

      //@formatter:off
      message
         .append( "   Reason:          " ).append( reason ).append( "\n" );
      //@formatter:on

      return message.toString();
   }

}

/* EOF */