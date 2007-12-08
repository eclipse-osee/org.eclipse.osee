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

import java.io.File;
import java.io.IOException;
import org.eclipse.osee.framework.jdk.core.text.Rule;
import org.eclipse.osee.framework.jdk.core.text.change.ChangeSet;

/**
 * @author Ryan D. Brooks
 */
public class HtmlTableBuilder extends Rule {

   public HtmlTableBuilder() {
      super("html");
   }

   public ChangeSet computeChanges(CharSequence seq) {
      ChangeSet changeSet = new ChangeSet(seq);

      changeSet.insertBefore(0, "<html>\n<body>\n<table cellpadding=2 cellspacing=0 border=1>\n".toCharArray());

      char[] rowStartChars = {'\t', '<', 't', 'r', '>', '\n'};
      char[] cellStartChars = "\t\t<td>".toCharArray();
      char[] cellEndChars = "</td>\n".toCharArray();
      char[] rowEndChars = {'\t', '<', '/', 't', 'r', '>', '\n'};

      int lineStart = 0;
      int lineEnd = 0;
      int length = seq.length();
      for (int i = 0; i < length; i++) {
         if (seq.charAt(i) == '\n') { /* find the lineEnd of a line */
            lineEnd = i + 1;
            changeSet.insertBefore(lineStart, rowStartChars);

            int tabStart = lineStart;
            for (int k = lineStart; k < lineEnd; k++) {
               if (seq.charAt(k) == '\t' || k == i) { // if tab or end of line
                  changeSet.insertBefore(tabStart, cellStartChars);
                  tabStart = k + 1;
                  changeSet.replace(k, tabStart, cellEndChars);
               }
            }
            changeSet.insertBefore(lineEnd, rowEndChars);
            lineStart = lineEnd;
         }
      }
      changeSet.insertBefore(length, "</table>\n</body>\n</html>".toCharArray());

      return changeSet;
   }

   public static void main(String[] args) throws IOException {
      StringBuffer buffer = new StringBuffer();
      buffer.append(System.getProperty("user.home"));
      buffer.append(File.separator);
      buffer.append(HtmlTableBuilder.class.getName());
      buffer.append(".table.txt");
      new HtmlTableBuilder().process(new File(buffer.toString()));
   }
}
