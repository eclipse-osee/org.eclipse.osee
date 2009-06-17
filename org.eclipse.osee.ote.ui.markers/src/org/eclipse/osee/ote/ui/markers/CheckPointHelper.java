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
package org.eclipse.osee.ote.ui.markers;

import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.w3c.dom.Element;
  
public class CheckPointHelper implements Comparable<CheckPointHelper> {

      /* (non-Javadoc)
       * @see java.lang.Object#toString()
       */
      @Override
      public String toString() {
         return String.format("%s[%s, %s]", testPointName, expected, actual);
      }

      /**
       * 
       */
      public void increment() {
         count++;
      }

      /* (non-Javadoc)
       * @see java.lang.Object#equals(java.lang.Object)
       */
      @Override
      public boolean equals(Object obj) {
         if (obj instanceof CheckPointHelper) {
            return key.equals(((CheckPointHelper) obj).key);
         }
         return false;
      }

      /* (non-Javadoc)
       * @see java.lang.Object#hashCode()
       */
      @Override
      public int hashCode() {
         return key.hashCode();
      }

      private String testPointName;
      private String expected;
      private String actual;
      private String key;
      private int count = 1;

      public CheckPointHelper(Element el) {
         testPointName = Jaxp.getChildText(el, "TestPointName");
         expected = Jaxp.getChildText(el, "Expected");
         actual = Jaxp.getChildText(el, "Actual");
         key = testPointName + expected + actual;
      }
      
      public CheckPointHelper(CheckPointData data){
         testPointName = data.getName();
         expected = data.getExpected();
         actual = data.getActual();
         key = testPointName + expected + actual;
      }

      /* (non-Javadoc)
       * @see java.lang.Comparable#compareTo(java.lang.Object)
       */
      public int compareTo(CheckPointHelper o) {
         return o.count - this.count;
      }

   }
