/*
 * Created on Nov 30, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.branch.management.exchange.transform;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.branch.management.exchange.handler.BranchData;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.jdk.core.text.Rule;
import org.eclipse.osee.framework.jdk.core.text.change.ChangeSet;
import org.eclipse.osee.framework.jdk.core.util.GUID;

public class V0_8_3_BranchRule extends Rule {
   private static final Pattern branchPattern =
         Pattern.compile("<entry ()branch_type=\"(\\d+)\" .*\\s+<branch_name>(.*?)</branch_name>");

   public V0_8_3_BranchRule() {
      super(null);
   }

   @Override
   public ChangeSet computeChanges(CharSequence seq) {
      ChangeSet changeSet = new ChangeSet(seq);

      Matcher branchMatcher = branchPattern.matcher(seq);
      while (branchMatcher.find()) {
         if (branchMatcher.group(2).equals("1")) {
            changeSet.replace(branchMatcher.start(2), branchMatcher.end(2), '2');
         }

         String guid = GUID.create();
         if (branchMatcher.group(3).equals(CoreBranches.SYSTEM_ROOT.getName())) {
            guid = CoreBranches.SYSTEM_ROOT.getGuid();
         } else if (branchMatcher.group(3).equals(CoreBranches.COMMON.getName())) {
            guid = CoreBranches.COMMON.getGuid();
         }
         changeSet.insertBefore(branchMatcher.end(1), BranchData.BRANCH_GUID + "=\"" + guid + "\" branch_state=\"-1\" ");
         ruleWasApplicable = true;
      }

      return changeSet;
   }
}