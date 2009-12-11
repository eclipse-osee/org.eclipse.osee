/*
 * Created on Nov 30, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.branch.management.exchange.transform;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.jdk.core.text.Rule;
import org.eclipse.osee.framework.jdk.core.text.change.ChangeSet;

public class V0_9_0_ManifestRule extends Rule {
   private static final Pattern typeEntryPattern =
         Pattern.compile("<entry id=\"osee\\.((branch\\.definitions)|(.*?.data.type))\\.xml.*\\s+");

   //<entry id="osee.branch.definitions.xml" priority="2" source="osee_branch_definitions"  />
   public V0_9_0_ManifestRule() {
      super(null);
   }

   @Override
   public ChangeSet computeChanges(CharSequence seq) {
      ChangeSet changeSet = new ChangeSet(seq);
      ruleWasApplicable = true;

      Matcher typeMatcher = typeEntryPattern.matcher(seq);
      while (typeMatcher.find()) {
         changeSet.delete(typeMatcher.start(), typeMatcher.end());
      }

      return changeSet;
   }
}