/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.display.presenter;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.display.api.data.StyledText;
import org.eclipse.osee.framework.jdk.core.type.MatchLocation;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.utility.NameComparator;
import org.eclipse.osee.orcs.utility.SortOrder;
import com.google.common.collect.Ordering;

/**
 * @author Roberto E. Escobar
 */
public final class Utility {

   private Utility() {
      //
   }

   public static List<ArtifactReadable> sort(Iterable<ArtifactReadable> toSort) {
      return Ordering.from(new NameComparator(SortOrder.ASCENDING)).sortedCopy(toSort);
   }

   public static List<StyledText> getMatchedText(String data, List<MatchLocation> matches) {
      List<StyledText> text = new ArrayList<StyledText>();
      if (matches.isEmpty()) {
         text.add(new StyledText(data, false));
      } else {
         int lastStop = 0;
         for (MatchLocation location : matches) {
            int start = location.getStartPosition();
            int stop = location.getEndPosition();
            String section;
            if (start > lastStop) {
               section = data.substring(lastStop, start - 1);
               text.add(new StyledText(section, false));
            }

            int startAt = start;
            if (startAt - 1 < 0) {
               startAt = 0;
            } else {
               startAt = start - 1;
            }
            section = data.substring(startAt, stop);
            text.add(new StyledText(section, true));
            lastStop = location.getEndPosition();
         }
         if (lastStop < data.length()) {
            String section = data.substring(lastStop, data.length());
            text.add(new StyledText(section, false));
         }
      }
      return text;
   }
}
