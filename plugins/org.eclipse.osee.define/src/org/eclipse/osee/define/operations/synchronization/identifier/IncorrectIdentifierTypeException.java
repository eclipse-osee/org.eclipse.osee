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

package org.eclipse.osee.define.operations.synchronization.identifier;

import org.eclipse.osee.framework.jdk.core.util.Message;

/**
 * {@link RuntimeException} thrown when an {@link Identifier} is not of the expected {@link IdentifierType}.
 *
 * @author Loren K. Ashley
 */

public class IncorrectIdentifierTypeException extends RuntimeException {

   /**
    * Serialization version identifier
    */

   private static final long serialVersionUID = 1L;

   /**
    * Creates a new {@link RuntimeException} with a message describing the {@link Identifier} that was of the wrong
    * {@link IdentifierType} and the expected {@link IdentifierType}.
    *
    * @param identifier the {@link Identifier} of the wrong {@link IdentifierType}.
    * @param expectedIdentifierType the expected {@link IdentifierType}.
    * @param message additional context for the exception message. This parameter maybe <code>null</code>.
    */

   public IncorrectIdentifierTypeException(Identifier identifier, IdentifierType expectedIdentifierType, String message) {
      super(IncorrectIdentifierTypeException.buildMessage(identifier, expectedIdentifierType, message));
   }

   /**
    * Creates a new {@link RuntimeException} with a message describing the {@link Identifier} that was of the wrong
    * {@link IdentifierType} and the expected {@link IdentifierType}.
    *
    * @param identifier the {@link Identifier} of the wrong {@link IdentifierType}.
    * @param expectedIdentifierType the expected {@link IdentifierType}.
    */

   public IncorrectIdentifierTypeException(Identifier identifier, IdentifierType expectedIdentifierType) {
      super(IncorrectIdentifierTypeException.buildMessage(identifier, expectedIdentifierType, null));
   }

   /**
    * Creates a new {@link RuntimeException} with a message describing the {@link Identifier} that was of the wrong
    * {@link IdentifierType} and the expected {@link IdentifierType}.
    *
    * @param identifier the {@link Identifier} of the wrong {@link IdentifierType}.
    * @param expectedIdentifierType the expected {@link IdentifierType}.
    * @param message additional context for the exception message. This parameter maybe <code>null</code>.
    * @param cause the {@link Throwable} which led to this exception being thrown. This parameter maybe
    * <code>null</code>.
    */

   public IncorrectIdentifierTypeException(Identifier identifier, IdentifierType expectedIdentifierType, String message, Throwable cause) {
      this(identifier, expectedIdentifierType, message);

      this.initCause(cause);
   }

   /**
    * Creates a new {@link RuntimeException} with a message describing the {@link Identifier} that was of the wrong
    * {@link IdentifierType} and the expected {@link IdentifierType}.
    *
    * @param identifier the {@link Identifier} of the wrong {@link IdentifierType}.
    * @param expectedIdentifierType the expected {@link IdentifierType}.
    * @param cause the {@link Throwable} which led to this exception being thrown. This parameter maybe
    * <code>null</code>.
    */

   public IncorrectIdentifierTypeException(Identifier identifier, IdentifierType expectedIdentifierType, Throwable cause) {
      this(identifier, expectedIdentifierType);

      this.initCause(cause);
   }

   /**
    * Creates a new {@link RuntimeException} with a message describing the {@link Identifier} whose
    * {@link IdentifierType} was not a member of the expected {@link IdentifierTypeGroup}s.
    *
    * @param identifier the {@link Identifier} whose {@link IdentifierType} is not in the expected
    * {@link IdentifierTypeGroup}.
    * @param message additional context for the exception message. This parameter maybe <code>null</code>.
    * @param missingIdentifierTypeGroup the first expected {@link IdentifierTypeGroup} <code>identifier</code> was not a
    * member of. {@link IdentifierType} is expected to be member of.
    * @param expectedIdentifierTypeGroups the {@link IdentifierTypeGroup}s the <code>identifier</code> is expected to be
    * member of.
    */

   public IncorrectIdentifierTypeException(Identifier identifier, String message, IdentifierTypeGroup missingIdentifierTypeGroup, IdentifierTypeGroup... expectedIdentifierTypeGroups) {
      super(IncorrectIdentifierTypeException.buildMessage(identifier, message, missingIdentifierTypeGroup,
         expectedIdentifierTypeGroups));
   }

   /**
    * Creates a new {@link RuntimeException} with a message describing the {@link Identifier} whose
    * {@link IdentifierType} was not a member of the expected {@link IdentifierTypeGroup}s.
    *
    * @param identifier the {@link Identifier} whose {@link IdentifierType} is not in the expected
    * {@link IdentifierTypeGroup}.
    * @param missingIdentifierTypeGroup the first expected {@link IdentifierTypeGroup} <code>identifier</code> was not a
    * member of. {@link IdentifierType} is expected to be member of.
    * @param expectedIdentifierTypeGroups the {@link IdentifierTypeGroup}s the <code>identifier</code> is expected to be
    * member of.
    */

