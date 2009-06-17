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
public class StacktraceData {

   private String line;
   private String source;
   
   /**
    * @param name
    */
   StacktraceData() {
   }

   /**
    * @param value
    * @param value2
    */
   public StacktraceData(String line, String source) {
      this.line = line;
      this.source = source;
   }

   /**
    * @return the line
    */
   public String getLine() {
      return line;
   }

   /**
    * @return the source
    */
   public String getSource() {
      return source;
   }

}
