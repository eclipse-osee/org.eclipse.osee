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
import org.eclipse.osee.framework.jdk.core.util.GUID;

public class LegacyBranchRule extends Rule {
   private static final Pattern branchPattern = Pattern.compile("<entry ()branch_type");

   public LegacyBranchRule() {
      super(null);
   }

   @Override
   public ChangeSet computeChanges(CharSequence seq) {
      ChangeSet changeSet = new ChangeSet(seq);

      Matcher branchMatcher = branchPattern.matcher(seq);
      while (branchMatcher.find()) {
         changeSet.insertBefore(branchMatcher.end(1), "branch_guid=\"" + GUID.create() + "\" branch_state=\"2\" ");
         ruleWasApplicable = true;
      }

      return changeSet;
   }
}