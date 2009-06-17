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
package org.eclipse.osee.ote.core.framework.saxparse.elements;


/**
 * @author Andrew M. Finkbeiner
 *
 */
public class InfoGroupData {

   private String title;
   /**
    * @param title 
    * @param name
    */
   InfoGroupData(String title) {
      this.title = title;
   }
   /**
    * @return the title
    */
   public String getTitle() {
      return title;
   }
}
