/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.coverage.util;

/**
 * @author Angel Avila
 */
public class LineData {

   private final String lineText;
   private final boolean isException;
   private final int lineNumber;

   public LineData(String lineText, boolean isException, int lineNumber) {
      this.lineText = lineText;
      this.isException = isException;
      this.lineNumber = lineNumber;
   }

   public String getLineText() {
      return lineText;
   }

   public boolean getIsException() {
      return isException;
   }

   public int getLineNumber() {
      return lineNumber;
   }
}
