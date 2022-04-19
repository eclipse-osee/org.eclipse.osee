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

import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.enums.EnumToken;
import org.eclipse.osee.synchronization.rest.IdentifierType;
import org.eclipse.osee.synchronization.rest.forest.morphology.AbstractGroveThing;
import org.eclipse.osee.synchronization.rest.forest.morphology.GroveThing;
import org.eclipse.osee.synchronization.util.ParameterArray;

/**
 * Class to represent an enumeration member in the Synchronization Artifact.
 *
 * @author Loren K. Ashley
 */

public final class EnumValueGroveThing extends AbstractGroveThing {

   /**
    * Creates a new {@link EnumValueGroveThing} object with a unique identifier.
    */

   EnumValueGroveThing(GroveThing parent) {
      super(IdentifierType.ENUM_VALUE.createIdentifier(), 2);
   }

   @Override
   public boolean validateNativeThings(Object... nativeThings) {
      //@formatter:off
      return
            ParameterArray.validateNonNullAndSize(nativeThings, 2, 2)
         && (nativeThings[0] instanceof AttributeTypeToken)
         && (nativeThings[1] instanceof EnumToken);
      //@formatter:on
   }

}

/* EOF */
