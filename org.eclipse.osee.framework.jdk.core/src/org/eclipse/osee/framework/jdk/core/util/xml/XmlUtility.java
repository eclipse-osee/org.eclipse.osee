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
package org.eclipse.osee.framework.jdk.core.util.xml;

/**
 * @author Paul K. Waldfogel
 */
public class XmlUtility {

   public static String removeNotUTF8Characters(String contentString) {
      String resultString = contentString;
      String[][] nonUTF8CharactersOfInterest = { {"–", "-"}, {"’", "'"}, {"’", "'"}, {"“", "\""}, {"”", "\""}};//Wider than usual dash , smaller than usual bullet
      for (int i = 0; i < nonUTF8CharactersOfInterest.length; i++) {
         String[] splitsOfNonUTF8 = resultString.split(nonUTF8CharactersOfInterest[i][0]);//Wider than usual dash or bullet
         if (splitsOfNonUTF8.length > 1) {
            StringBuffer myStringBuffer = new StringBuffer();
            for (int j = 0; j < splitsOfNonUTF8.length; j++) {
               myStringBuffer.append(splitsOfNonUTF8[j]);
               if (splitsOfNonUTF8[j].length() > 0 && j < splitsOfNonUTF8.length - 1) {
                  myStringBuffer.append(nonUTF8CharactersOfInterest[i][1]);
               }
            }
            resultString = myStringBuffer.toString();
         }
      }
      String[] splits = resultString.split("[^\\p{Space}\\p{Graph}]");
      int stringPosition = 0;
      if (splits.length > 1) {
         StringBuffer myStringBuffer = new StringBuffer();
         for (int i = 0; i < splits.length; i++) {
            stringPosition = stringPosition + splits[i].length();
            myStringBuffer.append(splits[i]);
            stringPosition = stringPosition + 1;
            if (splits[i].length() > 0 && i < splits.length - 1) {
               myStringBuffer.append("-");
            }
         }
         resultString = myStringBuffer.toString();
      }

      return resultString;
   }
}