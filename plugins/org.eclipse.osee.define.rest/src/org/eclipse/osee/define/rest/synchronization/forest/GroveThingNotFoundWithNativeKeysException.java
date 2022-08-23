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

package org.eclipse.osee.define.rest.synchronization.forest;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * {@link RuntimeException} which is thrown when a {@link GroveThing} was not found in a {@link Grove} with a native key
 * set.
 *
 * @author Loren K. Ashley
 */

public class GroveThingNotFoundWithNativeKeysException extends RuntimeException {

   /**
    * Serialization version identifier
    */

   private static final long serialVersionUID = 1L;

   /**
    * Creates a new {@link RuntimeException} with a message describing the {@link Grove} and native key set that did not
    * locate an expected {@link GroveThing}.
    *
    * @param grove the {@link Grove} that was searched.
    * @param nativeKeys the native key set used to look for an associated {@link GroveThing}.
    */

   public GroveThingNotFoundWithNativeKeysException(Grove grove, Object... nativeKeys) {
      //@formatter:off
      super
         (
            new StringBuilder( 1024 )
                   .append( "\n" )
                   .append( "GroveThing Not Found With Native Keys In Grove." ).append( "\n" )
                   .append( "   Grove Type:  " ).append( Objects.nonNull( grove ) ? grove.getType() : "(null)" ).append( "\n" )
                   .append( "   Native Keys: " ).append( Objects.nonNull( nativeKeys ) ? Arrays.stream( nativeKeys ).map( Object::toString ).collect( Collectors.joining( ", ", "[ ", " ]" ) ) : "(null)" ).append( "\n" )
                   .toString()
         );

      //@formatter:on
   }

   /**
    * Creates a new {@link RuntimeException} with a message describing the {@link Grove} and native key set that did not
    * locate an expected {@link GroveThing}.
    *
    * @param cause the {@link Throwable} which led to this exception being thrown. This parameter maybe
    * <code>null</code>.
    * @param grove the {@link Grove} that was searched.
    * @param nativeKeys the native key set used to look for an associated {@link GroveThing}.
    */

   public GroveThingNotFoundWithNativeKeysException(Throwable cause, Grove grove, Object... nativeKeys) {
      this(grove, nativeKeys);

      this.initCause(cause);
   }
}

/* EOF */
