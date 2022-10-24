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

/**
 * Enumeration of {@link IdentifierFactory} types.
 *
 * @author Loren K. Ashley
 */

public enum IdentifierFactoryType {

   /**
    * Counting {@link IdentifierFactory} implementations are used for export and generate new primary identifiers using
    * a type prefix and a counter.
    */

   COUNTING,

   /**
    * Pattern matching {@link IdentifierFactory} implementations are used for import. Each generated primary identifier
    * is associated with the string representation of a foreign thing's identifier. If the foreign thing identifier
    * string matches the pattern for the type of primary identifier being generated, the new primary identifier will be
    * created directly from the foreign thing identifier string. Otherwise, the new primary identifier will be generated
    * with the prefix for the identifier type followed by "-G-" and a count.
    */

   PATTERN_MATCHING;
}

/* EOF */