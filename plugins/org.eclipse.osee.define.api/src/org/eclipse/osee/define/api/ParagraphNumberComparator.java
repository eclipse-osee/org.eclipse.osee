/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.define.api;

import java.util.Comparator;
import org.eclipse.osee.activity.api.ActivityLog;
import org.eclipse.osee.framework.core.data.CoreActivityTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Roberto E. Escobar
 * @author Ryan D. Brooks
 */
public final class ParagraphNumberComparator implements Comparator<ArtifactReadable> {

   private final ActivityLog activityLog;

   public ParagraphNumberComparator(ActivityLog activityLog) {
      this.activityLog = activityLog;
   }

   @Override
   public int compare(ArtifactReadable art1, ArtifactReadable art2) {
      try {
         int toReturn = 0;
         String paragraph1 = art1.getSoleAttributeValue(CoreAttributeTypes.ParagraphNumber, "");
         String paragraph2 = art2.getSoleAttributeValue(CoreAttributeTypes.ParagraphNumber, "");
         int[] set1 = getParagraphIndices(paragraph1);
         int[] set2 = getParagraphIndices(paragraph2);
         int length1 = set1.length;
         int length2 = set2.length;

         int size = length1 < length2 ? length1 : length2;
         if (size == 0 && length1 != length2) {
            toReturn = length1 < length2 ? -1 : 1;
         } else {
            for (int index = 0; index < size; index++) {
               toReturn = Integer.compare(set1[index], set2[index]);
               if (toReturn != 0) {
                  break;
               }
            }
         }
         return toReturn;
      } catch (Exception ex) {
         activityLog.createThrowableEntry(CoreActivityTypes.OSEE_ERROR, ex);
      }
      return 1;
   }

   private int[] getParagraphIndices(String paragraph) {
      int[] paragraphs;
      if (Strings.isValid(paragraph)) {
         String[] values = paragraph.split("\\.");
         paragraphs = new int[values.length];
         for (int index = 0; index < values.length; index++) {
            paragraphs[index] = Integer.parseInt(values[index].replace("-", ""));
         }
      } else {
         paragraphs = new int[0];
      }
      return paragraphs;
   }
}