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

package org.eclipse.osee.define.operations.synchronization.identifier;

import java.util.Optional;

/**
 * An {@link IdentifierFactory} contains an {@link IdentifierTracker} implementation for each {@link IdentifierType}.
 * The each {@link IdentifierTracker} is responsible for creating new primary identifiers for the associated
 * {@link IdentifierType}. {@link IdentifierTracker}s for {@link IdentifierFactoryType#PATTERN_MATCHING} factories also
 * maintain an association between the generated primary identifiers and the foreign identifier strings of the foreign
 * things the primary identifiers were generated for.
 *
 * @author Loren K. Ashley
 */

interface IdentifierTracker {

   /**
    * Creates a new primary {@link Identifier}.
    *
    * @param foreignIdentifier for {@link IdentifierFactoryType#COUNTING} {@link IdentifierTracker} implementations this
    * parameter must be <code>null</code>. For {@link IdentifierFactoryType#PATTERN_MATCHING} {@link IdentifierTracker}
    * implementations this parameter is the string representation of the foreign thing's identifier that the primary
    * identifier is being generated for.
    * @return the generated primary {@link Identifier}.
    * @throws IllegalStateException when:
    * <ul>
    * <li>the implementation is for a {@link IdentifierFactoryType#COUNTING} factory and the parameter
    * <code>foreignIdentifier</code> is non-<code>null</code>, or</li>
    * <li>the implementation is for a {@link IdentifierFactoryType#PATTERN_MATCHING} factory and the parameter
    * <code>foreignIdentifier</code> is <code>null</null>.</li>
    * </ul>
    */

   Identifier create(String foreignIdentifier);

   /**
    * Gets the primary {@link Identifier} that was generated for the specified <code>foreignIdentifierString</code>.
    *
    * @param foreignIdentifierString the string representation of the foreign thing's identifier.
    * @return when an associated primary {@link Identifier} exists for the specified
    * <code>foreignIdentifierString</code>, an {@link Optional} containing the primary {@link Identifier}; otherwise, an
    * empty {@link Optional}.
    * @throws IllegalStateException when this method is called for an {@link IdentifierTracker} that was created for a
    * {@link IdentifierFactoryType#COUNTING} factory.
    */

   Optional<Identifier> getPrimaryIdentifierByForeignIdentifierString(String foreignIdentifierString);
}

/* EOF */