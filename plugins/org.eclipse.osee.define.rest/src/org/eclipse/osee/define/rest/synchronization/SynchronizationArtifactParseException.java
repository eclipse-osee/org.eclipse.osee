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

import java.util.List;
import org.eclipse.emf.ecore.resource.Resource.Diagnostic;
import org.xml.sax.SAXParseException;

/**
 * {@link RuntimeException} which is thrown when a Synchronization Artifact fails to parse.
 *
 * @author Loren K. Ashley
 */

public class SynchronizationArtifactParseException extends RuntimeException {

   /**
    * Serialization version identifier
    */

   private static final long serialVersionUID = 1L;

   /**
    * Creates a new {@link RuntimeException} with a message describing the Synchronization Artifact parse failure.
    *
    * @param message a description of the parse error.
    * @param lineNumber the line position of the parse error.
    * @param columnNumber the column position of the parse error.
    */

   public SynchronizationArtifactParseException(String message, int lineNumber, int columnNumber) {
      super(SynchronizationArtifactParseException.buildMessage(message, lineNumber, columnNumber));
   }

   /**
    * Creates a new {@link RuntimeException} with a message describing the Synchronization Artifact parse failure.
    *
    * @param message a description of the parse error.
    * @param lineNumber the line position of the parse error.
    * @param columnNumber the column position of the parse error.
    * @param cause the {@link Throwable} which led to this exception being thrown. This parameter maybe
    * <code>null</code>.
    */

   public SynchronizationArtifactParseException(String message, int lineNumber, int columnNumber, Throwable cause) {
      this(message, lineNumber, columnNumber);

      this.initCause(cause);
   }

   /**
    * Creates a new {@link RuntimeException} with a message describing the Synchronization Artifact parse failure.
    *
    * @param saxParseException the {@link SAXParseException} which led to this exception being thrown.
    */

   public SynchronizationArtifactParseException(SAXParseException saxParseException) {
      this(saxParseException.getMessage(), saxParseException.getLineNumber(), saxParseException.getColumnNumber(),
         saxParseException);
   }

   /**
    * Creates a new {@link RuntimeException} with a message describing the Synchronization Artifact parse failure.
    *
    * @param errors a {@link List} of {@link Diagnostic} errors describing the parse failure.
    * @param warnings a {@link List} of {@link Diagnostic} warnings describing the parse failure.
    */

   public SynchronizationArtifactParseException(List<Diagnostic> errors, List<Diagnostic> warnings) {
      super(SynchronizationArtifactParseException.buildMessage(errors, warnings));
   }

   /**
    * Creates a new {@link RuntimeException} with a message describing the Synchronization Artifact parse failure.
    *
    * @param message a description of the parse error.
    */

   public SynchronizationArtifactParseException(String message) {
      super(SynchronizationArtifactParseException.buildMessage(message));
   }

   /**
    * Builds an error message {@link String} describing the exception.
    *
    * @param message a description of the parse error.
    * @return a {@link String} message describing the exception condition.
    */

   public static String buildMessage(String message) {
      //@formatter:off
      return
         new StringBuilder( 1024 )
            .append( "\n" )
            .append( "Synchronization Artifact failed to parse." ).append( "\n" )
            .append( "   Reason: " ).append( message ).append( "\n" )
            .toString();
      //@formatter:on
   }

   /**
    * Builds an error message {@link String} describing the exception.
    *
    * @param errors a {@link List} of {@link Diagnostic} errors describing the parse failure.
    * @param warnings a {@link List} of {@link Diagnostic} warnings describing the parse failure.
    * @return a {@link String} message describing the exception condition.
    */

   public static String buildMessage(List<Diagnostic> errors, List<Diagnostic> warnings) {

      var message = new StringBuilder((errors.size() + warnings.size()) * 512);

      for (var diagnostic : errors) {
         message.append(SynchronizationArtifactParseException.buildMessage(diagnostic.getMessage(),
            diagnostic.getLine(), diagnostic.getColumn()));
      }

      for (var diagnostic : warnings) {
         message.append(SynchronizationArtifactParseException.buildMessage(diagnostic.getMessage(),
            diagnostic.getLine(), diagnostic.getColumn()));
      }

      return message.toString();
   }

   /**
    * Builds an error message {@link String} describing the exception.
    *
    * @param message a description of the parse error.
    * @param lineNumber the line position of the parse error.
    * @param columnNumber the column position of the parse error.
    * @return a {@link String} message describing the exception condition.
    */

   public static String buildMessage(String message, int lineNumber, int columnNumber) {
      //@formatter:off
      return
         new StringBuilder( 1024 )
            .append( "\n" )
            .append( "Synchronization Artifact failed to parse." ).append( "\n" )
            .append( "   " ).append( message ).append( "\n" )
            .append( "   Line:   " ).append( lineNumber   ).append( "\n" )
            .append( "   Column: " ).append( columnNumber ).append( "\n" )
            .toString();
      //@formatter:on
   }
}

/* EOF */