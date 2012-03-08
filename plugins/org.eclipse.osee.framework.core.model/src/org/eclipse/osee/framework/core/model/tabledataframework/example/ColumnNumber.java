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
public class ColumnNumber extends ColumnAbstract {
   private static final String HEADER_STR = "Number";
   private final KeyColumn_1to10 keyCol1to10;

   //Column constructors have explicit dependencies in their parameter list.
   // This makes for simplified dependency documentation.
   public ColumnNumber(KeyColumn_1to10 keyCol1to10, boolean isVisible) {
      super(HEADER_STR, isVisible);
      this.keyCol1to10 = keyCol1to10;
   }

   @Override
   public Object getData() throws Exception {
      return keyCol1to10.getCurrent();
   }

}
