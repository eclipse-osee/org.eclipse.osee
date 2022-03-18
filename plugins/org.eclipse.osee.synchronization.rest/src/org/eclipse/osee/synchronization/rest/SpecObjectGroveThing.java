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
 * Class to represent a SpecObjectGroveThing in the Synchronization Artifact.
 *
 * @author Loren.K.Ashley
 */

public class SpecObjectGroveThing extends CommonObjectGroveThing {

   /**
    * Creates a new {@link SpecObjectGroveThing} object with a unique identifier.
    */

   SpecObjectGroveThing() {
      super(IdentifierType.SPEC_OBJECT.createIdentifier());
   }

}

/* EOF */
