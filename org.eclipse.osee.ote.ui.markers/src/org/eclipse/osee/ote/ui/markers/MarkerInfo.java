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

public class MarkerInfo {
   private String file;
   private int line;
   private String message;

   public MarkerInfo(String file, int line, String message) {
      this.file = file;
      this.line = line;
      this.message = message;
   }

   /**
    * @return the file
    */
   public String getFile() {
      return file;
   }

   /**
    * @return the line
    */
   public int getLine() {
      return line;
   }

   /**
    * @return the message
    */
   public String getMessage() {
      return message;
   }
}
