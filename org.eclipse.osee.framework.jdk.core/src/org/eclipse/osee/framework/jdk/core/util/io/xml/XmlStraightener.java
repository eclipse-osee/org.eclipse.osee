/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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

   /**
    * @param outExtension
    */
   public XmlStraightener(String outExtension) {
      super(outExtension);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.jdk.core.text.Rule#computeChanges(java.lang.CharSequence)
    */
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