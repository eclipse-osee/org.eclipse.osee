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

/**
 * @author Andrew M. Finkbeiner
 *
 */
public class CheckPointData {

   private boolean isFailed = false;
   private String name;
   private String expected;
   private String actual;
   
   public boolean isFailed() {
      return isFailed;
   }

   public void setFailed(boolean failed) {
      this.isFailed = failed;
   }

   public void setName(String name) {
      this.name = name;
   }

   public void setExpected(String expected) {
      this.expected = expected;
   }

   public void setActual(String actual) {
      this.actual = actual;
   }

   public String getName() {
      return name;
   }

   public String getExpected() {
      return expected;
   }

   public String getActual() {
      return actual;
   }

}
