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

import java.util.EnumMap;

/**
 * {@link RuntimeException} which is thrown when a duplicate entry is added to a class extending {@link EnumMap} that
 * only allows a value to be set once for each enumeration member.
 *
 * @author Loren K. Ashley
 */

public class EnumMapDuplicateEntryException extends RuntimeException {

   /**
    * Serialization version identifier
    */

   private static final long serialVersionUID = 1L;

   /**
    * Creates a new {@link RuntimeException} with a message describing the duplicate key.
    *
    * @param key the {@link} enumeration member that caused the exception.
    */

   public <K extends Enum<K>> EnumMapDuplicateEntryException(K key) {
      //@formatter:off
      super(
         new StringBuilder()
            .append( "Attempt to add entry to map with key that has already been used." ).append( "\n" )
            .append( "   Enumeration Key: " ).append( key.name() ).append( "\n" )
            .toString()
         );
      //@formatter:on
   }

}

/* EOF */