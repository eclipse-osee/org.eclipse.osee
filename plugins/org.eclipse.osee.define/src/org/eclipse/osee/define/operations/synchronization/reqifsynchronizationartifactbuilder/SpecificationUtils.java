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

package org.eclipse.osee.define.operations.synchronization.reqifsynchronizationartifactbuilder;

import java.util.stream.Stream;
import org.eclipse.osee.define.operations.synchronization.ForeignThingFamily;
import org.eclipse.osee.define.operations.synchronization.SimpleForeignThingFamily;
import org.eclipse.osee.define.operations.synchronization.identifier.IdentifierType;
import org.eclipse.rmf.reqif10.Identifiable;
import org.eclipse.rmf.reqif10.Specification;

/**
 * This class contains various methods for {@link Specification} foreign things.
 *
 * @author Loren K. Ashley
 */

public class SpecificationUtils {

   /**
    * Private constructor to prevent instantiation of the class.
    */

   private SpecificationUtils() {
   }

   /**
    * Creates an unordered {@link Stream} of {@link ForeignThingFamily} objects representing the ReqIF Specifications in
    * the ReqIF DOM.
    *
    * @param builder the {@link ReqIFSynchronizationArtifactBuilder}.
    * @return a {@link Stream} of {@link ForeignThingFamily} objects.
    */

   static Stream<ForeignThingFamily> extract(ReqIFSynchronizationArtifactBuilder builder) {
      //@formatter:off
      return
         builder.reqIf.getCoreContent().getSpecifications().stream()
         .map
            (
               ( specification ) -> new SimpleForeignThingFamily
                                           (
                                              specification,
                                              new String[]
                                              {
                                                 ((Identifiable) specification).getIdentifier()
                                              },
                                              new IdentifierType[]
                                              {
                                                 IdentifierType.SPECIFICATION
                                              }
                                           )
            );
         //@formatter:on
   }
}

/* EOF */
