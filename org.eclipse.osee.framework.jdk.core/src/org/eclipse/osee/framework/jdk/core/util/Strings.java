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

package org.eclipse.osee.framework.jdk.core.util;

/**
 * @author Jeff C. Phillips
 */
public class Strings {

   /**
    * Return string truncated if size > length, otherwise return string
    * 
    * @param string
    * @param length
    * @return
    */
   public static String truncate(String string, int length) {

      if (string.length() > length) {
         return string.substring(0, length);
      }

      return string;
   }

   public static boolean isValid(String value) {
      return value != null && value.length() > 0;
   }
}
