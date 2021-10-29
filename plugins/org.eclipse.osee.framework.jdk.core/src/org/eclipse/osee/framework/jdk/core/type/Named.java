/*********************************************************************
 * Copyright (c) 2009 Boeing
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

package org.eclipse.osee.framework.jdk.core.type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author Andrew M. Finkbeiner
 */
public interface Named extends Comparable<Named> {
   public static final String SENTINEL = "Sentinel";

   default String getName() {
      return toString();
   }

   @Override
   default int compareTo(Named other) {
      if (other != null && other.getName() != null && getName() != null) {
         return getName().compareTo(other.getName());
      }
      return -1;
   }

   public static List<String> getNames(Collection<? extends Named> namedObjects) {
      return namedObjects.stream().map(Named::getName).collect(Collectors.toCollection(ArrayList::new));
   }

   public static <T extends Named> List<String> getNames(Collection<T> namedObjects, Predicate<T> predicate) {
      return namedObjects.stream().filter(predicate).map(Named::getName).collect(
         Collectors.toCollection(ArrayList::new));
   }

   public static List<String> getNames(Named... namedObjects) {
      return getNames(Arrays.asList(namedObjects));
   }

   public static String[] getNamesArray(Named... namedObjects) {
      String[] names = new String[namedObjects.length];
      for (int i = 0; i < names.length; i++) {
         names[i] = namedObjects[i].getName();
      }
      return names;
   }

   public static String[] getNamesArray(Collection<? extends Named> namedObjects) {
      String[] names = new String[namedObjects.size()];
      int i = 0;
      for (Named namedObject : namedObjects) {
         names[i++] = namedObject.getName();
      }
      return names;
   }
}