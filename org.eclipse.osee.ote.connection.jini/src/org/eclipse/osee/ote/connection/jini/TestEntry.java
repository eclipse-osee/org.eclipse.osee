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
package org.eclipse.osee.ote.connection.jini;

import net.jini.entry.AbstractEntry;

/**
 * @author Ken J. Aguilar
 */
public class TestEntry extends AbstractEntry {
   /**
    * 
    */
   private static final long serialVersionUID = -2239353039479522642L;
   public final String data;

   public TestEntry() {
      data = "<none>";
   }

   /**
    * @param data
    */
   public TestEntry(String data) {
      super();
      this.data = data;
   }

   /**
    * @return the data
    */
   public String getData() {
      return data;
   }

}
