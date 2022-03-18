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
 * A specialization of {@link AbstractMapGrove} for the storage of {@link AttributeDefinitionGroveThing} objects needed for a
 * Synchronization Artifact.
 *
 * @author Loren K. Ashley
 */

class AttributeDefinitionGrove extends AbstractMapGrove {

   /**
    * Creates a new empty {@link AttributeDefinitionGrove}.
    */

   AttributeDefinitionGrove() {
      super(IdentifierType.ATTRIBUTE_DEFINITION);
   }

}

/* EOF */
