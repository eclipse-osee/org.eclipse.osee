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
import org.eclipse.osee.synchronization.rest.nativedatatype.NativeDataTypeKey;

/**
 * A specialization of {@link AbstractMapGrove} for the storage of {@link DataTypeDefinitionGroveThing} objects needed
 * for the Synchronization Artifact.
 * <p>
 * <table border="1" style='border-collapse:collapse'>
 * <caption>{@link DataTypeDefinitionGrove}</caption>
 * <tr>
 * <th>Grove Type</th>
 * <td>{@link IdentifierType#DATA_TYPE_DEFINITION}</td>
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
 * <td>{@link IdentifierType.DATA_TYPE_DEFINITION}</td>
 * </tr>
 * <tr>
 * <th>Native Rank</th>
 * <td>1</td>
 * </tr>
 * <tr>
 * <th rowspan="2">Native Key Types</th>
 * <th>High Rank (index 0)</th>
 * </tr>
 * <tr>
 * <td>{@link NativeDataTypeKey}</td>
 * </tr>
 * <tr>
 * <th>GroveThing Native Thing Classes</th>
 * <td>{@link NativeDataTypeKey}</td>
 * </tr>
 * </table>
 *
 * @author Loren.K.Ashley
 */

public final class DataTypeDefinitionGrove extends AbstractMapGrove {

   /**
    * Creates a new empty {@link DataTypeDefinitionGrove}.
    */

   DataTypeDefinitionGrove() {
      //@formatter:off
      super
         (
            IdentifierType.DATA_TYPE_DEFINITION,               /* Grove Association */
            new IdentifierType[][]                             /* Allowed Primary Key Types */
               {
                 { IdentifierType.DATA_TYPE_DEFINITION }
               },
            new Class<?>[] { NativeDataTypeKey.class }         /* Allowed Native Key Classes */
         );
      //@formatter:on
   }

}

/* EOF */
