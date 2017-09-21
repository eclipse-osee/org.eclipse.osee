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

import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.jdk.core.type.Named;

public class NamedComparator implements Comparator<Named> {
   private static final int NUMBER_STRING_LIMIT = 19;
   private static final Pattern numberPattern = Pattern.compile("[+-]?\\d+");

   private final Matcher numberMatcher = numberPattern.matcher("");
   private final SortOrder orderType;

   public NamedComparator(SortOrder orderType) {
      this.orderType = orderType;
   }

   private String getName(Named name) {
      String nameString = name != null ? name.getName() : "";
      return nameString != null ? nameString : "";
   }

   @Override
   public int compare(Named o1, Named o2) {
      String name1 = getName(o1);
      String name2 = getName(o2);

      if (areNumbers(name1, name2)) {
         if (name1.length() < NUMBER_STRING_LIMIT && name2.length() < NUMBER_STRING_LIMIT) {
            if (orderType.isAscending()) {
               return Long.valueOf(name1).compareTo(Long.valueOf(name2));
            } else {
               return Long.valueOf(name2).compareTo(Long.valueOf(name1));
            }
         }
      }
      if (orderType.isAscending()) {
         return name1.compareTo(name2);
      } else {
         return name2.compareTo(name1);
      }
   }

   private boolean areNumbers(String o1, String o2) {
      boolean result = false;
      if (o1 != null && o2 != null) {
         numberMatcher.reset(o1);
         if (numberMatcher.matches()) {
            numberMatcher.reset(o2);
            if (numberMatcher.matches()) {
               result = true;
            }
         }
      }
      return result;
   }
}