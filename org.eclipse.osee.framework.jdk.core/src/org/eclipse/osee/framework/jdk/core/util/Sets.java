/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.jdk.core.util;

import java.util.HashSet;
import java.util.Set;

/**
 * Utility methods for <code>Set</code>'s.
 * 
 * @author Robert A. Fisher
 */
public class Sets {

   /**
    * Compute the intersection of two sets, and return the resulting set.
    * 
    * @throws IllegalArgumentException if either argument is null.
    */
   public static <A extends Object> Set<A> intersect(Set<A> set1, Set<A> set2) {
      if (set1 == null) throw new IllegalArgumentException("set1 can not be null.");
      if (set2 == null) throw new IllegalArgumentException("set2 can not be null.");

      Set<A> intersection = new HashSet<A>();

      // Pick the smaller of the two sets as this will be the largest
      // possible intersection.
      boolean set1Larger = set1.size() > set2.size();
      Set<A> baseSet = set1Larger ? set2 : set1;
      Set<A> otherSet = set1Larger ? set1 : set2;

      for (A item : baseSet)
         if (otherSet.contains(item)) intersection.add(item);

      return intersection;
   }
}
