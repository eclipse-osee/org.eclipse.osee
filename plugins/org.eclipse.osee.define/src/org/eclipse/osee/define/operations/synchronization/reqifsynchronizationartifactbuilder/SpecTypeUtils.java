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

import java.util.Objects;
import java.util.Optional;
import org.eclipse.osee.define.operations.synchronization.identifier.IdentifierType;
import org.eclipse.rmf.reqif10.SpecObjectType;
import org.eclipse.rmf.reqif10.SpecRelationType;
import org.eclipse.rmf.reqif10.SpecType;
import org.eclipse.rmf.reqif10.SpecificationType;

public class SpecTypeUtils {

   /**
    * Private constructor to prevent instantiation of the class.
    */

   private SpecTypeUtils() {
   }

   /**
    * Gets the {@link IdentifierType} associated with the provided ReqIF {@link SpecType}.
    *
    * @param specType a {@link SpecType} thing from the ReqIF DOM.
    * @return when an associated {@link IdentifierType} is found, an {@link Optional} containing the associated
    * {@link IdentifierType}; otherwise, an empty {@link Optional}.
    * @throws NullPointerException when the parameter <code>specType</code> is <code>null</code>.
    */

   static Optional<IdentifierType> getIdentifierType(SpecType specType) {

      Objects.requireNonNull(specType, "SpecTypeUtils::getIdentifierType, parameter \"specType\" is null.");

      //@formatter:off
      var specTypeIdentifierType =
         ( specType instanceof SpecificationType )
            ? IdentifierType.SPECIFICATION_TYPE
            : ( specType instanceof SpecObjectType )
                 ? IdentifierType.SPEC_OBJECT_TYPE
                 : ( specType instanceof SpecRelationType )
                      ? IdentifierType.SPEC_RELATION_TYPE
                      : null;

      return
         Objects.nonNull( specTypeIdentifierType )
            ? Optional.of( specTypeIdentifierType )
            : Optional.empty();
      //@formatter:on
   }
}

/* EOF */
