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

package org.eclipse.osee.define.rest.synchronization.forest.morphology;

import java.util.Arrays;
import java.util.stream.Collectors;
import org.eclipse.osee.define.rest.synchronization.forest.GroveThing;

/**
 * {@link RuntimeException} thrown to indicate that an attempt was made to add a {@link GroveThing} to a {@link Store}
 * that already has an entry for the {@link GroveThing}.
 *
 * @author Loren K. Ashley
 */
public class DuplicateStoreEntryException extends RuntimeException {

   /**
    * Default message for a no keys condition.
    */

   private static final String[] noKeys = new String[] {"(no keys)"};

   /**
    * Serialization version identifier
    */

   private static final long serialVersionUID = 1L;

   /**
    * Creates a new {@link RuntimeException} with a message describing the {@link Store} and {@link GroveThing}.
    *
    * @param store the {@link Store} the duplicate was added to.
    * @param groveThing the duplicate {@link GroveThing}.
    */

   public DuplicateStoreEntryException(Store store, GroveThing groveThing) {
      //@formatter:off
      super(
         new StringBuilder()
            .append( "\n" )
            .append( "Attempt to add a GroveThing with a duplicate key to the Grove Store." ).append( "\n" )
            .append( "   Store Type:              " ).append( store.getType().toString() ).append( "\n" )
            .append( "   Grove Thing Keys:        " ).append( Arrays.stream( groveThing.getPrimaryKeys().orElse( DuplicateStoreEntryException.noKeys ) ).map( Object::toString ).collect(Collectors.joining( ",", "[ ", " ]")) ).append( "\n" )
            .append( "   Grove Thing Native Keys: " ).append( Arrays.stream( groveThing.getNativeKeys() .orElse( DuplicateStoreEntryException.noKeys ) ).map( Object::toString ).collect(Collectors.joining( ",", "[ ", " ]")) ).append( "\n" )
            .toString()
         );
      //@formatter:on
   }

   /**
    * Creates a new {@link RuntimeException} with a message describing the {@link Store} and {@link GroveThing}.
    *
    * @param store the {@link Store} the duplicate was added to.
    * @param groveThing the duplicate {@link GroveThing}.
    * @param cause the {@link Throwable} which led to this exception being thrown. This parameter maybe
    * <code>null</code>.
    */

   public DuplicateStoreEntryException(Store store, GroveThing groveThing, Throwable cause) {
      this(store, groveThing);

      this.initCause(cause);
   }
}

/* EOF */
