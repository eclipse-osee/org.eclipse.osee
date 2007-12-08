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
package org.eclipse.osee.framework.jdk.core.text.change;

/**
 * @author Ryan D. Brooks
 */
public class SimpleCharSequence implements CharSequence {
   private char[] chars;
   private int startIndex;
   private int endIndex;

   /**
    * 
    */
   public SimpleCharSequence(char[] chars, int startIndex, int endIndex) {
      super();
      this.chars = chars;
      this.startIndex = startIndex;
      this.endIndex = endIndex;
   }

   public SimpleCharSequence(char[] chars) {
      this(chars, 0, chars.length);
   }

   /* (non-Javadoc)
    * @see java.lang.CharSequence#length()
    */
   public int length() {
      return endIndex - startIndex;
   }

   /* (non-Javadoc)
    * @see java.lang.CharSequence#charAt(int)
    */
   public char charAt(int index) {
      return chars[index + startIndex];
   }

   /* (non-Javadoc)
    * @see java.lang.CharSequence#subSequence(int, int)
    */
   public CharSequence subSequence(int start, int end) {
      return new SimpleCharSequence(chars, start + startIndex, end + startIndex);
   }

   public String toString() {
      return new String(chars, startIndex, endIndex - startIndex);
   }

   public boolean equals(Object obj) {
      // TODO: implement
      return false;
   }
}
