/**
 * 
 */
package org.eclipse.osee.framework.ui.skynet.artifact;

import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

public class ArtifactNameComparator implements Comparator<Artifact> {
   private static final Pattern numberPattern = Pattern.compile("[+-]?\\d+");
   private final Matcher numberMatcher = numberPattern.matcher("");

   @Override
   public int compare(Artifact artifact1, Artifact artifact2) {
      String name1 = artifact1.getName();
      String name2 = artifact2.getName();

      numberMatcher.reset(name1);
      if (numberMatcher.matches()) {
         numberMatcher.reset(name2);
         if (numberMatcher.matches()) {
            return Integer.valueOf(name1).compareTo(Integer.valueOf(name2));
         }
      }
      return name1.compareTo(name2);
   }
}