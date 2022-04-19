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

import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.synchronization.rest.IdentifierType;
import org.eclipse.osee.synchronization.rest.forest.morphology.AbstractMapGrove;

/**
 * A specialization of {@link AbstractMapGrove} for the storage of {@link AttributeDefinitionGroveThing} objects needed
 * for a Synchronization Artifact.
 * <p>
 * <table border="1" style='border-collapse:collapse'>
 * <caption>{@link AttributeDefinitionGrove}</caption>
 * <tr>
 * <th>Grove Type</th>
 * <td colspan="2">{@link IdentifierType#ATTRIBUTE_DEFINITION}</td>
 * </tr>
 * <tr>
 * <th>Primary Rank</th>
 * <td colspan="2">2</td>
 * </tr>
 * <tr>
 * <th rowspan="2">Primary Key Types</th>
 * <th>High Rank (index 0)</th>
 * <th>Low Rank (index 1)</th>
 * </tr>
 * <tr>
 * <td>{@link IdentifierType.SPECIFICATION_TYPE}, {@link IdentifierType.SPEC_OBJECT_TYPE}</td>
 * <td>{@link IdentifierType.ATTRIBUTE_DEFINITION}</td>
 * </tr>
 * <tr>
 * <th>Native Rank</th>
 * <td colspan="2">2</td>
 * </tr>
 * <tr>
 * <th rowspan="2">Native Key Types</th>
 * <th>High Rank (index 0)</th>
 * <th>Low Rank (index 1)</th>
 * </tr>
 * <tr>
 * <td>{@link Long}</td>
 * <td>{@link Long}</td>
 * </tr>
 * <tr>
 * <th>GroveThing Native Thing Classes</th>
 * <td>{@link ArtifactTypeToken}</td>
 * <td>{@link AttributeTypeToken}</td>
 * </tr>
 * </table>
 *
 * @author Loren K. Ashley
 */

public final class AttributeDefinitionGrove extends AbstractMapGrove {

   /**
    * Creates a new empty {@link AttributeDefinitionGrove}.
    */

   AttributeDefinitionGrove() {
      //@formatter:off
      super
         (
            IdentifierType.ATTRIBUTE_DEFINITION,                                           /* Grove Association         */
            new IdentifierType[][]                                                         /* Allowed Primary Key Types */
               {
                 { IdentifierType.SPECIFICATION_TYPE, IdentifierType.SPEC_OBJECT_TYPE },
                 { IdentifierType.ATTRIBUTE_DEFINITION }
               },
            new Class<?>[] {Long.class, Long.class}                                        /* Allowed Native Key Classes */
         );
      //@formatter:on
   }

}

/* EOF */
