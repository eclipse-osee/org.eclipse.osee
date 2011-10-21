/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.display.api.data;

/**
 * @author Roberto E. Escobar
 */
public class StyledText {

   private final String data;
   private final boolean isHighLighted;

   public StyledText(String data, boolean isHighLighted) {
      super();
      this.data = data;
      this.isHighLighted = isHighLighted;
   }

   public String getData() {
      return data;
   }

   public boolean isHighLighted() {
      return isHighLighted;
   }

   @Override
   public String toString() {
      return "StyledText [data=" + data + ", isHighLighted=" + isHighLighted + "]";
   }

}
