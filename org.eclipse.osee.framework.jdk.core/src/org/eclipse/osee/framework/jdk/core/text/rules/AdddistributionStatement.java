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
package org.eclipse.osee.framework.jdk.core.text.rules;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.osee.framework.jdk.core.text.Rule;
import org.eclipse.osee.framework.jdk.core.text.change.ChangeSet;

/**
 * @author Ryan D. Brooks
 */
public class AdddistributionStatement extends Rule {
   private static final Pattern classDeclarationP =
         Pattern.compile("^[/\\s\\*]*((?:Created on [^\n]+)?[\\s\\*]*(?:PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE)?[/\\s\\*]*)package");
   private static final char[] distributionStatement =
         "/*******************************************************************************\n * Copyright (c) 2004, 2007 Boeing.\n * All rights reserved. This program and the accompanying materials\n * are made available under the terms of the Eclipse Public License v1.0\n * which accompanies this distribution, and is available at\n * http://www.eclipse.org/legal/epl-v10.html\n *\n * Contributors:\n *     Boeing - initial API and implementation\n *******************************************************************************/\n".toCharArray();

   public AdddistributionStatement() {
      super("done");
      setFileNamePattern(Pattern.compile(".*\\.java"));
   }

   public ChangeSet computeChanges(CharSequence seq) {
      ChangeSet changeSet = new ChangeSet(seq);

      Matcher matcher = classDeclarationP.matcher(seq);
      if (matcher.find()) {
         setRuleWasApplicable(true);
         changeSet.replace(0, matcher.end(1), distributionStatement);
      }

      return changeSet;
   }
}