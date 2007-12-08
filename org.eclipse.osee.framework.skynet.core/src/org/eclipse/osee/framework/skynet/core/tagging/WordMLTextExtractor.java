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
package org.eclipse.osee.framework.skynet.core.tagging;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.jdk.core.util.xml.Xml;

/**
 * @author Ryan D. Brooks
 */
public class WordMLTextExtractor {
   private static final Pattern textTagP = Pattern.compile("<w:t>(.+)</w:t>");

   /**
    * 
    */
   public WordMLTextExtractor() {
      super();
   }

   public static List<String> extractText(String wordML) {
      List<String> textStrings = new LinkedList<String>();
      Matcher textMatcher = textTagP.matcher(wordML);

      while (textMatcher.find()) {
         textStrings.add(Xml.unescape(textMatcher.group(1)));
      }
      return textStrings;
   }
}
