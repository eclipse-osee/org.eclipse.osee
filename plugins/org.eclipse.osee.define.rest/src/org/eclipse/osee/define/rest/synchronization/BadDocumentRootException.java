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
    * @param message a description of the invalid document roots.
    */

   public BadDocumentRootException(String message) {
      super(message);
   }
}

/* EOF */
