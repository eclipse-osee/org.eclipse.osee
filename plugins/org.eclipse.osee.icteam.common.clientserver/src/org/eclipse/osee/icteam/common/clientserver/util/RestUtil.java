/*********************************************************************
 * Copyright (c) 2020 Robert Bosch Engineering and Business Solutions Ltd India
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
package org.eclipse.osee.icteam.common.clientserver.util;

/**
 * Restutil class
 * 
 * @author Ajay Chandrahasan
 */
public class RestUtil {

   /**
    * @param xml
    * @return currentState by replacing a regex with empty string
    */
   public static String getCurrentState(final String xml) {
      String currentState = xml.replaceFirst(";.*$", "");
      return currentState;
   }

   /**
    * @param xml
    * @return total hours in String format
    */
   public static String getTotalHoursString(final String xml) {
      String ret = "";
      if (xml.length() > 0) {
         String[] split = xml.split(";");
         if (split.length >= 3) {
            ret = split[2];
         }
      }
      return ret;
   }

}
