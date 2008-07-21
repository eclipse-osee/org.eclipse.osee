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
package org.eclipse.osee.framework.search.engine;

import java.util.Properties;

/**
 * @author Roberto E. Escobar
 */
public class Options {

   private Properties properties;

   public Options() {
      this.properties = new Properties();
   }

   public boolean getBoolean(String key) {
      return new Boolean(getString(key));
   }

   public String getString(String key) {
      return this.properties.getProperty(key, "");
   }

   public void put(String key, String value) {
      if (value != null && value.length() > 0) {
         this.properties.put(key, value);
      }
   }

   public void put(String key, boolean value) {
      this.properties.put(key, Boolean.toString(value));
   }

   /* (non-Javadoc)
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString() {
      return properties.toString();
   }
}
