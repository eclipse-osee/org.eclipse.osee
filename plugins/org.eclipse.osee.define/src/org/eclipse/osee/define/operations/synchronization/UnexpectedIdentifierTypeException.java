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

import java.util.Objects;
import org.eclipse.osee.define.operations.synchronization.identifier.IdentifierType;
import org.eclipse.osee.define.operations.synchronization.identifier.IdentifierTypeGroup;

/**
 * {@link RuntimeException} which is thrown when an unexpected {@link IdentifierType} is encountered.
 * 
 * @author Loren K. Ashley
 */

public class UnexpectedIdentifierTypeException extends RuntimeException {

   /**
    * Serialization version identifier
    */

   private static final long serialVersionUID = 1L;

   /**
    * Creates a new {@link RuntimeException} with a message describing the unexpected {@link IdentiferType}.
    * 
    * @param identifierType the unexpected {@link IdentifierType} encountered.
    * @param expectedIdentifierTypeGroup the {@link IdentifierTypeGroup} the {@link IdentifierType} is expected to be a
    * member of.
    */

   public UnexpectedIdentifierTypeException(IdentifierType identifierType, IdentifierTypeGroup expectedIdentifierTypeGroup) {

      super(UnexpectedIdentifierTypeException.buildMessage(identifierType, expectedIdentifierTypeGroup));
   }

   /**
    * Creates a new {@link RuntimeException} with a message describing the unexpected {@link IdentiferType}.
    * 
    * @param identifierType the unexpected {@link IdentifierType} encountered.
    * @param expectedIdentifierTypeGroup the {@link IdentifierTypeGroup} the {@link IdentifierType} is expected to be a
    * member of.
    * @param cause the {@link Throwable} which led to this exception being thrown. This parameter maybe
    * <code>null</code>.
    */

   public UnexpectedIdentifierTypeException(IdentifierType identifierType, Throwable cause, IdentifierTypeGroup expectedIdentifierTypeGroup) {
      this(identifierType, expectedIdentifierTypeGroup);

      this.initCause(cause);
   }

   /**
    * Builds an error message {@link String} describing the exception.
    *
    * @param identifierType the unexpected {@link IdentifierType} that was encountered.
    * @param expectedIdentifierTypeGroup the {@link IdentifierTypeGroup} the {@link IdentifierType} is expected to be a
    * member of.
    * @return {@link String} message describing the exception condition.
    */

   public static String buildMessage(IdentifierType identifierType, IdentifierTypeGroup expectedIdentifierTypeGroup) {
      //@formatter:off
      return
         new StringBuilder( 1024 )
                .append( "\n" )
                .append( "Unexpected IdentifierType." ).append( "\n" )
                .append( "   Identifier Type:                " ).append( Objects.nonNull( identifierType )              ? identifierType                                                                     : "(null)" ).append( "\n" )
                .append( "   Expected Identifier Type Group: " ).append( Objects.nonNull( expectedIdentifierTypeGroup ) ? expectedIdentifierTypeGroup                                                        : "(null)" ).append( "\n" )
                .append( "   Expected Identifier Types:      " ).append( Objects.nonNull( expectedIdentifierTypeGroup ) ? IdentifierType.getIdentifierTypeGroupMembersMessage( expectedIdentifierTypeGroup ) : "(null)" ).append( "\n" )
                .toString();
      //@formatter:on

   }
}

/* EOF */
