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
//@formatter:off
package org.eclipse.osee.framework.ui.skynet.render.dsl;

import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;


/**
 * @author Theron Virgin
 * @author Megumi Telles
 */
public abstract class AbstractDslWordMlCreator {

   private static final String[] XML_CHARS = new String[] {"&", "<", ">", "\""};
   private static final String[] XML_ESCAPES = new String[] {"&amp;", "&lt;", "&gt;", "&quot;"};
   public static String emptyParagraph = "<w:p/>";
   public static String paragraphEnd = "</w:t></w:r></w:p>";
   public static String paragraphStart =
         "<w:p><w:pPr><w:pStyle w:val=\"reqlang1\"/><w:spacing w:before=\"0\" w:after=\"0\"/></w:pPr><w:r><w:t>";
   private static final String[] TAB_ARRAY =
         new String[] {"\t\t\t\t\t\t\t\t", "\t\t\t\t\t\t\t", "\t\t\t\t\t\t", "\t\t\t\t\t", "\t\t\t\t",
               "\t\t\t", "\t\t", "\t"};
   private static final String[] TAB_STYLE_ARRAY =
         new String[] {"w:val=\"reqlang9\"", "w:val=\"reqlang8\"", "w:val=\"reqlang7\"", "w:val=\"reqlang6\"",
               "w:val=\"reqlang5\"", "w:val=\"reqlang4\"", "w:val=\"reqlang3\"", "w:val=\"reqlang2\""};

   public String getWordMlFromAttribute(String attribute, PresentationType presentationType) {
      String toReturn = "";
      toReturn = emptyParagraph + getIfWordMl(attribute, true);
      return toReturn;
   }

   public String getIfWordMl(String attribute, boolean addEndParagraph) {
      String wordml = attribute;
      wordml = escape(wordml).toString();
      wordml = wordml.replace("\r\n", "\r");
      wordml = wordml.replace("\n\r", "\r");
      wordml = wordml.replace("\n", "\r");
      wordml = replaceTabs(wordml);
      wordml = wordml.replace("\r", paragraphEnd + paragraphStart);
      wordml = paragraphStart + wordml;
      wordml = wordml + paragraphEnd;

      if (addEndParagraph) {
         wordml = wordml.concat("<w:p></w:p>");
      }
      return wordml;
   }

   public String replaceTabs(String wordml) {
      for (int x = 0; x < TAB_ARRAY.length; x++) {
         wordml =
            wordml.replace(TAB_ARRAY[x], (paragraphEnd + paragraphStart).replace("w:val=\"reqlang1\"", TAB_STYLE_ARRAY[x]));
      }
      return wordml;
   }

   public CharSequence escape(CharSequence text) {
      String textString = text.toString();
      for (int x = 0; x < XML_CHARS.length; x++) {
         textString = textString.replaceAll(XML_CHARS[x], XML_ESCAPES[x]);
      }

      return textString;
   }

   public abstract boolean isArtifactAttribute(Attribute<?> attribute);

}
