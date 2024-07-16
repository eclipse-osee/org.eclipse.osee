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

package org.eclipse.osee.synchronization.rest;

/**
 * A specialization of {@link CommonObjectTypeGrove} for the storage of {@link SpecTypeGroveThing} objects needed for a
 * Synchronization Artifact.
 *
 * @author Loren K. Ashley
 */

public class SpecTypeGrove extends CommonObjectTypeGrove {

   /**
    * Creates a new empty {@link SpecTypeGrove}.
    */

   SpecTypeGrove() {
      super(IdentifierType.SPECIFICATION_TYPE);
   }
}

/* EOF */