   public IncorrectIdentifierTypeException(Identifier identifier, IdentifierTypeGroup missingIdentifierTypeGroup, IdentifierTypeGroup... expectedIdentifierTypeGroups) {
      super(IncorrectIdentifierTypeException.buildMessage(identifier, null, missingIdentifierTypeGroup,
         expectedIdentifierTypeGroups));
   }

   /**
    * Creates a new {@link RuntimeException} with a message describing the {@link Identifier} whose
    * {@link IdentifierType} was not a member of the expected {@link IdentifierTypeGroup}s.
    *
    * @param cause the {@link Throwable} which led to this exception being thrown. This parameter maybe
    * <code>null</code>.
    * @param identifier the {@link Identifier} whose {@link IdentifierType} is not in the expected
    * {@link IdentifierTypeGroup}.
    * @param message additional context for the exception message. This parameter maybe <code>null</code>.
    * @param missingIdentifierTypeGroup the first expected {@link IdentifierTypeGroup} <code>identifier</code> was not a
    * member of. {@link IdentifierType} is expected to be member of.
    * @param expectedIdentifierTypeGroups the {@link IdentifierTypeGroup}s the <code>identifier</code> is expected to be
    * member of.
    */

   public IncorrectIdentifierTypeException(Throwable cause, Identifier identifier, String message, IdentifierTypeGroup missingIdentifierTypeGroup, IdentifierTypeGroup... expectedIdentifierTypeGroups) {
      this(identifier, message, missingIdentifierTypeGroup, expectedIdentifierTypeGroups);

      this.initCause(cause);
   }

   /**
    * Creates a new {@link RuntimeException} with a message describing the {@link Identifier} whose
    * {@link IdentifierType} was not a member of the expected {@link IdentifierTypeGroup}s.
    *
    * @param cause the {@link Throwable} which led to this exception being thrown. This parameter maybe
    * <code>null</code>.
    * @param identifier the {@link Identifier} whose {@link IdentifierType} is not in the expected
    * {@link IdentifierTypeGroup}.
    * @param missingIdentifierTypeGroup the first expected {@link IdentifierTypeGroup} <code>identifier</code> was not a
    * member of. {@link IdentifierType} is expected to be member of.
    * @param expectedIdentifierTypeGroups the {@link IdentifierTypeGroup}s the <code>identifier</code> is expected to be
    * member of.
    */

   public IncorrectIdentifierTypeException(Throwable cause, Identifier identifier, IdentifierTypeGroup missingIdentifierTypeGroup, IdentifierTypeGroup... expectedIdentifierTypeGroups) {
      this(identifier, null, missingIdentifierTypeGroup, expectedIdentifierTypeGroups);

      this.initCause(cause);
   }

   /**
    * Builds an error message {@link String} describing the exception.
    *
    * @param message additional context for the exception message. This parameter maybe <code>null</code>.
    * @param identifier the {@link Identifier} of the wrong {@link IdentifierType}.
    * @param expectedIdentifierType the expected {@link IdentifierType}.
    * @return {@link String} message describing the exception condition.
    */

   public static String buildMessage(Identifier identifier, IdentifierType expectedIdentifierType, String message) {
      //@formatter:off
      return
         new Message()
                .title( "Identifier is not of the expected IdentifierType." )
                .title( message )
                .indentInc()
                .segment( "Identifier",               identifier.getText()   )
                .segment( "IdentifierType",           identifier.getType()   )
                .segment( "Expected Identifier Type", expectedIdentifierType )
                .toString()
                ;
      //@formatter:on
   }

   /**
    * Builds an error message {@link String} describing the exception.
    *
    * @param identifier the {@link Identifier} whose {@link IdentifierType} is not in the expected
    * {@link IdentifierTypeGroup}.
    * @param missingIdentifierTypeGroup the first expected {@link IdentifierTypeGroup} <code>identifier</code> was not a
    * member of. {@link IdentifierType} is expected to be member of.
    * @param expectedIdentifierTypeGroups the {@link IdentifierTypeGroup}s the <code>identifier</code> is expected to be
    * member of.
    */

   public static String buildMessage(Identifier identifier, String message, IdentifierTypeGroup missingIdentifierTypeGroup, IdentifierTypeGroup... expectedIdentifierTypeGroups) {
      //@formatter:off
      return
         new Message()
                .title( "The Identifier is not a member of the expected IdentifierTypeGroup." )
                .title( message )
                .indentInc()
                .segment( "Identifier",                       identifier.getText()         )
                .segment( "Identifier Type",                  identifier.getType()         )
                .segment( "Identifier Type Group Not Found",  missingIdentifierTypeGroup   )
                .segment( "Expected Identifier Type Groups",  expectedIdentifierTypeGroups )
                .toString()
                ;
      //@formatter:on
   }

}

/* EOF */
