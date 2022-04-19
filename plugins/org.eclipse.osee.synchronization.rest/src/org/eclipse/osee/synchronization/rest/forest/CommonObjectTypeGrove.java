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

package org.eclipse.osee.synchronization.rest.forest;

import org.eclipse.osee.synchronization.rest.IdentifierType;
import org.eclipse.osee.synchronization.rest.forest.morphology.AbstractMapGrove;

/**
 * A specialization of {@link AbstractMapGrove} for the storage of {@link SpecTypeGroveThing} or
 * {@link SpecObjectTypeGroveThing} objects needed for a Synchronization Artifact.
 *
 * @author Loren.K.Ashley
 */

public class CommonObjectTypeGrove extends AbstractMapGrove {

   /**
    * Creates a new empty {@link CommonObjectTypeGrove}.
    */

   CommonObjectTypeGrove(IdentifierType identifierType) {
      //@formatter:off
      super
         (
            identifierType,                  /* Grove Association */
            new IdentifierType[][]           /* Allowed Primary Key Types */
               {
                 { identifierType }
               },
            new Class<?>[] {Long.class}      /* Allowed Native Key Classes */
         );
      //@formatter:on
   }

}

/* EOF */
