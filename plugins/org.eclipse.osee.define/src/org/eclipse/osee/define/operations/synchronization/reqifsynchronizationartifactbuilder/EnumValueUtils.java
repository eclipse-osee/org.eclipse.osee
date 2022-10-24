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
import org.eclipse.rmf.reqif10.DatatypeDefinitionEnumeration;
import org.eclipse.rmf.reqif10.EnumValue;
import org.eclipse.rmf.reqif10.Identifiable;

/**
 * This class contains various methods for {@link EnumValue} foreign things.
 *
 * @author Loren K. Ashley
 */

public class EnumValueUtils {

   /**
    * Private constructor to prevent instantiation of the class.
    */

   private EnumValueUtils() {
   }

   /**
    * Creates an unordered {@link Stream} of {@link ForeignThingFamily} objects representing the ReqIF Enum Values in
    * the ReqIF DOM.
    *
    * @param builder the {@link ReqIFSynchronizationArtifactBuilder}.
    * @return a {@link Stream} of {@link ForeignThingFamily} objects.
    */

   static Stream<ForeignThingFamily> extract(ReqIFSynchronizationArtifactBuilder builder) {
      //@formatter:off
      return
         builder.reqIf.getCoreContent().getDatatypes().stream()
         .filter( ( datatypeDefinition ) -> datatypeDefinition instanceof DatatypeDefinitionEnumeration )
         .flatMap
            (
               ( datatypeDefinitionEnumeration ) ->

                  ((DatatypeDefinitionEnumeration) datatypeDefinitionEnumeration).getSpecifiedValues().stream()
                     .map
                        (
                           ( enumValue ) ->  new SimpleForeignThingFamily
                                                    (
                                                      enumValue,
                                                      new String[]
                                                      {
                                                        ((Identifiable) enumValue).getIdentifier()
                                                      },
                                                      new IdentifierType[]
                                                      {
                                                        IdentifierType.ENUM_VALUE
                                                      }
                                                    )
                        )
            );
      //@formatter:on
   }
}

/* EOF */
