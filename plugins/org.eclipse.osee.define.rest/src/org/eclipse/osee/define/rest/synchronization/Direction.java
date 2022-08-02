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

package org.eclipse.osee.define.rest.synchronization;

import org.eclipse.osee.define.rest.synchronization.identifier.IdentifierFactoryType;

/**
 * Enumeration used to indicated the export or import direction of a Synchronization Artifact operation.
 *
 * @author Loren K. Ashley
 */

public enum Direction {

   /**
    * Specifies that a Synchronization Artifact is being exported. The associated {@link IdentifierFactoryType} for
    * exports is {@link IdentifierFactoryType#COUNTING}.
    */

   EXPORT(IdentifierFactoryType.COUNTING),

   /**
    * Specifies that a Synchronization Artifact is being imported. The associated {@link IdentifierFactoryType} for
    * imports is {@link IdentifierFactoryType#PATTERN_MATCHING}.
    */

   IMPORT(IdentifierFactoryType.PATTERN_MATCHING);

   /**
    * Saves the {@link IdentifierFactoryType} associated with the Synchronization Artifact operation direction.
    */

   IdentifierFactoryType identifierFactoryType;

   /**
    * Constructor used for the single static instance of each enumeration member.
    * 
    * @param identifierFactoryType the {@link IdentifierFactoryType} to be associated with the direction specified by
    * the enumeration member.
    */

   Direction(IdentifierFactoryType identifierFactoryType) {
      this.identifierFactoryType = identifierFactoryType;
   }

   /**
    * Gets the {@link IdentifierFactoryType} associated with the enumeration member.
    *
    * @return the associated {@link IdentifierFactoryType}.
    */

   public IdentifierFactoryType getIdentifierFactoryType() {
      return this.identifierFactoryType;
   }
}

/* EOF */