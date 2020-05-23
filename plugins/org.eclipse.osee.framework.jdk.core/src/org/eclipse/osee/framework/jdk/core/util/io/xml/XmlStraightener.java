/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.jdk.core.util.io.xml;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.jdk.core.text.Rule;
import org.eclipse.osee.framework.jdk.core.text.change.ChangeSet;
import org.eclipse.osee.framework.jdk.core.util.Lib;

/**
 * @author Ryan D. Brooks
 */
public class XmlStraightener extends Rule {
   private static final Pattern tagPattern = Pattern.compile("<[^!][^>]*>");

   public XmlStraightener(String outExtension) {
      super(outExtension);
   }

   @Override
   public ChangeSet computeChanges(CharSequence seq) {
      ChangeSet changeSet = new ChangeSet(seq);

      Matcher tagMatcher = tagPattern.matcher(seq);
      while (tagMatcher.find()) {
         changeSet.insertBefore(tagMatcher.end(), Lib.lineSeparator);
         ruleWasApplicable = true;
      }

      return changeSet;
   }
}