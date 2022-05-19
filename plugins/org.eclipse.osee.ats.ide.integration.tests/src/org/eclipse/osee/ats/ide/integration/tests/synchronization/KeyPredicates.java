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

package org.eclipse.osee.ats.ide.integration.tests.synchronization;

import java.util.Objects;
import java.util.function.Predicate;
import org.eclipse.osee.framework.jdk.core.util.RankMap;

/**
 * A collection of {@link Predicate} implementations and arrays for use with {@link RankMap} for validating keys.
 *
 * @author Loren K. Ashley
 */

public class KeyPredicates {

   /**
    * Constructor is private to prevent instantiation of the class.
    */

   private KeyPredicates() {
   }

   /**
    * Predicate to determine if the {@link Object} is a {@link String}.
    *
    * @return <code>true</code> when the {@link Object} is a {@link String}; otherwise, <code>false</code>.
    */

   static Predicate<Object> keyIsString = new Predicate<Object>() {

      @Override
      public boolean test(Object key) {
         return Objects.nonNull(key) && (key instanceof String);
      }
   };

   /**
    * An array of one {@link #keyIsString} {@link Predicate} implementations for use with a single rank {@link RankMap}.
    */

   @SuppressWarnings("unchecked")
   static Predicate<Object> keysAreStringsRank1[] = new Predicate[] {keyIsString};

   /**
    * An array of two {@link #keyIsString} {@link Predicate} implementations for use with a double rank {@link RankMap}.
    */

   @SuppressWarnings("unchecked")
   static Predicate<Object> keysAreStringsRank2[] = new Predicate[] {keyIsString, keyIsString};

   /**
    * An array of two {@link #keyIsString} {@link Predicate} implementations for use with a tripple rank
    * {@link RankMap}.
    */

   @SuppressWarnings("unchecked")
   static Predicate<Object> keysAreStringsRank3[] = new Predicate[] {keyIsString, keyIsString, keyIsString};

}

/* EOF */