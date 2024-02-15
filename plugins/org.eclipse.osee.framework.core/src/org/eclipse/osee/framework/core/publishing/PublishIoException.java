/*********************************************************************
 * Copyright (c) 2024 Boeing
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

package org.eclipse.osee.framework.core.publishing;

import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * A {@link RuntimeException} for publishing file IO exceptions.
 *
 * @author Loren K. Ashley
 */

public class PublishIoException extends RuntimeException {

   /**
    * Default serialization identifier.
    */

   private static final long serialVersionUID = 1L;

   /**
    * Saves the path to the file being operated upon.
    */

   private final String filePath;

   /**
    * Creates a new {@link PublishIoException}.
    *
    * @param message a description of the exception.
    * @param filePath the path to the file being operated upon.
    * @param cause the causing exception.
    */

   public PublishIoException(String message, String filePath, Throwable cause) {
      super(message, cause);
      this.filePath = filePath;
   }

   public String getFilePath() {
      //@formatter:off
      return
         ( this.filePath != null )
            ? this.filePath
            : Strings.EMPTY_STRING;
      //@formatter:on
   }

}

/* EOF */
