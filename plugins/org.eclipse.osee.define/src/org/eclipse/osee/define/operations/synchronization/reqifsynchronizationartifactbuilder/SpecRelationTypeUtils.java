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
import org.eclipse.rmf.reqif10.SpecRelationType;

/**
 * This class contains various methods for {@link SpecRelationType} foreign things.
 *
 * @author Loren K. Ashley
 */

public class SpecRelationTypeUtils {

   /**
    * Private constructor to prevent instantiation of the class.
    */

   private SpecRelationTypeUtils() {
   }

   /**
    * Creates an unordered {@link Stream} of {@link ForeignThingFamily} objects representing the ReqIF Spec Relation
    * Types in the ReqIF DOM.
    *
    * @param builder the {@link ReqIFSynchronizationArtifactBuilder}.
    * @return a {@link Stream} of {@link ForeignThingFamily} objects.
    */

   static Stream<ForeignThingFamily> extract(ReqIFSynchronizationArtifactBuilder builder) {
      //@formatter:off
      return
         builder.reqIf.getCoreContent().getSpecTypes().stream()
            .filter( ( specType ) -> specType instanceof SpecRelationType )
            .map
               (
                  ( specRelationType ) -> new SimpleForeignThingFamily
                                                 (
                                                    specRelationType,
                                                    new String[]
                                                    {
                                                       ((Identifiable) specRelationType).getIdentifier()
                                                    },
                                                    new IdentifierType[]
                                                    {
                                                       IdentifierType.SPEC_RELATION_TYPE
                                                    }
                                                 )
               );
      //@formatter:on
   }
}

/* EOF */
