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

import java.util.Objects;
import org.eclipse.osee.define.rest.synchronization.forest.GroveThing;
import org.eclipse.osee.define.rest.synchronization.identifier.IdentifierType;
import org.eclipse.osee.define.rest.synchronization.identifier.IdentifierTypeGroup;

/**
 * {@link RuntimeException} which is thrown when a {@link GroveThing} with an unexpected {@link IdentifierType} is
 * encountered.
 *
 * @author Loren K. Ashley
 */

public class UnexpectedGroveThingTypeException extends RuntimeException {

   /**
    * Serialization version identifier
    */

   private static final long serialVersionUID = 1L;

   /**
    * Creates a new {@link RuntimeException} with a message describing the {@link GroveThing} with an unexpected
    * {@link IdentifierType}.
    *
    * @param groveThing the {@link GroveThing} with an unexpected {@link IdentifierType}.
    * @param expectedIdentifierType the expected {@link IdentifierType}.
    */

   public UnexpectedGroveThingTypeException(GroveThing groveThing, IdentifierType expectedIdentifierType) {

      super(UnexpectedGroveThingTypeException.buildMessage(groveThing, expectedIdentifierType));
   }

   /**
    * Creates a new {@link RuntimeException} with a message describing the {@link GroveThing} with an unexpected
    * {@link IdentifierType}.
    *
    * @param groveThing the {@link GroveThing} with an unexpected {@link IdentifierType}.
    * @param expectedIdentifierType the expected {@link IdentifierType}.
    * @param cause the {@link Throwable} which led to this exception being thrown. This parameter maybe
    * <code>null</code>.
    */

   public UnexpectedGroveThingTypeException(GroveThing groveThing, IdentifierType expectedIdentifierType, Throwable cause) {
      this(groveThing, expectedIdentifierType);

      this.initCause(cause);
   }

   /**
    * Creates a new {@link RuntimeException} with a message describing the {@link GroveThing} with an unexpected
    * {@link IdentifierType}.
    *
    * @param groveThing the {@link GroveThing} with an unexpected {@link IdentifierType}.
    * @param expectedIdentifierTypeGroup the {@link IdentifierTypeGroup} of expected {@link IdentifierType}s.
    */

   public UnexpectedGroveThingTypeException(GroveThing groveThing, IdentifierTypeGroup expectedIdentifierTypeGroup) {

      super(UnexpectedGroveThingTypeException.buildMessage(groveThing, expectedIdentifierTypeGroup));
   }

   /**
    * Creates a new {@link RuntimeException} with a message describing the {@link GroveThing} with an unexpected
    * {@link IdentifierType}.
    *
    * @param groveThing the {@link GroveThing} with an unexpected {@link IdentifierType}.
    * @param expectedIdentifierTypeGroup the {@link IdentifierTypeGroup} of expected {@link IdentifierType}s.
    * @param cause the {@link Throwable} which led to this exception being thrown. This parameter maybe
    * <code>null</code>.
    */

   public UnexpectedGroveThingTypeException(GroveThing groveThing, IdentifierTypeGroup expectedIdentifierTypeGroup, Throwable cause) {
      this(groveThing, expectedIdentifierTypeGroup);

      this.initCause(cause);
   }

   /**
    * Creates a new {@link RuntimeException} with a message describing the {@link GroveThing} with an unexpected
    * {@link LinkType}.
    *
    * @param groveThing the {@link GroveThing} with an unexpected {@link LinkType}.
    * @param expectedLinkType the expected {@link LinkType}.
    */

   public UnexpectedGroveThingTypeException(GroveThing groveThing, LinkType expectedLinkType) {

      //@formatter:off
      super
         (
            ( expectedLinkType instanceof IdentifierType )
               ? UnexpectedGroveThingTypeException.buildMessage( groveThing, (IdentifierType)      expectedLinkType )
               : UnexpectedGroveThingTypeException.buildMessage( groveThing, (IdentifierTypeGroup) expectedLinkType )
         );
      //@formatter:on
   }

   /**
    * Creates a new {@link RuntimeException} with a message describing the {@link GroveThing} with an unexpected
    * {@link LinkType}.
    *
    * @param groveThing the {@link GroveThing} with an unexpected {@link LinkType}.
    * @param expectedLinkType the expected {@link LinkType}.
    * @param cause the {@link Throwable} which led to this exception being thrown. This parameter maybe
    * <code>null</code>.
    */

   public UnexpectedGroveThingTypeException(GroveThing groveThing, LinkType expectedLinkType, Throwable cause) {
      this(groveThing, expectedLinkType);

      this.initCause(cause);
   }

   /**
    * Builds an error message {@link String} describing the exception.
    *
    * @param groveThing the {@link GroveThing} with an unexpected {@link IdentifierType}.
    * @param expectedIdentifierType the {@link IdentifierType} the {@link GroveThing} is expected to be.
    * @return {@link String} message describing the exception condition.
    */

   public static String buildMessage(GroveThing groveThing, IdentifierType expectedIdentifierType) {
      //@formatter:off
      return
         new StringBuilder( 1024 )
                .append( "\n" )
                .append( "GroveThing with unexpected IdentifierType encountered." ).append( "\n" )
                .append( "   Grove Thing Type:         " ).append( Objects.nonNull( groveThing )             ? groveThing.getType()   : "(groveThing is null)" ).append( "\n" )
                .append( "   Expected Identifier Type: " ).append( Objects.nonNull( expectedIdentifierType ) ? expectedIdentifierType : "(null)"               ).append( "\n" )
                .toString();
      //@formatter:on
   }

   /**
    * Builds an error message {@link String} describing the exception.
    *
    * @param groveThing the {@link GroveThing} with an unexpected {@link IdentifierType}.
    * @param expectedIdentifierTypeGroup the {@link IdentifierTypeGroup} the {@link GroveThing} is expected to be a
    * member of.
    * @return {@link String} message describing the exception condition.
    */

   public static String buildMessage(GroveThing groveThing, IdentifierTypeGroup expectedIdentifierTypeGroup) {
      //@formatter:off
      return
         new StringBuilder( 1024 )
                .append( "\n" )
                .append( "GroveThing with unexpected IdentifierType encountered." ).append( "\n" )
                .append( "   Grove Thing Type:               " ).append( Objects.nonNull( groveThing )                  ? groveThing.getType()                                                               : "(groveThing is null)" ).append( "\n" )
                .append( "   Expected Identifier Type Group: " ).append( Objects.nonNull( expectedIdentifierTypeGroup ) ? expectedIdentifierTypeGroup                                                        : "(null)"               ).append( "\n" )
                .append( "   Expected Identifier Types:      " ).append( Objects.nonNull( expectedIdentifierTypeGroup ) ? IdentifierType.getIdentifierTypeGroupMembersMessage( expectedIdentifierTypeGroup ) : "(null)"               ).append( "\n" )
                .toString();
      //@formatter:on
   }

}

/* EOF */
