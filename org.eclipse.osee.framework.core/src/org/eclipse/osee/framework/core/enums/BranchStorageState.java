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
package org.eclipse.osee.framework.core.enums;

/**
 * @author Jeff C. Phillips
 *
 */
public enum BranchStorageState {
   
   UN_ARCHIVED(0), ARCHIVED(1);
   private final int value;

   BranchStorageState(int value) {
      this.value = value;
   }

   public final int getValue() {
      return value;
   }
}
