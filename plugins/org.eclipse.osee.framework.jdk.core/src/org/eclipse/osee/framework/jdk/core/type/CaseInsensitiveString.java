/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.osee.framework.jdk.core.type;

/**
 * @author Ryan D. Brooks
 */
public class CaseInsensitiveString implements CharSequence, Comparable<CaseInsensitiveString> {
   private final String originalString;
   private final String upperCaseString;

   public CaseInsensitiveString(String string) {
      this.originalString = string;
      upperCaseString = string.toUpperCase();
   }

   @Override
   public String toString() {
      return originalString;
   }

   @Override
   public int hashCode() {
      return upperCaseString == null ? 0 : upperCaseString.hashCode();
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof CaseInsensitiveString) {
         CaseInsensitiveString other = (CaseInsensitiveString) obj;
         if (upperCaseString == null) {
            return other.upperCaseString == null;
         } else {
            return upperCaseString.equals(other.upperCaseString);
         }
      } else if (obj instanceof String) {
         return upperCaseString.equalsIgnoreCase((String) obj);
      }
      return false;
   }

   @Override
   public int length() {
      return originalString == null ? 0 : originalString.length();
   }

   @Override
   public char charAt(int index) {
      return originalString.charAt(index);
   }

   @Override
   public CharSequence subSequence(int beginIndex, int endIndex) {
      return originalString.subSequence(beginIndex, endIndex);
   }

   @Override
   public int compareTo(CaseInsensitiveString other) {
      return this.originalString.compareTo(other.originalString);
   }
}