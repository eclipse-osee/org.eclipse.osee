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
 * A specialization of {@link AbstractMapGrove} for the storage of {@link DataTypeDefinitionGroveThing} objects needed for the
 * Synchronization Artifact.
 *
 * @author Loren.K.Ashley
 */

public class DataTypeDefinitionGrove extends AbstractMapGrove {

   /**
    * Creates a new empty {@link DataTypeDefinitionGrove}.
    */

   DataTypeDefinitionGrove() {
      super(IdentifierType.DATA_TYPE_DEFINITION);
   }

}

/* EOF */
