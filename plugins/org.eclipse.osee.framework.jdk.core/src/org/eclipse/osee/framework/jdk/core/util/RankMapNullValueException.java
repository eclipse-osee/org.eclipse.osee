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

package org.eclipse.osee.framework.jdk.core.util;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * {@link RuntimeException} which is throw when a <code>null</code> value is provided to a {@link RankMap} method.
 *
 * @author Loren K. Ashley
 */

public class RankMapNullValueException extends RuntimeException {

   /**
    * Serialization version identifier
    */

   private static final long serialVersionUID = 1L;

   /**
    * Creates a new {@link RuntimeException} with a message describing the map and keys.
    *
    * @param rankMap the {@link RankMap} that a <code>null</code> value was provided to.
    * @param keys the keys that were provided.
    */

   public RankMapNullValueException(RankMap<?> rankMap, Object... keys) {
      super(RankMapNullValueException.buildMessage(rankMap, keys));
   }

   /**
    * Creates a new {@link RuntimeException} with a message describing the map and keys.
    *
    * @param cause the {@link Throwable} which led to this exception being thrown. This parameter maybe
    * <code>null</code>.
    * @param rankMap the {@link RankMap} that a <code>null</code> value was provided to.
    * @param keys the keys that were provided.
    */

   public RankMapNullValueException(Throwable cause, RankMap<?> rankMap, Object... keys) {
      this(rankMap, keys);

      this.initCause(cause);
   }

   /**
    * Builds an error message {@link String} describing the exception.
    *
    * @param rankMap the {@link RankMap} that a <code>null</code> value was provided to.
    * @param keys the keys that were provided.
    */

   public static String buildMessage(RankMap<?> rankMap, Object... keys) {
      //@formatter:off
      return
         new StringBuilder( 1024 )
            .append( "\n" )
            .append( "A null value was provided for the RankMap method." ).append( "\n" )
            .append( "   Rank Map:      " ).append( rankMap.identifier() ).append( "\n" )
            .append( "   Rank Map Rank: " ).append( rankMap.rank() ).append( "\n" )
            .append( "   Keys:     " ).append( Objects.nonNull( keys ) ? Arrays.stream( keys ).map( Object::toString ).collect( Collectors.joining( ", ", "[ ", " ]" ) ) : "(none)" ).append( "\n" )
            .toString();
      //@formatter:on

   }

}

/* EOF */
