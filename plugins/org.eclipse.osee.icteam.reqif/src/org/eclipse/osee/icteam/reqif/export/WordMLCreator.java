/*********************************************************************
 * Copyright (c) 2021 Robert Bosch Engineering and Business Solutions Ltd India
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Robert Bosch Engineering and Business Solutions Ltd India - initial API and implementation
 **********************************************************************/
package org.eclipse.osee.icteam.reqif.export;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Class would create WordML formats by reading each WordML
 * 
 * @author Manjunath Sangappa
 */
public class WordMLCreator

{

   private static String templateStart = "<w:p>";
   private static String templateEnd = "</w:p>";
   private static String template = "<w:r><w:t></w:t></w:r>";
   private static final String START_RUN = "<w:r>";
   private static final String BOLD_RUN = "<w:rPr><w:b/></w:rPr>";
   private static final String ITALIC_RUN = "<w:rPr><w:i/></w:rPr>";
   private static final String BOLD_ITALIC_RUN = "<w:rPr><w:b/><w:i/></w:rPr>";
   private static final String START_TEXT = "<w:t>";


   /**
    * 
    */
   public WordMLCreator() {

   }


   public StringBuilder createWordML(List<WordMLContent> listWordMlContent)
   {
      StringBuilder bufferTemp = new StringBuilder();
      try{

         bufferTemp.append(templateStart);
         ByteArrayInputStream in;

         in = new ByteArrayInputStream(template.getBytes("UTF-8"));


         for (WordMLContent wordMLContent : listWordMlContent) {
            int c;
            in.reset();
            while ((c = in.read()) != -1) {
               bufferTemp.append((char) c);
               if ((char) c == '>') {
                  String temp = bufferTemp.toString();
                  if (temp.endsWith(START_RUN)) {
                     if (wordMLContent.isBold() && wordMLContent.isItalic()) {
                        bufferTemp.append(BOLD_ITALIC_RUN);
                     }
                     else if (wordMLContent.isBold()) {
                        bufferTemp.append(BOLD_RUN);
                     }
                     else if (wordMLContent.isItalic()) {
                        bufferTemp.append(ITALIC_RUN);
                     }
                  }
                  if (temp.endsWith(START_TEXT)) {
                     String inputString = wordMLContent.getInputString();
                     if(inputString!=null && !inputString.isEmpty() && inputString.contains("&"))
                        inputString = inputString.replace("&", "&amp;");
                     bufferTemp.append(inputString);
                  }
               }

            }


         }
         bufferTemp.append(templateEnd);
      } catch (UnsupportedEncodingException e) {
         e.printStackTrace();
      }
      return bufferTemp;
   }

}