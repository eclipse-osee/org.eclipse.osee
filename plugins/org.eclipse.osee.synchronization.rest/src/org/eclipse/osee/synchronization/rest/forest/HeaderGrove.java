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
 * A specialization of {@link AbstractMapGrove} for the storage of {@link HeaderGroveThing} objects needed for a
 * Synchronization Artifact.
 * <p>
 * <table border="1" style='border-collapse:collapse'>
 * <caption>{@link AttributeValueGrove}</caption>
 * <tr>
 * <th>Grove Type</th>
 * <td>{@link IdentifierType#HEADER}</td>
 * </tr>
 * <tr>
 * <th>Primary Rank</th>
 * <td>1</td>
 * </tr>
 * <tr>
 * <th rowspan="2">Primary Key Types</th>
 * <th>High Rank (index 0)</th>
 * </tr>
 * <tr>
 * <td>{@link IdentifierType.HEADER}</td>
 * </tr>
 * <tr>
 * <th>Native Rank</th>
 * <td>0</td>
 * </tr>
 * </table>
 *
 * @author Loren K. Ashley
 */

public final class HeaderGrove extends AbstractMapGrove {

   /**
    * Creates a new empty {@link HeaderGrove}
    */

   HeaderGrove() {
      //@formatter:off
      super
         (
            IdentifierType.HEADER,           /* Grove Association */
            new IdentifierType[][]           /* Allowed Primary Key Types */
               {
                 { IdentifierType.HEADER }
               },
            null                             /* Allowed Native Key Classes */
         );
      //@formatter:on
   }
}

/* EOF */