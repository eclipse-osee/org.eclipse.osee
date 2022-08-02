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

package org.eclipse.osee.define.rest.synchronization.identifier;

/**
 * {@link RuntimeException} thrown when a request is made to generate a primary {@link Identifier} from a foreign
 * identifier string that has already been used to generate a primary {@link Identifier}.
 *
 * @author Loren K. Ashley
 */

class DuplicateIdentifierException extends RuntimeException {

   /**
    * Serialization version identifier
    */

   private static final long serialVersionUID = 1L;

   /**
    * Creates a new {@link RuntimeException} with a message describing the primary identifier type a duplicate
    * identifier was requested for.
    *
    * @param identifierType the type of primary identifier that was requested.
    * @param foreignIdentifierString the foreign identifier string a second primary identifier request was made for.
    * <code>null</code>.
    */

   public DuplicateIdentifierException(IdentifierType identifierType, String foreignIdentifierString) {
      super(DuplicateIdentifierException.buildMessage(identifierType, foreignIdentifierString));
   }

   /**
    * Creates a new {@link RuntimeException} with a message describing the primary identifier type a duplicate
    * identifier was requested for.
    *
    * @param identifierType the type of primary identifier that was requested.
    * @param foreignIdentifierString the foreign identifier string a second primary identifier request was made for.
    * @param cause the {@link Throwable} which led to this exception being thrown. This parameter maybe
    * <code>null</code>.
    */

   public DuplicateIdentifierException(IdentifierType identifierType, String foreignIdentifierString, Throwable cause) {
      this(identifierType, foreignIdentifierString);

      this.initCause(cause);
   }

   /**
    * Builds an error message {@link String} describing the exception.
    *
    * @param identifierType the type of primary identifier that was requested.
    * @param foreignIdentifierString the foreign identifier string a second primary identifier request was made for.
    * @return {@link String} message describing the exception condition.
    */

   public static String buildMessage(IdentifierType identifierType, String foreignIdentifierString) {
      //@formatter:off
      return
         new StringBuilder( 1024 )
                .append( "\n" )
                .append( "Attempt to create a second Primary Identifier from the same Foreign Identifier String." ).append( "\n" )
                .append( "   Primary Identifier Type:   " ).append( identifierType          ).append( "\n" )
                .append( "   Foreign Identifier String: " ).append( foreignIdentifierString ).append( "\n" )
                .toString();
      //@formatter:on
   }
}

/* EOF */