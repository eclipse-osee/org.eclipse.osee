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

public class V0_8_3_DbSchemaRule extends Rule {
   private static final Pattern typeTablePattern =
         Pattern.compile("\\s+<table name=\"osee_\\w+_type\".*?</table>", Pattern.DOTALL);

   private static final Pattern branchTablePattern = Pattern.compile("<table name=\"osee_branch\" >\\s+");

   private static final Pattern txsTablePattern = Pattern.compile("<table name=\"osee_txs\" >\\s+");

   public V0_8_3_DbSchemaRule() {
      super(null);
   }

   @Override
   public ChangeSet computeChanges(CharSequence seq) {
      ChangeSet changeSet = new ChangeSet(seq);
      ruleWasApplicable = true;
      Matcher typeMatcher = typeTablePattern.matcher(seq);
      while (typeMatcher.find()) {
         changeSet.delete(typeMatcher.start(), typeMatcher.end());
      }

      Matcher branchMatcher = branchTablePattern.matcher(seq);
      while (branchMatcher.find()) {
         changeSet.insertBefore(branchMatcher.end(),
               "<column id=\"branch_guid\" type=\"VARCHAR\" />\n<column id=\"branch_state\" type=\"INTEGER\" />\n");
      }

      Matcher txsMatcher = txsTablePattern.matcher(seq);
      while (txsMatcher.find()) {
         changeSet.insertBefore(txsMatcher.end(), "<column id=\"branch_id\" type=\"INTEGER\" />\n");
      }

      return changeSet;
   }
}