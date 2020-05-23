/*********************************************************************
 * Copyright (c) 2009 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.orcs.db.internal.exchange.transform;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.jdk.core.text.Rule;
import org.eclipse.osee.framework.jdk.core.text.change.ChangeSet;
import org.osgi.framework.Version;

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

   public void setVersion(Version version) {
      this.version = version.toString();
   }

   public String getVersion() {
      return version;
   }

   public void setReplaceVersion(boolean replaceVersion) {
      this.replaceVersion = replaceVersion;
   }
}