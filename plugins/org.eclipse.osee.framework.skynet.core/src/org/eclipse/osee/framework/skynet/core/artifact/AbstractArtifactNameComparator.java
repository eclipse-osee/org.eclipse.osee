/*******************************************************************************
 * Copyright (c) 2018 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.artifact;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Donald G. Dunne
 */
public abstract class AbstractArtifactNameComparator {
   private static final Pattern numberPattern = Pattern.compile("[+-]?\\d+");
   private final Matcher numberMatcher = numberPattern.matcher("");
   private boolean descending = false;
   private static final int NUMBER_STRING_LIMIT = 19;

   public AbstractArtifactNameComparator(boolean descending) {
      this.descending = descending;
   }

   public int compareNames(String name1, String name2) {
      numberMatcher.reset(name1);
      if (numberMatcher.matches()) {
         numberMatcher.reset(name2);
         if (numberMatcher.matches()) {
            if (name1.length() < NUMBER_STRING_LIMIT && name2.length() < NUMBER_STRING_LIMIT) {
               if (descending) {
                  return Long.valueOf(name2).compareTo(Long.valueOf(name1));
               } else {
                  return Long.valueOf(name1).compareTo(Long.valueOf(name2));
               }
            }
         }
      }
      if (descending) {
         return name2.compareTo(name1);
      } else {
         return name1.compareTo(name2);
      }
   }

}
