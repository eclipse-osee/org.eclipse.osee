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

import java.util.Optional;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.synchronization.rest.IdentifierType;
import org.eclipse.osee.synchronization.rest.IdentifierType.Identifier;
import org.eclipse.osee.synchronization.rest.forest.morphology.AbstractMapGrove;

/**
 * A specialization of {@link AbstractMapGrove} for the storage of {@link SpecObjectGroveThing} objects needed for a
 * Synchronization Artifact.
 * <p>
 * <table border="1" style='border-collapse:collapse'>
 * <caption>{@link SpecObjectGrove}</caption>
 * <tr>
 * <th>Grove Type</th>
 * <td colspan="3">{@link IdentifierType#SPEC_OBJECT}</td>
 * </tr>
 * <tr>
 * <th>Primary Rank</th>
 * <td colspan="3">3</td>
 * </tr>
 * <tr>
 * <th rowspan="2">Primary Key Types</th>
 * <th>High Rank (index 0)</th>
 * <th>(index 1)</th>
 * <th>Low Rank (index 2)</th>
 * </tr>
 * <tr>
 * <td>{@link IdentifierType.SPECIFICATION}</td>
 * <td>{@link IdentifierType.SPECIFICATION}, {@link IdentifierType.SPEC_OBJECT}</td>
 * <td>{@link IdentifierType.SPECIFICATION}, {@link IdentifierType.SPEC_OBJECT}</td>
 * </tr>
 * <tr>
 * <th>Native Rank</th>
 * <td colspan="3">1</td>
 * </tr>
 * <tr>
 * <th rowspan="2">Native Key Types</th>
 * <th colspan="3">High Rank (index 0)</th>
 * </tr>
 * <tr>
 * <td colspan="3">{@link Long}</td>
 * </tr>
 * <tr>
 * <th>GroveThing Native Thing Classes</th>
 * <td colspan="3">{@link ArtifactReadable}</td>
 * </tr>
 * </table>
 *
 * @author Loren K. Ashley
 */

public final class SpecObjectGrove extends AbstractMapGrove {

   /**
    * Creates a new empty {@link SpecObjetGrove}.
    */

   SpecObjectGrove() {
      //@formatter:off
      super
         (
            IdentifierType.SPEC_OBJECT,                                             /* Grove Association */
            new IdentifierType[][]                                                  /* Allowed Primary Key Types */
                   {
                     { IdentifierType.SPECIFICATION},
                     { IdentifierType.SPECIFICATION, IdentifierType.SPEC_OBJECT },
                     { IdentifierType.SPECIFICATION, IdentifierType.SPEC_OBJECT }
                   },
            new Class<?>[] { Long.class }                                           /* Allowed Native Key Classes */
         );
      //@formatter:on
   }

   @SuppressWarnings("unchecked")
   public Optional<SpecificationGroveThing> getSpecification(Identifier specificationIdentifier) {

      return (Optional<SpecificationGroveThing>) (Object) this.getByPrimaryKeys(specificationIdentifier,
         specificationIdentifier);
   }

   @SuppressWarnings("unchecked")
   public Optional<SpecObjectGroveThing> getSpecObject(Identifier specificationIdentifier, Identifier specObjectIdentifier) {

      return (Optional<SpecObjectGroveThing>) (Object) this.getByPrimaryKeys(specificationIdentifier,
         specObjectIdentifier);
   }

}

/* EOF */
