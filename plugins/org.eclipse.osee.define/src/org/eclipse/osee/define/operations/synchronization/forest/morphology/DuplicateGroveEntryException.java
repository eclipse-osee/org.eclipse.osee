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

package org.eclipse.osee.define.operations.synchronization.forest.morphology;

import org.eclipse.osee.define.operations.synchronization.forest.Grove;
import org.eclipse.osee.define.operations.synchronization.forest.GroveThing;

/**
 * {@link RuntimeException} thrown to indicate that an attempt was made to add a {@link GroveThing} to a {@link Grove}
 * that already has an entry for the {@link GroveThing}.
 *
 * @author Loren K. Ashley
 */

public class DuplicateGroveEntryException extends RuntimeException {

   /**
    * Serialization version identifier
    */

   private static final long serialVersionUID = 1L;

   /**
    * Creates a new {@link RuntimeException} with a message describing the {@link Grove} a duplicate {@link GroveThing}
    * was added to.
    *
    * @param grove the {@link Grove} being added to.
    */

   public DuplicateGroveEntryException(Grove grove) {
      //@formatter:off
      super(
         new StringBuilder()
            .append( "\n" )
            .append( "Attempt to add a GroveThing with a duplicate key to the Grove." ).append( "\n" )
            .append( "   Grove Type: " ).append( grove.getType().toString() ).append( "\n" )
            .toString()
         );
      //@formatter:on
   }

   /**
    * Creates a new {@link RuntimeException} with a message describing the {@link Grove} a duplicate {@link GroveThing}
    * was added to.
    *
    * @param grove the {@link Grove} being added to.
    * @param cause the {@link Throwable} which led to this exception being thrown. This parameter maybe
    * <code>null</code>.
    */

   public DuplicateGroveEntryException(Grove grove, Throwable cause) {
      this(grove);

      this.initCause(cause);
   }

}

/* EOF */
