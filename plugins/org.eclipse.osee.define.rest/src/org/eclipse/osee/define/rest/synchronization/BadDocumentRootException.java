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

import org.eclipse.osee.define.api.synchronization.Root;

/**
 * {@link RuntimeException} which is thrown when a document root specified with a {@link Root} object is invalid.
 *
 * @author Loren K. Ashley
 */

public class BadDocumentRootException extends RuntimeException {

   /**
    * Serialization version identifier
    */

   private static final long serialVersionUID = 1L;

   /**
    * Creates a new {@link RuntimeException} with a message describing the invalid {@link Root} objects.
    *
    * @parameter direction the export or import direction of the Synchronization Artifact operation.
    * @param validationMessage a description of the invalid document roots.
    */

   public BadDocumentRootException(Direction direction, String validationMessage) {
      super(BadDocumentRootException.buildMessage(direction, validationMessage));
   }

   /**
    * Creates a new {@link RuntimeException} with a message describing the invalid {@link Root} objects.
    *
    * @parameter direction the export or import direction of the Synchronization Artifact operation.
    * @param validationMessage a description of the invalid document roots.
    * @param cause the {@link Throwable} which led to this exception being thrown. This parameter may be
    * <code>null</code>.
    */

   public BadDocumentRootException(Direction direction, String validationMessage, Throwable cause) {
      this(direction, validationMessage);

      this.initCause(cause);
   }

   /**
    * Builds an error message {@link String} describing the exception.
    *
    * @parameter direction the export or import direction of the Synchronization Artifact operation.
    * @param validationMessage a description of the invalid document roots.
    * @return {@link String} message describing the exception condition.
    */

   public static String buildMessage(Direction direction, String validationMessage) {
      //@formatter:off
      return
         new StringBuilder( 1024 )
                .append( "\n" )
                .append( "One or more specified OSEE document roots was not found." ).append( "\n" )
                .append( "   Direction:          " ).append( direction ).append( "\n" )
                .append( "   Validation Message Follows: " ).append( "\n" )
                .append( validationMessage ).append( "\n" )
                .toString();
      //@formatter:on
   }

}

/* EOF */
