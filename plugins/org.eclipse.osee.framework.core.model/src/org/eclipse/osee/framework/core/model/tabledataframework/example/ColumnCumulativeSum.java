/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.model.tabledataframework.example;

import org.eclipse.osee.framework.core.model.tabledataframework.ColumnAbstract;

/**
 * @author Shawn F. Cook
 */
public class ColumnCumulativeSum extends ColumnAbstract {
   private static final String HEADER_STR = "Cumulative Sum";
   private final KeyColumn_1to10 keyCol1to10;
   private int sum;

   //Column constructors have explicit dependencies in their parameter list.
   // This makes for simplified dependency documentation.
   public ColumnCumulativeSum(KeyColumn_1to10 keyCol1to10, boolean isVisible) {
      super(HEADER_STR, isVisible);
      this.keyCol1to10 = keyCol1to10;
      sum = 0;
   }

   @Override
   public Object getData() throws Exception {
      //Note the implicit assumption of Integer type here.  This is intentional.
      // The threat of a runtime exception is forcing the developer
      // of this class to be aware of and maintain an active coordination
      // with the developer of the KeyColumn this class depends on.
      int curNumber = (Integer) keyCol1to10.getCurrent();
      sum += curNumber;
      return sum;
   }
}
