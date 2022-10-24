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

package org.eclipse.osee.define.operations.synchronization;

/**
 * An enumeration to describe a relationship terminal as either the {@link #SOURCE} or {@link #TARGET}.
 *
 * @author Loren K. Ashley
 */

public enum RelationshipTerminal {

   /**
    * Indicates the relationship terminal is the source of the relation.
    */

   SOURCE,

   /**
    * Indicates the relationship terminal is the target of the relation.
    */

   TARGET;
}

/* EOF */
