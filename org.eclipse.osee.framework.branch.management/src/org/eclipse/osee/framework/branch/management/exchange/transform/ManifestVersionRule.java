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

public class ManifestVersionRule extends Rule {
   private static final Pattern exportPattern = Pattern.compile("<export ");
   public static final Pattern versionPattern = Pattern.compile("exportVersion=\"([^\"]+)");

   private String version;
   private boolean replaceVersion;

   public ManifestVersionRule() {
      super(null);
   }

   @Override
   public ChangeSet computeChanges(CharSequence seq) {
      ChangeSet changeSet = new ChangeSet(seq);
      ruleWasApplicable = true;

      Matcher versionMatcher = versionPattern.matcher(seq);
      if (versionMatcher.find()) {
         if (replaceVersion) {
            changeSet.replace(versionMatcher.start(1), versionMatcher.end(1), version);
         } else {
            version = versionMatcher.group(1);
         }
      } else if (replaceVersion) {
         Matcher exportMatcher = exportPattern.matcher(seq);
         while (exportMatcher.find()) {
            changeSet.insertBefore(exportMatcher.end(), "exportVersion=\"" + version + "\" ");
         }
      }

      return changeSet;
   }

   public void setVersion(String version) {
      this.version = version;
   }

   public String getVersion() {
      return version;
   }

   public void setReplaceVersion(boolean replaceVersion) {
      this.replaceVersion = replaceVersion;
   }
}