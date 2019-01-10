/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.define.rest.importing.parsers;

import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * <p>
 * Used during importing to describe meta properties of <br/>
 * outline paragraph numbers and their surrounding data.
 * </p>
 * <b>NOTE</b>: This is an attempt (an experiment) at a more flexible parsing results of WordML.
 */
public final class RoughArtifactMetaData {

   private static final float CONFIDENCE = 80f;

   /**
    * <p>
    * Decide on some criteria what is a match. <br/>
    * </p>
    * 
    * @return based on CONFIDENCE and if underlying data is lengthwise the same.
    */
   public static boolean matches(String string1, String string2) {
      boolean match = false;

      String oPstyle = normalize(string1);
      String thisPstyle = normalize(string2);

      match = oPstyle.length() == thisPstyle.length();

      if (match) {
         match = confidenceCompare(oPstyle, thisPstyle) >= CONFIDENCE;
      }

      return match;
   }

   private static String normalize(String value) {
      return Strings.isValid(value) ? value.toLowerCase() : Strings.emptyString();
   }

   /**
    * Compares strings and returns percentage of similarity. Assuming two arguments are same length and have been
    * lowerCased.
    * 
    * @return amount of confidence 2 strings are similar
    */
   private static float confidenceCompare(String string1, String string2) {
      float caseConfidence = 0;
      for (int i = 0; i < string1.length(); i++) {
         if (string1.charAt(i) == string2.charAt(i)) {
            caseConfidence = (i + 1) / (float) string1.length() * 100;
         } else {
            break;
         }
      }
      return caseConfidence;
   }
}
