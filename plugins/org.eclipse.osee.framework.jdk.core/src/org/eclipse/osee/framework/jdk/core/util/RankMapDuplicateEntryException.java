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
 * {@link RuntimeException} which is thrown when an attempt is made to add an entry to a {@link RankMap} that already
 * has an association for the provided keys.
 *
 * @author Loren K. Ashley
 */

public class RankMapDuplicateEntryException extends RuntimeException {

   /**
    * Serialization version identifier
    */

   private static final long serialVersionUID = 1L;

   /**
    * Creates a new {@link RuntimeException} with a message describing the duplicate entry that was added to a
    * {@link RankMap}.
    *
    * @param rankMap the {@link RankMap} that the duplicate entry was added to.
    * @param value the duplicate value added to the map.
    * @param keys the keys the duplicate entry was to be associated with.
    */

   public RankMapDuplicateEntryException(RankMap<?> rankMap, Object value, Object... keys) {
      super(RankMapDuplicateEntryException.buildMessage(rankMap, value, keys));
   }

   /**
    * Creates a new {@link RuntimeException} with a message describing the duplicate entry that was added to a
    * {@link RankMap}.
    *
    * @param cause the {@link Throwable} which led to this exception being thrown. This parameter maybe
    * <code>null</code>.
    * @param rankMap the {@link RankMap} that the duplicate entry was added to.
    * @param value the duplicate value added to the map.
    * @param keys the keys the duplicate entry was to be associated with.
    */

   public RankMapDuplicateEntryException(Throwable cause, RankMap<?> rankMap, Object value, Object... keys) {
      this(rankMap, value, keys);

      this.initCause(cause);
   }

   /**
    * Builds an error message {@link String} describing the exception.
    *
    * @param rankMap the {@link RankMap} that the duplicate entry was added to.
    * @param value the duplicate value added to the map.
    * @param keys the keys the duplicate entry was to be associated with.
    */

   public static String buildMessage(RankMap<?> rankMap, Object value, Object... keys) {
      //@formatter:off
      return
         new StringBuilder( 1024 )
            .append( "\n" )
            .append( "Attempt to add a duplicate entry to a RankMap." ).append( "\n" )
            .append( "   Rank Map: " ).append( rankMap.identifier() ).append( "\n" )
            .append( "   Keys:     " ).append( Objects.nonNull( keys ) ? Arrays.stream( keys ).map( Object::toString ).collect( Collectors.joining( ", ", "[ ", " ]" ) ) : "(none)" ).append( "\n" )
            .append( "   Value:    " ).append( value ).append( "\n" )
            .toString();
      //@formatter:on
   }

}

/* EOF */
