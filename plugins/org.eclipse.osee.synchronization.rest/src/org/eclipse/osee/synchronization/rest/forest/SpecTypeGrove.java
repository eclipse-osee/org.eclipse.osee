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
import org.eclipse.osee.synchronization.rest.IdentifierType;

/**
 * A specialization of {@link CommonObjectTypeGrove} for the storage of {@link SpecTypeGroveThing} objects needed for a
 * Synchronization Artifact.
 * <p>
 * <table border="1" style='border-collapse:collapse'>
 * <caption>{@link SpecObjectTypeGrove}</caption>
 * <tr>
 * <th>Grove Type</th>
 * <td>{@link IdentifierType#SPECIFICATION_TYPE}</td>
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
 * <td>{@link IdentifierType.SPECIFICATION_TYPE}</td>
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
 * <td>{@link Long}</td>
 * </tr>
 * <tr>
 * <th>GroveThing Native Thing Classes</th>
 * <td>{@link ArtifactTypeToken}</td>
 * </tr>
 * </table>
 *
 * @author Loren K. Ashley
 */

public final class SpecTypeGrove extends CommonObjectTypeGrove {

   /**
    * Creates a new empty {@link SpecTypeGrove}.
    */

   SpecTypeGrove() {
      super(IdentifierType.SPECIFICATION_TYPE);
   }
}

/* EOF */
