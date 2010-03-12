/**
 * 
 */
package org.eclipse.osee.framework.skynet.core.artifact;

import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.skynet.core.types.IArtifact;

public class ArtifactNameComparator implements Comparator<IArtifact> {
   private static final Pattern numberPattern = Pattern.compile("[+-]?\\d+");
   private final Matcher numberMatcher = numberPattern.matcher("");
   private boolean descending = false;

   public ArtifactNameComparator() {

   }

   public ArtifactNameComparator(boolean descending) {
      this.descending = descending;
   }

   @Override
   public int compare(IArtifact artifact1, IArtifact artifact2) {
      String name1 = artifact1.getName();
      String name2 = artifact2.getName();

      numberMatcher.reset(name1);
      if (numberMatcher.matches()) {
         numberMatcher.reset(name2);
         if (numberMatcher.matches()) {
            if (descending) {
               return Integer.valueOf(name2).compareTo(Integer.valueOf(name1));
            } else {
               return Integer.valueOf(name1).compareTo(Integer.valueOf(name2));
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