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
public class MultilineStrLiteral extends Rule {
   public static final Pattern wrappedStrLiteralP = Pattern.compile("\n[^\"\n]+(\"[ \t]*\n)[^\"]*?\n(\\s*\")");
   public static final Pattern signleLineP = Pattern.compile("[^\n]*\n");

   public MultilineStrLiteral() {
      super(null); // don't change extension on resulting file (i.e. overwrite the original file)
   }

   public ChangeSet computeChanges(CharSequence seq) {
      Matcher signleLineM = signleLineP.matcher("");
      Matcher wrappedStrLiteralM = wrappedStrLiteralP.matcher(seq);
      ChangeSet changeSet = new ChangeSet(seq);
      char[] terminateLineChars = new char[] {'\\', 'n', '\"'};

      while (wrappedStrLiteralM.find()) {
         ruleWasApplicable = true;
         // delete the first quote and following white-space up to and including the first new line
         changeSet.delete(wrappedStrLiteralM.start(1), wrappedStrLiteralM.end(1));

         int subIndex = wrappedStrLiteralM.end(1);
         signleLineM.reset(seq.subSequence(subIndex, wrappedStrLiteralM.start(2)));
         while (signleLineM.find()) {
            changeSet.insertBefore(subIndex + signleLineM.start(0), '\"');
            changeSet.insertBefore(subIndex + signleLineM.end(0) - 1, terminateLineChars);
         }
         changeSet.delete(wrappedStrLiteralM.start(2) - 1, wrappedStrLiteralM.end(2));
      }
      return changeSet;
   }
}