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
public class OteLogData {

   private String level;
   private String logger;
   
   /**
    * @param string2 
    * @param string 
    * @param name
    */
   OteLogData(String level, String logger) {
      this.level = level;
      this.logger = logger;
   }

   /**
    * @return the level
    */
   public String getLevel() {
      return level;
   }

   /**
    * @return the logger
    */
   public String getLogger() {
      return logger;
   }
}
