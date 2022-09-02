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

package org.eclipse.osee.define.rest.synchronization.reqifsynchronizationartifactbuilder;

import java.util.stream.Stream;
import org.eclipse.osee.define.rest.synchronization.ForeignThingFamily;
import org.eclipse.osee.define.rest.synchronization.SimpleForeignThingFamily;
import org.eclipse.osee.define.rest.synchronization.identifier.IdentifierType;
import org.eclipse.rmf.reqif10.Identifiable;
import org.eclipse.rmf.reqif10.SpecificationType;

/**
 * This class contains various methods for {@link SpecificationType} foreign things.
 *
 * @author Loren K. Ashley
 */

public class SpecificationTypeUtils {

   /**
    * Private constructor to prevent instantiation of the class.
    */

   private SpecificationTypeUtils() {
   }

   /**
    * Creates an unordered {@link Stream} of {@link ForeignThingFamily} objects representing the ReqIF Specification
    * Types in the ReqIF DOM.
    *
    * @param builder the {@link ReqIFSynchronizationArtifactBuilder}.
    * @return a {@link Stream} of {@link ForeignThingFamily} objects.
    */

   static Stream<ForeignThingFamily> extract(ReqIFSynchronizationArtifactBuilder builder) {
      //@formatter:off
      return
         builder.reqIf.getCoreContent().getSpecTypes().stream()
            .filter( ( specType ) -> specType instanceof SpecificationType )
            .map
               (
                  ( specificationType ) -> new SimpleForeignThingFamily
                                                  (
                                                     specificationType,
                                                     new String[]
                                                     {
                                                        ((Identifiable) specificationType).getIdentifier()
                                                     },
                                                     new IdentifierType[]
                                                     {
                                                        IdentifierType.SPECIFICATION_TYPE
                                                     }
                                                  )
            );
      //@formatter:on
   }
}

/* EOF */
