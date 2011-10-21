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
package org.eclipse.osee.orcs.utility;

import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.orcs.data.ReadableArtifact;

/**
 * @author Ryan D. Brooks
 */
public class ArtifactNameComparator implements Comparator<ReadableArtifact> {
   private static final int NUMBER_STRING_LIMIT = 19;
   private static final Pattern numberPattern = Pattern.compile("[+-]?\\d+");

   private final Matcher numberMatcher = numberPattern.matcher("");
   private SortOrder orderType = SortOrder.ASCENDING;

   public ArtifactNameComparator(SortOrder orderType) {
      this.orderType = orderType;
   }

   @Override
   public int compare(ReadableArtifact artifact1, ReadableArtifact artifact2) {
      String name1 = artifact1.getName();
      String name2 = artifact2.getName();

      numberMatcher.reset(name1);
      if (numberMatcher.matches()) {
         numberMatcher.reset(name2);
         if (numberMatcher.matches()) {
            if ((name1.length() < NUMBER_STRING_LIMIT) && (name2.length() < NUMBER_STRING_LIMIT)) {
               if (orderType.isAscending()) {
                  return Long.valueOf(name1).compareTo(Long.valueOf(name2));
               } else {
                  return Long.valueOf(name2).compareTo(Long.valueOf(name1));
               }
            }
         }
      }
      if (orderType.isAscending()) {
         return name1.compareTo(name2);
      } else {
         return name2.compareTo(name1);
      }
   }
}