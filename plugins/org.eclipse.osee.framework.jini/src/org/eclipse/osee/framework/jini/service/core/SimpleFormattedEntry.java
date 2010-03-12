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
package org.eclipse.osee.framework.jini.service.core;

/**
 * @author Robert A. Fisher
 */
public class SimpleFormattedEntry extends FormmatedEntry {
   private static final long serialVersionUID = -4511921240754213614L;
   public String name;
   public String value;

   /**
    * 
    */
   public SimpleFormattedEntry() {
      super();
   }

   /**
    * @param name
    * @param value
    */
   public SimpleFormattedEntry(String name, String value) {
      this.name = name;
      this.value = value;
   }

   public final String getFormmatedString() {
      return name + " : " + value + "\n";
   }
}
